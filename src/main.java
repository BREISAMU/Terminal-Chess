//Imports
import java.util.Scanner;

// Basic Minefield Rules
/*
    Users attempt to reveal every square of a given field while avoiding randomly placed mines. Each square has a
    value (1-8), that tells the user how many mines surround it in a 3x3 tile. A flag allows users to skip over squares
    they believe to be mines. Hit a mine, and the game is over. If you reveal all squares and place flags on all the
    mines, you win.
 */

/* Main:   Initiates a game of Minefield by accepting user input on Debug mode, difficulty and an initial guess
           A loop then begins that accepts user guesses until the game is finished
 */
public class main{
    public static void main(String[] args) {

        int fChoice = 0;    // Integer representing whether user chosen to place a flag, will be updated with user guess
        int x = -1;         // Integer representing the x-value chosen by user
        int y = -1;         // Integer representing the y-value chosen by user

        String str;                 // String used to record users' input into the console

        Minefield mField = null;    // Minefield to serve as the game field

        Scanner s = new Scanner(System.in);     // Scanner to take in user input


        boolean debugMode = false;  // Boolean representing whether game should be played in debug mode

        // Prompts user on whether they would like to play in debug mode, and updates str with their answer
        System.out.println("Welcome to" + "\u001b[31m" + " Minefield" + "\u001b[0m" + "\nWould you like to play in debug mode?  (y / n)");
        str = s.nextLine();

        // If user chooses yes, debugMode boolean is updated and game will be played in debugMode
        if(str.toLowerCase().equals("y")){
            debugMode = true;
        }

        // While the user has not answered y or n, they are continually re-prompted until they do
        while(!(str.equals("y") || str.equals("n"))){
            System.out.println("Invalid answer. Would you like to play in debug mode?  (y / n)");
            str = s.nextLine();

            // If the user answers "n", debug is false by default and need not be updated
            if(str.equals("y")){
                debugMode = true;}
        }


        // Prompts user on whether they would like to play in easy/medium/hard mode, and updates str with their answer
        System.out.println("Would you like to play on easy, medium or hard? (Enter one:  e, m, h )");

        boolean validDifficulty = false;   // Boolean representing whether a valid difficulty has been chosen
        int mineCtr = 0;   // Integer representing how many mines should be placed on field

        while (!validDifficulty) {  // While a valid difficulty has not been chosen, user is continually prompted

            // Checks whether user has chosen "e", "m" or "h"
            // Difficulty decides number of rows, columns, flags and mines
            switch (s.nextLine().toLowerCase()) {

                // Once valid choice made:
                // Initiates minefield with rows, cols and flags according to difficulty choice
                // difficultyChosen updated to break loop
                // mineCtr updated to correspond with selected difficulty

                case "e":
                    mField = new Minefield(5, 5, 5);
                    validDifficulty = true;
                    mineCtr = 5;
                    break;
                case "m":
                    mField = new Minefield(9, 9, 12);
                    validDifficulty = true;
                    mineCtr = 12;
                    break;
                case "h":
                    mField = new Minefield(20, 20, 40);
                    validDifficulty = true;
                    mineCtr = 40;
                    break;
                default:
                    System.out.println("Sorry invalid choice, please try again. (e, m, h)");
                    break;
            }
    }

        boolean validFirstGuess = false;   // Boolean representing whether a valid first guess has been made

        while(!validFirstGuess){ // While a valid first guess has not been made, user is continually prompted

            // User prompted for first guess, str updated to choice
            System.out.println("Enter starting coordinates:  [x] [y]");
            str = s.nextLine();

            // User's inputs are turned into an array of coordinates, then each value is parsed into an integer
            // If a user guesses out of range or inputs something that can not be turned into an integer, the respective errors are caught
            // If an error occurs, validFirstGuess is not updated and loop continues
            try {

                x = Integer.parseInt(str.split(" ")[0]);    // Turns user choice of x-coordinate into integer
                y = Integer.parseInt(str.split(" ")[1]);    // Turns user choice of y-coordinate into integer

                // Checks whether guess is in bounds of the field
                if(x >= 0 && x < mField.getNumRows() && y >= 0 && y < mField.getNumCols()){
                    validFirstGuess = true;     // If in bounds, loop breaks as guess is valid
                } else {
                    System.out.print("Out of bounds. ");}  // If not in bounds, loop continues as guess is invalid

            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                // If user input can not properly be extracted or parsed, loop continues
                System.out.println("Invalid format. ");
            }
        }


        /* Section below initiates many aspects of the field:
        - Places an unrevealed cell with status 0 at every space on the field
        - Creates mines at random locations on the field
        - Updates each cell with their proper status (number of mines in proximity)
        - Reveals a limited amount of cells around starting guess
         */
        mField.populateZeroes();
        mField.createMines(x, y, mineCtr);
        mField.evaluateField();
        mField.revealStartingArea(x,y);
        mField.guess(x,y, false);

        if(debugMode){mField.debug();}   // If debug mode was chosen, print out every cell on field


        System.out.println(mField);      // Print out revealed cells on board
        boolean validGuess = false;     // Boolean representing whether a valid guess has been made


        while(!mField.gameOver()){   // While game is not over, user is continually prompted to guess cells
            validGuess = false;

            while(!validGuess){     // While a user's guess is not valid, they are continually prompted

                // User prompted for guess, str updated to choice
                System.out.println("Enter a coordinate and if you wish to place a flag (Remaining: " + mField.getNumFlags() + "): [x] [y] [f (-1, else)]");
                str = s.nextLine();

                // User's inputs are turned into an array of coordinates, then each value is parsed into an integer
                // If a user guesses out of range or inputs something that can not be turned into an integer, the respective errors are caught
                // If an error occurs, validFirstGuess is not updated and loop continues
                try {

                    x = Integer.parseInt(str.split(" ")[0]);    // Turns user choice of x-coordinate into integer
                    y = Integer.parseInt(str.split(" ")[1]);    // Turns user choice of y-coordinate into integer
                    fChoice = Integer.parseInt(str.split(" ")[2]);  // Turns user choice of flag placement into integer

                    // Checks whether guess is in bounds of the field
                    if(x >= 0 && x < mField.getNumRows() && y >= 0 && y < mField.getNumCols()){
                        validGuess = true;     // If in bounds, loop breaks as guess is valid
                    } else {
                        System.out.print("Out of bounds. ");}  // If not in bounds, loop continues as guess is invalid

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                    // If user input can not properly be extracted or parsed, loop continues
                    System.out.print("Invalid format. ");
                }
            }

            if(mField.guess(x, y, fChoice == -1)){  // Guess returns false if user guessed a mine without flagging

                if(debugMode){   // Print all cells if in debug mode
                    mField.debug();}

                System.out.println(mField); // Print all revealed cells
            }

        }


        boolean winner = true;  // Boolean representing whether a user has won the game

        // Iterates through each cell on field
        for (int r = 0; r < mField.getNumRows(); r++) {
            for (int c = 0; c < mField.getNumCols(); c++) {

                // Checks each cell and sets winner to false if a mine is found
                if(mField.getCell(r, c).getStatus().equals("M")){
                    winner = false;
                }

                mField.getCell(r, c).setRevealed(true); // Reveals each cell so that th user can see all cells in field
            }
        }


        System.out.println(mField);     // Prints out fully revealed field

        // Inform the user of whether they won or lost
        if(winner){
            System.out.println("Winner!! Congratulations.");
        } else {
            System.out.println("You lose!");
        }

        s.close();  // Closes scanner
    }

}
