package com.createphotomovement.resources;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Guards the split between shared and per-target resources.
 *
 * <p>
 * A model, texture or blockstate that is the same on every target lives once,
 * in {@code common/src/main/resources}. A target keeps only what really differs
 * there. If the same path exists in both places, the build has two candidates
 * for one file in the jar: Gradle either stops with a duplicate error or picks
 * one, and an edit to the other copy silently does nothing.
 */
class CommonResourcesTest {

    private static List<Path> filesUnder(Path root) {
        if (!Files.isDirectory(root))
            return List.of();
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(Files::isRegularFile).sorted().toList();
        } catch (Exception e) {
            throw new IllegalStateException("Could not walk " + root, e);
        }
    }

    @Test
    @DisplayName("the shared resource folder is in use")
    void commonResourcesExist() {
        assertTrue(!filesUnder(Target.commonResources()).isEmpty(),
                "common/src/main/resources is empty -- wrong path?");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.createphotomovement.resources.Target#targets")
    @DisplayName("no file exists both in common and in the target")
    void noFileIsInBothPlaces(Target target) {
        List<String> duplicates = new ArrayList<>();

        for (Path shared : filesUnder(Target.commonResources())) {
            Path relative = Target.commonResources().relativize(shared);
            if (Files.exists(target.ownResources().resolve(relative.toString())))
                duplicates.add(relative.toString().replace('\\', '/'));
        }

        if (!duplicates.isEmpty())
            fail(duplicates.size() + " file(s) exist in common/src/main/resources AND in " + target
                    + " -- keep the one in common if they are meant to be the same,"
                    + " or delete it from common if this target needs its own:\n  "
                    + String.join("\n  ", duplicates));
    }
}
