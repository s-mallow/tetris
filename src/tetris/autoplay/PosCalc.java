package tetris.autoplay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import tetris.autoplay.APlayerImplementation.Rotate;
import tetris.game.Board;
import tetris.game.MyTetrisFactory;
import tetris.game.TetrisGame;
import tetris.game.TetrisGameView;
import tetris.game.pieces.Piece;

public class PosCalc {

	private Goal goal;
	private final List<Rotate> comrotations = new ArrayList<>();
	private final List<Goal> scores = new ArrayList<>();
	private Gene currentgene;
	private static List<Gene> genescore = new ArrayList<>();
	private static double[] currents = { -4.142988626877937E-4, 3.521093303097849E-5, -1.4412162894339753, -0.32922792947462715, 0.004700040419676516,
			3.689885702813747E-4, 0.40635226243973527, 0.0013935541086734643, -1.067712594813029E-8, -0.8368018955048833, -1.7216819234477947, 
			1.5175787269870868, -0.13700702668934897, -1.9165128526181143, -5.330552985552031};
	public int playedpieces;
	private static AtomicInteger playedgenes = new AtomicInteger();

	private static final int GAMES = 5;

	public static void main(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool();
		Random ra = new Random();
//		for (int i = 0; i < 100; i++)
//			genescore.add(new Gene());
		try {
			Object o;	
			o = fromString(
					"");
			if (o instanceof List<?>)
				genescore = ((List<Gene>) o);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < genescore.get(2).getFeatures().length; i++) {
			if ((i + 1) % 6 == 0)
				System.out.println();
			System.out.print(genescore.get(2).getFeatures()[i] + " ");
		}
		System.out.println(
				"with Score " + genescore.get(2).getScore() / GAMES + "/" + genescore.get(2).getPointscore() / GAMES
						+ " from Generation " + genescore.get(2).getGeneration());
		for (int gen = 0; gen < 1000; gen++) {
			List<Long> seeds = new ArrayList<>();
			for (int games = 0; games < GAMES; games++)
				seeds.add(ra.nextLong());
			playedgenes.set(0);
			for (Gene g : genescore) {
				exec.execute(() -> {
					int points = 0;
					int pointscore = 0;
					for (long seed : seeds) {
						TetrisGame currentgame = MyTetrisFactory.createTetrisGame(new Random(seed));
						APlayerImplementation aplayer = new APlayerImplementation(new TetrisGameView(currentgame));
						aplayer.setGene(g);
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
						points += aplayer.playedpieces;
						pointscore += currentgame.getPoints();
					}
					g.setScore(pointscore);
					g.setPointscore(points);
					playedgenes.getAndIncrement();
				});
			}
			while (playedgenes.get() < 100)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			genescore.sort((Gene g1, Gene g2) -> Integer.compare(g2.getScore(), g1.getScore()));
			System.out.print("Best Gene Generation " + gen + ": ");
			for (int i = 0; i < genescore.get(0).getFeatures().length; i++) {
				if ((i + 1) % 6 == 0)
					System.out.println();
				System.out.print(genescore.get(0).getFeatures()[i] + " ");
			}
			System.out.println(
					"with Score " + genescore.get(0).getScore() / GAMES + "/" + genescore.get(0).getPointscore() / GAMES
							+ " from Generation " + genescore.get(0).getGeneration());
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
				if ((i + 1) % 6 == 0)
					System.out.println();
				System.out.print(genescore.get(loop).getFeatures()[i] + " ");
			}
			System.out.println("with Score " + genescore.get(loop).getScore() / GAMES + "/"
					+ genescore.get(loop).getPointscore() / GAMES + " from Generation "
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

	public int getColumn() {
		return goal.column;
	}

	public Rotate getRotation() {
		return goal.rotation;
	}

	public void calcNext(TetrisGameView game) {
//		if (currentgene == null) {
//			Object o;
//			try {
//				o = fromString(
//						"");
//				if (o instanceof List<?>)
//					currentgene = (Gene) ((List<?>) o).get(4);
//			} catch (ClassNotFoundException | IOException e) {
//				e.printStackTrace();
//			}
//		}
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

	private boolean finishrotations(Board board, Piece piece, int column) {
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

	private void startMovingBothDirs(Board board, Piece piece, int column, Rotate rotation) {
		comrotations.add(rotation);
		for (int i = 0; board.canAddPiece(piece, 2, column - i); i++)
			moveDown(board, piece, column - i, rotation);
		for (int i = 1; board.canAddPiece(piece, 2, column + i); i++)
			moveDown(board, piece, column + i, rotation);
	}

	private void moveDown(Board board, Piece piece, int column, Rotate rotation) {
		int i = 3;
		while (board.canAddPiece(piece, i, column))
			i++;
		Board tempboard = board.clone();
		tempboard.addPiece(piece, --i, column);
		double totalscore = 0;
		int completedrows = tempboard.deleteCompleteRows();
		int[] values = new int[] { completedrows, getHighestHeight(tempboard), getHoles(tempboard),
				getConnectedHoles(tempboard), getAltitudeDifference(tempboard), getMaximumWellDepth(tempboard),
				getBump(tempboard), getFloorTouches(tempboard), getWallTouches(tempboard), getTotalWellDepth(tempboard),
				(board.getNumberOfRows() - i) - ((piece.getHeight() - 1) - piece.getRotationPoint().getRow()), // landing
																												// height
				getBlocks(tempboard), getWeightedBlocks(tempboard), getRowTransitions(tempboard),
				getColumnTransitions(tempboard)};
		for (int fit = 0; fit < currents.length; fit++)
			totalscore += values[fit] * currents[fit];
		scores.add(new Goal(column, rotation, totalscore, board.getNumberOfColumns()));
	}
	
	private int getWeightedAboveBlocks(Board board) {
		int weightedblocks = 0;
		for (int i = 1; i < board.getNumberOfRows(); i++) {
			for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++) {
				if (board.getBoard()[i][i2] != null && board.getBoard()[i][i2] == null){
					i--;
					for(; i >= 0; i--)
						if(board.getBoard()[i][i2] != null)
							weightedblocks += board.getNumberOfRows() - i;
				}
			}
		}
		return weightedblocks;
	}

	private int getColumnTransitions(Board board) {
		int columntransitions = 0;
		for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++) {
			for (int i = 0; i < board.getNumberOfRows() - 1; i++)
				if ((board.getBoard()[i][i2] == null && board.getBoard()[i + 1][i2] != null)
						|| (board.getBoard()[i][i2] != null && board.getBoard()[i + 1][i2] == null))
					columntransitions++;
			if (board.getBoard()[board.getNumberOfRows() - 1][i2] == null)
				columntransitions++;
		}
		return columntransitions;
	}

	private int getRowTransitions(Board board) {
		int rowtransitions = 0;
		for (int i = 0; i < board.getNumberOfRows(); i++) {
			for (int i2 = 0; i2 < board.getNumberOfColumns() - 1; i2++)
				if ((board.getBoard()[i][i2] == null && board.getBoard()[i][i2 + 1] != null)
						|| (board.getBoard()[i][i2] != null && board.getBoard()[i][i2 + 1] == null))
					rowtransitions++;
			if (board.getBoard()[i][0] == null)
				rowtransitions++;
			if (board.getBoard()[i][board.getNumberOfColumns() - 1] == null)
				rowtransitions++;
		}
		return rowtransitions;
	}

	private int getWeightedBlocks(Board board) {
		int blocks = 0;
		for (int i = 0; i < board.getNumberOfRows(); i++)
			for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++)
				if (board.getBoard()[i][i2] != null)
					blocks += (board.getNumberOfRows() - i);
		return blocks;
	}

	private int getBlocks(Board board) {
		int blocks = 0;
		for (Piece.PieceType[] row : board.getBoard())
			for (Piece.PieceType piece : row)
				if (piece != null)
					blocks++;
		return blocks;
	}

	private int getMaxHoleDepth(Board board) {
		int[] holedepth = new int[board.getNumberOfColumns()];
		for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++) {
			boolean gotstone = false;
			int currenthole = 0;
			for (int i = 0; i < board.getBoard().length; i++) {
				if (!gotstone && board.getBoard()[i][i2] != null)
					gotstone = true;
				else if (gotstone) {
					if (board.getBoard()[i][i2] != null && currenthole > holedepth[i2]) {
						holedepth[i2] = currenthole;
						currenthole = 0;
					} else if (board.getBoard()[i][i2] == null)
						currenthole++;
				}
			}
		}
		return Arrays.stream(holedepth).max().getAsInt();
	}

	private int getHighestHeight(Board board) {
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					return board.getNumberOfRows() - i;
				}
		return 0;
	}

	private int getHoles(Board board) {
		boolean[] gotstone = new boolean[board.getNumberOfColumns()];
		int sum = 0;
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					gotstone[i2] = true;
				} else if (gotstone[i2]) {
					sum++;
				}
		return sum;
	}

	private int getConnectedHoles(Board board) {
		boolean[] gotstone = new boolean[board.getNumberOfColumns()];
		int sum = 0;
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					gotstone[i2] = true;
				} else if (gotstone[i2] && board.getBoard()[i - 1][i2] != null) {
					sum++;
				}
		return sum;
	}

	private int getAltitudeDifference(Board board) {
		int[] gotstone = new int[board.getNumberOfColumns()];
		for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++)
			for (int i = 0; i < board.getBoard().length; i++)
				if (board.getBoard()[i][i2] != null) {
					gotstone[i2] = board.getNumberOfRows() - i;
					break;
				}
		return Arrays.stream(gotstone).max().getAsInt() - Arrays.stream(gotstone).min().getAsInt();
	}

	private int getMaximumWellDepth(Board board) {
		int[] welldepth = new int[board.getNumberOfColumns()];
		for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++) {
			for (int i = 0; i < board.getBoard().length; i++) {
				if (board.getBoard()[i][i2] != null)
					break;
				if ((i2 == 0 || board.getBoard()[i][i2 - 1] != null)
						&& (i2 == board.getNumberOfColumns() - 1 || board.getBoard()[i][i2 + 1] != null))
					welldepth[i2]++;
				else
					welldepth[i2] = 0;
			}
		}
		return Arrays.stream(welldepth).max().getAsInt();
	}

	private int getTotalWellDepth(Board board) {
		int[] welldepth = new int[board.getNumberOfColumns()];
		for (int i2 = 0; i2 < board.getNumberOfColumns(); i2++) {
			for (int i = 0; i < board.getBoard().length; i++) {
				if (board.getBoard()[i][i2] != null)
					break;
				if ((i2 == 0 || board.getBoard()[i][i2 - 1] != null)
						&& (i2 == board.getNumberOfColumns() - 1 || board.getBoard()[i][i2 + 1] != null))
					welldepth[i2]++;
				else
					welldepth[i2] = 0;
			}
		}
		return Arrays.stream(welldepth).sum();
	}

	private int getFloorTouches(Board board) {
		int sum = 0;
		for (Piece.PieceType piece : board.getBoard()[board.getBoard().length - 1])
			if (piece != null)
				sum++;
		return sum;
	}

	private int getBump(Board board) {
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

	private int getWallTouches(Board board) {
		int sum = 0;
		for (int i = 0; i < board.getBoard().length; i++) {
			if (board.getBoard()[i][0] != null)
				sum++;
			if (board.getBoard()[i][board.getBoard()[0].length - 1] != null)
				sum++;
		}
		return sum;
	}

	private double getAddedHeight(Board board) {
		int sum = 0;
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					sum += board.getNumberOfRows() - i;
				}
		return sum;
	}

	private class Goal implements Comparable<Goal> {
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

	public void setGene(Gene g) {
		currentgene = g;
	}

}
