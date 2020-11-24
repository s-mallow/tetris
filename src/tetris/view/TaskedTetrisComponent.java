package tetris.view;

import java.util.Timer;
import java.util.TimerTask;

import tetris.game.TetrisGame;

@SuppressWarnings("serial")
public abstract class TaskedTetrisComponent extends TetrisComponent {

	/**
	 * The timer used to enforce steps.
	 */
	private final Timer timer = new Timer();

	/**
	 * The task used to enforce steps.
	 */
	private TimerTask task;

	/**
	 * Time in milliseconds until the task is triggered.
	 */
	private double delay;

	/**
	 * The change in delay after a task was triggered.
	 */
	private double delayOffset;

	/**
	 * The minimal delay.
	 */
	private double minDelay;
	boolean bla;

	public TaskedTetrisComponent(TetrisGame game, String msg, double delay, double delayOffset, double minDelay) {
		super(game, msg);
		this.delay = delay;
		this.minDelay = minDelay;
		this.delayOffset = delayOffset;
		bla = true;

		if (delay >= 0)
			resetTimer();
	}

	private final class IndirectTask extends TimerTask {
		@Override
		public void run() {
			if (task == null)
				return;
			if (getGame().isGameOver())
				return;
			if (bla)
				try {
					Thread.sleep(1000);
					bla = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			execute();
			adjustDelayAndResetTimer();
		}
	}

	protected void adjustDelayAndResetTimer() {
		delay = delay - delayOffset;
		delay = Math.max(minDelay, delay);
		resetTimer();
	}

	private void resetTimer() {
		if (this.task != null)
			this.task.cancel();
		this.task = new IndirectTask();
		timer.schedule(this.task, (long) delay);
	}

	protected abstract void execute();
}