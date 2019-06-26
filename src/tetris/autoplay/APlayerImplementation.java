package tetris.autoplay;

import tetris.game.Board;
import tetris.game.GameObserver;
import tetris.game.TetrisGameView;
import tetris.game.pieces.Piece;

public class APlayerImplementation implements GameObserver, AutoPlayer {

	private final TetrisGameView game;
	private Rotate goalrot;
	private Integer goalcol;
	private int movecolsrot;
	private boolean movecompleted;

	public APlayerImplementation(TetrisGameView game) {
		this.game = game;
		this.movecompleted = true;
		game.addObserver(this);
	}

	@Override
	public Move getMove() {
		if (movecompleted) {
			PosCalc.calcNext(game);
			goalrot = PosCalc.getRotation();
			goalcol = PosCalc.getColumn();
			movecompleted = false;
		}
		if (goalrot == null) {
			if (goalcol == game.getPieceColumn())
				return Move.DOWN;
			if (goalcol < game.getPieceColumn())
				return Move.LEFT;
			else
				return Move.RIGHT;
		}
		if (movecolsrot == 0)
			return rotate();
		if (movecolsrot < 0) {
			movecolsrot++;
			return Move.LEFT;
		}
		movecolsrot--;
		return Move.RIGHT;

	}

	private Move rotate() {
		Board board = game.getBoardCopy();
		Piece piece = game.getCurrentPieceCopy();
		board.removePiece(piece, game.getPieceRow(), game.getPieceColumn());
		if (canRotate(game.getPieceColumn())) {
			switch (goalrot) {
			case CCW:
				goalrot = null;
				return Move.ROTATE_CCW;
			case CW:
				goalrot = null;
				return Move.ROTATE_CW;
			case MIRROR:
				if (board.canAddPiece(piece.getClockwiseRotation(), game.getPieceRow(), game.getPieceColumn())
						&& board.canAddPiece(piece.getClockwiseRotation().getClockwiseRotation(), game.getPieceRow(),
								game.getPieceColumn())) {
					goalrot = Rotate.CW;
					return Move.ROTATE_CW;
				}
				goalrot = Rotate.CCW;
				return Move.ROTATE_CCW;
			}
		}
		for (int i = goalcol < game.getPieceColumn() ? -1 : 1; board.canAddPiece(piece, 2,
				game.getPieceColumn() + i); i += goalcol < game.getPieceColumn() ? -1 : 1)
			if (canRotate(game.getPieceColumn() + i)) {
				movecolsrot = i;
				break;
			}
		if (movecolsrot == 0)
			for (int i = goalcol < game.getPieceColumn() ? 1 : -1; board.canAddPiece(piece, 2,
					game.getPieceColumn() + i); i += goalcol < game.getPieceColumn() ? 1 : -1)
				if (canRotate(game.getPieceColumn() + i)) {
					movecolsrot = i;
					break;
				}
		if (movecolsrot < 0) {
			movecolsrot++;
			return Move.LEFT;
		}
		movecolsrot--;
		return Move.RIGHT;
	}

	private boolean canRotate(int i) {
		Board board = game.getBoardCopy();
		Piece piece = game.getCurrentPieceCopy();
		board.removePiece(piece, game.getPieceRow(), game.getPieceColumn());
		if (board.canAddPiece(piece.getClockwiseRotation(), 2, i)) {
			if (goalrot == Rotate.CW)
				return true;
			else if (goalrot == Rotate.MIRROR
					&& board.canAddPiece(piece.getClockwiseRotation().getClockwiseRotation(), 2, i))
				return true;
		}
		if (board.canAddPiece(piece.getCounterClockwiseRotation(), 2, i)) {
			if (goalrot == Rotate.CCW)
				return true;
			else if (goalrot == Rotate.MIRROR && board.canAddPiece(
					piece.getCounterClockwiseRotation().getCounterClockwiseRotation(), 2, i))
				return true;
		}
		return false;
	}

	@Override
	public void rowsCompleted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void piecePositionChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pieceLanded() {
		movecompleted = true;
	}

	@Override
	public void gameOver() {
		// TODO Auto-generated method stub

	}

	public enum Rotate {
		CW, CCW, MIRROR;
	}

}
