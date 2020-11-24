package tetris.autoplay;

import tetris.autoplay.AutoPlayer.Move;

public class QAccess {
	
	State state;
	Move move;
	
	public QAccess(State state, Move move) {
		this.state = state;
		this.move = move;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((move == null) ? 0 : move.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		QAccess other = (QAccess) obj;
		if (move != other.move)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

}
