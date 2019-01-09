package aoc_2018;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AocTestUtils {

    private AocTestUtils() {
    }

    static Stream<String> getInput(String name) throws IOException, URISyntaxException {
        return Files.readAllLines(Paths.get(Day2Test.class.getResource(name).toURI())).stream();
    }

    static Stream<String> getInput(int day) {
        try {
            return Files.readAllLines(Paths.get(Day2Test.class.getResource("day" + day + "_puzzle_input.txt").toURI())).stream();
        } catch (IOException | URISyntaxException e) {
            throw new Error("Cannot load input file.", e);
        }
    }
}
