package aoc_2018;

import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class Day1Test {

    private static final Logger LOGGER = getLogger(Day1Test.class);

    private static final String PUZZLE_INPUT_1 = "/aoc_2018/day1_puzzle_input.txt";

    @Test
    public void puzzleOne1() {
        assertEquals(3, Day1.solve("+1, +1, +1"));
    }

    @Test
    public void puzzleOne2() {
        assertEquals(0, Day1.solve("+1, +1, -2"));
    }

    @Test
    public void puzzleOne3() {
        assertEquals(-6, Day1.solve("-1, -2, -3"));
    }

    @Test
    public void puzzleOne() throws IOException {
        URL resourceAsStream = Day1.class.getResource(PUZZLE_INPUT_1);
        String inputData = String.join(",", Files.readAllLines(Paths.get(resourceAsStream.getPath())));
        LOGGER.info("Input data is: {}", inputData);
        LOGGER.info("Solution is: {}", Day1.solve(inputData));
    }

    @Test
    public void puzzleTwo1() {
        assertEquals(0, Day1.solve(Stream.of("+1, -1".split(","))));
    }

    @Test
    public void puzzleTwo2() {
        String[] split = "+3, +3, +4, -2, -4".split(",");
        assertEquals(10, Day1.solve(Stream.generate(new ArrayLoopSupplier<>(split))));
    }

    @Test
    public void puzzleTwo3() {
        String[] split = "-6, +3, +8, +5, -6".split(",");
        assertEquals(5, Day1.solve(Stream.generate(new ArrayLoopSupplier<>(split))));
    }

    @Test
    public void puzzleTwo4() {
        String[] split = "+7, +7, -2, -7, -4".split(",");
        assertEquals(14, Day1.solve(Stream.generate(new ArrayLoopSupplier<>(split))));
    }

    @Test
    public void puzzleTwo() throws IOException {
        URL resourceAsStream = Day1.class.getResource(PUZZLE_INPUT_1);
        List<String> elements = Files.readAllLines(Paths.get(resourceAsStream.getPath()));
        String inputData = String.join(",", elements);

        LOGGER.info("Input data is: {}", inputData);
        LOGGER.info("Solution is: {}", Day1.solve(Stream.generate(new ArrayLoopSupplier<>(elements.toArray(new String[]{})))));
    }


    private static class ArrayLoopSupplier<T> implements Supplier<T> {

        private final T[] data;

        private final int size;

        private int cursor = 0;

        private ArrayLoopSupplier(T[] data) {
            this.data = data;
            this.size = data.length;
        }

        @Override
        public T get() {
            if (cursor >= size) {
                cursor = 0;
            }
            return data[cursor++];
        }
    }
    
}
