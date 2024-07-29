// Imports
import java.util.Random;

// NOTE: IMPORTED AND USED ONLY FOR USE IN REMOVING PLACED FLAGS, NOT EVER USED FOR EXPLICIT PROJECT PROBLEMS
import java.util.HashMap;


/* Minefield: Serves as a general game engine for a game of Minefield.

Responsible for initializing the game, processing user guesses, checking game completion status,
producing a representation of the field, etc.

 */
public class Minefield {

    // Globals
    private int numFlags;   // Number of flags user still has to place
    private final int numRows;    // Number of rows on the game field
    private final int numCols;    // Number of columns on the game field
    private int revealCtr;  // Number of cells which have been revealed

    private final Cell[][] field;     // 2-D Array representation of the game field

    private boolean isGameOver;     // Boolean tracking whether game should continue or not

    private final Stack1Gen<int[]> mineLocs;  // Stack of mine locations on the board, used to assign statuses to each cell

    // NOTE: HashMap IMPORTED AND USED ONLY FOR USE IN REMOVING PLACED FLAGS, NOT EVER USED FOR EXPLICIT PROJECT PROBLEMS
    private final HashMap<String, Cell> oldFlags = new HashMap<String, Cell>();   // Tracks values replaced by flags so they can be removed


    // Constructor
    // All parameters determined by user difficulty input
    public Minefield(int rows, int columns, int flags) {

        revealCtr = 0;  // Field has nothing revealed to begin

        field = new Cell[rows][columns];  // Size of game field determined by difficulty selected

        // Integers used to track size of board and remaining flags
        numRows = rows;
        numCols = columns;
        numFlags = flags;

        isGameOver = false;

        // mineLocs tracks location of mines on field as an integer array
        mineLocs = new Stack1Gen<int[]>();
    }


    // Getters

    // Returns number of flags user has yet to place
    public int getNumFlags(){return numFlags;}

    // Return number of rows
    public int getNumRows(){return numRows;}

    // Return number of columns
    public int getNumCols(){return numCols;}

    // Returns value at a given set of coordinates on the game field
    public Cell getCell(int x, int y){return field[y][x];}


    // By default, all cells on field are set to status = 0 and revealed = false
    public void populateZeroes() {
        // Iterating through each cell on board
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                field[r][c] = new Cell(false, "0");
            }
        }
    }

    // Helper function to iterate the status of a cell so long as it is not a flag or mine
    public void iterateCell(int x, int y) {
        if (!(field[y][x].getStatus().equals("M") || field[y][x].getStatus().equals("F"))) {

            // Converting status from String value to integer so that it can be incremented
            String value = field[y][x].getStatus();
            int newValue = Integer.parseInt(value) + 1;
            value = Integer.toString(newValue);  // Changes status back to String

            field[y][x].setStatus(value);  // Reassigns cell's status to new String
        }
    }

    // Helper function that finds every in bounds bordering neighbor of a given cell
    public void checkBorders(int x, int y) {

        // Case of top left corner
        if (x == 0 && y == 0) {
            iterateCell(x + 1, y);
            iterateCell(x, y + 1);
            iterateCell(x + 1, y + 1);

        // Case of top left corner
        } else if (x == 0 && y == numRows - 1) {
            iterateCell(x + 1, y);
            iterateCell(x, y - 1);
            iterateCell(x + 1, y - 1);

        // Case of top left corner
        } else if (x == numCols - 1 && y == 0) {
            iterateCell(x - 1, y);
            iterateCell(x, y + 1);
            iterateCell(x - 1, y + 1);

        // Case of top left corner
        } else if (x == numCols - 1 && y == numRows - 1) {
            iterateCell(x - 1, y);
            iterateCell(x, y - 1);
            iterateCell(x - 1, y - 1);

        // Case of cell on top border
        } else if (x == 0) {
            iterateCell(x + 1, y - 1);
            iterateCell(x + 1, y);
            iterateCell(x + 1, y + 1);
            iterateCell(x, y + 1);
            iterateCell(x, y - 1);

        // Case of cell on bottom border
        } else if (x == numCols - 1) {
            iterateCell(x - 1, y + 1);
            iterateCell(x - 1, y);
            iterateCell(x - 1, y - 1);
            iterateCell(x, y + 1);
            iterateCell(x, y - 1);

        // Case of cell on far left border
        } else if (y == 0) {
            iterateCell(x - 1, y + 1);
            iterateCell(x, y + 1);
            iterateCell(x + 1, y + 1);
            iterateCell(x + 1, y);
            iterateCell(x - 1, y);

        // Case of cell on far right border
        } else if (y == numRows - 1) {
            iterateCell(x - 1, y - 1);
            iterateCell(x, y - 1);
            iterateCell(x + 1, y - 1);
            iterateCell(x - 1, y);
            iterateCell(x + 1, y);

        // General case, no illegal neighbors
        } else {
            iterateCell(x, y + 1);
            iterateCell(x, y - 1);
            iterateCell(x + 1, y + 1);
            iterateCell(x + 1, y);
            iterateCell(x + 1, y - 1);
            iterateCell(x - 1, y + 1);
            iterateCell(x - 1, y);
            iterateCell(x - 1, y - 1);
        }
    }

    // Increments the status of each cell surrounding a mine
    public void evaluateField() {
        while (!mineLocs.isEmpty()) {  // Checks whether all mine locations have been addressed
            int[] check = mineLocs.pop();  // Array storing 2 coordinates of mine location removed from stack
            checkBorders(check[0], check[1]);  // The bordering cells of each mine are located and updated
        }
    }

    // Populates fields with mine objects according to selected user difficulty
    public void createMines(int x, int y, int mines) {

        Random rand = new Random();  // Random variable used for random mine locations

        // Well there are still mines to be placed, they are placed around field
        while (mines > 0) {

            // Random location of mine determined
            int mineX = rand.nextInt(numCols);
            int mineY = rand.nextInt(numRows);

            // Loops while a mine is placed on the starting location or where a mine exists already
            // until a proper location is found.
            while (mineX == x || mineY == y || field[mineY][mineX].getStatus().equals("M")) {
                mineX = rand.nextInt(numCols);
                mineY = rand.nextInt(numRows);
            }

            // Once location is found, mine is placed and remaining mines are decremented
            field[mineY][mineX] = new Cell(false, "M");
            mines--;

            // Location of each mind is stored in stack so that surrounding tiles' statuses can be determined
            mineLocs.push(new int[]{mineX, mineY});
        }
    }

    /* Reveals a cell of the user's choice or gives user opportunity to place and remove flag from a given cell
     Returns boolean of whether guess was legal and the cell was not a mine
     */
    public boolean guess(int x, int y, boolean flag) {
        boolean shouldReveal = true;  // Boolean value tracking whether a cell should be revealed

        // Handles case of user placing / removing flag
        if(flag && field[y][x].getStatus().equals("F")){  // Case of placing a flag on cell with flag

            field[y][x] = oldFlags.get("" + x + y);     // Populates cell with data that was originally on square before it was flagged
            shouldReveal = field[y][x].getRevealed();   // If a flag is removed, cell should not be revealed
            if(!shouldReveal){
                revealCtr--;
            }
            numFlags++;     // Number of flags user has left increases because one was removed


        } else if (flag) {      // General flag placement case
            if (numFlags >= 1) {    // Ensure user has flags left to place

                // Data in flagged cell is stored with its coordinates as key in case flag is removed
                oldFlags.put("" + x + y, new Cell(field[y][x].getRevealed(), field[y][x].getStatus()));

                field[y][x].setStatus("F");     // Cell set to a flag
                numFlags--;     // Number of flags user has left decreases because one was placed

            } else {
                // Flag can not be placed if they all have been placed
                System.out.println("No more flags to place!");
                return false;
            }
        }

        // If a zero is guessed, all neighboring zeroes are revealed
        if (field[y][x].getStatus().equals("0")) {
            revealZeroes(x, y);}

        // If a mine is guessed, game ends and field is revealed
        if (field[y][x].getStatus().equals("M") && !flag) {
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numRows; c++) {
                    field[r][c].setRevealed(true);      // Reveals each cell on field
                }
            }

            isGameOver = true;  // Boolean change will break game loop in main
            return false;
        }

        // shouldReveal true by default, used only in case that user "guesses" a revealed square to place a flag
        // revealCtr iterated so that it is known when game should end
        if(!field[y][x].getRevealed() && shouldReveal){
            field[y][x].setRevealed(true);
            revealCtr++;
        }

        return true;    // Guess was legal
    }

    // Checks if all spaces have been revealed. Updates isGameOver to true if so. Returns isGameOver
    public boolean gameOver() {

        if(revealCtr == (numCols * numRows)){ // If every cell on the board has been removed, then game is over
            isGameOver = true;
        }
        return isGameOver;
    }

    // Helper function that returns a 2-d array of a cell's neighbors' indices (above, below, left, right)
    public int[][] findNeighbors(int x, int y) {
        int[][] allNeighbors = new int[4][2];

        allNeighbors[0] = new int[] {x, y + 1};     // Right
        allNeighbors[1] = new int[] {x, y - 1};     // Left

        allNeighbors[2] = new int[] {x + 1, y};     // Below
        allNeighbors[3] = new int[] {x - 1, y};     // Above

        return allNeighbors;    // Returns array of each neighbor's coordinates
    }

    // When a cell with status 0 is guessed, reveals all neighboring cells with status of 0
    public void revealZeroes(int x, int y) {

        Stack1Gen<int[]> stack = new Stack1Gen<int[]>();  // Stack used to store references to all zeroes which have not been revealed
        stack.push(new int[]{x, y});    // Pushes the coordinates which were guessed into the stack

        // Loops until coordinate stack is empty
        // Removes each coordinate values from the stack so that neighbors can be checked upon each iteration
        while (!stack.isEmpty()) {

            int[] vals = stack.pop();   // Pops and stores a coordinate pair to check neighbors

            // Reveals a cell and updates a counter of revealed cells if a cell is not already revealed
            if(!field[vals[1]][vals[0]].getRevealed()){
                field[vals[1]][vals[0]].setRevealed(true);
                revealCtr++;}

            // Calls helper function that returns an array of all neighboring cell coordinates
            int[][] neighbors = findNeighbors(vals[0], vals[1]);

            // Iterates through each element in neighbors to ensure they are inbounds, equal to 0, and unrevealed
            for (int[] element : neighbors) {
                boolean inBounds = element[0] >= 0 && element[0] < numRows && element[1] >= 0 && element[1] < numCols;

                // If inbounds, equal to 0, and unrevealed, cell is pushed to the stack
                if (inBounds && !field[element[1]][element[0]].getRevealed() && field[element[1]][element[0]].getStatus().equals("0")) {
                    stack.push(element);
                }
            }

        }
    }

    // Reveals a limited amount of cells around the user's first guess
    public void revealStartingArea(int x, int y) {

        Q1Gen<int[]> q = new Q1Gen<int[]>(); // Queue used to store references to all cells which will be revealed
        Stack1Gen<int[]> zeroLocs = new Stack1Gen<int[]>(); // Stack storing all encountered zeros to reveal all neighboring zeroes

        q.add(new int[] {x, y});    // Adds the user's first guess to the queue

        // While there are still cells to be checked and a mine has not been hit yet, adds and check cell neighbors
        while(q.length() > 0){

            // Removes a coordinate pair from the queue to check
            int[] vals = q.remove();

            // If an unrevealed cell has a status of 0, its coordinates ar pushed to the stack tracking 0 locations
            if(field[vals[1]][vals[0]].getStatus().equals("0") && !field[vals[1]][vals[0]].getRevealed()){
                zeroLocs.push(vals);
            }

            // Reveals a cell and updates a counter of revealed cells if a cell is not already revealed
            if(!field[vals[1]][vals[0]].getRevealed()) {
                revealCtr++;
                field[vals[1]][vals[0]].setRevealed(true);
            }

            // Stops revealing cells once a mine is revealed
            if(field[vals[1]][vals[0]].getStatus().equals("M")){
                break;
            }

            // Checks each neighboring cell and adds them to the queue if they are in bounds and not revealed
            for(int[] neighbor : findNeighbors(vals[0], vals[1])){

                boolean isRevealed;
                boolean inBounds = (neighbor[0] < numRows && neighbor[0] >= 0) && (neighbor[1] < numCols && neighbor[1] >= 0);

                // If a guess is not inbounds, the revealed status can not be checked
                if(inBounds){
                    isRevealed = field[neighbor[1]][neighbor[0]].getRevealed();
                } else {isRevealed = false;}

                // If a neighboring cell is both inbounds and not revealed, the cell is added to the queue
                if(inBounds && !isRevealed){
                    q.add(neighbor);
                }
            }
        }

        // Calls reveal zeroes for each zero revealed in the starting area
        while(!zeroLocs.isEmpty()){
            int[] coords = zeroLocs.pop();
            int xIdx = coords[0];
            int yIdx = coords[1];

            revealZeroes(xIdx, yIdx);
        }
    }

    // Prints each cell of the field and each axis, regardless of revealed status
    public void debug() {
        String fieldString = "     ";   // Initializes the string representing the field

        // Appends the x-axis values to the field string
        for(int i = 0; i < numCols; i++){
            if(i >= 10){
                fieldString += i + " ";     // One space if the value is 2 digits
            } else {
                fieldString += i + "  ";    // Two spaces if the value is 1 digit
            }
        }
        fieldString += "\n\n";

        // Loop that produces out a string representation of the field
        for (int r = 0; r < numRows; r++) {

            // Appends the y-axis value to the front of each row in the field
            if(r >= 10){
                fieldString += r + "   ";
            } else {fieldString += r + "    ";}

            // Determines what color each cell status should be, and appends each cell to the field string
            for (int c = 0; c < numCols; c++) {

                    String space = field[r][c].getStatus();

                    // Checks status value of each cell to determine color
                    switch (space){
                        case "M":
                            fieldString += "\u001b[31m" + space + "\u001b[0m" + "  ";
                            break;
                        case "F":
                            fieldString += space + "  ";
                            break;
                        case "0":
                            fieldString += "\u001b[32m" + space + "\u001b[0m" + "  ";
                            break;
                        case "1":
                            fieldString += "\u001b[33m" + space + "\u001b[0m" + "  ";
                            break;
                        case "2":
                            fieldString += "\u001b[34m" + space + "\u001b[0m" + "  ";
                            break;
                        case "3":
                            fieldString += "\u001b[35m" + space + "\u001b[0m" + "  ";
                            break;
                        case "4", "5", "6", "7", "8":
                            fieldString += "\u001b[36m" + space + "\u001b[0m" + "  ";
                            break;
                    }
                }
            fieldString += "\n";
            }

        // Prints out String representation of field
        System.out.println(fieldString);
        }

    // Returns a String representation of each revealed cell in the field
    public String toString() {

        String ret = "     ";   // Initializes the string representing the field

        // Appends the x-axis values to the field string
        for(int i = 0; i < numCols; i++){
            if(i >= 10){
                ret += i + " ";
            } else {
                ret += i + "  ";
            }
        }

        ret += "\n\n";

        // Loop that modifies ret to produce a string representation of the field
        for (int r = 0; r < numRows; r++) {

            // Appends the y-axis value to the front of each row in the field
            if(r >= 10){
                ret += r + "   ";
            } else {ret += r + "    ";}


            // Determines what color each cell status should be, and appends each cell to the field string if it is revealed
            for (int c = 0; c < numCols; c++) {

                if (field[r][c].getRevealed()) { // Assures cell is revealed before appending it

                    String space = field[r][c].getStatus();

                    // Checks status value of each cell to determine color
                    switch (space){
                        case "M":
                            ret += "\u001b[31m" + space + "\u001b[0m" + "  ";
                            break;
                        case "F":
                            ret += space + "  ";
                            break;
                        case "0":
                            ret += "\u001b[32m" + space + "\u001b[0m" + "  ";
                            break;
                        case "1":
                            ret += "\u001b[33m" + space + "\u001b[0m" + "  ";
                            break;
                        case "2":
                            ret += "\u001b[34m" + space + "\u001b[0m" + "  ";
                            break;
                        case "3":
                            ret += "\u001b[35m" + space + "\u001b[0m" + "  ";
                            break;
                        case "4", "5", "6", "7", "8":
                            ret += "\u001b[36m" + space + "\u001b[0m" + "  ";
                            break;
                    }
                } else { // If the cell is not revealed, print - in cell location
                    ret += "-  ";
                }
        }
        ret += "\n";
    }

        return ret;     // Returns String representation of each revealed cell in the field
    }
}

