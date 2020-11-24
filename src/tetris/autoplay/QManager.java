package tetris.autoplay;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tetris.autoplay.AutoPlayer.Move;
import tetris.game.MyTetrisFactory;
import tetris.game.TetrisGame;
import tetris.game.TetrisGameView;
import tetris.view.AutoplayerView;

public class QManager {

	public static void main(String[] args) {
		for (int i = 0; i < TOTAL_GENS; i++) {
			TetrisGame game = MyTetrisFactory.createTetrisGame(new Random());
			AutoPlayer ap = new APlayerImplementation(new TetrisGameView(game));
			game.step();
			int stepcounter = 0;
			while (!game.isGameOver()) {
				if (stepcounter > 5000) {
					ap.gameOver();
					break;
				}
				switch (ap.getMove()) {
				case DOWN:
					if (!game.moveDown())
						game.step();
					break;
				case LEFT:
					game.moveLeft();
					break;
				case RIGHT:
					game.moveRight();
					break;
				case ROTATE_CW:
					game.rotatePieceClockwise();
					break;
				case ROTATE_CCW:
					game.rotatePieceCounterClockwise();
					break;
				}
				stepcounter++;
			}
			System.out.println(i);
		}
		int count = 0;
		for (double qa : qtable.values()) {
			if (qa > 0) {
				System.out.println(qa);
				count++;
			}
		}
		System.out.println(count);
		AutoplayerView.main(new String[0]);
	}

	private static final Map<QAccess, Double> qtable = new HashMap<>(4000);
	private static final Random ra = new Random();
	private static int gen;
	private static QAccess lastmove;

	private static final int TOTAL_GENS = 70000;

	private static final double ALPHA = 0.1;
	private static final double GAMMA = 0.9;

	public static Move getNextMove(Move[] avmoves, State state, double reward) {
		calcLastQ(avmoves, state, reward);
		if (ra.nextInt(TOTAL_GENS) < gen) {
			double maxq = Double.MIN_VALUE;
			Move bestmove = null;
			for (Move mv : avmoves) {
				QAccess qa = new QAccess(state, mv);
				if (qtable.containsKey(qa)) {
					double tempq = qtable.get(qa);
					if (tempq >= maxq) {
						maxq = tempq;
						bestmove = mv;
					}
				} else if (maxq < 0)
					maxq = 0;
			}
			if (bestmove != null)
				return saveMove(bestmove, state);
		}
		return saveMove(avmoves[ra.nextInt(avmoves.length)], state);
	}

	public static void calcLastQ(Move[] avmoves, State state, double reward) {
		if (lastmove != null) {
			double maxq = Double.MIN_VALUE;
			for (Move mv : avmoves) {
				QAccess qa = new QAccess(state, mv);
				if (qtable.containsKey(qa)) {
					double tempq = qtable.get(qa);
					if (tempq >= maxq)
						maxq = tempq;
				} else if (maxq < 0)
					maxq = 0;
			}
			qtable.put(lastmove, qtable.get(lastmove) + ALPHA * (reward + GAMMA * maxq - qtable.get(lastmove)));
			lastmove = null;
		}
	}

	private static Move saveMove(Move move, State state) {
		lastmove = new QAccess(state, move);
		qtable.put(lastmove, 0D);
		return move;
	}

	public static void gameOver(double reward) {
		qtable.put(lastmove, qtable.get(lastmove) + ALPHA * (reward - qtable.get(lastmove)));
		gen++;
	}

}
