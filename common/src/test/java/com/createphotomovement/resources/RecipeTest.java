package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RecipeTest {

    private static JsonObject readObject(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("recipe results use the field name this Minecraft version expects")
    void resultFieldMatchesTheVersion(Target target) {
        // 1.21.1 renamed the result item field from "item" to "id". Using the wrong one
        // makes the recipe load but produce nothing.
        String expected = target.recipeResultField();
        String wrong = target.isMc1211() ? "item" : "id";

        List<String> offenders = new ArrayList<>();
        List<Path> recipes = Target.listJson(target.recipes());
        assertFalse(recipes.isEmpty(), target + " ships no recipes -- wrong path?");

        for (Path file : recipes) {
            JsonObject root = readObject(file);
            if (!root.has("result"))
                continue;
            JsonElement result = root.get("result");
            if (!result.isJsonObject())
                continue;
            JsonObject resultObject = result.getAsJsonObject();

            String name = target.repoRoot().relativize(file).toString();
            if (resultObject.has(wrong) && !resultObject.has(expected))
                offenders.add(name + " uses \"" + wrong + "\", expected \"" + expected + "\"");
            else if (!resultObject.has(expected))
                offenders.add(name + " has no \"" + expected + "\" in its result");
        }

        if (!offenders.isEmpty())
            fail(offenders.size() + " recipe(s) with the wrong result field in " + target + ":\n  "
                    + String.join("\n  ", offenders));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("shaped recipes declare no keys their pattern never uses")
    void noUnusedPatternKeys(Target target) {
        List<String> offenders = new ArrayList<>();

        for (Path file : Target.listJson(target.recipes())) {
            JsonObject root = readObject(file);
            if (!root.has("pattern") || !root.has("key"))
                continue;

            Set<Character> used = new TreeSet<>();
            JsonArray pattern = root.getAsJsonArray("pattern");
            for (JsonElement row : pattern)
                for (char c : row.getAsString().toCharArray())
                    if (c != ' ')
                        used.add(c);

            Set<String> unused = new TreeSet<>();
            for (String key : root.getAsJsonObject("key").keySet())
                if (key.length() != 1 || !used.contains(key.charAt(0)))
                    unused.add(key);

            if (!unused.isEmpty())
                offenders.add(target.repoRoot().relativize(file) + " declares unused key(s) " + unused);
        }

        if (!offenders.isEmpty())
            fail(offenders.size() + " shaped recipe(s) with unused keys in " + target + ":\n  "
                    + String.join("\n  ", offenders));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("shaped recipe patterns use no character the key block never defines")
    void noUndefinedPatternCharacters(Target target) {
        List<String> offenders = new ArrayList<>();

        for (Path file : Target.listJson(target.recipes())) {
            JsonObject root = readObject(file);
            if (!root.has("pattern") || !root.has("key"))
                continue;

            Set<String> defined = root.getAsJsonObject("key").keySet();
            Set<Character> undefined = new TreeSet<>();
            for (JsonElement row : root.getAsJsonArray("pattern"))
                for (char c : row.getAsString().toCharArray())
                    if (c != ' ' && !defined.contains(String.valueOf(c)))
                        undefined.add(c);

            if (!undefined.isEmpty())
                offenders.add(target.repoRoot().relativize(file) + " uses undefined character(s) " + undefined);
        }

        if (!offenders.isEmpty())
            fail(offenders.size() + " shaped recipe(s) with undefined pattern characters in " + target + ":\n  "
                    + String.join("\n  ", offenders));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("shaped recipe patterns are rectangular")
    void patternsAreRectangular(Target target) {
        List<String> offenders = new ArrayList<>();

        for (Path file : Target.listJson(target.recipes())) {
            JsonObject root = readObject(file);
            if (!root.has("pattern"))
                continue;

            JsonArray pattern = root.getAsJsonArray("pattern");
            int width = -1;
            for (JsonElement row : pattern) {
                int length = row.getAsString().length();
                if (width == -1)
                    width = length;
                else if (width != length) {
                    offenders.add(target.repoRoot().relativize(file) + " has rows of differing width");
                    break;
                }
            }
        }

        if (!offenders.isEmpty())
            fail(offenders.size() + " recipe(s) with a ragged pattern in " + target + ":\n  "
                    + String.join("\n  ", offenders));
    }
}
