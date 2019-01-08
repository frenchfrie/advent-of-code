package aoc_2018;

import com.google.common.base.MoreObjects;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import com.google.inject.grapher.graphviz.CompassPoint;

import org.slf4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class Day6 {

    private static final Logger LOGGER = getLogger(Day6.class);

    public static Point findPointWithLargestFiniteInfluence(Stream<String> coordinates) {
        List<Location> sourcePoints = coordinates.map(Location::parsePoint).collect(Collectors.toList());

        Map<Point, Location> locationsLibrary = new HashMap<>();
        for (Location location : sourcePoints) {
            locationsLibrary.put(location.coordinates, location);
        }

        Rectangle surfaceCoveredBySource = getSurfaceCovered(sourcePoints);
        LOGGER.info("points:\n{}", drawMap(surfaceCoveredBySource, sourcePoints));

        Set<Integer> idOnBorders = findBorderIds(locationsLibrary, surfaceCoveredBySource);

        LOGGER.info("Found id on the border: {}", idOnBorders);

        TreeMap<Integer, Integer> idToScore = aggregateScores(locationsLibrary.values());
        LOGGER.info("Scores is: {}", idToScore);
        return sourcePoints.get(idToScore.entrySet().stream().filter(e -> !idOnBorders.contains(e.getKey())).max(Comparator.comparing(Map.Entry::getValue)).get().getKey()).coordinates;
    }

    private static Set<Integer> findBorderIds(Map<Point, Location> locationsLibrary, Rectangle surfaceCoveredBySource) {
        Set<Integer> idOnBorders = new HashSet<>();
        for (int x = surfaceCoveredBySource.x, maxX = surfaceCoveredBySource.width + surfaceCoveredBySource.x; x <= maxX; x++) {
            int maxY = surfaceCoveredBySource.height + surfaceCoveredBySource.y;
            if (x == surfaceCoveredBySource.x || x == (maxX)) {
                for (int y = surfaceCoveredBySource.y; y <= maxY; y++) {
                    Point borderPoint = new Point(x, y);
                    Location location = locationsLibrary.get(borderPoint);
                    LOGGER.debug("Border point found: {} of id: {}", borderPoint, location.id);
                    idOnBorders.add(location.id);
                }
            } else {
                Point topBorderPoint = new Point(x, surfaceCoveredBySource.y);
                int topId = locationsLibrary.get(topBorderPoint).id;
                LOGGER.debug("Border point found: {} of id: {}", topBorderPoint, topId);
                idOnBorders.add(topId);

                Point bottomBorderPoint = new Point(x, maxY);
                Location bottomLocation = locationsLibrary.get(bottomBorderPoint);
                LOGGER.debug("Border point found: {} of id: {}", bottomBorderPoint, bottomLocation.id);
                idOnBorders.add(bottomLocation.id);
            }
        }
        return idOnBorders;
    }

    private static TreeMap<Integer, Integer> aggregateScores(Collection<Location> locations) {
        return locations.stream().collect(Collectors.toMap(l -> l.id, l -> 1, (i1, i2) -> i1 + i2, TreeMap::new));
    }

    private static boolean isCompletelyFilled(Rectangle surface, Map<Point, Location> locationsLibrary) {
        boolean filled = true;
        for (int x = surface.x, maxX = surface.width + surface.x; x <= maxX; x++) {
            for (int y = surface.y, maxY = surface.height + surface.y; y <= maxY; y++) {
                Point key = new Point(x, y);
                if (!locationsLibrary.containsKey(key)) {
                    filled = false;
                    break;
                }
            }
            if (!filled) break;
        }
        return filled;
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
                raster[location.coordinates.x][location.coordinates.y] = location.id != null ? Integer.toString(location.id) : ".";
            }
        }
        StringBuilder drawn = new StringBuilder();
        drawn.append(' ');
        for (int x = 0; x < surfaceWidth; x++) {
            drawn.append(x % 10);
        }
        drawn.append('\n');
        for (int y = 0; y < surfaceHeight; y++) {
            drawn.append(y%10);
            for (int x = 0; x < surfaceWidth; x++) {
                drawn.append(raster[x][y]);
            }
            drawn.append('\n');
        }
        return drawn.toString();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Location {

        private static int nextAvailableSourceId = 0;

        private Integer id; // id of source location
        private Integer distance; // distance from a source location, 0 if source
        private final boolean source; // is a source location
        private final Point coordinates;

        public Location(Integer id, boolean source, Point coordinates, Integer distance) {
            this.id = id;
            this.source = source;
            this.coordinates = coordinates;
            this.distance = distance;
        }

        private static Location createSource(Point coordinates) {
            return new Location(nextAvailableSourceId++, true, coordinates, 0);
        }

        private static Location parsePoint(String input) {
            String[] split = input.split(",");
            Point point = new Point(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
            return createSource(point);
        }

        public void expand(Graph<Location> graph, int id, int distance, int depht) {


            if (this.id == null) {
                // untouched location
                this.id = id;
                this.distance = distance;
            } else if (this.id == id) {
                // already owned node
            } else {
                // another source owned node
                if (this.distance == distance) {
                    this.id = -1;
                    this.distance = null;
                } else if (this.distance < distance) {
                    // discard, we are nearer to another source
                } else {
                    // we are closer
                    this.id = id;
                    this.distance = distance;
                }
            }

            if (distance < depht) {
                Set<Location> locations = graph.adjacentNodes(this);
                locations.stream()
                        .filter(n -> (n.id == null || n.id == id))
                        .forEach(n -> n.expand(graph, id, distance + 1, depht));
            }

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
                    .add("distance", distance)
                    .add("source", source)
                    .add("coordinates", coordinates)
                    .toString();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Point findPointWithLargestFiniteInfluence_v2(Stream<String> coordinates) {
        Map<Point, Location> sourcePoints = coordinates.map(Location::parsePoint).collect(Collectors.toMap(l -> l.coordinates, l -> l));

        Rectangle surfaceCoveredBySource = getSurfaceCovered(sourcePoints.values());

        LOGGER.info("surface covered is: {}", surfaceCoveredBySource);

        LOGGER.info("points:\n{}", drawMap(surfaceCoveredBySource, sourcePoints.values()));

        MutableGraph<Location> graph = GraphBuilder.undirected().allowsSelfLoops(false)
                .expectedNodeCount(surfaceCoveredBySource.height * surfaceCoveredBySource.width).build();

        // populate graph
        Location[][] locationRaster = new Location[surfaceCoveredBySource.width + 1][surfaceCoveredBySource.height + 1];
        for (int x = surfaceCoveredBySource.x, maxX = surfaceCoveredBySource.width + surfaceCoveredBySource.x; x <= maxX; x++) {
            for (int y = surfaceCoveredBySource.y, maxY = surfaceCoveredBySource.height + surfaceCoveredBySource.y; y <= maxY; y++) {
                Point currentPoint = new Point(x, y);
                Location sourceLocation = sourcePoints.get(currentPoint);
                Location value = sourceLocation == null ? new Location(null, false, currentPoint, null) : sourceLocation;
                int absoluteX = x - surfaceCoveredBySource.x;
                int absoluteY = y - surfaceCoveredBySource.y;
                locationRaster[absoluteX][absoluteY] = value;
                graph.addNode(value);
            }
        }

        for (int x = 0, maxX = surfaceCoveredBySource.width; x <= maxX; x++) {
            for (int y = 0, maxY = surfaceCoveredBySource.height; y <= maxY; y++) {
                Location value = locationRaster[x][y];
                if (x > 0) {
                    graph.putEdge(value, locationRaster[x - 1][y]);
                }
                if (x < surfaceCoveredBySource.width-1) {
                    graph.putEdge(value, locationRaster[x + 1][y]);
                }
                if (y > 0) {
                    graph.putEdge(value, locationRaster[x][y - 1]);
                }
                if (y < surfaceCoveredBySource.height-1) {
                    graph.putEdge(value, locationRaster[x][y + 1]);
                }
            }
        }
        LOGGER.info("points:\n{}", drawMap(surfaceCoveredBySource, graph.nodes()));

        ImmutableGraph<Location> immutableGraph = ImmutableGraph.copyOf(graph);
        for (int i = 0; i < 1000; i++) {
            for (Location source : sourcePoints.values()) {
                source.expand(immutableGraph, source.id, 0, i);
            }
            LOGGER.info("points:\n{}", drawMap(surfaceCoveredBySource, graph.nodes()));
            if (immutableGraph.nodes().stream().allMatch(n -> n.id != null)) {
                LOGGER.info("All locations are tagged with an ID");
                break;
            }
        }

        Set<Integer> idOnBorders = findBorderIds(immutableGraph.nodes().stream().collect(Collectors.toMap(n -> n.coordinates, n -> n)), surfaceCoveredBySource);

        LOGGER.info("Found id on the border: {}", idOnBorders);
        TreeMap<Integer, Integer> idToScore = aggregateScores(immutableGraph.nodes());
        LOGGER.info("Scores is: {}", idToScore);

        Optional<Map.Entry<Integer, Integer>> max = idToScore.entrySet().stream().filter(e -> !idOnBorders.contains(e.getKey())).max(Comparator.comparing(Map.Entry::getValue));
        Map.Entry<Integer, Integer> integerIntegerEntry = max.get();
        return sourcePoints.values().stream().filter(l -> Objects.equals(l.id, integerIntegerEntry.getKey())).findAny().get().coordinates;
    }
}
