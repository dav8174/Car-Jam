package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.Car;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class JamPTUI implements Observer<JamModel> {
    /** jam model */
    private JamModel jam;

    /** current file */
    private String file;

    /** true if car is current selected */
    private boolean selected;

    /** current car selected */
    private Car selectedCar;

    /** current row selected */
    private int selectedRow;

    /** current column selected */
    private int selectedCol;

    /**
     * Constructor
     *
     * @param initFile initial file
     * @throws FileNotFoundException if file not found
     */
    public JamPTUI(String initFile) throws FileNotFoundException {
        this.file = initFile;
        this.jam = new JamModel(this.file);
        this.selected = false;
        this.selectedRow = 0;
        this.selectedCol = 0;
        initializeView();
    }

    /**
     * Adds PTUI as a observer of the model
     */
    private void initializeView(){
        this.jam.addObserver(this);
    }

    /**
     * updates the PTUI
     *
     * @param jamModel the jam model
     */
    @Override
    public void update(JamModel jamModel) {
        System.out.print(this.jam);
    }

    /**
     * The run loop prompts for user input and makes calls into the Model.
     */
    public void run(){
        System.out.println("Loaded: " + this.file);
        update(this.jam);
        System.out.println("h(int)              -- hint next move");
        System.out.println("l(oad) filename     -- load new puzzle file");
        System.out.println("s(elect) r c        -- select cell at r, c");
        System.out.println("q(uit)              -- quit the game");
        System.out.println("r(eset)             -- reset the current game");
        try (Scanner in = new Scanner( System.in )){
            // loops until quit
            while(true){
                System.out.print("> ");
                String arg = in.next();

                // quits PTUI
                if(arg.equals("q"))
                    return ;

                // load new file
                else if(arg.equals("l")){
                    String newFile = in.next();
                    try(Scanner f = new Scanner(new File(newFile))){
                        this.file = newFile;
                        this.jam = new JamModel(this.file);
                    }
                    catch(FileNotFoundException f){
                        System.out.println("Failed to load: " + newFile);
                    }
                }

                // resets model
                else if(arg.equals("r")){
                    this.jam = new JamModel(this.file);
                }

                // if game is won
                else if(this.jam.isWon()){
                    if(arg.equals("h") || arg.equals("s"))
                        System.out.println("Already solved!");
                    else{
                        System.out.println("h(int)              -- hint next move");
                        System.out.println("l(oad) filename     -- load new puzzle file");
                        System.out.println("s(elect) r c        -- select cell at r, c");
                        System.out.println("q(uit)              -- quit the game");
                        System.out.println("r(eset)             -- reset the current game\n");
                    }
                }

                // gets next step
                else if(arg.equals("h")){
                    System.out.println("Next Step!");

                    // turns model into config
                    JamConfig config = this.jam.makeConfig();
                    Solver solver = new Solver();
                    List<Configuration> steps = (List<Configuration>) solver.solve(config);

                    // finds next step in puzzle
                    JamConfig nextConfig = (JamConfig) steps.get(1);

                    // turns config into model
                    this.jam = nextConfig.makeModel();
                }

                // select car or make move
                else if(arg.equals("s")){
                    int row = in.nextInt();
                    int col = in.nextInt();

                    // if car is already selected
                    if(this.selected){
                        if(this.jam.isValidMove(selectedCar, row, col)){
                            this.jam.makeMove(selectedCar, row, col);
                        }
                        else{
                            System.out.println("Cannot move from (" + this.selectedRow + ", " + this.selectedCol + ") to (" + row + ", " + col + ")");
                        }
                        this.selected = false;
                    }

                    // if car is not selected
                    else{
                        if (this.jam.isEmpty(row, col)){
                            System.out.println("No car at (" + row + ", " + col + ")");
                        }
                        // selects car
                        else {
                            this.selectedCar = this.jam.getCar(row, col);
                            this.selectedRow = row;
                            this.selectedCol = col;
                            System.out.println("Selected (" + row + ", " + col + ")");
                            this.selected = true;
                        }
                    }
                }

                // if input not recognized
                else{
                    System.out.println("h(int)              -- hint next move");
                    System.out.println("l(oad) filename     -- load new puzzle file");
                    System.out.println("s(elect) r c        -- select cell at r, c");
                    System.out.println("q(uit)              -- quit the game");
                    System.out.println("r(eset)             -- reset the current game\n");
                }

                // update model
                update(this.jam);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * The main routine.
     *
     * @param args command line arguments (unused)
     * @throws FileNotFoundException if file not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        }
        else{
            String filename = args[0];
            JamPTUI ptui = new JamPTUI(filename);
            ptui.run();
        }
    }
}
