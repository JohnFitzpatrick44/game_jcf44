import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.Image;
	/**
     * Class for powers with positive effects, including adding a ball, making the paddle larger, and decreasing ball speed.
     * @author Jack
     * @version %G%
     */
public class PowerUp extends Power {
	private Breakout game;
	public PowerUp(Brick owner, Breakout game) {
		super(owner);
		this.setImage(new Image(Breakout.POWER_UP_IMAGE));
		this.game = game;
	}
	/**
	 * Triggers random positive effect when it hits the paddle. Always reverts controls to normal. Adds 200 points.
	 */
	public void trigger() {
		game.setScore(game.getScore() + 200);
		game.resetControls();
		int effect = ThreadLocalRandom.current().nextInt(0,3);
		if(effect == 0) game.getBalls().add(new Ball(game));
		else if(effect == 1) for(Ball b : game.getBalls()) b.changeVelocity(-1);
		else if(effect == 2) game.getPaddle().changeWidth(1);
	}
}