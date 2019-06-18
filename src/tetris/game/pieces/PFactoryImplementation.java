package tetris.game.pieces;

import java.util.Random;

import tetris.game.pieces.Piece.PieceType;

public class PFactoryImplementation implements PieceFactory {
	
	private Random ra;
	
	public PFactoryImplementation(Random ra) {
		this.ra = ra;
	}

	@Override
	public Piece getIPiece() {
		return new PImplementation(PieceType.I);
	}

	@Override
	public Piece getJPiece() {
		return new PImplementation(PieceType.J);
	}

	@Override
	public Piece getLPiece() {
		return new PImplementation(PieceType.L);
	}

	@Override
	public Piece getOPiece() {
		return new PImplementation(PieceType.O);
	}

	@Override
	public Piece getSPiece() {
		return new PImplementation(PieceType.S);
	}

	@Override
	public Piece getZPiece() {
		return new PImplementation(PieceType.Z);
	}

	@Override
	public Piece getTPiece() {
		return new PImplementation(PieceType.T);
	}

	@Override
	public Piece getNextRandomPiece() {
		PieceType[] types = PieceType.values();
		return new PImplementation(types[ra.nextInt(types.length)]);
	}

}
