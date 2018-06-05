import java.util.Arrays;
import java.util.List;

import java.util.ArrayList;


/**
 * An immutable representation of a tetris piece in a particular rotation. Each
 * piece is defined by the blocks that make up its body.
 * 
 * Typical client code looks like...
 * 
 * <pre>
 * Piece pyra = new Piece(PYRAMID_STR); // Create piece from string
 * int width = pyra.getWidth(); // 3
 * Piece pyra2 = pyramid.computeNextRotation(); // get rotation
 * 
 * Piece[] pieces = Piece.getPieces(); // the array of all root pieces
 * </pre>
 */
public class Piece {

	/// CONSTRUCTORS

	/**
	 * Defines a new piece given a TPoint[] array of its body. Makes its own
	 * copy of the array and the TPoints inside it.
	 */
	public Piece(List<TPoint> points) {
	    this.body = new ArrayList<TPoint>(points);
		this.width = getMaxCoordinateX(points) + 1;
		this.height = getMaxCoordinateY(points) + 1;
		this.skirt = calculateSkirt(points);
	}
	
	/**
	 * Alternate constructor, takes a String with the x,y body points all
	 * separated by spaces, such as "0 0 1 0 2 0 1 1". (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/**
	 * Creates a copy of the piece given in argument.
	 * @param piece : piece to copy
	 */
	public Piece(Piece piece) {
	    this(piece.getBody());
	}


	/// METHODS


	/**
	 * Given a string of x,y pairs ("0 0 0 1 0 2 1 0"), parses the points into a
	 * List of TPoint. (Provided code)
	 */
	private static List<TPoint> parsePoints(String rep) {

		// if rep isn't a string of x,y pair, terminates
		if (rep.replaceAll("\\s", "").length() % 2 != 0){
			System.err.println("Error in parsePoints, argument rep isn't a group of (x,y) pairs.");
			System.err.println("Argument rep = " + rep);
			System.exit(1);
		}

	    ArrayList<TPoint> points = new ArrayList<TPoint>();
	    String[] separatedPoints = rep.split("\\s");

	    int pairCounter = 0;
		TPoint pair = new TPoint(0,0); // pair we'll add to points

	    // for each point
	    for(String point: separatedPoints){

	    	// If the string isn't a digit, terminates
	    	if ( point.length() != 1 || !Character.isDigit(point.charAt(0)) ){
				System.err.println("Error in parsePoints, argument rep isn't a group of (x,y) pairs.");
				System.err.println("Argument rep = " + rep);
				System.exit(1);

			} else {

	    		// get the digit
	    		int digit = Integer.parseInt(point);

	    		// adds the digit to the right position in the x,y pair
	    		if (pairCounter == 0)      pair.x = digit;
	    		else if (pairCounter == 1) pair.y = digit;

	    		pairCounter++;

	    		// if pair counter reachs 2, we add the new pair to the list
	    		if (pairCounter == 2) {
					pairCounter = 0;
					points.add(pair);
					pair = new TPoint(0,0);
				}
			}
		}

		return points;
	}
	
	/**
	 * Returns the width of the piece measured in blocks.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the piece measured in blocks.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns a reference to the piece's body. The caller should not modify this
	 * list.
	 */
	public List<TPoint> getBody() {
		return this.body;
	}

	/**
	 * Returns a reference to the piece's skirt. For each x value across the
	 * piece, the skirt gives the lowest y value in the body. This is useful for
	 * computing where the piece will land. The caller should not modify this
	 * list.
	 */
	public List<Integer> getSkirt() {
		return this.skirt;
	}

	/**
	 * Returns a new piece that is 90 degrees counter-clockwise rotated
	 */
	public Piece computeNextRotation() {

		List<TPoint> rotated = new ArrayList<>();

		for (TPoint p : this.body) {
			rotated.add(new TPoint(this.height - 1 -p.y,p.x));
		}

		return new Piece(rotated);
	}

	/**
	 * Returns true if two pieces are the same -- their bodies contain the same
	 * points. Interestingly, this is not the same as having exactly the same
	 * body arrays, since the points may not be in the same order in the bodies.
	 * Used internally to detect if two rotations are effectively the same.
	 */
	public boolean equals(Object obj) {

		// check the type
		Piece objPiece;
		try {
			objPiece = (Piece) obj;
		} catch (ClassCastException e){
			return false;
		}

		List<TPoint> objPiecePoints = objPiece.getBody();

		// tests the size
		if (objPiecePoints.size() == this.body.size()) {
			// tests existence of each item
			for (TPoint point : objPiecePoints) {
				if (!this.body.contains(point))
					return false;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a string representing the piece.
	 * @return string
	 */
	public String toString() {

	    String str = "";

	    int counter = 0;
	    for (TPoint point: this.body) {
			str += point.x + " ";
			str += point.y;

			if (counter != this.body.size())
				str += " ";

			counter++;
		}

		return str;
	}

	/**
	 * Returns an array containing the first rotation of each of the 7 standard
	 * tetris pieces in the order STICK, L1, L2, S1, S2, SQUARE, PYRAMID. The
	 * next (counterclockwise) rotation can be obtained from each piece with the
	 * message. In this way, the client can iterate
	 * through all the rotations until eventually getting back to the first
	 * rotation. (provided code)
	 */
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces == null) {
			Piece.pieces = new Piece[] { 
					new Piece(STICK_STR), 
					new Piece(L1_STR),
					new Piece(L2_STR), 
					new Piece(S1_STR),
					new Piece(S2_STR),
					new Piece(SQUARE_STR),
					new Piece(PYRAMID_STR)};
		}

		return Piece.pieces;
	}


	/*
		Methods used to construct the object
	 */


	/**
	 * Returns the maximum X coordinate in the list of points given in argument.
	 * @param points : point list to analyse. Returns -1 if the list is empty.
	 * @return max coordinate x
	 */
	public static int getMaxCoordinateX(List<TPoint> points){
		if (points.isEmpty())
			return -1;

		// assumes 0 is the max
		int max = 0;
		// for each point, checks whether its x is the bigger ever seen.
		for (TPoint point: points)
			if (max < point.x) max = point.x;

		return max;
	}

	/**
	 * Returns the maximum Y coordinate in the list of points given in argument.
	 * @param points : point list to analyse. Returns -1 if the list is empty.
	 * @return max coordinate Y
	 */
	public static int getMaxCoordinateY(List<TPoint> points){
		if (points.isEmpty())
			return -1;

		// assumes 0 is the max
		int max = 0;
		// for each point, checks whether its y is the bigger ever seen.
		for (TPoint point: points)
			if (max < point.y) max = point.y;

		return max;
	}

	/**
	 * Returns the list of points whose abscissa is columnIndex.
	 * @param points : list of points to analyse
	 * @param columnIndex : index of the column to return
	 * @return column at columnIndex
	 */
	public static List<TPoint> getColumn(List<TPoint> points, int columnIndex){
		ArrayList<TPoint> column = new ArrayList<>();

		for (TPoint point: points){
			if (point.x == columnIndex)
				column.add(point);
		}

		return column;
	}

	/**
	 * Returns the lowest point of the list, that is the point with the
	 * lowest y ordinate. If the list contains several points with the lowest y,
	 * the first found point is returned. If the list is empty, returns (-1, -1).
	 * @param points : list of point to analyse.
	 * @return : lowest point
	 */
	public static TPoint getLowestPoint(List<TPoint> points){
		if (points.isEmpty())
			return new TPoint(-1,-1);

		// assumes lowest is the first
		TPoint lowest = points.get(0);

		// for all points, tests if it's lower than the current lowest
		for (TPoint point: points)
			if (point.y < lowest.y)
				lowest = point;

		return lowest;
	}

	/**
	 * Calculates and returns the skirt of a piece given a list
	 * of TPoint.
	 * @param points : points representing the piece.
	 */
	public static List<Integer> calculateSkirt(List<TPoint> points){
		List<Integer> skirt = new ArrayList<>();

		// for x in 0 -> width
		for (int i = 0; i <= getMaxCoordinateX(points); i++){
			// gets column at x == i, gets the lowest point of the column, adds its ordinate to skirt
			skirt.add(getLowestPoint(getColumn(points, i)).y);
		}

		return skirt;
	}

	/// ATTRIBUTES


	// String constants for the standard 7 Tetris pieces
	public static final String STICK_STR = "0 0 0 1 0 2 0 3";
	public static final String L1_STR = "0 0 0 1 0 2 1 0";
	public static final String L2_STR = "0 0 1 0 1 1 1 2";
	public static final String S1_STR = "0 0 1 0 1 1 2 1";
	public static final String S2_STR = "0 1 1 1 1 0 2 0";
	public static final String SQUARE_STR = "0 0 0 1 1 0 1 1";
	public static final String PYRAMID_STR = "0 0 1 0 1 1 2 0";

	private List<TPoint> body;
	private List<Integer> skirt;
	private int width;
	private int height;

	static private Piece[] pieces; // singleton static array of first rotations

}
