package tetris.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import tetris.game.pieces.Piece;
import tetris.game.pieces.PieceFactory;

public class TGameImplementation implements TetrisGame {

	private final Set<GameObserver> observers;
	private final Board board;
	private final PieceFactory factory;
	private Piece currentpiece;
	private int row;
	private int column;
	private Piece nextpiece;
	private int completedrows;
	private long points;
	private boolean gameover;

	public TGameImplementation(Random ra) {
		observers = new HashSet<>();
		board = MyTetrisFactory.createBoard(MyTetrisFactory.DEFAULT_ROWS, MyTetrisFactory.DEFAULT_COLUMNS);
		factory = MyTetrisFactory.createPieceFactory(ra);
		completedrows = 0;
		points = 0;
		nextpiece = factory.getNextRandomPiece();
		gameover = false;
	}

	@Override
	public void addObserver(GameObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(GameObserver observer) {
		observers.remove(observer);
	}

	@Override
	public Piece getCurrentPiece() {
		return currentpiece;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public Piece getNextPiece() {
		return nextpiece;
	}

	@Override
	public int getNumberOfCompletedRows() {
		return completedrows;
	}

	@Override
	public int getPieceColumn() {
		return column;
	}

	@Override
	public int getPieceRow() {
		return row;
	}

	@Override
	public long getPoints() {
		return points;
	}

	@Override
	public boolean isGameOver() {
		return gameover;
	}

	@Override
	public boolean moveDown() {
		return movePiece(1, 0);
	}

	@Override
	public boolean moveLeft() {
		return movePiece(0, -1);
	}

	@Override
	public boolean moveRight() {
		return movePiece(0, 1);
	}

	private boolean movePiece(int vertical, int horizontal) {
		if (currentpiece == null)
			return false;
		if (!board.canRemovePiece(currentpiece, row, column))
			return false;
		board.removePiece(currentpiece, row, column);
		boolean moved = false;
		if (board.canAddPiece(currentpiece, row + vertical, column + horizontal)) {
			row = row + vertical;
			column = column + horizontal;
			moved = true;
		}
		board.addPiece(currentpiece, row, column);
		if(moved)
			callObservers(GameObserver::piecePositionChanged);
		return moved;
	}

	@Override
	public boolean newPiece() {
		if (currentpiece != null)
			callObservers(GameObserver::pieceLanded);
		int completednow = board.deleteCompleteRows();
		if (completednow > 0) {
			points += completednow == 4 ? 1000 : 100 + (completednow - 1) * 200;
			completedrows += completednow;
			callObservers(GameObserver::rowsCompleted);
		}
		if (!board.canAddPiece(nextpiece, 2, board.getNumberOfColumns() / 2))
			return false;
		row = 2;
		column = board.getNumberOfColumns() / 2;
		board.addPiece(nextpiece, row, column);
		currentpiece = nextpiece;
		nextpiece = factory.getNextRandomPiece();
		return true;
	}

	@Override
	public boolean rotatePieceClockwise() {
		return rotatePiece(currentpiece.getClockwiseRotation());
	}

	@Override
	public boolean rotatePieceCounterClockwise() {
		return rotatePiece(currentpiece.getCounterClockwiseRotation());
	}

	private boolean rotatePiece(Piece rotatedpiece) {
		if (!board.canRemovePiece(currentpiece, row, column))
			return false;
		board.removePiece(currentpiece, row, column);
		if (board.canAddPiece(rotatedpiece, row, column)) {
			currentpiece = rotatedpiece;
		}
		board.addPiece(currentpiece, row, column);
		if (rotatedpiece == currentpiece)
			callObservers(GameObserver::piecePositionChanged);
		return rotatedpiece == currentpiece;
	}

	@Override
	public void setGameOver() {
		gameover = true;
		callObservers(GameObserver::gameOver);
	}

	@Override
	public void step() {
		if (isGameOver())
			return;
		if (!moveDown() && !newPiece()) {
			setGameOver();
		}
	}

	private void callObservers(Consumer<GameObserver> obsup) {
		for(GameObserver obs : observers)
			obsup.accept(obs);
	}

}
