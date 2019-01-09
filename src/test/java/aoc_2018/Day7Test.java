package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class Day7Test {

    private static final Logger LOGGER = getLogger(Day7Test.class);

    private Day7 day7 = new Day7();

    @Test
    public void solve_testData() {
        Day7.Result result = day7.solve(Stream.of(
                "Step C must be finished before step A can begin.",
                "Step C must be finished before step F can begin.",
                "Step A must be finished before step B can begin.",
                "Step A must be finished before step D can begin.",
                "Step B must be finished before step E can begin.",
                "Step D must be finished before step E can begin.",
                "Step F must be finished before step E can begin."), 0, 2);

        assertEquals("CABFDE", result.steps);
        assertEquals(15, result.executionTime);
    }

    @Test
    public void solve() {
        Day7.Result result = day7.solve(AocTestUtils.getInput(7), 60, 5);

        LOGGER.info("tasks done: {}", result.steps);
        LOGGER.info("Executed in: {}", result.executionTime);
    }
}
