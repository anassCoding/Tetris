import static org.junit.Assert.*;

import org.junit.Test;

public class TestBrain {

	@Test
	public void testStick() {
		Board b = new Board(3, 5);
		Brain brain = new DefaultBrain();
		b.place(new Piece(Piece.STICK_STR), 0,  0);
		b.commit();
		b.place(new Piece(Piece.STICK_STR), 2,  0);
		b.commit();

		Piece p = new Piece(Piece.STICK_STR).computeNextRotation();
		Brain.Move bestMove = brain.bestMove(b, p, 5);
		
		assertEquals(1, bestMove.x);
		assertEquals(0, bestMove.y);
		assertEquals(new Piece(Piece.STICK_STR), bestMove.piece);
	}

	@Test
	public void testL() {
		Board b = new Board(5, 5);
		Brain brain = new DefaultBrain();
		b.place(new Piece(Piece.STICK_STR).computeNextRotation(), 0,  0);
		b.commit();
		b.place(new Piece(Piece.STICK_STR).computeNextRotation(), 0,  1);
		b.commit();

		Piece p = new Piece(Piece.L1_STR);
		
		Brain.Move bestMove = brain.bestMove(b, p, 5);
		
		assertEquals(3, bestMove.x);
		assertEquals(0, bestMove.y);
		assertEquals(new Piece("1 2 1 1 1 0 0 2"), bestMove.piece);
	}
}
