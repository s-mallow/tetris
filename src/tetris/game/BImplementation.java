package tetris.game;

import tetris.game.pieces.Piece;
import tetris.game.pieces.Piece.PieceType;
import tetris.game.pieces.Point;

public class BImplementation implements Board {

	private final PieceType[][] board;

	public BImplementation(int rows, int columns) {// TODO assert row,column > 0
		board = new PieceType[rows][columns];
	}

	private BImplementation(BImplementation oldboard) {
		board = new PieceType[oldboard.getNumberOfRows()][];
		for (int i = 0; i < board.length; i++)
			board[i] = oldboard.board[i].clone();
	}

	@Override
	public PieceType[][] getBoard() {
		return board;
	}

	@Override
	public int getNumberOfRows() {
		return board.length;
	}

	@Override
	public int getNumberOfColumns() {
		return board[0].length;
	}

	@Override
	public void addPiece(Piece piece, int row, int column) {
		if (!canAddPiece(piece, row, column))
			throw new IllegalArgumentException();
		boolean body[][] = piece.getBody();
		int startrow = row - piece.getRotationPoint().getRow();
		int startcolumn = column - piece.getRotationPoint().getColumn();
		for (int itrow = 0; itrow < piece.getHeight(); itrow++)
			for (int itcolumn = 0; itcolumn < piece.getWidth(); itcolumn++)
				if (body[itrow][itcolumn])
					board[startrow + itrow][startcolumn + itcolumn] = piece.getPieceType();

	}

	@Override
	public boolean canAddPiece(Piece piece, int row, int column) {
		Point point = piece.getRotationPoint();
		if (point == null || row - point.getRow() < 0 || column - point.getColumn() < 0
				|| row + ((piece.getHeight() - 1) - point.getRow()) >= getNumberOfRows()
				|| column + ((piece.getWidth() - 1) - point.getColumn()) >= getNumberOfColumns())
			return false;
		boolean body[][] = piece.getBody();
		int startrow = row - point.getRow();
		int startcolumn = column - point.getColumn();
		for (int itrow = 0; itrow < piece.getHeight(); itrow++)
			for (int itcolumn = 0; itcolumn < piece.getWidth(); itcolumn++)
				if (body[itrow][itcolumn])
					if (board[startrow + itrow][startcolumn + itcolumn] != null)
						return false;
		return true;
	}

	@Override
	public void removePiece(Piece piece, int row, int column) {
		if (!canRemovePiece(piece, row, column))
			throw new IllegalArgumentException();
		boolean body[][] = piece.getBody();
		int startrow = row - piece.getRotationPoint().getRow();
		int startcolumn = column - piece.getRotationPoint().getColumn();
		for (int itrow = 0; itrow < piece.getHeight(); itrow++)
			for (int itcolumn = 0; itcolumn < piece.getWidth(); itcolumn++)
				if (body[itrow][itcolumn])
					board[startrow + itrow][startcolumn + itcolumn] = null;

	}

	@Override
	public boolean canRemovePiece(Piece piece, int row, int column) {
		Point point = piece.getRotationPoint();
		if (point == null || row - point.getRow() < 0 || column - point.getColumn() < 0
				|| row + ((piece.getHeight() - 1) - point.getRow()) >= getNumberOfRows()
				|| column + ((piece.getWidth() - 1) - point.getColumn()) >= getNumberOfColumns())
			return false;
		boolean body[][] = piece.getBody();
		int startrow = row - point.getRow();
		int startcolumn = column - point.getColumn();
		for (int itrow = 0; itrow < piece.getHeight(); itrow++)
			for (int itcolumn = 0; itcolumn < piece.getWidth(); itcolumn++)
				if (body[itrow][itcolumn])
					if (board[startrow + itrow][startcolumn + itcolumn] != piece.getPieceType())
						return false;
		return true;
	}

	@Override
	public int deleteCompleteRows() {
		int removed = 0;
		for (int itrow = 0; itrow < getNumberOfRows(); itrow++)
			for (int itcolumn = 0; itcolumn < getNumberOfColumns(); itcolumn++) {
				if (board[itrow][itcolumn] == null)
					break;
				if (itcolumn + 1 == getNumberOfColumns()) {
					shiftDown(itrow);
					removed++;
				}
			}
		return removed;
	}

	private void shiftDown(int itrow) {
		for (int i = itrow; i > 0; i--)
			board[i] = board[i - 1];
		board[0] = new PieceType[getNumberOfColumns()];
	}

	@Override
	public Board clone() {
		return new BImplementation(this);
	}

}
