package tetris.game.pieces;

import java.util.Arrays;

public class PImplementation implements Piece {

	private final PieceType type;
	private final boolean[][] body;
	private final Point rotationpoint;

	public PImplementation(PieceType type) {
		this.type = type;
		switch (type) {
		case L:
			body = new boolean[][] { { true, false }, { true, false }, { true, true } };
			rotationpoint = new Point(1, 0);
			break;
		case J:
			body = new boolean[][] { { false, true }, { false, true }, { true, true } };
			rotationpoint = new Point(1, 1);
			break;
		case T:
			body = new boolean[][] { { true, true, true }, { false, true, false } };
			rotationpoint = new Point(0, 1);
			break;
		case O:
			body = new boolean[][] { { true, true }, { true, true } };
			rotationpoint = new Point(1, 1);
			break;
		case I:
			body = new boolean[][] { { true }, { true }, { true }, { true } };
			rotationpoint = new Point(1, 0);
			break;
		case Z:
			body = new boolean[][] { { true, true, false }, { false, true, true } };
			rotationpoint = new Point(1, 1);
			break;
		case S:
			body = new boolean[][] { { false, true, true }, { true, true, false } };
			rotationpoint = new Point(1, 1);
			break;
		default:
			body = null;
			rotationpoint = null;
		}
	}

	private PImplementation(PImplementation oldpiece, int rotation) {
		type = oldpiece.type;
		switch (rotation) {
		case 0:
			body = new boolean[oldpiece.getHeight()][];
			for (int i = 0; i < body.length; i++)
				body[i] = oldpiece.body[i].clone();
			rotationpoint = new Point(oldpiece.rotationpoint.getRow(), oldpiece.rotationpoint.getColumn());
			break;
		case 1:
			body = new boolean[oldpiece.getWidth()][oldpiece.getHeight()];
			for (int icol = 0; icol < body.length; icol++)
				for (int irow = 0; irow < body[0].length; irow++)
					body[icol][irow] = oldpiece.body[(oldpiece.getHeight() - 1) - irow][icol];
			rotationpoint = new Point(oldpiece.rotationpoint.getColumn(),
					(oldpiece.getHeight() - 1) - oldpiece.rotationpoint.getRow());
			break;
		case -1:
			body = new boolean[oldpiece.getWidth()][oldpiece.getHeight()];
			for (int icol = 0; icol < body.length; icol++)
				for (int irow = 0; irow < body[0].length; irow++)
					body[icol][irow] = oldpiece.body[irow][(oldpiece.getWidth() - 1) - icol];
			rotationpoint = new Point((oldpiece.getWidth() - 1) - oldpiece.rotationpoint.getColumn(),
					oldpiece.rotationpoint.getRow());
			break;
		default:
			body = null;
			rotationpoint = null;
		}
	}

	@Override
	public int getWidth() {
		return body[0].length;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return body.length;
	}

	@Override
	public boolean[][] getBody() {
		return body;
	}

	@Override
	public Piece getClockwiseRotation() {
		return new PImplementation(this, 1);
	}

	@Override
	public Piece getCounterClockwiseRotation() {
		return new PImplementation(this, -1);
	}

	@Override
	public Point getRotationPoint() {
		return rotationpoint;
	}

	@Override
	public PieceType getPieceType() {
		return type;
	}

	@Override
	public Piece clone() {
		return new PImplementation(this, 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(body);
		result = prime * result + ((rotationpoint == null) ? 0 : rotationpoint.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		PImplementation other = (PImplementation) obj;
		if (!Arrays.deepEquals(body, other.body))
			return false;
		if (rotationpoint == null) {
			if (other.rotationpoint != null)
				return false;
		} else if (!rotationpoint.equals(other.rotationpoint))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
