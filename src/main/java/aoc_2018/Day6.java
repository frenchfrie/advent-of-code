package aoc_2018;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class Day6 {

    private static final Logger LOGGER = getLogger(Day6.class);

    private static Set<Integer> findBorderIds(Map<Point, Location> locationsLibrary, Rectangle surfaceCoveredBySource) {
        Set<Integer> idOnBorders = new HashSet<>();
        for (int x = surfaceCoveredBySource.x, maxX = surfaceCoveredBySource.width + surfaceCoveredBySource.x; x <= maxX; x++) {
            int maxY = surfaceCoveredBySource.height + surfaceCoveredBySource.y;
            if (x == surfaceCoveredBySource.x || x == (maxX)) {
                for (int y = surfaceCoveredBySource.y; y <= maxY; y++) {
                    Point borderPoint = new Point(x, y);
                    Location location = locationsLibrary.get(borderPoint);
                    LOGGER.debug("Border point found: {} of id: {}", borderPoint, location.getId());
                    idOnBorders.add(location.getId());
                }
            } else {
                Point topBorderPoint = new Point(x, surfaceCoveredBySource.y);
                Location topBorderLocation = locationsLibrary.get(topBorderPoint);
                if (topBorderLocation != null) {
                    Integer topId = topBorderLocation.getId();
                    LOGGER.debug("Border point found: {} of id: {}", topBorderPoint, topId);
                    idOnBorders.add(topId);
                }

                Point bottomBorderPoint = new Point(x, maxY);
                Location bottomLocation = locationsLibrary.get(bottomBorderPoint);
                if (bottomLocation != null) {
                    LOGGER.debug("Border point found: {} of id: {}", bottomBorderPoint, bottomLocation.getId());
                    idOnBorders.add(bottomLocation.getId());
                }
            }
        }
        idOnBorders.remove(null);
        return idOnBorders;
    }

    private static TreeMap<Integer, Integer> aggregateScores(Collection<Location> locations) {
        return locations.stream().filter(l -> l.getId() != null).collect(Collectors.toMap(Location::getId, l -> 1, (i1, i2) -> i1 + i2, TreeMap::new));
    }

    private static Rectangle getSurfaceCovered(Collection<Location> locations) {
        AtomicReference<Rectangle> rectangleAtomicReference = new AtomicReference<>();
        locations.forEach(l -> rectangleAtomicReference.set(rectangleAtomicReference.get() == null ? new Rectangle(l.coordinates) : rectangleAtomicReference.get().union(new Rectangle(l.coordinates))));
        return rectangleAtomicReference.get();
    }

    private static String drawMap(Rectangle rectangle, Collection<Location> pointList) {
        int surfaceHeight = rectangle.y + rectangle.height + 1;
        int surfaceWidth = rectangle.x + rectangle.width + 1;
        String[][] raster = new String[surfaceWidth][surfaceHeight];
        for (int i = 0; i < raster.length; i++) {
            Arrays.fill(raster[i], "X");
        }

        for (Location location : pointList) {
            if (location.coordinates.x >= 0 && location.coordinates.x < surfaceWidth
                    && location.coordinates.y >= 0 && location.coordinates.y < surfaceHeight) {

                Integer locationId = location.id;


                raster[location.coordinates.x][location.coordinates.y] = locationId != null ? Integer.toString(locationId) : (location.totalDistToAlSources < 10000 ? "#" : ".");
            }
        }
        StringBuilder drawn = new StringBuilder();
        drawn.append(' ');
        for (int x = 0; x < surfaceWidth; x++) {
            drawn.append(x % 10);
        }
        drawn.append('\n');
        for (int y = 0; y < surfaceHeight; y++) {
            drawn.append(y % 10);
            for (int x = 0; x < surfaceWidth; x++) {
                drawn.append(raster[x][y]);
            }
            drawn.append('\n');
        }
        return drawn.toString();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static class Location {

        private static int nextAvailableSourceId = 0;

        private final Point coordinates;
        private Integer id;
        private Location source;
        private int totalDistToAlSources;

        // not source
        public Location(Point coordinates) {
            this.id = null;
            this.coordinates = coordinates;
        }

        // sources
        public Location(int id, Point coordinates) {
            this.id = id;
            this.coordinates = coordinates;
        }

        public Point getCoordinates() {
            return coordinates;
        }

        public Location getSource() {
            return source;
        }

        public void setSource(Location source) {
            if (source != null) {
                this.source = source;
            } else {
                this.source = null;
            }
        }

        public Integer getId() {
            return id == null && source != null ? source.getId() : id;
        }

        private static Location parsePoint(String input) {
            String[] split = input.split(",");
            Point point = new Point(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
            return new Location(nextAvailableSourceId++, point);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Location location = (Location) o;
            return Objects.equals(coordinates, location.coordinates);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinates);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .add("source", source)
                    .add("coordinates", coordinates)
                    .toString();
        }

        public void setTotalDistToAlSources(int distanceSum) {
            this.totalDistToAlSources = distanceSum;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Result findPointWithLargestFiniteInfluence_v3(Stream<String> coordinates) {
        Map<Point, Location> sourcePoints = coordinates.map(Location::parsePoint).collect(Collectors.toMap(l -> l.coordinates, l -> l));

        Rectangle surfaceCoveredBySource = getSurfaceCovered(sourcePoints.values());

        LOGGER.info("surface covered is: {}", surfaceCoveredBySource);

        LOGGER.info("points:\n{}", drawMap(surfaceCoveredBySource, sourcePoints.values()));

        // populate graph
        Location[][] locationRaster = new Location[surfaceCoveredBySource.width + 1][surfaceCoveredBySource.height + 1];
        for (int x = surfaceCoveredBySource.x, maxX = surfaceCoveredBySource.width + surfaceCoveredBySource.x; x <= maxX; x++) {
            for (int y = surfaceCoveredBySource.y, maxY = surfaceCoveredBySource.height + surfaceCoveredBySource.y; y <= maxY; y++) {
                Point currentPoint = new Point(x, y);
                Location sourceLocation = sourcePoints.get(currentPoint);
                Location value;
                if (sourceLocation == null) {
                    value = new Location(currentPoint);
                    setNearestSource(value, sourcePoints.values());
                } else {
                    value = sourceLocation;
                    setDistance(value, sourcePoints.values());
                }
                int absoluteX = x - surfaceCoveredBySource.x;
                int absoluteY = y - surfaceCoveredBySource.y;
                locationRaster[absoluteX][absoluteY] = value;
            }
        }
        LOGGER.info("after distance calculation:\n{}", drawMap(surfaceCoveredBySource, Arrays.stream(locationRaster).flatMap(Arrays::stream).collect(Collectors.toList())));

        Set<Integer> idOnBorders = findBorderIds(Arrays.stream(locationRaster).flatMap(Arrays::stream).collect(Collectors.toMap(n -> n.coordinates, n -> n)), surfaceCoveredBySource);

        LOGGER.info("Found id on the border: {}", idOnBorders);
        TreeMap<Integer, Integer> idToScore = aggregateScores(Arrays.stream(locationRaster).flatMap(Arrays::stream).collect(Collectors.toList()));
        LOGGER.info("Scores is: {}", idToScore);

        Optional<Map.Entry<Integer, Integer>> max = idToScore.entrySet().stream().filter(e -> !idOnBorders.contains(e.getKey())).max(Comparator.comparing(Map.Entry::getValue));
        Map.Entry<Integer, Integer> integerIntegerEntry = max.get();
        Location mostUsedLocation = sourcePoints.values().stream().filter(l -> Objects.equals(l.getId(), integerIntegerEntry.getKey())).findAny().get();

        long under10_000DistanceSummLocationNumber = Arrays.stream(locationRaster).flatMap(Arrays::stream).filter(l -> l.totalDistToAlSources < 10_000).count();
        return new Result(mostUsedLocation, max.get().getValue(), (int) under10_000DistanceSummLocationNumber);
    }

    public static class Result {
        public final Location mostUsedLocation;
        public final Integer score;
        public final Integer under10_000DistanceSummLocationNumber;

        public Result(Location mostUsedLocation, Integer score, Integer under10_000DistanceSummLocationNumber) {
            this.mostUsedLocation = mostUsedLocation;
            this.score = score;
            this.under10_000DistanceSummLocationNumber = under10_000DistanceSummLocationNumber;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("mostUsedLocation", mostUsedLocation)
                    .add("score", score)
                    .add("under10_000DistanceSummLocationNumber", under10_000DistanceSummLocationNumber)
                    .toString();
        }
    }

    private static void setNearestSource(Location loc, Collection<Location> sources) {
        int minDistance = Integer.MAX_VALUE;
        int distanceSum = 0;
        Location nearestLocation = null;
        for (Location source : sources) {
            int distance = Math.abs(loc.coordinates.x - source.coordinates.x) + Math.abs(loc.coordinates.y - source.coordinates.y);
            distanceSum += distance;
            if (distance < minDistance) {
                minDistance = distance;
                nearestLocation = source;
            } else if (distance == minDistance) {
//                LOGGER.info("found equidistant ({}) location from {}:\n{} and\n{}", distance, loc, nearestLocation, source);
                nearestLocation = null;
            }
        }
        loc.setSource(nearestLocation);
        loc.setTotalDistToAlSources(distanceSum);
    }

    private static void setDistance(Location loc, Collection<Location> sources) {
        int distanceSum = 0;
        for (Location source : sources) {
            if (!source.equals(loc)) {
                int distance = Math.abs(loc.coordinates.x - source.coordinates.x) + Math.abs(loc.coordinates.y - source.coordinates.y);
                distanceSum += distance;
            }
        }
        loc.setTotalDistToAlSources(distanceSum);
    }
}
