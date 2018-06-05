import java.util.Arrays;
import java.util.List;

/**
 * Represents a Tetris board -- essentially a 2-d grid of booleans. Supports
 * tetris pieces and row clearing. Has an "undo" feature that allows clients to
 * add and remove pieces efficiently. Does not do any drawing or have any idea
 * of pixels. Instead, just represents the abstract 2-d board.
 */
public class Board {

	/// CONSTRUCTORS
	
	/**
	 * Creates an empty board of the given width and height measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;

		this.grid = new boolean[width][height];
		this.committed = true;

		this.widths  = new int[height];
		this.heights = new int[width];

		this.copy = new Board(this);
	}

    /**
     * Copy constructor
     */
    public Board(Board board){
        this.width  = board.getWidth();
        this.height = board.getHeight();

        this.grid      = board.getEntireGrid();
        this.committed = board.isCommitted();

        this.widths  = board.getWidths();
        this.heights = board.getHeights();

        this.copy = board.getCopy();
    }

	/// METHODS

	/**
	 * Returns the max column height present in the board. For an empty board
	 * this is 0.
	 */
    public int getMaxHeight() {
        int maxHeight = 0;
        int y;
        for (int x = 0; x < width; x++) {
            y = this.height;
            while (y > 0){
                if (grid[x][y - 1])
                    break;
                y--;
            }
            if (y > maxHeight)
                maxHeight = y;
        }
        return maxHeight;
    }

	/**
	 * Given a piece and an x, returns the y value where the piece would come to
	 * rest if it were dropped straight down at that x.
	 * 
	 * <p>
	 * Implementation: use the skirt and the col heights to compute this fast --
	 * O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {

	    List<Integer> pieceSkirt = piece.getSkirt();
        int y = 0;
        int yTmp = 0;

        // parcourir toutes les colonnes où se trouve la pièce
        for (int i = x; i < (x + pieceSkirt.size()); i++){
            yTmp = getColumnHeight(i) - pieceSkirt.get(i - x);
            if (yTmp > y)
                y = yTmp;
        }

        return y;

	}

	/**
	 * Returns the height of the given column -- i.e. the y value of the highest
	 * block + 1. The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
        int y = 0;
        for (y = this.height; y > 0; y--){
            if (grid[x][y - 1])
                break;
        }
        return y;
	}

	/**
	 * Returns the number of filled blocks in the given row.
	 */
	public int getRowWidth(int y) {
        int xCounter = 0;
        for (int x = this.width - 1; x >= 0; x--){
            xCounter += grid[x][y] ? 1 : 0;
        }
        return xCounter;
	}

	/**
	 * Returns true if the given block is filled in the board. Blocks outside of
	 * the valid width/height area always return true.
	 */
	public boolean getGrid(int x, int y) {
	    return this.grid[x][y];
	}

	/**
	 * Attempts to add the body of a piece to the board. Copies the piece blocks
	 * into the board grid. Returns PLACE_OK for a regular placement, or
	 * PLACE_ROW_FILLED for a regular placement that causes at least one row to
	 * be filled.
	 * 
	 * <p>
	 * Error cases: A placement may fail in two ways. First, if part of the
	 * piece may falls out of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 * Or the placement may collide with existing blocks in the grid in which
	 * case PLACE_BAD is returned. In both error cases, the board may be left in
	 * an invalid state. The client can use undo(), to recover the valid,
	 * pre-place state.
	 */
	public int place(Piece piece, int x, int y) {

	    // If the board isn't committed, an error is raised, else, the board is uncommitted to allow undo/commit
        if (!this.committed) {
            throw new RuntimeException("can only place object if the board has been committed");
        } else {
            this.committed = false;
        }

        for (TPoint t : piece.getBody()) {

            // check out of bound
            if (t.x + x < 0 || t.y + y < 0 || t.y + y >= this.height || t.x + x >= this.width)
                return PLACE_OUT_BOUNDS;

            // check collision
            else if (this.grid[t.x + x][t.y + y])
                return PLACE_BAD;

            // place
            this.grid[t.x + x][t.y + y] = true;

            updateWidthsHeights();

            // check row fill
            if(widths[t.y + y] == this.width)
                return PLACE_ROW_FILLED;

        }

        return PLACE_OK;
	}

	/**
	 * Deletes rows that are filled all the way across, moving things above
	 * down. Returns the number of rows cleared.
	 */
	public int clearRows() {
	    int cleared = 0;
	    int rowMovingUp = 0;
	    for (int row = height - 1; row >= 0; row--){
	        // if the row is filled
	        if (this.widths[row] == this.width){

	            // moving everything above down
                for (rowMovingUp = row  + 1; rowMovingUp < height; rowMovingUp++){
                    this.widths[rowMovingUp - 1] = this.widths[rowMovingUp];
                    for (int x = 0; x < this.width; x++)
                        this.grid[x][rowMovingUp - 1] = this.grid[x][rowMovingUp];
                }
                // clearing the first row, as it is always empty after a row clearing
                for (int x = 0; x < this.width; x++)
                    this.grid[x][this.height - 1] = false;

	            // one more cleared
	            cleared++;
            }
        }

        updateWidthsHeights();

        return cleared;
	}

	/**
	 * Reverts the board to its state before up to one place and one
	 * clearRows(); If the conditions for undo() are not met, such as calling
	 * undo() twice in a row, then the second undo() does nothing. See the
	 * overview docs.
	 */
	public void undo() {

		// Only accept one undo/commit
		if (!this.committed){

			// load Board back up
			this.widths = this.copy.getWidths();
			this.heights = this.copy.getHeights();
			this.grid = this.copy.getEntireGrid();

		    this.copy = new Board(this); 	   // recreate back up
		    this.committed = true;             // prevent undo/commit until new action
		}
	}

	/**
	 * Puts the board in the committed state.
	 */
	public void commit() {

		// Only accept one undo/commit
		if (!this.committed){
		    this.committed = true;       // prevent undo/commit until new action
            this.copy = new Board(this); // create backup from current board disposition
		}
	}

	/*
	 * Renders the board state as a big String, suitable for printing. This is
	 * the sort of print-obj-state utility that can help see complex state
	 * change over time. (provided debugging utility)
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = this.height - 1; y >= 0; y--) {
			buff.append('|');
			for (int x = 0; x < this.width; x++) {
				if (getGrid(x, y))
					buff.append('+');
				else
					buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x = 0; x < this.width + 2; x++)
			buff.append('-');
		return buff.toString();
	}

	// Only for unit tests
	protected void updateWidthsHeights() {
		Arrays.fill(this.widths, 0);

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (this.grid[i][j]) {
					this.widths[j] += 1;
					this.heights[i] = Math.max(j + 1, this.heights[i]);
				}
			}
		}
	}


	/// GETTERS

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    /**
     * Returns a copy of the widths array
     * @return widths copy
     */
    public int[] getWidths() { return widths.clone(); }

    /**
     * Returns a copy of the heights array
     * @return heights copy
     */
    public int[] getHeights() { return heights.clone(); }

    /**
     * Returns a copy of the grid 2D array
     * @return grid copy
     */
    public boolean[][] getEntireGrid() {
	    boolean[][] copyGrid = grid.clone();
	    for (int row = 0; row < grid.length; row++){
	        copyGrid[row] = grid[row].clone();
        }
        return copyGrid;
	}

    public boolean isCommitted() { return committed; }

    public Board getCopy() { return copy; }

    /// ATTRIBUTES

	private int width;
	private int height;
	private int[] widths;
	private int[] heights;

	protected boolean[][] grid;
	private boolean committed;

	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	private Board copy;
}
