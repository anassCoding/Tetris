import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class BoardTest {

	@Test
	public void testTwoPieces() {
		Board b = new Board(6, 6);
		
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);
		b.place(pyr1, 0, 0);
		b.commit();
		b.place(pyr1, 2, 1);

		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getColumnHeight(2));
		assertEquals(3, b.getColumnHeight(3));
		
		assertEquals(3, b.getMaxHeight());
		
		assertEquals(3, b.getRowWidth(0));
		assertEquals(4, b.getRowWidth(1));
		assertEquals(1, b.getRowWidth(2));
		assertEquals(0, b.getRowWidth(3));
	}
	
	@Test
	public void testSample1() {
		Board b = new Board(3, 6);
		
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);
		b.place(pyr1, 0, 0);
		
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));

		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	
	@Test
	public void testSample2() {
		Board b = new Board(3, 6);
		
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);
		Piece s = new Piece(Piece.S1_STR);
		Piece sRotated = s.computeNextRotation();
		
		b.place(pyr1, 0, 0);
		
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}
	
	@Test
	public void testUndo() {
		Board b = new Board(3, 6);
		
		Piece p1 = new Piece(Piece.STICK_STR);
		b.place(p1, 1, 1);
		b.undo();
		
		for (int i = 0; i < b.getWidth(); i++) {
			assertEquals(0, b.getColumnHeight(i));
		}

		for (int i = 0; i < b.getHeight(); i++) {
			assertEquals(0, b.getRowWidth(i));
		}
		
		Board b2 = new Board(3, 6);
		b2.place(p1, 1, 1);
		b2.commit();
		b2.place(p1, 0, 3);
		b2.undo();
		
		int count = 0;
		for (int i = 0; i < b2.grid.length; i++) {
			for (int j = 0; j < b2.grid[i].length; j++) {
				if (b2.grid[i][j]) {
					count += 1;
				}
			}
		}
		
		assertEquals(4, count);
	}

	@Test
	public void testMaxHeight() {
		Board b = new Board(3, 6);
		assertEquals(0, b.getMaxHeight());
		
		Piece p = new Piece(Piece.STICK_STR);
		b.place(p, 0, 0);
		assertEquals(4, b.getMaxHeight());
	}
	
	@Test
	public void testFallingMaxHeight() {
		Board b = new Board(3, 6);
		
		Piece p = new Piece(Piece.STICK_STR);
		b.place(p, 0, 1);
		assertEquals(5, b.getMaxHeight());

		b.undo();
		b.place(p, 0, 0);
		b.commit();
		assertEquals(4, b.getMaxHeight());
	}
	
	@Test
	public void testSeveralUndo() {
		Piece p1 = new Piece(Piece.STICK_STR);
		Board b2 = new Board(3, 10);
		
		b2.place(p1, 1, 1);
		
		b2.commit();
		b2.place(p1, 0, 3);
		b2.undo();
		b2.place(p1, 0, 0);
		b2.undo();
		
		assertTrue(Arrays.equals(new int[] {0, 5, 0}, b2.getHeights()));
	}
	
	@Test
	public void testClearRowsNothing() {
		// nothing to do
		
		Board b = new Board(5, 5);
		b.grid[0][0] = true;
		b.grid[1][0] = true;
		b.grid[0][1] = true;
		
		Board expected = new Board(5, 5);
		expected.grid[0][0] = true;
		expected.grid[1][0] = true;
		expected.grid[0][1] = true;
		
		assertEquals(b.clearRows(), 0);
				
		assertArrayEquals(expected.grid, b.grid);;
	}

	@Test
	public void testClearRowsOneLine() {
		// remove one line without having anything to drop
		
		Board b = new Board(5, 5);
		b.grid[0][0] = true;
		b.grid[1][0] = true;
		b.grid[2][0] = true;
		b.grid[3][0] = true;
		b.grid[4][0] = true;
		//b.grid[0][1] = true;
		
		b.updateWidthsHeights();
		
		Board expected = new Board(5, 5);

		assertEquals(b.clearRows(), 1);
				
		assertArrayEquals(expected.grid, b.grid);;
	}

	@Test
	public void testClearRowsOneLineWithDrop() {
		// remove one line and drop the next line
		
		Board b = new Board(5, 5);
		b.grid[0][0] = true;
		b.grid[1][0] = true;
		b.grid[2][0] = true;
		b.grid[3][0] = true;
		b.grid[4][0] = true;
		b.grid[0][1] = true;
		
		b.updateWidthsHeights();
		
		Board expected = new Board(5, 5);
		expected.grid[0][0] = true;
		
		assertEquals(1, b.clearRows());
				
		assertArrayEquals(expected.grid, b.grid);;
	}

	@Test
	public void testClearRowsTwoLinesWithDrop() {
		// remove one line and drop the next line
		
		Board b = new Board(5, 5);
		b.grid[0][0] = true;
		b.grid[1][0] = true;
		b.grid[2][0] = true;
		b.grid[3][0] = true;
		b.grid[4][0] = true;
		
		b.grid[0][1] = true;
		b.grid[1][1] = true;
		b.grid[2][1] = true;
		b.grid[3][1] = true;
		b.grid[4][1] = true;
		
		b.grid[0][2] = true;
		b.grid[0][3] = true;
		b.grid[4][2] = true;
		
		b.updateWidthsHeights();
		
		Board expected = new Board(5, 5);
		expected.grid[0][0] = true;
		expected.grid[0][1] = true;
		expected.grid[4][0] = true;
		
		assertEquals(b.clearRows(), 2);
				
		assertArrayEquals(expected.grid, b.grid);;
	}
	
	@Test
	public void testClearHeights() {
		// can we call dropHeight after having cleared the board
		
		Board b = new Board(5, 5);
		b.grid[0][0] = true;
		b.grid[1][0] = true;
		b.grid[2][0] = true;
		b.grid[3][0] = true;
		b.grid[4][0] = true;
		
		b.updateWidthsHeights();
		
		b.clearRows();
		
		assertEquals(0, b.dropHeight(new Piece(Piece.SQUARE_STR), 0));
	}
	
	@Test
	public void clearComplicated() {
		Board b = new Board(5, 7);
		b.grid[0][0] = true;
		b.grid[1][0] = false;
		b.grid[2][0] = true;
		b.grid[3][0] = false;
		b.grid[4][0] = true;
		
		b.grid[0][1] = true;
		b.grid[1][1] = true;
		b.grid[2][1] = true;
		b.grid[3][1] = true;
		b.grid[4][1] = true;
		
		b.grid[0][2] = true;
		b.grid[1][2] = true;
		b.grid[2][2] = false;
		b.grid[3][2] = true;
		b.grid[4][2] = false;
		
		b.updateWidthsHeights();

		Board expected = new Board(5, 7);
		expected.grid[0][0] = true;
		expected.grid[1][0] = false;
		expected.grid[2][0] = true;
		expected.grid[3][0] = false;
		expected.grid[4][0] = true;
		
		expected.grid[0][1] = true;
		expected.grid[1][1] = true;
		expected.grid[2][1] = false;
		expected.grid[3][1] = true;
		expected.grid[4][1] = false;
		
		expected.updateWidthsHeights();
	
		assertEquals(1, b.clearRows());
		
		assertArrayEquals(expected.grid, b.grid);;
		
	}
}
