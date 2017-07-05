package minesweeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

/**
 * ADT that represents minesweeper board
 */
public class Board {
    
    /**
     * Abstraction function
     * 
     * Represents a board of size columns * rows with board cells
     * 
     * Rep invariant
     * 
     * board array size is columns * rows
     * each cell, that is dug and empty, contains number of adjacent bombs
     * 
     * Rep exposure
     * 
     * all field are private
     * columns and rows are ints
     * board points to mutable Cell, but none Cell objects ever accepted or returned
     * 
     * Thread safety
     * 
     * columns and rows are immutable
     * all accesses to board happen within Board and Cell methods. All methods in Board
     * are synchronized with lock of the whole board. Cell class is not thread-safe by itself,
     * but we can only use Cell through Board methods, so we also have block on the whole 
     * board. 
     * 
     * (We cannot only block one cell, because we often need adjacent cells and we don't know
     * how many before we call the method)
     */
    
    private final int columns;
    private final int rows;
    private final Cell[][] board;
    
    private synchronized void checkRep() {
        assert board != null;
        assert board.length == columns;
        assert board[0].length == rows;
        for(int x = 0; x < columns; ++x) {
            for(int y = 0; y < rows; ++y ) {
                if(board[x][y].state() != Cell.State.DUG ||
                        board[x][y].containsBomb()) {
                    continue;
                }
                int bombs = 0;
                for(int i = 0; i < NEIGHBORS_NUMBER; ++i) {
                    int xx = x + X_NEIGHBORS[i];
                    int yy = y + Y_NEIGHBORS[i];
                    if(xx >= 0 && xx < columns && yy >= 0 && yy < rows) {
                        if(board[xx][yy].containsBomb()) bombs++;
                    }
                }   
                assert bombs == board[x][y].neighborBombs();
            }
        }
    }
    
    /**
     * Constructs the same board as in the file, specified by the following grammar
     * * <pre>
     *   FILE ::= BOARD LINE+
     *   BOARD ::= X SPACE Y NEWLINE
     *   LINE ::= (VAL SPACE)* VAL NEWLINE
     *   VAL ::= 0 | 1
     *   X ::= INT
     *   Y ::= INT
     *   SPACE ::= " "
     *   NEWLINE ::= "\n" | "\r" "\n"?
     *   INT ::= [0-9]+
     * </pre>
     * @param file file where board stores
     * @throws FileNotFoundException if file was not found
     */
    public Board(File file) throws FileNotFoundException {
        Scanner c = new Scanner( new FileInputStream(file));
        columns = c.nextInt();
        rows = c.nextInt();
        board = new Cell[columns][rows];
        boolean fileBoard[][] = new boolean[rows][columns];
        for(int i = 0;i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                fileBoard[i][j] = c.nextInt() == 1;
            }
        }
        c.close();
        for(int i = 0;i < columns; ++i) {
            for(int j = 0; j < rows; ++j) {
                board[i][j] = new Cell(fileBoard[j][i]);
                //System.out.print(board[i][j].containsBomb() + " ");
            }
            //System.out.println();
        }
        checkRep();
    }
    
    /**
     * Constructs a new random board with x columns and y rows
     * @param x number of columns, x > 0
     * @param y number of rows, y > 0
     */
    public Board(int x, int y) {
        if(x <= 0 || y <= 0 ){
            throw new IllegalArgumentException("Number of columns and rows must be greater than zero");
        }
        columns = x;
        rows = y;
        board = new Cell[columns][rows];
        Random r = new Random(47);
        for(int i = 0;i<columns; ++i) {
            for(int j = 0; j < rows; ++j) {
                board[i][j] = new Cell(r.nextInt(3) != 0); // true with probability 1/3
            }
        }
        checkRep();
    }
    
    //Observers
    
    /**
     * returns number of columns on the board
     */
    public synchronized int columnsNumber() {
        return columns;
    }
    

    /**
     * returns number of rows on the board
     */
    public synchronized int rowsNumber() {
        return rows;
    }
    
    /**
     * Observe whether cell (x,y) is touched (not flagged and not dug)
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     * @return true if and only if cell (x,y) is untouched
     */
    public synchronized boolean isUntouched(int x, int y) {
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        return board[x][y].state() == Cell.State.UNTOUCHED;
    }

    /**
     * Observe whether cell (x,y) is flagged
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     * @return true if and only if cell (x,y) is flagged
     */
    public synchronized boolean isFlagged(int x, int y) {
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        return board[x][y].state() == Cell.State.FLAGGED;
    }

    /**
     * Observe whether cell (x,y) is dug
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     * @return true if and only if cell (x,y) is dug
     */
    public synchronized boolean isDug(int x, int y) {
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        return board[x][y].state() == Cell.State.DUG;
    }  

    /**
     * Finds number of adjacent bombs to dug empty cell (x,y)
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     * @return number of adjacent bombs to cell (x,y)
     * @throws UnsupportedOperationException if cell (x,y) is not dug or contains a bomb
     */
    public synchronized int neighborBombs(int x, int y) throws UnsupportedOperationException {
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        return board[x][y].neighborBombs();
    }
    
    //Mutators
    
    private final int NEIGHBORS_NUMBER = 8;
    private final int X_NEIGHBORS[] = {1, 1, 1, 0, 0, -1, -1, -1};
    private final int Y_NEIGHBORS[] = {-1, 0, 1, -1, 1, -1, 0, 1};
    
    /**
     * Digs cell (x,y) if it is untouched,
     * updates the number of neighbor bombs of this cell, 
     * if cell contains a bomb, makes this cell empty and updates dug empty neighbor cells
     * digs its neighbors if neighbor bombs number is zero
     * if was touched - does nothing
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     * @return true if and only if cell was dug and contained a bomb
     */
    public synchronized boolean digIfUntouched(int x, int y){
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        Cell cell = board[x][y];
        if(board[x][y].state() == Cell.State.UNTOUCHED) {
            boolean bomb = cell.containsBomb();
            cell.digIfUntouched();
            if(bomb) {
                cell.removeBomb();
                for(int i = 0; i < NEIGHBORS_NUMBER; ++i) {
                    int xx = x + X_NEIGHBORS[i];
                    int yy = y + Y_NEIGHBORS[i];
                    if(xx >= 0 && xx < columns && yy >= 0 && yy < rows) {
                        if(board[xx][yy].state() == Cell.State.DUG && 
                                !board[xx][yy].containsBomb()) {
                            countNeighborBombs(xx, yy);
                        }
                    }
                }    
            }
            int neighbors = countNeighborBombs(x,y);
            if(neighbors == 0) {
                for(int i = 0; i < NEIGHBORS_NUMBER; ++i) {
                    int xx = x + X_NEIGHBORS[i];
                    int yy = y + Y_NEIGHBORS[i];
                    if(xx >= 0 && xx < columns && yy >= 0 && yy < rows) {
                        if(board[xx][yy].state() != Cell.State.DUG) {
                            digEmpty(xx,yy);
                        }
                    }
                }
            }
            checkRep();
            return bomb;
        } else {
            return false;
        }
    }
    
    /**
     * digs an empty cell whether it untouched or flagged
     * updates the number of neighbor bombs of this cell, 
     * digs its neighbors if neighbor bombs number is zero
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     * @throws UnsupportedOperationException if cell is not empty
     */
    private synchronized void digEmpty(int x, int y) {
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        if(board[x][y].containsBomb()) {
            throw new UnsupportedOperationException();
        }
        board[x][y].digEmpty();
        int neighbors = countNeighborBombs(x,y);
        if(neighbors == 0) {
            for(int i = 0; i < NEIGHBORS_NUMBER; ++i) {
                int xx = x + X_NEIGHBORS[i];
                int yy = y + Y_NEIGHBORS[i];
                if(xx >= 0 && xx < columns && yy >= 0 && yy < rows) {
                    if(board[xx][yy].state() != Cell.State.DUG) {
                        digEmpty(xx,yy);
                    }
                }
            }
        }
    }

    /**
     * counts number of adjacent bombs of cell (x, y) on the board
     * and renew that number on cell(x,y)
     * @param x column, 0 <= x < columns
     * @param y row, 0 <= y < rows
     * @throws UnsupportedOperationException if cell is not dug or contains a bomb
     */
    private synchronized int countNeighborBombs(int x, int y) {
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        if(board[x][y].state() != Cell.State.DUG ||
                board[x][y].containsBomb()) {
            throw new UnsupportedOperationException();
        }
        int bombs = 0;
        for(int i = 0; i < NEIGHBORS_NUMBER; ++i) {
            int xx = x + X_NEIGHBORS[i];
            int yy = y + Y_NEIGHBORS[i];
            if(xx >= 0 && xx < columns && yy >= 0 && yy < rows) {
                if(board[xx][yy].containsBomb()) bombs++;
            }
        }        
        board[x][y].setNeighborBombs(bombs);
        return bombs;
    }
    
    /**
     * Flags cell (x,y) if it was untouched
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     */
    public synchronized void flagIfUntouched(int x, int y){
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        board[x][y].flagIfUntouched();
        checkRep();
    }
    
    /**
     * Deflags cell (x,y) if it was flagged
     * @param x 0 <= x < columnsNumber
     * @param y 0 <= y < rowsNumber
     */
    public synchronized void deflagIfFlagged(int x, int y){
        if(x < 0 || x >= columns ||
                y < 0 || y >= rows) {
            throw new IllegalArgumentException();
        }
        board[x][y].deflagIfFlagged();
        checkRep();
    }
    
    /**
     * Two boards are equal if and only if
     * the sizes are the same
     * each cell is in the same state and either contains a bomb or not
     */
    @Override
    public boolean equals(Object that) {
        if(!(that instanceof Board)) {
            return false;
        }
        Board thatBoard = (Board) that;
        if(columns != thatBoard.columns || rows != thatBoard.rows) {
            return false;
        }
        for(int i = 0; i < columns; ++i) {
            for(int j = 0; j < rows; ++j ) {
                if(!board[i][j].equals(thatBoard.board[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Returns a hash code. Consistent with equals
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
    
    /**
     * Returns a string representation of a board
     * X direction is horizontal, Y is vertical
     * each row starts from new line (separated by "\n" character)
     * within a line cells are separated by space character
     * each cell is represented by one character
     * "-" if untouched
     * "F" if flagged
     * " " if dug and neighbor cells do not contain bombs
     * 1-8 number of neighbor bombs otherwise
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rows; ++i ) {
            sb.append(board[0][i].toString());
            for(int j = 1; j < columns; ++j ) {
                sb.append(" ");
                sb.append(board[j][i].toString());
            }
            sb.append("\r\n");
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }
}

/**
 * Class that represents a cell of the minesweeper board
 * NOT thread safe
 * @author AcerS
 *
 */
class Cell {
    
    /**
     * Abstruction function
     * 
     * represents a cell that contains bomb if and only if containsBomb is true
     * which is in state state and has neighborBombs number of adjacent bombs
     * 
     * Representaion invariant
     * 
     * each cell either untouched or flagged and empty or contains a bomb OR dug and empty
     * 
     * Rep exposure
     * 
     * all field are private and points to immutable objects
     * @author AcerS
     *
     */
    enum State { UNTOUCHED, FLAGGED, DUG};
    private boolean containsBomb;
    private State state;
    private int neighborBombs;
    
    private void checkRep() {
        assert state != null;
        if(state == State.DUG) {
            assert containsBomb == false;
        }
    }
    
    /**
     * Constructs an untouched cell
     * @param bomb is true if cell must contain a bomb
     */
    Cell(boolean bomb) {
       containsBomb = bomb;
       state = State.UNTOUCHED;
       checkRep();
    }
    
    /**
     * @return a state of this cell
     */
    State state() {
        return state;
    }
    
    /**
     * @return true if and only if cell contains a bomb
     */
    boolean containsBomb(){
        return containsBomb;
    }
    
    /**
     * @return number of adjacent bombs
     * @throws UnsupportedOperationException if cell was not dug
     */
    int neighborBombs() throws UnsupportedOperationException{
        if(state != State.DUG) {
            throw new UnsupportedOperationException();
        }
        return neighborBombs;
    }
    
    /**
     * sets a flag if cell is untouched
     */
    void flagIfUntouched() {
        if(state == State.UNTOUCHED) {
            state = State.FLAGGED;
        }
        checkRep();
    }
    
    /**
     * deflags a cell if it was flagged and makes it untouched
     */
    void deflagIfFlagged() {
        if(state == State.FLAGGED) {
            state = State.UNTOUCHED;
        }
        checkRep();
    }
    
    /**
     * digs a cell if it was untouched and removes a bomb
     */
    void digIfUntouched() {
        if(state == State.UNTOUCHED) {
            state = State.DUG;
            containsBomb = false;
        }
        checkRep();
    }
    
    /**
     * digs this empty cell
     * @throws UnsupportedOperationException if cell was not empty
     */
    void digEmpty() {
        if(containsBomb) {
            throw new UnsupportedOperationException();
        }
        state = State.DUG;
        checkRep();
    }
    
    /**
     * removes bomb from the cell
     */
    void removeBomb() {
        containsBomb = false;
        checkRep();
    }
    
    /**
     * sets neighborBombs to the specified value
     * @param bombs
     */
    void setNeighborBombs(int bombs) {
        this.neighborBombs = bombs;
        checkRep();
    }
    
    /**
     * Checks whether Object equals to this cell
     * two cells are equal if and only if they are in the same state, and they are both 
     * empty or they both contain a bomb. Number of neighbor bombs do not influence equality.
     */
    @Override
    public boolean equals(Object that) {
        if(!(that instanceof Cell)) {
            return false;
        }
        Cell thatCell = (Cell) that;
        return containsBomb == thatCell.containsBomb && state == thatCell.state;
    }
    
    /**
     * returns a hash code depending on state and containing a bomb
     */
    @Override
    public int hashCode() {
        return Objects.hash(containsBomb, state);
    }
    
    /**
     * Returns a string representation of a cell
     * "-" if untouched
     * "F" if flagged
     * " " if dug and neighbor cells do not contain bombs
     * 1-8 number of neighbor bombs otherwise
     */
    @Override
    public String toString() {
        if(state == State.UNTOUCHED) {
            return "-";
        } else if(state == State.FLAGGED) {
            return "F";
        } else if(neighborBombs == 0) {
            return " ";
        } else {
            return String.valueOf(neighborBombs);
        }
    }
}
