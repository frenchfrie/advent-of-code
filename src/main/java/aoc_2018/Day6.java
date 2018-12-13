package aoc_2018;

import com.google.inject.grapher.graphviz.CompassPoint;

import org.slf4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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

        AtomicInteger distance = new AtomicInteger(1);
        boolean passed = false;
        do {
            sourcePoints.forEach(l -> l.expand(locationsLibrary, distance.get()));
            LOGGER.info("points:\n{}", drawMap(surfaceCoveredBySource, locationsLibrary.values()));
            distance.incrementAndGet();
            if (!passed && distance.get() > 200) {
                LOGGER.error("We passed 200!");
                passed = true;
            }
        } while (distance.get() < 200 || !isCompletelyFilled(surfaceCoveredBySource, locationsLibrary));

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

    private static void createGraph(Rectangle surface, Map<Point, Location> sources) {
        Location[][] locTable = new Location[surface.width + 1][surface.height + 1];
        for (int x = surface.x, maxX = surface.width + surface.x; x <= maxX; x++) {
            for (int y = surface.y, maxY = surface.height + surface.y; y <= maxY; y++) {
                Point key = new Point(x, y);
                Location location = sources.get(key);
                locTable[x - surface.x][y -surface.y] = 
                
                
            }
        }
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

                String render = location.id >= 0 ? Integer.toString(location.id) : ".";
                raster[location.coordinates.x][location.coordinates.y] = render;
            }
        }
        StringBuilder drawn = new StringBuilder();
        for (int y = 0; y < surfaceHeight; y++) {
            for (int x = 0; x < surfaceWidth; x++) {
                drawn.append(raster[x][y]);
            }
            drawn.append('\n');
        }
        return drawn.toString();
    }

    private static class Location {

        private static int nextAvailableSourceId = 0;

        private int id;
        private final boolean source;
        private final Point coordinates;
        private final int distance;

        private Location northNeighbor;
        private Location eastNeighbor;
        private Location southNeighbor;
        private Location westNeighbor;

        public Location(int id, boolean source, Point coordinates, int distance) {
            this.id = id;
            this.source = source;
            this.coordinates = coordinates;
            this.distance = distance;
        }

        private static Location createSource(Point coordinates) {
            return new Location(nextAvailableSourceId++, true, coordinates, 0);
        }

        private Location createNode(CompassPoint direction) {
            Point coordinates = new Point();
            Location location = new Location(this.id, false, coordinates, distance + 1);
            switch (direction) {
                case NORTH:
                    coordinates.setLocation(this.coordinates.x, this.coordinates.y - 1);
                    break;
                case EAST:
                    coordinates.setLocation(this.coordinates.x + 1, this.coordinates.y);
                    break;
                case SOUTH:
                    coordinates.setLocation(this.coordinates.x, this.coordinates.y + 1);
                    break;
                case WEST:
                    coordinates.setLocation(this.coordinates.x - 1, this.coordinates.y);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return location;
        }

        private static Location parsePoint(String input) {
            String[] split = input.split(",");
            Point point = new Point(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
            return createSource(point);
        }

        private void expand(Map<Point, Location> locationsLibrary, int distance) {
            if (distance == 0) return;
            if (northNeighbor == null || eastNeighbor == null || southNeighbor == null || westNeighbor == null) {
                if (northNeighbor == null) {
                    Location northNode = createNode(CompassPoint.NORTH);
                    Location value = locationsLibrary.putIfAbsent(northNode.coordinates, northNode);
                    if (value == null) {
                        this.northNeighbor = northNode;
                    } else {
                        // value already associated !
                        if (value.id != northNode.id && value.distance == northNode.distance && !value.source) {
                            value.id = -1;
                        }
                        this.northNeighbor = value;
                    }
                }
                if (eastNeighbor == null) {
                    Location eastNode = createNode(CompassPoint.EAST);
                    Location value = locationsLibrary.putIfAbsent(eastNode.coordinates, eastNode);
                    if (value == null) {
                        this.eastNeighbor = eastNode;
                    } else {
                        // value already associated !
                        if (value.id != eastNode.id && value.distance == eastNode.distance && !value.source) {
                            value.id = -1;
                        }
                        this.eastNeighbor = value;
                    }
                }
                if (southNeighbor == null) {
                    Location southNode = createNode(CompassPoint.SOUTH);
                    Location value = locationsLibrary.putIfAbsent(southNode.coordinates, southNode);
                    if (value == null) {
                        this.southNeighbor = southNode;
                    } else {
                        // value already associated !
                        if (value.id != southNode.id && value.distance == southNode.distance && !value.source) {
                            value.id = -1;
                        }
                        this.southNeighbor = value;
                    }
                }
                if (westNeighbor == null) {
                    Location westNode = createNode(CompassPoint.WEST);
                    Location value = locationsLibrary.putIfAbsent(westNode.coordinates, westNode);
                    if (value == null) {
                        this.westNeighbor = westNode;
                    } else {
                        // value already associated !
                        if (value.id != westNode.id && value.distance == westNode.distance && !value.source) {
                            value.id = -1;
                        }
                        this.westNeighbor = value;
                    }
                }
            } else {
                northNeighbor.expand(locationsLibrary, distance - 1);
                eastNeighbor.expand(locationsLibrary, distance - 1);
                southNeighbor.expand(locationsLibrary, distance - 1);
                westNeighbor.expand(locationsLibrary, distance - 1);
            }
        }

    }

}
