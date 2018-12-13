package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

public class Day5Test {

    private static final Logger LOGGER = getLogger(Day5Test.class);
    
    @Test
    public void react1() {
        assertEquals("dabCBAcaDA", Day5.react("dabAcCaCBAcCcaDA"));
    }

    @Test
    public void react() throws IOException, URISyntaxException {
        String input = getInput();
        String reactedPolymer = Day5.react(input);
        LOGGER.info("After many reactions, polymer went from, to:\n{} -> {}\n{} -> {}", input.length(), input, reactedPolymer.length(), reactedPolymer);
    }

    @Test
    public void findBestRemoval1() {
        assertEquals("daDA", Day5.findBestRemoval("dabAcCaCBAcCcaDA").getResultingPolymer());
    }

    @Test
    public void findBestRemoval() throws IOException, URISyntaxException {
        Day5.Result bestRemoval = Day5.findBestRemoval(getInput());
        LOGGER.info("Found best to remove letter {} which gives a polymer of {} units:\n{}", bestRemoval.getKeyRemoved(), bestRemoval.getPolymerSize(), bestRemoval.getResultingPolymer());
    }

    private String getInput() throws IOException, URISyntaxException {
        return Files.readString(Paths.get(Day2Test.class.getResource("day5_puzzle_input.txt").toURI())).trim();
    }

}