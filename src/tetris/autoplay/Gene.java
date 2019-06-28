package tetris.autoplay;

import java.io.Serializable;
import java.util.Random;

public class Gene implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8809437431149250481L;
	private final double[] features;
	private final int generation;
	private int score;
	private int pointscore;
	
	public static final int NOFEATURES = 9;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	private final Random ra;

	public Gene() {
		ra = new Random();
		features = new double[NOFEATURES];
		for(int i = 0; i < features.length; i++)
			features[i] = (ra.nextDouble() - 0.5) * 2;
		generation = 0;
	}

	private Gene(double[] features, int generation) {
		this.ra = new Random();
		this.features = features;
		this.generation = generation;
	}
	
	public double[] getFeatures() {
		return features;
	}

	public Gene breed(Gene other, int generation) {
		double[] newfeatures = new double[NOFEATURES];
		for(int i = 0; i < features.length; i++)
			newfeatures[i] = combineChromosome(features[i], other.features[i], score, other.score);
		return new Gene(newfeatures, generation);
	}

	private double combineChromosome(double chr1, double chr2, double score1, double score2) {
		if (ra.nextDouble() < 0.1)
			return (ra.nextDouble() - 0.5) * 2;
		else if (ra.nextDouble() < 0.5)
			return chr1;
		else
			return chr2;
	}

	public int getGeneration() {
		return generation;
	}

	public int getPointscore() {
		return pointscore;
	}

	public void setPointscore(int pointscore) {
		this.pointscore = pointscore;
	}

}
