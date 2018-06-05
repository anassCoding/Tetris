/*
 This is just a trivial "struct" type class --
 it simply holds an int x/y point for use by Tetris,
 and supports equals() and toString().
 We'll allow public access to x/y, so this
 is not an object really.
 */
public class TPoint {

	/// CONSTRUCTORS

	public TPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// Creates a TPoint, copied from an existing TPoint
	public TPoint(TPoint point) {
		this.x = point.x;
		this.y = point.y;
	}

	/// METHODS

	@Override
	public int hashCode() {
		return this.x + this.y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof TPoint)) {
			return false;
		}

		TPoint pt = (TPoint) other;
		return (this.x == pt.x && this.y == pt.y);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}


	/// ATTRIBUTES

	public int x;
	public int y;

}
