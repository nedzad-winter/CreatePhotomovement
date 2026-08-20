package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Every block needs a blockstate, a block model, an item model, a loot table
 * and an English name. Miss one and the block still registers -- it just turns
 * into a purple-and-black cube, or drops nothing when broken.
 *
 * <p>
 * The set of blocks is taken from the blockstate files, which is the one
 * listing that must exist for a block to render at all.
 */
class BlockCompletenessTest {

    private static Set<String> blockNames(Target target) {
        return new TreeSet<>(Target.baseNames(target.blockstates()));
    }

    private static JsonObject readObject(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("every block has an item model")
    void everyBlockHasAnItemModel(Target target) {
        Set<String> models = new TreeSet<>(Target.baseNames(target.models().resolve("item")));
        Set<String> missing = new TreeSet<>(blockNames(target));
        missing.removeAll(models);

        assertFalse(blockNames(target).isEmpty(), target + " has no blockstates -- wrong path?");
        if (!missing.isEmpty())
            fail(missing.size() + " block(s) without an item model in " + target + ":\n  "
                    + String.join("\n  ", missing));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("every block has a loot table")
    void everyBlockHasALootTable(Target target) {
        Set<String> loot = new TreeSet<>();
        for (Path file : Target.listJson(target.lootTables()))
            loot.add(file.getFileName().toString().replaceFirst("\\.json$", ""));

        Set<String> missing = new TreeSet<>(blockNames(target));
        missing.removeAll(loot);

        if (!missing.isEmpty())
            fail(missing.size() + " block(s) without a loot table in " + target + " (they drop nothing when broken):\n  "
                    + String.join("\n  ", missing));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("every block has an English name")
    void everyBlockHasAnEnglishName(Target target) {
        JsonObject lang = readObject(target.lang("en_us"));

        Set<String> missing = new TreeSet<>();
        for (String block : blockNames(target)) {
            String key = "block." + Target.MOD_ID + "." + block;
            if (!lang.has(key))
                missing.add(key);
        }

        if (!missing.isEmpty())
            fail(missing.size() + " block(s) without an en_us name in " + target + ":\n  "
                    + String.join("\n  ", missing));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("every blockstate points at models that exist")
    void blockstatesReferenceExistingModels(Target target) {
        List<String> dangling = new ArrayList<>();

        for (Path file : Target.listJson(target.blockstates())) {
            JsonObject root = readObject(file);
            for (String modelRef : collectModelReferences(root)) {
                if (!modelRef.startsWith(Target.MOD_ID + ":"))
                    continue; // vanilla or another mod's model, not ours to check
                String relative = modelRef.substring(Target.MOD_ID.length() + 1);
                Path model = target.models().resolve(relative + ".json");
                if (!Files.isRegularFile(model))
                    dangling.add(target.repoRoot().relativize(file) + " -> " + modelRef);
            }
        }

        if (!dangling.isEmpty())
            fail(dangling.size() + " blockstate(s) referencing a missing model in " + target + ":\n  "
                    + String.join("\n  ", dangling));
    }

    /** Pulls every {@code "model": "..."} value out of a blockstate file. */
    private static Set<String> collectModelReferences(JsonObject root) {
        Set<String> refs = new LinkedHashSet<>();
        root.entrySet().forEach(entry -> collectModelReferences(entry.getValue(), refs));
        return refs;
    }

    private static void collectModelReferences(com.google.gson.JsonElement element, Set<String> into) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("model") && object.get("model").isJsonPrimitive())
                into.add(object.get("model").getAsString());
            object.entrySet().forEach(entry -> collectModelReferences(entry.getValue(), into));
        } else if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> collectModelReferences(child, into));
        }
    }
}
