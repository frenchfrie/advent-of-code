package aoc_2018;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Range;

import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.slf4j.LoggerFactory.getLogger;

public class Day4 {

    private static final Logger LOGGER = getLogger(Day4.class);

    public static Guard findMostAsleepGuard(Stream<String> records) {
        Map<Integer, Guard> guardsMap = buildGuardSchedules(records);
        return guardsMap.values().stream().sorted(Comparator.comparing(Guard::getSleepDuration).reversed()).findFirst().get();
    }

    public static Guard getMostFrequentlyAsleepAtSameMinute(Stream<String> records) {
        Map<Integer, Guard> guardsMap = buildGuardSchedules(records);
        return guardsMap.values().stream().sorted(Comparator.<Guard, Integer>comparing(g -> g.getMaxSleepMinute().getValue()).reversed()).findFirst().get();
    }

    private static Map<Integer, Guard> buildGuardSchedules(Stream<String> records) {
        Map<Integer, Guard> guardsMap = new HashMap<>();

        records.map(EventRecord::parse).sorted(Comparator.naturalOrder())
                .forEach(new Consumer<>() {

                    private Guard currentGuard;

                    @Override
                    public void accept(EventRecord e) {
                        LOGGER.info("handling event:{}", e);
                        switch (e.type) {
                            case GUARD_BEGINS_SHIFT:
                                currentGuard = guardsMap.computeIfAbsent(e.guardId, Guard::new);
                                break;
                            case GUARD_FALLS_ASLEEP:
                                if (currentGuard == null) {
                                    LOGGER.warn("No guards and falling asleep!");
                                } else {
                                    currentGuard.fallsAsleep(e.date);
                                }
                                break;
                            case GUARD_WAKES_UP:
                                if (currentGuard == null) {
                                    LOGGER.warn("No guards and waking up!");
                                } else {
                                    currentGuard.wakeUp(e.date);
                                    LOGGER.info("Guard #{} waking up. New stats are:\n{}", currentGuard.guardId, currentGuard.printScores());
                                }
                                break;
                        }
                    }
                }); return guardsMap;
    }

    private static class EventRecord implements Comparable<EventRecord> {
        // year-month-day hour:minute
        private static final java.time.format.DateTimeFormatter format
                = new DateTimeFormatterBuilder().appendPattern("uuuu-MM-dd HH:mm").toFormatter(Locale.US);
        private final LocalDateTime date;
        private final Type type;
        private final Integer guardId;

        private EventRecord(LocalDateTime date, Type type, Integer guardId) {
            this.date = date;
            this.type = type;
            this.guardId = guardId;
        }

        // [1518-11-04 00:02] Guard #99 begins shift
        // [1518-11-04 00:36] falls asleep
        // [1518-11-04 00:46] wakes up
        @Override
        public int compareTo(EventRecord o) {
            return date.compareTo(o.date);
        }

        public static EventRecord parse(String recordAsString) {
            String datePart = recordAsString.substring(1, 17);
            LocalDateTime date = LocalDateTime.parse(datePart, format);
            String typeIdentifier = recordAsString.substring(19, 24);
            Type type;
            Integer guardId;
            switch (typeIdentifier) {
                case "Guard":
                    type = Type.GUARD_BEGINS_SHIFT;
                    String guardIdAsString = substringBefore(substringAfter(recordAsString, "Guard #"), " begins shift");
                    guardId = Integer.parseInt(guardIdAsString);
                    break;
                case "falls":
                    type = Type.GUARD_FALLS_ASLEEP;
                    guardId = null;
                    break;
                case "wakes":
                    type = Type.GUARD_WAKES_UP;
                    guardId = null;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return new EventRecord(date, type, guardId);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("date", date)
                    .add("type", type)
                    .add("guardId", guardId)
                    .toString();
        }

        public enum Type {
            GUARD_FALLS_ASLEEP,
            GUARD_WAKES_UP,
            GUARD_BEGINS_SHIFT,

        }
    }

    public static class Guard {
        private final int guardId;
        private State state;
        private List<Range<LocalDateTime>> asleepPeriods = new ArrayList<>();
        private Duration timeAsleep = Duration.ZERO;
        private LocalDateTime lastTimeFallingAsleep;
        private Map<Integer, Integer> minutesScores = new LinkedHashMap<>();

        private Guard(int guardId) {
            this.guardId = guardId;
            for (int minute = 0; minute < 60; minute++) {
                minutesScores.put(minute, 0);
            }
        }

        public int getId() {
            return guardId;
        }

        public Map.Entry<Integer, Integer> getMaxSleepMinute() {
            return minutesScores.entrySet().stream().sorted(Comparator.comparing((Function<Map.Entry<Integer, Integer>, Integer>) Map.Entry::getValue).reversed()).findFirst().get();
        }

        private enum State {
            AWAKE,
            ASLEEP
        }

        public void wakeUp(LocalDateTime time) {
            timeAsleep = timeAsleep.plus(Duration.between(lastTimeFallingAsleep, time));
            Range<LocalDateTime> closed = Range.closedOpen(lastTimeFallingAsleep, time);
            asleepPeriods.add(closed);
            minutesScores.keySet().stream() //
                    .filter(k -> closed.contains(LocalDateTime.from(time).withMinute(k))) //
                    .forEach(k -> minutesScores.compute(k, (k1, v1) -> v1 + 1));
            state = State.AWAKE;
        }

        public void fallsAsleep(LocalDateTime time) {
            lastTimeFallingAsleep = time;
            state = State.ASLEEP;
        }

        public Duration getSleepDuration() {
            return timeAsleep;
        }
        
        public String printScores() {
            return minutesScores.values().stream().map(i -> Integer.toString(i)).collect(Collectors.joining(" ")) + " max is " +
                    getMaxSleepMinute();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("guardId", guardId)
                    .add("state", state)
                    .add("timeAsleep", timeAsleep)
                    .add("lastTimeFallingAsleep", lastTimeFallingAsleep)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Guard guard = (Guard) o;
            return guardId == guard.guardId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(guardId);
        }
    }

}
