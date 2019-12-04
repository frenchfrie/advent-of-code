package aoc_2018;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class Day4Test {

    private static final Logger LOGGER = getLogger(Day4Test.class);

    @NotNull
    private static Stream<String> getTestData() {
        return Stream.of(
                "[1518-11-01 00:00] Guard #10 begins shift",
                "[1518-11-01 00:05] falls asleep",
                "[1518-11-01 00:25] wakes up",
                "[1518-11-01 00:30] falls asleep",
                "[1518-11-01 00:55] wakes up",
                "[1518-11-01 23:58] Guard #99 begins shift",
                "[1518-11-02 00:40] falls asleep",
                "[1518-11-02 00:50] wakes up",
                "[1518-11-03 00:05] Guard #10 begins shift",
                "[1518-11-03 00:24] falls asleep",
                "[1518-11-03 00:29] wakes up",
                "[1518-11-04 00:02] Guard #99 begins shift",
                "[1518-11-04 00:36] falls asleep",
                "[1518-11-04 00:46] wakes up",
                "[1518-11-05 00:03] Guard #99 begins shift",
                "[1518-11-05 00:45] falls asleep",
                "[1518-11-05 00:55] wakes");
    }

    @Test
    public void lol1() {
        Day4.Guard mostAsleepGuard = Day4.findMostAsleepGuard(getTestData());
        LOGGER.debug("Found guard #" + mostAsleepGuard.getId() + " with a sleep duration of: " + mostAsleepGuard.getSleepDuration().toString());
        LOGGER.debug("stats:\n" + mostAsleepGuard.printScores());
        assertEquals(10, mostAsleepGuard.getId());
    }

    @Test
    public void lol2() {
        Day4.Guard guard = Day4.getMostFrequentlyAsleepAtSameMinute(getTestData());
        Map.Entry<Integer, Integer> maxSleepMinute = guard.getMaxSleepMinute();
        LOGGER.debug("Found guard #" + guard.getId() + " with a sleep minute: " + maxSleepMinute.getKey() + " at score: " + maxSleepMinute.getValue());
        LOGGER.debug("stats:\n" + guard.printScores());
        assertEquals(99, guard.getId());
    }

    @Test
    public void lol() throws IOException, URISyntaxException {
        Day4.Guard guard = Day4.findMostAsleepGuard(getInput());
        LOGGER.info("Most asleep guard is {} with sleep time as:\n{}\nKey is: {} x {} = {}",
                guard.getId(), guard.printScores(), guard.getId(), guard.getMaxSleepMinute().getKey(), guard.getId() * guard.getMaxSleepMinute().getKey());
    }


    @Test
    public void lolSuite() throws IOException, URISyntaxException {
        Day4.Guard guard = Day4.getMostFrequentlyAsleepAtSameMinute(getInput());
        LOGGER.info("Most regularly guard is {} with sleep time as:\n{}\nKey is: {} x {} = {}",
                guard.getId(), guard.printScores(), guard.getId(), guard.getMaxSleepMinute().getKey(), guard.getId() * guard.getMaxSleepMinute().getKey());
    }

    private Stream<String> getInput() throws IOException, URISyntaxException {
        return Files.readAllLines(Paths.get(Day2Test.class.getResource("day4_puzzle_input.txt").toURI())).stream();
    }


}