package puzzles.common.solver;

import java.util.*;

public class Solver {
    // total configs
    public int totalCounter;

    // unique configs
    public int uniqueCounter;

    public Solver(){
        totalCounter = 1;
        uniqueCounter = 0;
    }

    // find shortest path
    public Collection<Configuration> solve(Configuration config) {
        //start node
        Configuration startConfig = config;

        // end node
        Configuration finalConfig = null;

        // queue of configs to process
        List<Configuration> queue = new LinkedList<>();
        queue.add(config);

        //map of configs
        Map<Configuration, Configuration> predecessors = new HashMap<>();
        predecessors.put(config, null);

        // process queue
        while (!queue.isEmpty()) {
            Configuration current = queue.remove(0);

            // check if solution
            if (current.isSolution()) {
                finalConfig = current;
                break;
            }

            // go through neighbors
            for (Configuration child : current.getNeighbors()) {
                this.totalCounter++;
                if (!predecessors.containsKey(child)) {
                    predecessors.put(child, current);
                    queue.add(child);
                }
            }
        }

        // path from start to end node
        List<Configuration> path = new LinkedList<>();

        // construct path
        if (predecessors.containsKey(finalConfig)) {
            Configuration currConfig = finalConfig;
            while (!currConfig.equals(startConfig)) {
                path.add(0, currConfig);
                currConfig = predecessors.get(currConfig);
            }
            path.add(0, startConfig);
        }

        uniqueCounter = predecessors.size();
        return path;
    }
}