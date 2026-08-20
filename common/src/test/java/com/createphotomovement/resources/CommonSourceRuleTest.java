package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Guards the rule that makes the shared source set worth having.
 *
 * <p>
 * All three targets compile {@code common/src/main/java}. The moment a file
 * there imports something loader-specific, two of the three builds break -- but
 * only after a full Minecraft compile, which is slow and easy to misread. This
 * test says so in a second.
 */
class CommonSourceRuleTest {

    private static final Pattern FORBIDDEN_IMPORT = Pattern.compile(
            "^\\s*import\\s+(net\\.neoforged|net\\.minecraftforge|net\\.fabricmc|fuzs\\.)");

    /** Fully qualified usages sneak past an import check. */
    private static final Pattern FORBIDDEN_REFERENCE = Pattern.compile(
            "\\b(net\\.neoforged|net\\.minecraftforge|net\\.fabricmc|fuzs)\\.");

    private static Path commonJava() {
        return Target.repoRoot().resolve("common/src/main/java");
    }

    private static List<Path> javaFiles() {
        try (Stream<Path> stream = Files.walk(commonJava())) {
            return stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("Could not walk " + commonJava(), e);
        }
    }

    private static List<String> lines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    @Test
    @DisplayName("common/ imports nothing loader-specific")
    void noLoaderImports() {
        List<Path> files = javaFiles();
        assertFalse(files.isEmpty(), "common/src/main/java is empty -- wrong path?");

        List<String> offenders = new ArrayList<>();
        for (Path file : files) {
            List<String> lines = lines(file);
            for (int i = 0; i < lines.size(); i++)
                if (FORBIDDEN_IMPORT.matcher(lines.get(i)).find())
                    offenders.add(Target.repoRoot().relativize(file) + ":" + (i + 1) + " " + lines.get(i).trim());
        }

        if (!offenders.isEmpty())
            fail("Loader-specific import(s) in common/ -- these break two of the three builds:\n  "
                    + String.join("\n  ", offenders));
    }

    @Test
    @DisplayName("common/ has no fully qualified loader references either")
    void noFullyQualifiedLoaderReferences() {
        List<String> offenders = new ArrayList<>();

        for (Path file : javaFiles()) {
            List<String> lines = lines(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String withoutComment = line.replaceAll("//.*", "");
                if (FORBIDDEN_REFERENCE.matcher(withoutComment).find())
                    offenders.add(Target.repoRoot().relativize(file) + ":" + (i + 1) + " " + line.trim());
            }
        }

        if (!offenders.isEmpty())
            fail("Fully qualified loader reference(s) in common/:\n  " + String.join("\n  ", offenders));
    }

    @Test
    @DisplayName("the logic package imports no Minecraft at all")
    void logicPackageIsMinecraftFree() {
        // This is what lets the arithmetic be unit-tested without a game. If a
        // Minecraft import appears here, the tests in this module stop compiling --
        // but this check names the offending line instead of dumping a compiler error.
        Path logic = commonJava().resolve("com/createphotomovement/logic");
        List<String> offenders = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(logic)) {
            for (Path file : stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".java")).toList()) {
                List<String> lines = lines(file);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.matches("^\\s*import\\s+(net\\.minecraft|com\\.simibubi|net\\.createmod|dev\\.engine_room)\\..*"))
                        offenders.add(Target.repoRoot().relativize(file) + ":" + (i + 1) + " " + line.trim());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not walk " + logic, e);
        }

        if (!offenders.isEmpty())
            fail("Minecraft import(s) in common/logic -- these would make the arithmetic untestable headlessly:\n  "
                    + String.join("\n  ", offenders));
    }
}
