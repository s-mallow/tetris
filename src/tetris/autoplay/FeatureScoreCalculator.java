package tetris.autoplay;

import java.util.Arrays;
import java.util.stream.IntStream;
import tetris.game.Board;
import tetris.game.pieces.Piece;

public class FeatureScoreCalculator {

	public static int[] getScores(Board board, int removedlines) {
		int[] res = new int[] { getHighestHeight(board), getHoles(board), getConnectedHoles(board), removedlines,
				getAltitudeDifference(board), getMaximumWellDepth(board) };
		return res;
	}

	private static int getHighestHeight(Board board) {
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++)
				if (board.getBoard()[i][i2] != null) {
					return board.getNumberOfRows() - i;
				}
		return 0;
	}

	private static int getHoles(Board board) {
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

	private static int getConnectedHoles(Board board) {
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

	private static int getAltitudeDifference(Board board) {
		int highest = 0;
		int lowest = 0;
		boolean[] gotstone = new boolean[board.getNumberOfColumns()];
		for (int i = 0; i < board.getBoard().length; i++)
			for (int i2 = 0; i2 < board.getBoard()[i].length; i2++) {
				if (board.getBoard()[i][i2] != null) {
					if (highest == 0)
						highest = board.getNumberOfRows() - i;
					if (!gotstone[i2])
						gotstone[i2] = true;
				}
				if (IntStream.range(0, gotstone.length).mapToObj(idx -> gotstone[idx]).allMatch(s -> s)) {
					lowest = board.getNumberOfRows() - i;
					break;
				}
			}
		return highest - lowest;
	}

	private static int getMaximumWellDepth(Board board) {
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

}
