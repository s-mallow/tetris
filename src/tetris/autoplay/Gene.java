package tetris.autoplay;

import java.io.Serializable;
import java.util.Random;

public class Gene implements Serializable {

	private final double rows, height, holes, bump;
	private final int generation;
	private int score;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	private final Random ra;

	public Gene() {
		ra = new Random();
		rows = (ra.nextDouble() - 0.5) * 2;
		height = (ra.nextDouble() - 0.5) * 2;
		holes = (ra.nextDouble() - 0.5) * 2;
		bump = (ra.nextDouble() - 0.5) * 2;
		generation = 0;
	}

	private Gene(double rows, double height, double holes, double bump, int generation) {
		this.ra = new Random();
		this.rows = rows;
		this.height = height;
		this.holes = holes;
		this.bump = bump;
		this.generation = generation;
	}

	public double getRows() {
		return rows;
	}

	public double getHeight() {
		return height;
	}

	public double getHoles() {
		return holes;
	}

	public double getBump() {
		return bump;
	}

	public Gene breed(Gene other, int generation) {
		return new Gene(combineChromosome(rows, other.rows), combineChromosome(height, other.height),
				combineChromosome(holes, other.holes), combineChromosome(bump, other.bump), generation);
	}

	private double combineChromosome(double chr1, double chr2) {
		if (ra.nextDouble() < 0.1)
			return (ra.nextDouble() - 0.5) * 2;
		else
			return (chr2 + chr1) / 2;
	}

	public int getGeneration() {
		return generation;
	}

}
