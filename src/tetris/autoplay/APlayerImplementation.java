package tetris.autoplay;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

import tetris.game.Board;
import tetris.game.GameObserver;
import tetris.game.TetrisGameView;
import tetris.game.pieces.Piece;
import tetris.game.pieces.Piece.PieceType;

public class APlayerImplementation implements AutoPlayer, GameObserver {

	private final TetrisGameView game;
	private long oldpoints;
	private double nextreward;
	private int turnssamepiece = 0;
	private boolean down;

	public APlayerImplementation(TetrisGameView game) {
		this.game = game;
		game.addObserver(this);
		nextreward = 0;
		oldpoints = 0;
		down = false;
	}

	@Override
	public void rowsCompleted() {
//		System.out.println(game.getPoints() - oldpoints);
		nextreward += (game.getPoints() - oldpoints) / 100;
		oldpoints = game.getPoints();
	}

	@Override
	public void piecePositionChanged() {
		turnssamepiece++;
		if (turnssamepiece > 100)
			nextreward -= 10;
	}

	@Override
	public void pieceLanded() {
		down = false;
		turnssamepiece = 0;
	}

	@Override
	public void gameOver() {
		QManager.gameOver(nextreward - 100);
	}

	@Override
	public Move getMove() {
		if(down)
			return Move.DOWN;
		Board board = game.getBoardCopy();
		Piece piece = game.getCurrentPieceCopy();
		boolean[][] boolboard = new boolean[board.getNumberOfRows()][board.getNumberOfColumns()];
		int startingrow = game.getPieceRow() - piece.getRotationPoint().getRow() + piece.getHeight();
		for (int i = startingrow; i < board.getBoard().length; i++) {
			PieceType[] row = board.getBoard()[i];
			for (int i2 = 0; i2 < row.length; i2++)
				boolboard[i][i2] = row[i2] == null ? false : true;
		}
		board.removePiece(piece, game.getPieceRow(), game.getPieceColumn());
		List<Move> avmoves = new ArrayList<>();
		avmoves.add(Move.DOWN);
		if (board.canAddPiece(piece, game.getPieceRow(), game.getPieceColumn() + 1))
			avmoves.add(Move.RIGHT);
		if (board.canAddPiece(piece, game.getPieceRow(), game.getPieceColumn() - 1))
			avmoves.add(Move.LEFT);
		if (board.canAddPiece(piece.getClockwiseRotation(), game.getPieceRow(), game.getPieceColumn()))
			avmoves.add(Move.ROTATE_CW);
		if (board.canAddPiece(piece.getCounterClockwiseRotation(), game.getPieceRow(), game.getPieceColumn()))
			avmoves.add(Move.ROTATE_CCW);
		Move nextmove = QManager.getNextMove(avmoves.toArray(Move[]::new), new State(boolboard, game.getPieceRow(), game.getPieceColumn(), piece.getPieceType()), nextreward);
		nextreward = 0;
		if (nextmove == Move.DOWN)
			down = true;
		return nextmove;
	}

}
