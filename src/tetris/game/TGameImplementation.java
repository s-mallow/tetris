package tetris.game;

import java.util.Set;

import tetris.game.pieces.Piece;

public class TGameImplementation implements TetrisGame {
	
	private Set<GameObserver> observers;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Board getBoard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Piece getNextPiece() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfCompletedRows() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPieceColumn() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPieceRow() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isGameOver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveLeft() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean newPiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rotatePieceClockwise() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rotatePieceCounterClockwise() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGameOver() {
		// TODO Auto-generated method stub

	}

	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

}
