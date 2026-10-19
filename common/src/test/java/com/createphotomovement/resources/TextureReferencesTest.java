package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A model that names a texture the mod does not ship still loads -- it renders
 * the purple-and-black missing texture instead. {@link BlockCompletenessTest}
 * checks that blockstates find their models; this checks the next link in the
 * chain, from models to textures.
 *
 * <p>
 * Only this mod's own textures are checked. Create's and Minecraft's live in
 * other jars that these tests cannot see.
 */
class TextureReferencesTest {

    private static final String OWN_PREFIX = Target.MOD_ID + ":";

    private static JsonObject readObject(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    private static Path texture(Target target, String reference) {
        return target.assets().resolve("textures").resolve(reference.substring(OWN_PREFIX.length()) + ".png");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("every texture a model names exists")
    void modelTexturesExist(Target target) {
        List<String> missing = new ArrayList<>();

        for (Path model : Target.listJson(target.models())) {
            JsonObject root = readObject(model);
            if (!root.has("textures") || !root.get("textures").isJsonObject())
                continue;
            for (var entry : root.getAsJsonObject("textures").entrySet()) {
                JsonElement value = entry.getValue();
                if (!value.isJsonPrimitive())
                    continue;
                String reference = value.getAsString();
                // "#name" points at another slot of the same model; other namespaces
                // are somebody else's jar.
                if (!reference.startsWith(OWN_PREFIX))
                    continue;
                if (!Files.isRegularFile(texture(target, reference)))
                    missing.add(target.repoRoot().relativize(model) + " [" + entry.getKey() + "] -> " + reference);
            }
        }

        if (!missing.isEmpty())
            fail(missing.size() + " model texture(s) missing in " + target + ":\n  " + String.join("\n  ", missing));
    }

    /**
     * The bearing used to be Create's windmill bearing with a redstone-block
     * outline: it named Create's side texture and Minecraft's redstone block, and
     * shipped two textures of its own that nothing used. Now it carries its own
     * solar panel inlay and brass trim, and this pins that the model actually
     * points at them -- a copy-paste that quietly reverts to Create's texture would
     * still render, just without the mod's look.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("the solar windmill bearing uses its own side and trim textures")
    void bearingUsesItsOwnTextures(Target target) {
        JsonObject textures = readObject(target.models().resolve("block/solar_windmill_bearing.json"))
                .getAsJsonObject("textures");

        assertEquals(OWN_PREFIX + "block/solar_windmill_bearing_side", textures.get("bearing_side").getAsString(),
                "side texture in " + target);
        assertEquals(OWN_PREFIX + "block/solar_windmill_bearing_trim", textures.get("redstone").getAsString(),
                "trim texture in " + target);
        assertTrue(Files.isRegularFile(texture(target, textures.get("bearing_side").getAsString())),
                "side texture file in " + target);
        assertTrue(Files.isRegularFile(texture(target, textures.get("redstone").getAsString())),
                "trim texture file in " + target);
    }

    /**
     * The bearing's item model is a full copy of its block model plus the rotating
     * top, not a child of it, so it does not pick up changes to the block on its
     * own. The held item kept the old Create-and-redstone look after the block
     * got its own textures, which is how this was noticed.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("the bearing item shows the same textures as the bearing block")
    void bearingItemMatchesBlock(Target target) {
        JsonObject block = readObject(target.models().resolve("block/solar_windmill_bearing.json"))
                .getAsJsonObject("textures");
        JsonObject item = readObject(target.models().resolve("item/solar_windmill_bearing.json"))
                .getAsJsonObject("textures");

        for (String slot : List.of("bearing_side", "redstone", "particle"))
            assertEquals(block.get(slot).getAsString(), item.get(slot).getAsString(),
                    "texture slot '" + slot + "' of the item model in " + target);
    }
}
