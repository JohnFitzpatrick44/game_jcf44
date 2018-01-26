import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.Image;

	/**
     * Class for drop powers with negative effects, including switching controls, decreasing paddle size, and increasing ball speed.
     * @author Jack
     * @version %G%
     */
public class PowerDown extends DropPower {
	
	/**
	 * Image used for power down item
	 */
	public static final String POWER_DOWN_IMAGE = "sizepower.gif";
	
	/**
	 * Constructor, sets image specified above
	 * @param owner Owner Brick
	 * @param game	Relevant Breakout game
	 */
	public PowerDown(Brick owner, Breakout game) {
		super(owner, game);
		this.setImage(new Image(POWER_DOWN_IMAGE));
	}
	
	/**
	 * Triggers random negative effect when it hits the paddle. Subtracts 200 points.
	 */
	public void trigger() {
		getGame().setScore(getGame().getScore() - 200);
		int effect = ThreadLocalRandom.current().nextInt(0,3);
		if(effect == 0) getGame().switchControls();
		else if(effect == 1) for(Ball b : getGame().getBalls()) b.changeVelocity(1);
		else if(effect == 2) getGame().getPaddle().changeWidth(-1);
	}
}