package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.awt.*;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

public class Day3Test {

    private static final Logger LOGGER = getLogger(Day3Test.class);
    
    private static final String PUZZLE_INPUT = "/aoc_2018/day3_puzzle_input.txt";

    @Test
    public void countOverlapping1() {
        assertEquals(4, Day3.countOverlapping(new Dimension(8, 8), Stream.of("#1 @ 1,3: 4x4",
                "#2 @ 3,1: 4x4",
                "#3 @ 5,5: 2x2")));
    }

    @Test
    public void countOverlapping() throws Exception {
        Stream<String> strings = Files.readAllLines(Paths.get(Day2Test.class.getResource(PUZZLE_INPUT).toURI())).stream();

        LOGGER.info("Total overlapping surface is: {}", Day3.countOverlapping(new Dimension(1_000, 1_000), strings));
    }
}