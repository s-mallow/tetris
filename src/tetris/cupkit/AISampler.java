package tetris.cupkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import tetris.autoplay.AutoPlayer;
import tetris.autoplay.AutoPlayer.Move;
import tetris.game.MyTetrisFactory;
import tetris.game.TetrisGame;
import tetris.game.TetrisGameView;

public class AISampler {
	private static List<Long> points = new CopyOnWriteArrayList<>();
	
	public long playout(long seed) throws IllegalArgumentException {
		TetrisGame game = MyTetrisFactory.createTetrisGame(new Random(seed));
		game.step();
		AutoPlayer player = MyTetrisFactory.createAutoPlayer(new TetrisGameView(game));
		long points = playout(game, player);
//		System.out.println("Seed                " + seed);
		return points;
	}

	public long playout(TetrisGame game, AutoPlayer player) throws IllegalArgumentException {
		// limits
		final long stepLimit = 5000;
		// ms
		final long stepThinkTime = 100;
		// ms to ns
		final long totalThinkTime = stepLimit * stepThinkTime * 1000000;

		// tracking
		long elapsedThinkTime = 0;

		long startTime = System.nanoTime();
		long steps;
		for (steps = 0; steps < stepLimit && !game.isGameOver();) {
			// query next AI decision
			Move move;
			{
				long startThinkTime = System.nanoTime();
				move = player.getMove();
				long endThinkTime = System.nanoTime();
				elapsedThinkTime += endThinkTime - startThinkTime;
			}

			// enforce think time limit
			if (elapsedThinkTime >= totalThinkTime) {
				break;
			}

			// execute
			boolean valid = true;
			switch (move) {
			case DOWN:
				game.step();
				steps++;
				break;
			case LEFT:
				valid = game.moveLeft();
				break;
			case RIGHT:
				valid = game.moveRight();
				break;
			case ROTATE_CCW:
				valid = game.rotatePieceCounterClockwise();
				break;
			case ROTATE_CW:
				valid = game.rotatePieceClockwise();
				break;
			default:
				throw new IllegalArgumentException("Unknown move kind");
			}

			if (!valid)
				throw new IllegalArgumentException("AI attempted invalid move");
		}
		long endTime = System.nanoTime();

		long elapsedTime = endTime - startTime;
		double elapsedMillis = elapsedTime / 1000000.0;
		double elapsedThinkMillis = elapsedThinkTime / 1000000.0;

//		System.out.println("---------------------------------------");
//		System.out.println("Points              " + game.getPoints());
//		System.out.println("Steps               " + steps);
//		System.out.println("Elapsed time        " + elapsedMillis + " ms");
//		System.out.println("Elapsed think time  " + elapsedThinkMillis + " ms");
//		System.out.println("AvgTime per step    " + (elapsedMillis / steps) + " ms");
		points.add(game.getPoints());
		return game.getPoints();
	}

	public static void main(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool();
		Random seeder = new Random(524323474361L);
		for (long i = 0; i < 500; ++i) {
			exec.execute(() ->
			new AISampler().playout(seeder.nextInt()));
		}
		exec.shutdown();
		try {
			exec.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		points.sort(Long::compare);
		System.out.println(points.get((int)(points.size() * 0.01)));
		System.out.println(points.get((int)(points.size() * 0.05)));
		System.out.println(points.get((int)(points.size() * 0.1)));
		System.out.println(points.get((int)(points.size() * 0.15)));
		System.out.println(points.get((int)(points.size() * 0.2)));
		System.out.println(points.get((int)(points.size() * 0.3)));
		System.out.println(points.get((int)(points.size() * 0.4)));
		System.out.println(points.get((int)(points.size() * 0.5)));
	}
}
