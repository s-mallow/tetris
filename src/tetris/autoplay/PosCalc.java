package tetris.autoplay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import tetris.autoplay.APlayerImplementation.Rotate;
import tetris.game.Board;
import tetris.game.MyTetrisFactory;
import tetris.game.TetrisGame;
import tetris.game.TetrisGameView;
import tetris.game.pieces.Piece;

public class PosCalc {

	private static Goal goal;
	private static final List<Rotate> comrotations = new ArrayList<>();
	private static final List<Goal> scores = new ArrayList<>();
	private static Gene currentgene;
	private static List<Gene> genescore = new ArrayList<>();

	public static void main(String[] args) {
		Random ra = new Random();
		for (int i = 0; i < 100; i++)
			genescore.add(new Gene());
		for (int gen = 0; gen < 100; gen++) {
			List<Long> seeds = new ArrayList<>();
			for (int games = 0; games < 10; games++)
				seeds.add(ra.nextLong());
			for (Gene g : genescore) {
				currentgene = g;
				int points = 0;
				for (long seed : seeds) {
					TetrisGame currentgame = MyTetrisFactory.createTetrisGame(new Random(seed));
					AutoPlayer aplayer = MyTetrisFactory.createAutoPlayer(new TetrisGameView(currentgame));
					currentgame.step();
					int steps = 0;
					while (!currentgame.isGameOver() && steps++ < 10000)
						switch (aplayer.getMove()) {
						case DOWN:
							if (!currentgame.moveDown())
								currentgame.step();
							break;
						case LEFT:
							currentgame.moveLeft();
							break;
						case RIGHT:
							currentgame.moveRight();
							break;
						case ROTATE_CCW:
							currentgame.rotatePieceCounterClockwise();
							break;
						case ROTATE_CW:
							currentgame.rotatePieceClockwise();
							break;
						}
					points += currentgame.getPoints();
				}
				g.setScore(points);
			}
			genescore.sort((Gene g1, Gene g2) -> Integer.compare(g2.getScore(), g1.getScore()));
			System.out.println("Best Gene Generation " + gen + ": " + genescore.get(0).getRows() + " "
					+ genescore.get(0).getHeight() + " " + genescore.get(0).getHoles() + " "
					+ genescore.get(0).getBump() + " with Score " + genescore.get(0).getScore() + " from Generation "
					+ genescore.get(0).getGeneration());
			for (int i = 0; i < 30; i++) {
				List<Gene> randoms = new ArrayList<>();
				for (int i2 = 0; i2 < 10; i2++) {
					int rand;
					do {
						rand = ra.nextInt(100 - i);
					} while (randoms.contains(genescore.get(rand)));
					randoms.add(genescore.get(rand));
				}
				randoms.sort((Gene g1, Gene g2) -> Integer.compare(g2.getScore(), g1.getScore()));
				genescore.add(randoms.get(0).breed(randoms.get(1), gen+1));
				genescore.remove(randoms.get(randoms.size() - 1));
			}
		}
		for(int i = 0; i < 30; i++)
		System.out.println(i + ": " + genescore.get(i).getRows() + " "
				+ genescore.get(i).getHeight() + " " + genescore.get(i).getHoles() + " "
				+ genescore.get(i).getBump() + " with Score " + genescore.get(i).getScore() + " from Generation "
				+ genescore.get(i).getGeneration());
		try {
			System.out.println(toString((Serializable)genescore));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }

	public static int getColumn() {
		return goal.column;
	}

	public static Rotate getRotation() {
		return goal.rotation;
	}

	public static void calcNext(TetrisGameView game) {
		comrotations.clear();
		scores.clear();
		Piece piece = game.getCurrentPieceCopy();
		Board board = game.getBoardCopy();
		int row = game.getPieceRow();
		int column = game.getPieceColumn();
		board.removePiece(piece, row, column);
		for (int i = 0; finishrotations(board, piece, column - i); i++)
			;
		for (int i = 1; finishrotations(board, piece, column + i); i++)
			;
		scores.sort((Goal g1, Goal g2) -> g2.compareTo(g1));
//		for (Goal g : scores)
//			System.out.println(g.column + " - " + g.rotation + ": " + g.score + " " + g.minimummoves);
		goal = scores.get(0);
//		System.out.println("hier: " + scores.get(0).score);
	}

	private static boolean finishrotations(Board board, Piece piece, int column) {
		if (board.canAddPiece(piece, 2, column))
			moveDown(board, piece, column, null);
		else
			return false;
		if (!comrotations.contains(Rotate.CW) && board.canAddPiece(piece.getClockwiseRotation(), 2, column)) {
			startMovingBothDirs(board, piece.getClockwiseRotation(), column, Rotate.CW);
			if (!comrotations.contains(Rotate.MIRROR)
					&& board.canAddPiece(piece.getClockwiseRotation().getClockwiseRotation(), 2, column))
				startMovingBothDirs(board, piece.getClockwiseRotation().getClockwiseRotation(), column, Rotate.MIRROR);
		}
		if (!comrotations.contains(Rotate.CCW) && board.canAddPiece(piece.getCounterClockwiseRotation(), 2, column)) {
			startMovingBothDirs(board, piece.getCounterClockwiseRotation(), column, Rotate.CCW);
			if (!comrotations.contains(Rotate.MIRROR)
					&& board.canAddPiece(piece.getCounterClockwiseRotation().getCounterClockwiseRotation(), 2, column))
				startMovingBothDirs(board, piece.getCounterClockwiseRotation().getCounterClockwiseRotation(), column,
						Rotate.MIRROR);
		}
		return true;
	}

	private static void startMovingBothDirs(Board board, Piece piece, int column, Rotate rotation) {
		comrotations.add(rotation);
		for (int i = 0; board.canAddPiece(piece, 2, column - i); i++)
			moveDown(board, piece, column - i, rotation);
		for (int i = 1; board.canAddPiece(piece, 2, column + i); i++)
			moveDown(board, piece, column + i, rotation);
	}

	private static void moveDown(Board board, Piece piece, int column, Rotate rotation) {
		int i = 3;
		while (board.canAddPiece(piece, i, column))
			i++;
		Board tempboard = board.clone();
		tempboard.addPiece(piece, i - 1, column);
		double score = tempboard.deleteCompleteRows() * 0.07575515363489922;
		score += getHighestHeight(tempboard) * -0.03446169192540163;
		score += getHoles(tempboard) * -0.6719033528997505;
		score += getBump(tempboard) * -0.09707746932098558;
		scores.add(new Goal(column, rotation, score, board.getNumberOfColumns()));
	}

	private static int getBump(Board board) {
		int[] columnsum = new int[board.getNumberOfColumns()];
		for (int i = 1; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					columnsum[i2]++;
				}
		int sum = 0;
		for (int i = 1; i < columnsum.length; i++)
			sum += Math.abs(columnsum[i] - columnsum[i - 1]);
		return sum;
	}

	private static double getHoles(Board board) {
		boolean[] gotstone = new boolean[board.getNumberOfColumns()];
		double sum = 0;
		for (int i = 1; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					gotstone[i2] = true;
				} else if (gotstone[i2]) {
					sum++;
				}
		return sum;
	}

	private static int getHighestHeight(Board board) {
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					return board.getNumberOfRows() - i;
				}
		return 0;
	}

	private static double getAverageHeight(Board board) {
		List<Integer> completedcolumns = new ArrayList<>();
		double sum = 0;
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length
					&& completedcolumns.size() != board.getNumberOfColumns(); i2++)
				if (board.getBoard()[i][i2] != null && !completedcolumns.contains(i2)) {
					sum += board.getNumberOfRows() - i;
					completedcolumns.add(i2);
				}
		return sum / board.getNumberOfColumns();
	}

	private static class Goal implements Comparable<Goal> {
		public Goal(int column, Rotate rotation, double score, int numberofcolumns) {
			super();
			this.column = column;
			this.rotation = rotation;
			this.score = score;
			this.minimummoves = Math.abs((numberofcolumns / 2) - column);
			if (rotation != null)
				switch (rotation) {
				case CCW:
					minimummoves += 1;
					break;
				case CW:
					minimummoves += 1;
					break;
				case MIRROR:
					minimummoves += 2;
					break;
				}
		}

		public final int column;
		public final Rotate rotation;
		public final double score;
		private int minimummoves;

		@Override
		public int compareTo(Goal o) {
			if (score != o.score)
				return Double.compare(score, o.score);
			return o.minimummoves - minimummoves;
		}
	}

}
