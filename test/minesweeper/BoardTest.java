package minesweeper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

/**
 * Tests for Board ADT
 */
public class BoardTest {
    
    /**
     * Testing strategy
     * 
     * Downloading board from file, creating new random board
     * Number of columns and rows - entered and from file
     * Parameters legal or illegal
     * Observers on different kinds of cells ( untouched, flagged, dug + empty, bomb)
     * Number of neighbors on edge cells and not
     * Mutators on different kinds of cells
     * ! Must dig empty flagged cell
     * 0 0 0        F - -
     * 0 0 0        - - -
     * 0 0 0        - - -
     * dig(1,1) must open all cells
     * Equals, hashCode, toString
     */
    
    /**
     * Check assertion
     */
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    //Downloading board from file
    @Test
    public void testFromFile() throws FileNotFoundException {
        Board board = new Board(new File("test\\boards\\board_5_4.txt"));
        assertEquals("Columns number", 5, board.columnsNumber());
        assertEquals("Rows number", 4, board.rowsNumber());
        
        assertTrue("Untouched empty cell", board.isUntouched(0, 0));
        assertTrue("Untouched bomb cell", board.isUntouched(1, 0));
        
        board.flagIfUntouched(0, 0);
        board.flagIfUntouched(2, 0);
        assertTrue("Flagged empty cell", board.isFlagged(0, 0));
        assertFalse("Unflagged bomb cell", board.isFlagged(1, 0));
        assertTrue("Flagged bomb cell", board.isFlagged(2, 0));
        assertFalse("Unflagged empty cell", board.isFlagged(3, 0));
        
        board.deflagIfFlagged(2, 0);
        assertFalse("Deflagged cell", board.isFlagged(2, 0));
        
        assertFalse("Dig empty cell",board.digIfUntouched(4, 2));
        assertTrue("Dug cell", board.isDug(4, 2));  

        board.flagIfUntouched(1, 2);
        board.digIfUntouched(0, 3);
        assertTrue("Dug empty flagged neighbor", board.isDug(1, 2));
        assertEquals("Dug bomb cell neighbors", 2, board.neighborBombs(1, 2));
        assertTrue("Dig bomb cell", board.digIfUntouched(2, 2));
        assertTrue("Dug bomb cell", board.isDug(2, 2)); 
        assertEquals("Cell neighbors after boom", 1, board.neighborBombs(1, 2));
    }
    
    /**
     * Testing empty board
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testEmtpy() throws FileNotFoundException {
        Board board = new Board(new File("test\\boards\\emptyBoard_3_3"));
        assertEquals("empty board", board.toString(), "- - -\r\n- - -\r\n- - -");
    }
    
    /**
     * Testing board with specified size
     */
    @Test
    public void testSpecifiedSize() {
        Board board = new Board(3,5);
        assertEquals("Columns number", 3, board.columnsNumber());
        assertEquals("Row number", 5, board.rowsNumber());
        try {
            new Board(1,0);
            assertTrue("Creating board with 0 rows", false);
        } catch (IllegalArgumentException e) {}
    }
    
    /**
     * Testing objects overridden methods
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testObjectMethods() throws FileNotFoundException {
        Board board1 = new Board(new File("test\\boards\\board_5_4.txt"));
        Board board2 = new Board(new File("test\\boards\\board_5_4.txt"));
        assertEquals("Hash codes of equal boards", board1.hashCode(), board2.hashCode());
        
        board1.flagIfUntouched(1, 0);        
        assertFalse("Flagged and untouched cells", board1.equals(board2));
        board2.flagIfUntouched(1, 0);
        assertTrue("Two flagged cells", board1.equals(board2));
        assertEquals("Hash codes of equal boards", board1.hashCode(), board2.hashCode());
        board1.digIfUntouched(0, 3);
        assertFalse("Touched and untouched", board1.equals(board2));
        board2.digIfUntouched(0, 3);
        assertTrue("Two touched", board2.equals(board1));
        assertEquals("Hash codes of equal boards", board1.hashCode(), board2.hashCode());
        
        board1.flagIfUntouched(3, 0);
        String s = "- F - F -\r\n- - - - -\r\n1 2 - - -\r\n  1 - - -";
        //System.out.println(s);
        assertEquals("String representation", s, board1.toString());
    }
}
