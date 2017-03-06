//Bryan Platt
import java.util.Scanner;
import java.util.Random;

public class Minesweeper {
	
	public char[][] board;
	public int numrows = 10;
	public int numcols = 10;
	public int numBombs = 10;
	
	public int spotCount = 0;
	public char unclicked = '.';
	public boolean[][] bombBoard;
	
	Random rng = new Random();
	Scanner input = new Scanner(System.in);
	
	public Minesweeper() {
		board = new char[numrows + 2][numcols + 2];
		for (int row = 0; row <= numrows; row++) {
			for (int col = 0; col <= numcols; col++) {
				board[row][col] = unclicked;
			}
		}
		//a boolean array that contains the "bomb" values for the game board
		bombBoard = new boolean[numrows + 2][numcols + 2];
		for (int row = 0; row <= numrows; row++) {
			for (int col = 0; col <= numcols; col++) {
				bombBoard[row][col] = false;
			}
		}
	}
	
	public static void main(String [] args) {
		Minesweeper myGame = new Minesweeper();
		System.out.println("Hello, we are going to play Minesweeper!");
		myGame.printBoard();
		myGame.makeBombs();
		myGame.playGame();
	}
	
	//simply displays the contents of the game board
	public void printBoard() {
		//same print style from the game of life
		System.out.print("    ");
		for (int col = 1; col <= numcols; col++) {
			if (col/10 > 0) {
				System.out.print(col/10);
			}
			else {
				System.out.print(" ");
			}
		}
		System.out.println();
		System.out.print("    ");
		for (int col = 1; col <= numcols; col++) {
			System.out.print(col%10);
		}
		System.out.println();
		for (int row = 1; row <= numrows; row++) {
			if (row < 10) {
				System.out.print(" ");
			}
			System.out.print(row + ": ");
			for (int col = 1; col <= numcols; col++) {
				System.out.print(board[row][col]);
			}
			System.out.println();
		}
	}
	
	//creates the random bombs for the game 
	public void makeBombs() {
		int number = 0;
		int countBombs = 0;
		
		/*this will run through the whole array (multiple times if necessary)
		and randomly add true values to the bomb array until there are 10*/
		while (countBombs < numBombs) {
			for (int row = 1; row <= numrows; row++) {
				for (int col = 1; col <= numcols; col++) {
					if (countBombs == numBombs) {
						break;
					}
					number = rng.nextInt(10);
					if (number == 5 && bombBoard[row][col] == false) {
						bombBoard[row][col] = true;
						countBombs++;
					}
				}
			}
		}
	}
	
	//performs the main functions of the game
	public void playGame() {
		int playerRow, playerCol;
		int localBombs = 0;
		
		//if there are 10 bombs, there are 90 non-bombs, and you have to reveal them all to win
		while (spotCount < 90) {
			System.out.println("Which row?");
			playerRow = input.nextInt();
			System.out.println("Which column?");
			playerCol = input.nextInt();
			
			//prevents array out of bounds errors
			if (playerRow > 10 || playerRow < 1 ||
				playerCol > 10 || playerCol < 1) {
				System.out.println("Only numbers 1-10 please. Pick again.");
				playGame();
			}
			//prevents the program from counting spots that were already used
			if (board[playerRow][playerCol] != '.') {
				System.out.println("That spot is already revealed. Pick again.");
				playGame();
			}
			//ends the game if there's a bomb
			if (bombBoard[playerRow][playerCol]) {
				System.out.println("Game over, you hit a bomb.");
				endGame();
			}
			localBombs = countNeighbors(playerRow, playerCol); //how many bombs neighbor the spot
			updateBoard(localBombs, playerRow, playerCol);     
			printBoard();
		}
		//if the while loop exits, the game is over
		System.out.println();
		System.out.println("Congrats, you win the game!");
		endGame();
	}
	
	//checks how many bombs neighbor the spot at the inputed coordinates and returns it 
	public int countNeighbors(int row, int col) {
		int count = 0;
		
		//these special cases prevent any potential out of bounds errors
		if (row == 1 && col == 1) {
			for (int r = row; r <= row + 1; r++) {
				for (int c = col; c <= col + 1; c++) {
					if(bombBoard[r][c] == true) {
						count++;
					}
				}
			}
		}
		else if (row == 1) {
			for (int r = row; r <= row + 1; r++) {
				for (int c = col - 1; c <= col + 1; c++) {
					if(bombBoard[r][c] == true) {
						count++;
					}
				}
			}
		}
		else if (col == 1) {
			for (int r = row - 1; r <= row + 1; r++) {
				for (int c = col; c <= col + 1; c++) {
					if(bombBoard[r][c] == true) {
						count++;
					}
				}
			}
		}
		//the general case
		else {
			for (int r = row - 1; r <= row + 1; r++) {
				for (int c = col - 1; c <= col + 1; c++) {
					if(bombBoard[r][c] == true) {
						count++;
					}
				}
			}
		}
		return count;
	}
	
	//adds the new values to the board as determined by countNeighbor()
	public void updateBoard(int bombs, int row, int col) {
		//if there are bombs around the spot, it simply reveals that number
		if (bombs > 0) {
			for (int check = 1; check <= 8; check++) {
				if (check == bombs) {
					board[row][col] = (char)((int)('0') + check);
					spotCount++;
				}
			}
		}
		//reveals all of the spots neighboring an empty spot, and recurses to reveal the neighbors of any empty neighbors, and so forth
		if (bombs == 0) {
			if (row == 1 && col == 1) {
				for (int r = row; r <= row + 1; r++ ) {
					for (int c = col; c <= col + 1; c++) {
						if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) == 0) {
							board[r][c] = ' ';
							spotCount++;
							updateBoard(countNeighbors(r , c) , r , c);
						}
						else if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) > 0) {
							for (int check = 1; check <= 8; check++) {
								if (check == countNeighbors(r , c)) {
									board[r][c] = (char)((int)('0') + check);
									spotCount++;
								}
							}
						}
					}
				}
			}
			else if (row == 1) {
				for (int r = row; r <= row + 1; r++ ) {
					for (int c = col - 1; c <= col + 1; c++) {
						if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) == 0) {
							board[r][c] = ' ';
							spotCount++;
							updateBoard(countNeighbors(r , c) , r , c);
						}
						else if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) > 0) {
							for (int check = 1; check <= 8; check++) {
								if (check == countNeighbors(r , c)) {
									board[r][c] = (char)((int)('0') + check);
									spotCount++;
								}
							}
						}
					}
				}
			}
			else if (col == 1) {
				for (int r = row - 1; r <= row + 1; r++ ) {
					for (int c = col; c <= col + 1; c++) {
						if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) == 0) {
							board[r][c] = ' ';
							spotCount++;
							updateBoard(countNeighbors(r , c) , r , c);
						}
						else if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) > 0) {
							for (int check = 1; check <= 8; check++) {
								if (check == countNeighbors(r , c)) {
									board[r][c] = (char)((int)('0') + check);
									spotCount++;
								}
							}
						}
					}
				}
			}
			else {
				for (int r = row - 1; r <= row + 1; r++ ) {
					for (int c = col - 1; c <= col + 1; c++) {
						if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) == 0) {
							board[r][c] = ' ';
							spotCount++;
							updateBoard(countNeighbors(r , c) , r , c);							
						}
						else if (board[r][c] == '.' && bombBoard[r][c] == false && countNeighbors(r , c) > 0) {
							for (int check = 1; check <= 8; check++) {
								if (check == countNeighbors(r , c)) {
									board[r][c] = (char)((int)('0') + check);
									spotCount++;
								}
							}
						}
					}
				}
			}
		}
	}
	
	//allows for multiple plays of the game
	public void endGame() {
		String holdAnswer;
		char condition;
		Minesweeper newGame = new Minesweeper();
		
		System.out.println("Would you like to play again? Y or N?");
		//runs twice in order to stop an input error with the string
		holdAnswer = input.nextLine();
		holdAnswer = input.nextLine();
		condition = holdAnswer.charAt(0);
		
		if (condition == 'y' || condition == 'Y') {
			System.out.println("Okay, let's go again.");
			newGame.printBoard();
			newGame.makeBombs();
			newGame.playGame();
		}
		if (condition == 'n' || condition == 'N') {
			System.out.println("Okay, thanks for playing!");
			System.exit(1);
		}
		else {
			System.out.println("Sorry, I couldn't understand that. Thanks for playing!");
			System.exit(1);
		}
	}	
}