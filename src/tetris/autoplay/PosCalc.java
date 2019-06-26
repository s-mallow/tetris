package tetris.autoplay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
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

	private static final int GAMES = 5;

	public static void main(String[] args) {
		Random ra = new Random();
		for (int i = 0; i < 100; i++)
			genescore.add(new Gene());
//		Object o;
//		try {
//			o = fromString(
//					"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");
//			if (o instanceof List<?>)
//				genescore = ((List<Gene>) o);
//		} catch (ClassNotFoundException | IOException e) {
//			e.printStackTrace();
//			return;
//		}
		for (int gen = 0; gen < 25; gen++) {
			List<Long> seeds = new ArrayList<>();
			for (int games = 0; games < GAMES; games++)
				seeds.add(ra.nextLong());
			for (Gene g : genescore) {
				currentgene = g;
				int points = 0;
				for (long seed : seeds) {
					TetrisGame currentgame = MyTetrisFactory.createTetrisGame(new Random(seed));
					AutoPlayer aplayer = MyTetrisFactory.createAutoPlayer(new TetrisGameView(currentgame));
					currentgame.step();
					int steps = 0;
					while (!currentgame.isGameOver() && steps++ < 5000)
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
				points = points / GAMES;
				g.setScore(points);
			}
			genescore.sort((Gene g1, Gene g2) -> Integer.compare(g2.getScore(), g1.getScore()));
			System.out.print("Best Gene Generation " + gen + ": ");
			for (int i = 0; i < genescore.get(0).getFeatures().length; i++) {
				if ((i + 1) % 6 == 0)
					System.out.println();
				System.out.print(genescore.get(0).getFeatures()[i] + " ");
			}
			System.out.println("with Score " + genescore.get(0).getScore() + " from Generation "
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
				genescore.add(randoms.get(0).breed(randoms.get(1), gen + 1));
				genescore.remove(randoms.get(randoms.size() - 1));
			}
		}
		for (int loop = 0; loop < 30; loop++) {
			System.out.print(loop + ": ");
			for (int i = 0; i < genescore.get(loop).getFeatures().length; i++) {
				if ((i + 1) % 7 == 0)
					System.out.println();
				System.out.print(genescore.get(loop).getFeatures()[i] + " ");
			}
			System.out.println("with Score " + genescore.get(loop).getScore() + " from Generation "
					+ genescore.get(loop).getGeneration());
		}
		try {
			System.out.println(toString((Serializable) genescore));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Write the object to a Base64 string. */
	private static String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	private static Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	public static int getColumn() {
		return goal.column;
	}

	public static Rotate getRotation() {
		return goal.rotation;
	}

	public static void calcNext(TetrisGameView game) {
		if (currentgene == null) {
			Object o;
			try {
				o = fromString(
						"");
				if (o instanceof List<?>)
					currentgene = (Gene) ((List<?>) o).get(0);
				System.out.println(currentgene.getGeneration());
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		double totalscore = 0;
		int[] featscores = new int[] { tempboard.deleteCompleteRows(), getHighestHeight(tempboard), getHoles(tempboard),
				getBump(tempboard), walltouches(tempboard), floortouches(tempboard) };
		for (int feat = 0; feat < currentgene.getFeatures().length; feat++)
			totalscore += featscores[feat] * currentgene.getFeatures()[feat];
		scores.add(new Goal(column, rotation, totalscore, board.getNumberOfColumns()));
	}

	private static int floortouches(Board board) {
		int sum = 0;
		for (Piece.PieceType piece : board.getBoard()[board.getBoard().length - 1])
			if (piece != null)
				sum++;
		return sum;
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

	private static int getHoles(Board board) {
		boolean[] gotstone = new boolean[board.getNumberOfColumns()];
		int sum = 0;
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

	private static int walltouches(Board board) {
		int sum = 0;
		for (int i = 0; i < board.getBoard().length; i++) {
			if (board.getBoard()[i][0] != null)
				sum++;
			if (board.getBoard()[i][board.getBoard()[0].length - 1] != null)
				sum++;
		}
		return sum;
	}

	private static double getAddedHeight(Board board) {
		int sum = 0;
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					sum += board.getNumberOfRows() - i;
				}
		return sum;
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
