package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * 1.20.5 renamed the data pack folders to their singular forms: {@code blocks} to
 * {@code block}, {@code loot_tables} to {@code loot_table}, and so on. Our 1.20.1
 * targets need the old names, the 1.21.1 target needs the new ones.
 *
 * <p>
 * Getting this wrong is silent. The game does not warn about a folder it does not
 * recognise -- it simply never reads the files inside. Both 1.20.1 targets shipped
 * their mineable tags under {@code tags/block/} up to and including v0.3.3, so the
 * axe and pickaxe entries never applied. Same failure mode as a missing loot table:
 * no error anywhere, just behaviour quietly gone.
 *
 * <p>
 * Only two levels are checked, and deliberately so: the folders directly under a
 * namespace, and the folders directly under its {@code tags/}. Anything deeper is a
 * plain id path rather than a registry name -- {@code loot_table/blocks/} keeps its
 * plural on 1.21.1, and a rule that walked the whole tree would flag it.
 */
class DataFolderNamesTest {

    /** Directly under {@code data/<namespace>/}. Singular form to plural form. */
    private static final Map<String, String> NAMESPACE_FOLDERS = Map.of(
            "loot_table", "loot_tables",
            "recipe", "recipes",
            "structure", "structures",
            "advancement", "advancements",
            "tags", "tags");

    /** Directly under {@code data/<namespace>/tags/}. Singular form to plural form. */
    private static final Map<String, String> TAG_FOLDERS = Map.of(
            "block", "blocks",
            "item", "items",
            "fluid", "fluids",
            "entity_type", "entity_types",
            "game_event", "game_events");

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("data folders use the naming of their Minecraft version")
    void dataFoldersUseTheNamingOfTheirMinecraftVersion(Target target) {
        Path dataRoot = target.dataRoot();
        assertTrue(Files.isDirectory(dataRoot), target + " has no data folder at " + dataRoot);

        List<String> problems = new ArrayList<>();
        List<Path> namespaces = subdirectories(dataRoot);
        assertTrue(!namespaces.isEmpty(), target + " ships no data namespaces -- wrong path?");

        for (Path namespace : namespaces) {
            check(target, namespace, NAMESPACE_FOLDERS, problems);
            check(target, namespace.resolve("tags"), TAG_FOLDERS, problems);
        }

        if (!problems.isEmpty())
            fail(problems.size() + " misnamed data folder(s) in " + target
                    + " (the game reads nothing inside them):\n  "
                    + String.join("\n  ", problems));
    }

    /**
     * Flags every subdirectory of {@code parent} that uses the naming of the other
     * Minecraft version. Folders the maps say nothing about are left alone.
     */
    private static void check(Target target, Path parent, Map<String, String> renamed, List<String> problems) {
        for (Path directory : subdirectories(parent)) {
            String name = directory.getFileName().toString();

            for (Map.Entry<String, String> entry : renamed.entrySet()) {
                String singular = entry.getKey();
                String plural = entry.getValue();
                if (singular.equals(plural))
                    continue;

                String expected = target.isMc1211() ? singular : plural;
                String wrong = target.isMc1211() ? plural : singular;

                if (name.equals(wrong))
                    problems.add(relative(target, directory) + " should be named '" + expected + "'");
            }
        }
    }

    private static List<Path> subdirectories(Path parent) {
        if (!Files.isDirectory(parent))
            return List.of();
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, Files::isDirectory)) {
            stream.forEach(result::add);
        } catch (IOException e) {
            throw new IllegalStateException("Could not list " + parent, e);
        }
        result.sort(Path::compareTo);
        return result;
    }

    private static String relative(Target target, Path directory) {
        return target.resources().relativize(directory).toString().replace('\\', '/');
    }
}
