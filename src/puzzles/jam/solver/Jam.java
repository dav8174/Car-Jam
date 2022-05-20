package puzzles.jam.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.JamConfig;

import java.io.FileNotFoundException;
import java.util.List;

public class Jam {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            System.out.println("Usage: java Jam filename");
        } else {
            String filename = args[0];

            System.out.println("File: " + filename);

            JamConfig jam = new JamConfig(filename);
            System.out.print(jam);

            Solver solver = new Solver();
            List<Configuration> steps = (List<Configuration>) solver.solve(jam);

            System.out.println("Total Configs: " + solver.totalCounter);
            System.out.println("Unique Configs: " + solver.uniqueCounter);

            if (steps.isEmpty())
                System.out.println("No Solution");
            for (int i = 0; i < steps.size(); ++i)
                System.out.println("Step " + i + ":\n" + steps.get(i).toString());


        }
    }
}