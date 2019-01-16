package aoc_2018;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class Day8 {

    private static final Logger LOGGER = getLogger(Day8.class);

    public Result solve(Stream<String> instructions) {
        Node root = Node.parseNode(instructions.map(Integer::parseInt).iterator());
        LOGGER.info("tree is:\n" + root.asStringTree());
        return new Result(root.addUpMetadata(), root.getValue());
    }

    public static class Node {
        static int nextId = 0;
        final int id;
        List<Node> children;
        List<Integer> metadata;

        public Node() { id = nextId++; }

        public static Node parseNode(Iterator<Integer> data) {
            Node node = new Node();
            Integer quantityOfChildNodes = data.next();
            Integer quantityOfMetadataEntries = data.next();
            node.children = new ArrayList<>();
            for (int i = 0; i < quantityOfChildNodes; i++) {
                node.children.add(Node.parseNode(data));
            }
            node.metadata = new ArrayList<>();
            for (int i = 0; i < quantityOfMetadataEntries; i++) {
                node.metadata.add(data.next());
            }
            return node;
        }

        public void traverse(Consumer<Node> traverser) {
            traverser.accept(this);
            for (Node child : children) {
                child.traverse(traverser);
            }
        }

        /** Solution for puzzle part 1 */
        public int addUpMetadata() {
            AtomicInteger summ = new AtomicInteger(0);
            this.traverse(n -> n.metadata.forEach(summ::addAndGet));
            return summ.get();
        }

        /** puzzle part 2 solution */
        public int getValue() {
            int value;
            if (children.isEmpty()) {
                value = metadata.stream().mapToInt(i -> i).sum();
            } else {
                int maxIndex = children.size();
                value = metadata.stream()
                        .mapToInt(i -> i-1) // convert 1-based indexing to 0-based
                        .filter(i -> i < maxIndex) // filter out indexes that refer to no child
                        .mapToObj(i -> children.get(i)) // get corresponding child node
                        .mapToInt(Node::getValue) // get child value
                        .sum(); // sum 'em up
            }
            return value;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("children", children)
                    .add("metadata", metadata)
                    .toString();
        }

        public String asStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("node: ").append(id).append(": ").append(metadata);
            int lastIndex = children.size() - 1;
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String prefix;
                if (i < lastIndex) {
                    sb.append("\n+-");
                    prefix = "\n| ";
                } else {
                    sb.append("\n\\-");
                    prefix = "\n  ";
                }
                sb.append(StringUtils.replace(child.asStringTree(), "\n", prefix));
            }
            return sb.toString();
        }
    }

    public static class Result {
        public final int sumOfMetadataEntries;
        public final int sumOfNodesValues;

        public Result(int sumOfMetadataEntries, int sumOfNodesValues) {
            this.sumOfMetadataEntries = sumOfMetadataEntries;
            this.sumOfNodesValues = sumOfNodesValues;
        }
    }

}
