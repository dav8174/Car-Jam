package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class JamConfig implements Configuration {
    /** empty cell value display */
    public final static String EMPTY_CELL = ".";

    /** square board of cars */
    private String[][] board;

    /** list of cars */
    private List<Car> cars;

    /**
     * Constructor
     *
     * @param filename the filename
     * @throws FileNotFoundException if file not found
     */
    public JamConfig(String filename) throws FileNotFoundException {
        Scanner f = new Scanner(new File(filename));
        int rows = f.nextInt();
        int cols = f.nextInt();
        this.board = new String[rows][cols];
        this.cars = new ArrayList<>(f.nextInt());

        // read in cars
        while(f.hasNext()){
            String name = f.next();
            int initRow = f.nextInt();
            int initCol = f.nextInt();
            int lastRow = f.nextInt();
            int lastCol = f.nextInt();
            Car car = new Car(name, initRow, initCol, lastRow, lastCol);
            cars.add(car);
        }

        // populate board with cars
        for(Car car: cars){
            for(int r = 0; r < board.length; ++r){
                for(int c = 0; c < board[r].length; ++c){
                    if(car.isThere(r, c))
                        board[r][c] = car.name;
                }
            }
        }

        // populate board with empty cells
        for(int r = 0; r < board.length; ++r){
            for(int c = 0; c < board.length; ++c){
                if(board[r][c] == null){
                    board[r][c] = EMPTY_CELL;
                }
            }
        }
        f.close();
    }

    /**
     * Copy constructor
     *
     * @param copy JamConfig instance
     */
    public JamConfig(JamConfig copy){
        this.board = new String[copy.board.length][copy.board[0].length];
        this.cars = new ArrayList<>();

        // copy board
        for(int r = 0; r < board.length; ++r){
            for(int c = 0; c < board[r].length; ++c){
                this.board[r][c] = copy.board[r][c];
            }
        }

        // copy cars
        for(Car car: copy.cars){
            Car tempCar = new Car(car.name, car.frontRow, car.frontCol, car.lastRow, car.lastCol);
            this.cars.add(tempCar);
        }
    }

    public JamConfig(String[][] board, List<Car> cars){
        this.board = new String[board.length][board[0].length];
        this.cars = new ArrayList<>();

        // copy board
        for(int r = 0; r < board.length; ++r){
            for(int c = 0; c < board[r].length; ++c){
                this.board[r][c] = board[r][c];
            }
        }

        // copy cars
        for(Car car: cars){
            Car tempCar = new Car(car.name, car.frontRow, car.frontCol, car.lastRow, car.lastCol);
            this.cars.add(tempCar);
        }
    }

    /**
     * Creates a Model from the Config
     * (used for hints in GUI and PTUI)
     *
     * @returns JamModel
     */
    public JamModel makeModel(){
        JamModel model = new JamModel(this.board,this.cars);
        return model;
    }

    /**
     * getSuccessors
     *
     * @returns Collection of Configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Set<Configuration> list = new HashSet<>();

        // go through each car
        for(Car car: cars){
            int i = cars.indexOf(car);

            // horizontal
            if(car.horizontal){

                // move car left
                if(car.frontCol != 0){
                    if (board[car.frontRow][car.frontCol - 1].equals(EMPTY_CELL)) {
                        JamConfig temp = new JamConfig(this);
                        Car tempCar = temp.cars.get(i);
                        tempCar.move(true, false, false, false);
                        temp.board[car.frontRow][car.frontCol - 1] = car.name;
                        temp.board[car.frontRow][car.lastCol] = EMPTY_CELL;
                        list.add(temp);
                    }
                }

                // move car right
                if(car.lastCol != board[0].length-1){
                    if (board[car.frontRow][car.lastCol + 1].equals(EMPTY_CELL)) {
                        JamConfig temp = new JamConfig(this);
                        Car tempCar = temp.cars.get(i);
                        tempCar.move(false, true, false, false);
                        temp.board[car.frontRow][car.lastCol + 1] = car.name;
                        temp.board[car.frontRow][car.frontCol] = EMPTY_CELL;
                        list.add(temp);
                    }
                }
            }

            // vertical
            else{

                // move car up
                if(car.frontRow != 0){
                    if (board[car.frontRow - 1][car.frontCol].equals(EMPTY_CELL)) {
                        JamConfig temp = new JamConfig(this);
                        Car tempCar = temp.cars.get(i);
                        tempCar.move(false, false, true, false);
                        temp.board[car.frontRow - 1][car.frontCol] = car.name;
                        temp.board[car.lastRow][car.frontCol] = EMPTY_CELL;
                        list.add(temp);
                    }
                }

                // move car down
                if(car.lastRow != board.length-1){
                    if (board[car.lastRow + 1][car.frontCol].equals(EMPTY_CELL)) {
                        JamConfig temp = new JamConfig(this);
                        Car tempCar = temp.cars.get(i);
                        tempCar.move(false, false, false, true);
                        temp.board[car.lastRow + 1][car.frontCol] = car.name;
                        temp.board[car.frontRow][car.frontCol] = EMPTY_CELL;
                        list.add(temp);
                    }
                }
            }
        }
        return list;
    }

    /**
     * isSolution() method
     *
     * @return true if current configuration is goal; false otherwise
     */
    @Override
    public boolean isSolution() {
        //checks for an "X" in the last column
        for(int r = 0; r < board.length; r++)
            if(board[r][board[0].length-1].equals("X"))
                return true;
        return false;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof JamConfig){
            JamConfig jam = (JamConfig) other;
            for(int r = 0; r < board.length; ++r) {
                for (int c = 0; c < board[r].length; ++c) {
                    if(!this.board[r][c].equals(jam.board[r][c]))
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * toString() method
     *
     * @return String representing configuration board.
     */
    public String toString(){
        StringBuilder builder = new StringBuilder();

        for(int r = 0; r < board.length; ++r){
            for(int c = 0; c < board[r].length; ++c){
                builder.append(board[r][c] + " ");
                }
            builder.append("\n");
        }
        return builder.toString();
    }
}

