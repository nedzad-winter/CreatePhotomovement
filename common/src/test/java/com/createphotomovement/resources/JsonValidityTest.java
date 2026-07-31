package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A malformed JSON file does not fail the build -- it fails silently at runtime
 * and the block or recipe simply never appears. These tests catch that at
 * commit time instead.
 */
class JsonValidityTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("every shipped JSON file parses")
    void everyJsonFileParses(Target target) {
        List<Path> files = target.allJsonFiles();
        assertFalse(files.isEmpty(), target + " ships no JSON files at all -- wrong path?");

        List<String> broken = new ArrayList<>();
        for (Path file : files) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonParser.parseReader(reader);
            } catch (Exception e) {
                broken.add(target.repoRoot().relativize(file) + " -- " + e.getMessage());
            }
        }

        if (!broken.isEmpty())
            fail(broken.size() + " unparseable JSON file(s) in " + target + ":\n  " + String.join("\n  ", broken));
    }

    // Deliberately not tested: a UTF-8 byte order mark at the start of a file.
    // 441 of the shipped JSON files have one and the mod works, because Gson's
    // JsonReader skips a leading BOM. Asserting against it would fail the build over
    // something that demonstrably breaks nothing.
}
