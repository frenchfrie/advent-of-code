package aoc_2018;

import com.google.common.base.MoreObjects;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.slf4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class Day7 {

    private static final Logger LOGGER = getLogger(Day7.class);

    static List<String> alphabet = Arrays.asList("A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z");

    @SuppressWarnings("UnstableApiUsage")
    public Result solve(Stream<String> instructions, int taskBasePlusTime, int workerNumber) {
        MutableGraph<Task> graph = GraphBuilder.directed().build();
        Map<String, Task> taskLib = new HashMap<>();
        instructions.forEach(
                i -> {
                    Task parentStep = taskLib.computeIfAbsent(i.substring(5, 6), id -> new Task(id,taskBasePlusTime));
                    Task successorStep = taskLib.computeIfAbsent(i.substring(36, 37), id -> new Task(id, taskBasePlusTime));
                    graph.putEdge(parentStep, successorStep);
                }
        );
        LOGGER.info("graph created: {}", graph);

        TimeHolder timeHolder = new TimeHolder();
        List<Task> stepsExecuted = new ArrayList<>();
        Workers workers = new Workers(timeHolder, workerNumber, stepsExecuted::add);

        do {
            Set<Task> nodes = graph.nodes();
            nodes.stream()
                    .filter(n -> !n.isRunning()) //no need for task already being processed
                    .filter(n -> !n.isQueued()) //no need for task already being processed
                    .filter(n -> !n.isDone()) // ignore tasks that already have been executed
                    .filter(n -> graph.predecessors(n).stream().allMatch(Task::isDone)) // keep only tasks with predecessors done
                    .sorted()
                    .forEachOrdered(workers::submit);
            timeHolder.advance();
        } while (graph.nodes().stream().anyMatch(t -> !t.isDone()));

        return new Result(stepsExecuted.stream().map(t -> t.id).collect(Collectors.toList()), timeHolder.time);
    }

    public static class Result {

        public final String steps;
        public final int executionTime;

        public Result(List<String> steps, int executionTime) {
            this.steps = String.join("", steps);
            this.executionTime = executionTime;
        }
    }

    private class Workers implements PropertyChangeListener {

        Queue<Task> queue = new ArrayDeque<>();
        List<Worker> workers;
        Consumer<Task> taskDoneListener;

        public Workers(TimeHolder timeHolder, int number, Consumer<Task> taskDoneListener) {
            workers = new ArrayList<>(number);
            for (int i = 0; i < number; i++) {
                Worker worker = new Worker(Integer.toString(i), timeHolder);
                worker.suscribe(this);
                workers.add(worker);
            }
            this.taskDoneListener = taskDoneListener;
        }

        public void submit(Task task) {
            LOGGER.info("Submitting task {} to work force", task);
            Optional<Worker> freeWorker = workers.stream().filter(worker1 -> !worker1.isWorking()).findAny();
            if (freeWorker.isPresent()) {
                freeWorker.get().submit(task);
            } else {
                task.status = Task.Status.QUEUED;
                queue.add(task);
            }
        }

        public boolean isWorking() {
            return workers.stream().anyMatch(Worker::isWorking);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() instanceof Worker) {
                Worker source = (Worker) evt.getSource();
                if (evt.getNewValue() == null) {
                    Task poll = queue.poll();
                    if (poll != null) {
                        poll.status = Task.Status.READY;
                        source.submit(poll);
                    }
                    Task oldValue = (Task) evt.getOldValue();
                    if (oldValue != null && oldValue.isDone()) {
                        LOGGER.info("Task {} submitted as DONE", oldValue.id);
                        taskDoneListener.accept(oldValue);
                    }
                }
            }
        }
    }

    /**
     * Fabulous working elf.
     */
    private static class Worker implements PropertyChangeListener {
        private final TimeHolder timeHolder;
        private final String id;
        private Task task;
        private Integer startTime;
        private PropertyChangeSupport support = new PropertyChangeSupport(this);

        public Worker(String id, TimeHolder timeHolder) {
            this.timeHolder = timeHolder;
            this.id = id;
            timeHolder.suscribe(this);
        }

        public Task getTask() {
            return task;
        }

        private void setTask(Task task) {
            Task old = this.task;
            this.task = task;
            support.firePropertyChange("task", old, task);
        }

        public boolean submit(Task task) {
            if (task.status != Task.Status.READY) {
                throw new IllegalStateException();
            } else if (isWorking()) {
                // already working
                return false;
            } else {
                LOGGER.info("{} - Task {} submitted to elf {}", timeHolder.time, task.id, id);
                task.status = Task.Status.IN_PROGRESS;
                this.startTime = timeHolder.time;
                this.setTask(task);
                return true;
            }
        }

        public boolean isWorking() {
            return getTask() != null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (isWorking()) {
                int currentTime = (int) evt.getNewValue();
                if (currentTime >= startTime + getTask().duration) {
                    // task is finished
                    stopTask();
                }
            }
        }

        private void stopTask() {
            getTask().status = Task.Status.DONE;
            this.startTime = null;
            LOGGER.info("{} - Task {} finished by elf {}", timeHolder.time, task.id, id);
            this.setTask(null);
        }

        public void suscribe(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }

        public void unSuscribe(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }
    }

    private static class Task implements Comparable<Task> {
        private final String id;
        private Status status;
        private int duration;

        private Task(String id, int taskBasePlusTime) {
            this.id = id;
            this.status = Status.READY;
            duration = taskBasePlusTime + 1 + alphabet.indexOf(id);
        }

        @Override
        public int compareTo(Task o) {
            return id.compareTo(o.id);
        }

        public boolean isReady() {
            return status == Status.READY;
        }

        public boolean isQueued() {
            return status == Status.QUEUED;
        }

        public boolean isRunning() {
            return status == Status.IN_PROGRESS;
        }

        public boolean isDone() {
            return status == Status.DONE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Task task = (Task) o;
            return id.equals(task.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .add("status", status)
                    .add("duration", duration)
                    .toString();
        }

        public enum Status {
            READY,
            QUEUED,
            IN_PROGRESS,
            DONE
        }
    }

    private class TimeHolder {
        private int time = 0;
        private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        public void suscribe(PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        public void unSuscribe(PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        public void advance() {
            int oldTime = time;
            time += 1;
//            LOGGER.info("Ding! Time is now {}", time);
            if (time > 10_000) {
                throw new Error("Time collapse on itself");
            }
            propertyChangeSupport.firePropertyChange("time", oldTime, time);
        }
    }

}
