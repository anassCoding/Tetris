import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class PieceTest {

	@Test
	public void testWidthHeight() {
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);

		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());		

		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}
	
	@Test 
	public void TestWidthHeightAfterRotation() {
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);
		
		Piece pyr2 = pyr1.computeNextRotation();
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
	}
	
	@Test
	public void testRotation() {
		Piece p = new Piece(Piece.STICK_STR);
		assertEquals(p.computeNextRotation(), new Piece("0 0 1 0 2 0 3 0"));

		p = new Piece(Piece.PYRAMID_STR);
		assertEquals(p.computeNextRotation(), new Piece("0 1 1 0 1 1 1 2"));
	}
	
	
	// Test the skirt returned by a few pieces
	@Test
	public void testSampleSkirt() {
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);

		assertEquals(new ArrayList<Integer>(Arrays.asList(new Integer[] {0, 0, 0})), pyr1.getSkirt());

		Piece s = new Piece(Piece.S1_STR);	
		assertEquals(new ArrayList<Integer>(Arrays.asList(new Integer[] {0, 0, 1})), s.getSkirt());
	}
	
	@Test
	public void testSkirtAfterRotation() {
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);
		Piece pyr2 = pyr1.computeNextRotation();
		Piece pyr3 = pyr2.computeNextRotation();
		
		assertEquals(new ArrayList<Integer>(Arrays.asList(new Integer[] {1, 0, 1})), pyr3.getSkirt());
		
	}
	
	
}
