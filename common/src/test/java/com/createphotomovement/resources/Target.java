package com.createphotomovement.resources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * One of the three build targets, as seen from the resource tests.
 *
 * <p>
 * The three do not lay their data out identically: 1.21.1 renamed the
 * {@code loot_tables} and {@code recipes} data folders to their singular forms.
 * This type hides that so the tests can talk about "the loot tables of this
 * target" without repeating the version check.
 */
public final class Target {

    public static final String MOD_ID = "createphotomovement";

    /** Set by the Gradle test task; falls back to walking up from the working dir. */
    private static final Path REPO_ROOT = findRepoRoot();

    public static final Target NEOFORGE_1201 = new Target("neoforge/1201", "1.20.1");
    public static final Target NEOFORGE_1211 = new Target("neoforge/1211", "1.21.1");
    public static final Target FABRIC_1201 = new Target("fabric/1201", "1.20.1");

    private final String path;
    private final String minecraftVersion;

    private Target(String path, String minecraftVersion) {
        this.path = path;
        this.minecraftVersion = minecraftVersion;
    }

    public static List<Target> all() {
        return List.of(NEOFORGE_1201, NEOFORGE_1211, FABRIC_1201);
    }

    /** JUnit {@code @MethodSource} entry point. */
    public static Stream<Target> targets() {
        return all().stream();
    }

    public static Path repoRoot() {
        return REPO_ROOT;
    }

    private static Path findRepoRoot() {
        String configured = System.getProperty("repoRoot");
        if (configured != null && !configured.isBlank())
            return Paths.get(configured);

        // Fallback for running the tests straight from an IDE.
        Path candidate = Paths.get("").toAbsolutePath();
        while (candidate != null && !Files.exists(candidate.resolve("settings.gradle")))
            candidate = candidate.getParent();
        if (candidate == null)
            throw new IllegalStateException("Could not locate the repository root; set -DrepoRoot=...");
        return candidate;
    }

    public boolean isMc1211() {
        return "1.21.1".equals(minecraftVersion);
    }

    /**
     * The field naming a recipe's result item. Renamed from {@code item} to
     * {@code id} in 1.21.1.
     */
    public String recipeResultField() {
        return isMc1211() ? "id" : "item";
    }

    public Path resources() {
        return REPO_ROOT.resolve(path).resolve("src/main/resources");
    }

    public Path assets() {
        return resources().resolve("assets").resolve(MOD_ID);
    }

    public Path data() {
        return resources().resolve("data").resolve(MOD_ID);
    }

    /** The data folder itself, i.e. the parent of every namespace this target ships. */
    public Path dataRoot() {
        return resources().resolve("data");
    }

    public Path blockstates() {
        return assets().resolve("blockstates");
    }

    public Path models() {
        return assets().resolve("models");
    }

    public Path lang(String locale) {
        return assets().resolve("lang").resolve(locale + ".json");
    }

    /** Renamed to the singular in 1.21.1. */
    public Path lootTables() {
        return data().resolve(isMc1211() ? "loot_table" : "loot_tables");
    }

    /** Renamed to the singular in 1.21.1. */
    public Path recipes() {
        return data().resolve(isMc1211() ? "recipe" : "recipes");
    }

    /** Every .json file this target ships, anywhere under its resources. */
    public List<Path> allJsonFiles() {
        return listJson(resources());
    }

    public static List<Path> listJson(Path root) {
        if (!Files.isDirectory(root))
            return List.of();
        try (Stream<Path> stream = Files.walk(root)) {
            List<Path> result = new ArrayList<>();
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .forEach(result::add);
            result.sort(Path::compareTo);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Could not walk " + root, e);
        }
    }

    /** File names without the .json suffix, directly inside the given directory. */
    public static List<String> baseNames(Path directory) {
        return listJson(directory).stream()
                .map(p -> p.getFileName().toString().replaceFirst("\\.json$", ""))
                .sorted()
                .toList();
    }

    @Override
    public String toString() {
        return path;
    }
}
