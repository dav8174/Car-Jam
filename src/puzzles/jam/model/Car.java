package puzzles.jam.model;

public class Car {
    /** name of the car */
    public String name;

    /** true if car is horizontal; false if vertical*/
    public boolean horizontal;

    /** length of car; either 2 or 3 */
    public int length;

    /** front row of car; if vertical, top-most row */
    public int frontRow;

    /** front column of car; if horizontal, left-most column */
    public int frontCol;

    /** back row of car; if vertical, bottom row */
    public int lastRow;

    /** back column of car; if horizontal, right-most column */
    public int lastCol;

    /**
     * Constructor
     * represents one car
     *
     * @param name name of car
     * @param initRow initRow
     * @param initCol initCol
     * @param lastRow lastRow
     * @param lastCol lastCol
     */
    public Car(String name, int initRow, int initCol, int lastRow, int lastCol) {
        this.name = name;
        if(initRow == lastRow){
            horizontal = true;
            length = 1 + lastCol - initCol;
        }
        else if(initCol == lastCol){
            horizontal = false;
            length = 1 + lastRow - initRow;
        }
        this.frontRow = initRow;
        this.frontCol = initCol;
        this.lastRow = lastRow;
        this.lastCol = lastCol;
    }

    /**
     * Checks if this car is at row, col
     *
     * @param row row being checked
     * @param col column being checked
     * @return true if car is there
     */
    public boolean isThere(int row, int col){
        if(frontRow == row && frontCol == col)
            return true;
        if(lastRow == row && lastCol == col)
            return true;
        if(length > 2){
            if(horizontal) {
                if (lastRow == row && lastCol - 1 == col)
                    return true;
            }
            else {
                if (lastRow - 1 == row && lastCol == col)
                    return true;
            }
        }
        return false;
    }

    /**
     * Move car in model
     *
     * @param left true if moving left
     * @param right true if moving right
     * @param up true if moving up
     * @param down true if moving down
     */
    public void move(boolean left, boolean right, boolean up, boolean down){
        if(left){
            frontCol--;
            lastCol--;
        }
        else if(right){
            frontCol++;
            lastCol++;
        }
        else if(up){
            frontRow--;
            lastRow--;
        }
        else if(down){
            frontRow++;
            lastRow++;
        }
    }
}
