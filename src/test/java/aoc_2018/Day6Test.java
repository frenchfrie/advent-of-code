package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class Day6Test {

    private static final Logger LOGGER = getLogger(Day6Test.class);

    private Stream<String> getInput() throws IOException, URISyntaxException {
        String name = "day6_puzzle_input.txt";
        return AocTestUtils.getInput(name);
    }

    @Test
    public void v2_findPointWithLargestFiniteInfluence1() {
        Day6.Result result = Day6.findPointWithLargestFiniteInfluence_v3(Stream.of(
                "1, 1",
                "1, 6",
                "8, 3",
                "3, 4",
                "5, 5",
                "8, 9"

        ));
        assertEquals(new Point(5, 5), result.mostUsedLocation.getCoordinates());
    }

    @Test
    public void v2_findPointWithLargestFiniteInfluence() throws IOException, URISyntaxException {
//        ((ch.qos.logback.classic.Logger) LOGGER).getLoggerContext().getLogger(Day6.class).setLevel(Level.WARN);
        LOGGER.warn("Most extended point is {}", Day6.findPointWithLargestFiniteInfluence_v3(getInput()));
    }

}