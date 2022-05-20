package puzzles.jam.model;

import puzzles.common.Observer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class JamModel {
    /** the collection of observers of this model */
    private final List<Observer<JamModel>> observers = new LinkedList<>();

    /** empty cell value display */
    public final static String EMPTY_CELL = ".";

    /** square board of cars */
    public String[][] board;

    /** list of cars */
    public List<Car> cars;

    /**
     * Constructor
     *
     * @param filename the filename
     * @throws FileNotFoundException if file not found
     */
    public JamModel(String filename) throws FileNotFoundException {
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
     * Constructor
     * Used for making a model from a config in makeModel
     *
     * @param board the board
     * @param cars list of Cars
     * @throws FileNotFoundException if file not found
     */
    public JamModel(String[][] board, List<Car> cars){
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
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel> observer) {
        this.observers.add(observer);
    }

    /**
     * Creates a Config from the Model
     *
     * @returns JamConfig
     */
    public JamConfig makeConfig(){
        JamConfig config = new JamConfig(this.board, this.cars);
        return config;
    }

    /**
     * Checks if board is empty at row, col
     *
     * @param row row being checked
     * @param col col being checked
     * @returns true if empty
     */
    public boolean isEmpty(int row, int col){
        if(this.board[row][col].equals(EMPTY_CELL)){
            return true;
        }
        return false;
    }

    /**
     * Returns Car at row, col
     *
     * @param row row being checked
     * @param col col being checked
     * @returns Car
     */
    public Car getCar(int row, int col){
        for(Car car: cars){
            if(this.board[row][col].equals(car.name)){
                return car;
            }
        }
        return null;
    }

    /**
     * Checks if move is valid
     *
     * @param car car being moved
     * @param row row being checked
     * @param col col being checked
     * @returns true if valid move
     */
    public boolean isValidMove(Car car, int row, int col){
        // if car is horizontal
        if(car.horizontal){
            // if not in same row
            if(car.frontRow != row)
                return false;
            // if moving left
            if(col < car.frontCol){
                // checks every spot between selected spot and car
                int dist = car.frontCol - col;
                for(int i = 0; i < dist; i++)
                    if(!this.board[row][col + i].equals(EMPTY_CELL))
                        return false;
            }
            // if moving right
            else if(col > car.lastCol){
                int dist = col - car.lastCol;
                for(int i = 0; i < dist; i++)
                    if(!this.board[row][col - i].equals(EMPTY_CELL))
                        return false;
            }
        }

        // if car is vertical
        else{
            // if car is not in same column
            if(car.frontCol != col)
                return false;
            // if moving up
            if(row < car.frontRow){
                int dist = car.frontRow - row;
                for(int i = 0; i < dist; i++)
                    if(!this.board[row + i][col].equals(EMPTY_CELL))
                        return false;
            }
            // if moving down
            else if(row > car.lastRow){
                int dist = row - car.lastRow;
                for(int i = 0; i < dist; i++)
                    if(!this.board[row - i][col].equals(EMPTY_CELL))
                        return false;
            }
        }
        return true;
    }

    /**
     * Makes move
     *
     * @param car car being moved
     * @param row row being moved to
     * @param col col being moved to
     */
    public void makeMove(Car car, int row, int col){
        // if car is horizontal
        if(car.horizontal){
            // if moving left
            if(col < car.frontCol){
                // erases car from initial spot
                this.board[car.frontRow][car.frontCol] = EMPTY_CELL;
                this.board[car.lastRow][car.lastCol] = EMPTY_CELL;
                if (car.length == 3)
                    this.board[car.lastRow][car.lastCol - 1] = EMPTY_CELL;

                // moves car
                this.board[row][col] = car.name;
                int dist = car.frontCol - col;
                for(int i = 0; i < dist; i++)
                    car.move(true, false, false, false);
                this.board[row][col+1] = car.name;
                if(car.length == 3){
                    this.board[row][col+2] = car.name;
                }
            }
            // if moving right
            else if(col > car.lastCol){
                this.board[car.frontRow][car.frontCol] = EMPTY_CELL;
                this.board[car.lastRow][car.lastCol] = EMPTY_CELL;
                if (car.length == 3)
                    this.board[car.lastRow][car.lastCol - 1] = EMPTY_CELL;

                this.board[row][col] = car.name;
                int dist = col - car.lastCol;
                for(int i = 0; i < dist; i++)
                    car.move(false, true, false, false);
                this.board[row][col-1] = car.name;
                if(car.length == 3){
                    this.board[row][col-2] = car.name;
                }
            }
            // if selected car is already at row, col
            else
                return ;
        }
        // if car is vertical
        else{
            // if moving up
            if(row < car.frontRow){
                this.board[car.frontRow][car.frontCol] = EMPTY_CELL;
                this.board[car.lastRow][car.lastCol] = EMPTY_CELL;
                if (car.length == 3)
                    this.board[car.lastRow - 1][car.lastCol] = EMPTY_CELL;

                this.board[row][col] = car.name;
                int dist = car.frontRow - row;
                for(int i = 0; i < dist; i++)
                    car.move(false, false, true, false);
                this.board[row+1][col] = car.name;
                if(car.length == 3){
                    this.board[row+2][col] = car.name;
                }
            }
            // if moving down
            else if(row > car.lastRow){
                this.board[car.frontRow][car.frontCol] = EMPTY_CELL;
                this.board[car.lastRow][car.lastCol] = EMPTY_CELL;
                if (car.length == 3)
                    this.board[car.lastRow - 1][car.lastCol] = EMPTY_CELL;

                this.board[row][col] = car.name;
                int dist = row - car.lastRow;
                for(int i = 0; i < dist; i++)
                    car.move(false, false, false, true);
                this.board[row-1][col] = car.name;
                if(car.length == 3){
                    this.board[row-2][col] = car.name;
                }
            }
            // if selected car is already at row, col
            else
                return ;
        }
        alertObservers();
    }

    /**
     * Checks if game is won
     *
     * @returns true if game is won
     */
    public boolean isWon(){
        // checks last column for "X"
        for(int r = 0; r < board.length; r++)
            if(board[r][board[0].length-1].equals("X"))
                return true;
        return false;
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers() {
        for (var observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Returns a string representation of the board, suitable for printing out.
     * jam-4.txt board would be:<br>
     * <br><tt>
     *   0 1 2 3 4 5 <br>
     *   ------------<br>
     * 0|. . O . A A <br>
     * 1|. . O . . . <br>
     * 2|X X O . . . <br>
     * 3|P P P . . Q <br>
     * 4|. . . . . Q <br>
     * 5|. . . . . Q <br>
     *</tt>
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("  ");
        for(int c = 0; c < board[0].length; c++){
            builder.append(c + " ");
        }
        builder.append('\n' + "  ");

        for(int c = 0; c < board[0].length; c++){
            builder.append("--");
        }
        builder.append('\n');

        for(int r = 0; r < board.length; r++){
            builder.append(r + "|");
            for(int c = 0; c < board[r].length; c++){
                builder.append(board[r][c] + " ");
            }
            builder.append("\n");
        }
        builder.append("\n");

        return builder.toString();
    }
}
