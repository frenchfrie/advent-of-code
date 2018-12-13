package aoc_2018;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * https://adventofcode.com/2018/day/2
 */
public class Day2 {

    private static final Logger LOGGER = getLogger(Day2.class);
    
    public static int checksum(Stream<String> input) {
        AtomicInteger twoLetterCounter = new AtomicInteger(0);
        AtomicInteger threeLetterCounter = new AtomicInteger(0);
        input.map(Day2::getCharacterCount)
                .forEach(count -> {
                    if (count.containsValue(2)) {
                        twoLetterCounter.incrementAndGet();
                    }
                    if (count.containsValue(3)) {
                        threeLetterCounter.incrementAndGet();
                    }
                });
        LOGGER.info("Found {} with 2 identical letters and {} with 3.", twoLetterCounter, threeLetterCounter);
        return twoLetterCounter.get() * threeLetterCounter.get();
    }

    private static Map<Character, Integer> getCharacterCount(String input) {
        Map<Character, Integer> counter = new HashMap<>();
        char[] chars = input.toCharArray();
        for (Character character : chars) {
            counter.merge(character, 1, (a, b) -> a + b);
        }
        return counter;
    }

    public static String findCommonLetters(Stream<String> input) {
        List<String> valuesRead = new ArrayList<>();
        List<Pair<String, String>> foundSimilar = new ArrayList<>();

        input.forEach(v -> {
            for (String valueRead : valuesRead) {
                int levenshteinDistance = new LevenshteinDistance(1).apply(v, valueRead);
                if (levenshteinDistance == 1) {
                    foundSimilar.add(new ImmutablePair<>(valueRead, v));
                }
            }
            valuesRead.add(v);
        });
        LOGGER.info("Found {} similar values: {}", foundSimilar.size(), foundSimilar);
        if (foundSimilar.size() == 1) {
            Pair<String, String> next = foundSimilar.iterator().next();
            StringBuilder sb = new StringBuilder();
            char[] charArrayLeft = next.getLeft().toCharArray();
            char[] charArrayRight = next.getRight().toCharArray();
            for (int i = 0; i < charArrayLeft.length; i++) {
                char cLeft = charArrayLeft[i];
                char cRight = charArrayRight[i];
                if (cLeft == cRight) {
                    sb.append(cLeft);
                }
            }
            return sb.toString();
        } else {
            throw new RuntimeException();
        }
    }

}
