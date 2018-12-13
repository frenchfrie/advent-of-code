package aoc_2018;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class Day5 {

    private static final Logger LOGGER = getLogger(Day5.class);

    public static Result findBestRemoval(String polymer) {
        Optional<Result> samllestResult = polymer.chars().map(Character::toLowerCase).sorted().mapToObj(Character::toString).distinct().
                map(letter -> {
                    String react = react(StringUtils.removeIgnoreCase(polymer, letter));
                    return new Result(letter, react);
                }).min(Comparator.comparing(Result::getPolymerSize));
        return samllestResult.get();
    }

    public static class Result {
        private final String keyRemoved;
        private final String resultingPolymer;
        private final int polymerSize;

        public Result(String keyRemoved, String resultingPolymer) {
            this.keyRemoved = keyRemoved;
            this.resultingPolymer = resultingPolymer;
            this.polymerSize = resultingPolymer.length();
        }

        public int getPolymerSize() {
            return polymerSize;
        }

        public String getKeyRemoved() {
            return keyRemoved;
        }

        public String getResultingPolymer() {
            return resultingPolymer;
        }
    }

    public static String react(String polymer) {
        String polymerReacting = polymer;
        boolean reacted;
        do {
            reacted = false;
            StringBuilder polymerAfterReaction = new StringBuilder();

            Character previous = null;
            Character current = null;
            char[] chars = polymerReacting.toCharArray();
            for (int i = 0, arrayLength = chars.length; i < arrayLength; i++) {
                current = chars[i];
                if (previous != null) {
                    if (doReact(previous, current)) {
                        // we have a reaction ! Nullify previous skipping previous AND current char
                        previous = null;
                        current = null;
                        reacted = true;
                    } else {
                        polymerAfterReaction.append(previous);
                        previous = current;
                    }
                } else {
                    // we are at the beginning or reaction just happened
                    previous = current;
                }
            }
            if (current != null) polymerAfterReaction.append(current);
            polymerReacting = polymerAfterReaction.toString();
        } while (reacted);
        return polymerReacting;
    }

    private static boolean doReact(char char1, char char2) {
        return (Character.isUpperCase(char2) && Character.toLowerCase(char2) == char1) || (Character.isLowerCase(char2) && Character.toUpperCase(char2) == char1);
    }


}
