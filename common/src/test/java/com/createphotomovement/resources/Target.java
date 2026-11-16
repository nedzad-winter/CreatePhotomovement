package com.createphotomovement.resources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
 *
 * <p>
 * A target's resources come from two places: the files every target shares
 * ({@code common/src/main/resources}) and the target's own
 * ({@code <target>/src/main/resources}). Gradle lays the two on top of each
 * other when it builds the jar. {@link #resources()} does the same into a
 * scratch folder, so the tests see exactly the set of files the jar will
 * contain. {@link #source(Path)} turns a path in that scratch folder back into
 * the real file, for failure messages.
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

    /** The merged copy of common + own resources; built on first use. */
    private Path merged;

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

    /** The resources shared by every target. */
    public static Path commonResources() {
        return REPO_ROOT.resolve("common/src/main/resources");
    }

    /** The resources only this target has. */
    public Path ownResources() {
        return REPO_ROOT.resolve(path).resolve("src/main/resources");
    }

    /**
     * Everything this target ships: the shared resources with the target's own laid
     * over them, copied into {@code common/build/merged-resources/<target>}.
     */
    public synchronized Path resources() {
        if (merged == null) {
            Path into = REPO_ROOT.resolve("common/build/merged-resources").resolve(path);
            deleteTree(into);
            copyTree(commonResources(), into);
            copyTree(ownResources(), into);
            merged = into;
        }
        return merged;
    }

    /**
     * The real file behind a path under {@link #resources()}, relative to the
     * repository root -- so a failure names the file to edit, not its copy.
     */
    public Path source(Path file) {
        Path relative = resources().relativize(file.toAbsolutePath());
        Path own = ownResources().resolve(relative);
        Path real = Files.exists(own) ? own : commonResources().resolve(relative);
        return REPO_ROOT.relativize(real);
    }

    private static void copyTree(Path from, Path into) {
        if (!Files.isDirectory(from))
            return;
        try (Stream<Path> stream = Files.walk(from)) {
            for (Path source : (Iterable<Path>) stream::iterator) {
                Path destination = into.resolve(from.relativize(source).toString());
                if (Files.isDirectory(source))
                    Files.createDirectories(destination);
                else
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not copy " + from + " to " + into, e);
        }
    }

    private static void deleteTree(Path root) {
        if (!Files.exists(root))
            return;
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(java.util.Comparator.reverseOrder()).toList())
                Files.delete(path);
        } catch (Exception e) {
            throw new IllegalStateException("Could not clear " + root, e);
        }
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
