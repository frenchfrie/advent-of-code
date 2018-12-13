package aoc_2018;

import com.google.common.base.MoreObjects;

import org.slf4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class Day3 {

    private static final Logger LOGGER = getLogger(Day3.class);

    public static int countOverlapping(Dimension fabricSize, Stream<String> claims) {
        Raster raster = new Raster(fabricSize);
        LOGGER.info("Raster original:\n{}", raster.toString());

        java.util.List<Claim> claimsStore = new java.util.ArrayList<>();

        claims.map(Claim::parse) //
                .peek(claimsStore::add) //
                .forEach(c -> {
                    raster.incrementsValues(c.rectangle);
//                    LOGGER.info("Raster updated:\n{}", raster.toString());
                    LOGGER.info("Overlap surface is: {}", raster.overlapsSurface());
                });

        LOGGER.info("Raster at the end is:\n{}", raster);

        int overlap = raster.overlapsSurface();

        for (Claim claim : claimsStore) {
            int maxOverlapping = raster.incrementsValues(claim.rectangle);
            if (maxOverlapping == 2) {
                // we get our champion claim !
                LOGGER.info("claim with no overlap is: {}", claim);
            }
        }

        return overlap;
    }

    private static int surface(Rectangle rectangle) {
        return rectangle.height * rectangle.width;
    }

    private static class Raster {

        private int[][] content;

        public Raster(Dimension dimension) {
            content = new int[dimension.width][dimension.height];
            for (int[] booleans : content) {
                Arrays.fill(booleans, 0);
            }
        }

        public int incrementsValues(Rectangle surface) {
            int maxOverlap = 0;
            int upperLeftCornerX = surface.x;
            int upperRightCornerX = upperLeftCornerX + surface.width;
            int upperLeftCornerY = surface.y;
            int upperRightCornerY = upperLeftCornerY + surface.height;
            for (int i = upperLeftCornerX; i < upperRightCornerX; i++) {
                for (int j = upperLeftCornerY; j < upperRightCornerY; j++) {
                    int newValue = content[i][j] + 1;
                    content[i][j] = newValue;
                    maxOverlap = Math.max(newValue, maxOverlap);
                }
            }
            return maxOverlap;
        }

        public int overlapsSurface() {
            int overlap = 0;
            for (int i = 0; i < content.length; i++) {
                for (int j = 0; j < content[i].length; j++) {
                    if (content[i][j] > 1) {
                        overlap += 1;
                    }
                }
            }
            return overlap;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < content.length; i++) {
                for (int j = 0; j < content[i].length; j++) {
                    sb.append(content[i][j]);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    private static class Claim {

        private static final Pattern CLAIM_PATTERN = Pattern.compile("#(?<id>\\d+) @ (?<abs>\\d+),(?<ord>\\d+): (?<width>\\d+)x(?<height>\\d+)");

        private int id;

        private Rectangle rectangle;

        private static Claim parse(String claimAsString) {
            Matcher matcher = CLAIM_PATTERN.matcher(claimAsString);
            if (matcher.matches()) {
                Claim claim = new Claim();
                claim.id = Integer.parseInt(matcher.group("id"));
                int abs = Integer.parseInt(matcher.group("abs"));
                int ord = Integer.parseInt(matcher.group("ord"));
                int width = Integer.parseInt(matcher.group("width"));
                int height = Integer.parseInt(matcher.group("height"));
                claim.rectangle = new Rectangle(abs, ord, width, height);
                return claim;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .add("rectangle", rectangle)
                    .toString();
        }
    }
}
