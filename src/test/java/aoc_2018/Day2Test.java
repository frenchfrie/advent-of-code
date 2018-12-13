package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

public class Day2Test {

    private static final Logger LOGGER = getLogger(Day2Test.class);

    private static final String PUZZLE_INPUT_1 = "/aoc_2018/day2_puzzle_input.txt";

    @Test
    public void puzzleOne1() {
        Stream<String> input = Stream.of("abcdef", "bababc", "abbcde", "abcccd", "aabcdd", "abcdee", "ababab");

        assertEquals(12, Day2.checksum(input));
    }

    @Test
    public void puzzleOne() throws IOException, URISyntaxException {
        List<String> strings = Files.readAllLines(Paths.get(Day2Test.class.getResource(PUZZLE_INPUT_1).toURI()));
        LOGGER.info("Result is: {}", Day2.checksum(strings.stream()));
    }

    @Test
    public void puzzleTwo1() {
        Stream<String> input = Stream.of(
                "abcde",
                "fghij",
                "klmno",
                "pqrst",
                "fguij",
                "axcye",
                "wvxyz");

        assertEquals("fgij", Day2.findCommonLetters(input));
    }

    @Test
    public void puzzleTwo() throws URISyntaxException, IOException {
        Stream<String> input = Files.readAllLines(Paths.get(Day2Test.class.getResource(PUZZLE_INPUT_1).toURI())).stream();
        
        LOGGER.info("Result is: {}", Day2.findCommonLetters(input));
        
    }

}