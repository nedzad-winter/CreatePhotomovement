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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A missing translation key shows up in game as the raw key, e.g.
 * {@code block.createphotomovement.solar_generator}. These tests keep the three
 * locales and the three targets in step.
 */
class TranslationParityTest {

    private static final List<String> LOCALES = List.of("en_us", "de_de", "es_es");

    private static Set<String> keys(Target target, String locale) {
        Path file = target.lang(locale);
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            return new TreeSet<>(object.keySet());
        } catch (Exception e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("de_de and es_es carry exactly the keys en_us has")
    void translationsMatchEnglish(Target target) {
        Set<String> english = keys(target, "en_us");
        assertFalse(english.isEmpty(), target + " has an empty en_us -- wrong path?");

        List<String> problems = new ArrayList<>();
        for (String locale : List.of("de_de", "es_es")) {
            Set<String> translated = keys(target, locale);

            Set<String> missing = new TreeSet<>(english);
            missing.removeAll(translated);
            if (!missing.isEmpty())
                problems.add(locale + " is missing " + missing.size() + " key(s): " + missing);

            Set<String> extra = new TreeSet<>(translated);
            extra.removeAll(english);
            if (!extra.isEmpty())
                problems.add(locale + " has " + extra.size() + " key(s) en_us does not: " + extra);
        }

        if (!problems.isEmpty())
            fail("Translation drift in " + target + ":\n  " + String.join("\n  ", problems));
    }

    @Test
    @DisplayName("all three targets ship the same set of translation keys")
    void targetsAgreeOnKeys() {
        List<String> problems = new ArrayList<>();

        for (String locale : LOCALES) {
            Target reference = Target.NEOFORGE_1211;
            Set<String> expected = keys(reference, locale);

            for (Target target : Target.all()) {
                if (target == reference)
                    continue;
                Set<String> actual = keys(target, locale);

                Set<String> missing = new TreeSet<>(expected);
                missing.removeAll(actual);
                if (!missing.isEmpty())
                    problems.add(locale + ": " + target + " is missing " + missing.size()
                            + " key(s) present in " + reference + ": " + missing);

                Set<String> extra = new TreeSet<>(actual);
                extra.removeAll(expected);
                if (!extra.isEmpty())
                    problems.add(locale + ": " + target + " has " + extra.size()
                            + " key(s) absent from " + reference + ": " + extra);
            }
        }

        if (!problems.isEmpty())
            fail("Translation keys differ between targets:\n  " + String.join("\n  ", problems));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("no translation value is empty")
    void noEmptyTranslations(Target target) {
        List<String> problems = new ArrayList<>();

        for (String locale : LOCALES) {
            Path file = target.lang(locale);
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : object.keySet())
                    if (object.get(key).getAsString().isBlank())
                        problems.add(locale + " has an empty value for " + key);
            } catch (Exception e) {
                throw new IllegalStateException("Could not read " + file, e);
            }
        }

        if (!problems.isEmpty())
            fail("Empty translations in " + target + ":\n  " + String.join("\n  ", problems));
    }
}
