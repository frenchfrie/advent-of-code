package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class Day8Test {

    private static final Logger LOGGER = getLogger(Day8Test.class);

    @Test
    public void solvePart1_test() {
        Day8.Result result = new Day8().solve(Arrays.stream("2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2".split(" ")));

        LOGGER.info("Sum of all metadata entries: {}", result.sumOfMetadataEntries);
        LOGGER.info("Value of the root node: {}", result.sumOfNodesValues);
        assertEquals(138, result.sumOfMetadataEntries);
        assertEquals(66, result.sumOfNodesValues);
    }

    @Test
    public void solve() {
        Stream<String> input = Arrays.stream(AocTestUtils.getInput(8).findFirst().get().split(" "));

        Day8.Result result = new Day8().solve(input);

        LOGGER.info("Sum of all metadata entries: {}", result.sumOfMetadataEntries);
        LOGGER.info("Value of the root node: {}", result.sumOfNodesValues);
    }
}