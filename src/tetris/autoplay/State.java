package tetris.autoplay;

import java.util.Arrays;

import tetris.game.pieces.Piece.PieceType;

public class State {
	
	private final boolean[][] board;
	private final int row;
	private final int column;
	private final PieceType currenttype;

	public State(boolean[][] board, int row, int column, PieceType currenttype) {
		this.board = board;
		this.row = row;
		this.column = column;
		this.currenttype = currenttype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		result = prime * result + column;
		result = prime * result + ((currenttype == null) ? 0 : currenttype.hashCode());
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		if (column != other.column)
			return false;
		if (currenttype != other.currenttype)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
}
