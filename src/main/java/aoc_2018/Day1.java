package aoc_2018;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * https://adventofcode.com/2018/day/1
 */
public class Day1 {


    public static int solve(String commaSeparatedValues) {
        int frequency = 0;
        String[] variations = commaSeparatedValues.split(",");
        for (String variation : variations) {
            frequency += Integer.parseInt(variation.trim());
        }
        return frequency;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static int solve(Stream<String> values) {
        List<Integer> frequencies = new ArrayList<>();
        Integer frequency = 0;
        Iterator<String> valuesIterator = values.iterator();
        while (valuesIterator.hasNext()) {
            Integer nextValue = Integer.parseInt(valuesIterator.next().trim());
            frequency += nextValue;
            if (frequencies.contains(frequency)) {
                break;
            } else {
                frequencies.add(frequency);
            }
        }
        return frequency;
    }

}
