import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.Image;
	/**
     * Class for powers with negative effects, including switching controls, decreasing paddle size, and increasing ball speed.
     * @author Jack
     * @version %G%
     */
public class PowerDown extends Power {
	private Breakout game;
	public PowerDown(Brick owner, Breakout game) {
		super(owner);
		this.setImage(new Image(Breakout.POWER_DOWN_IMAGE));
		this.game = game;
	}
	/**
	 * Triggers random negative effect when it hits the paddle. Subtracts 200 points.
	 */
	public void trigger() {
		game.setScore(game.getScore() - 200);
		int effect = ThreadLocalRandom.current().nextInt(0,3);
		if(effect == 0) game.switchControls();
		else if(effect == 1) for(Ball b : game.getBalls()) b.changeVelocity(1);
		else if(effect == 2) game.getPaddle().changeWidth(-1);
	}
}