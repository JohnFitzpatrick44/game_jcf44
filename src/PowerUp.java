// This class, a subclass of DropPower.java, shows an implementation of a power. This specific class represents positive effects, 
// shown in the trigger method. This shows the two abstract superclasses Power and DropPower in action, and how inheritance
// ensures compatibility and increases code flexibility.

import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.Image;

	/**
     * Class for drop powers with positive effects, including adding a ball, making the paddle larger, and decreasing ball speed.
     * @author Jack Fitzpatrick
     * @version %G%
     */
public class PowerUp extends DropPower {
	
	/**
	 * Image used for power up item
	 */
	public final static String POWER_UP_IMAGE = "extraballpower.gif";
	
	/**
	 * Constructor, sets image specified above
	 * @param owner Owner Brick
	 * @param game	Relevant Breakout game
	 */
	public PowerUp(Brick owner, Breakout game) {
		super(owner, game);
		this.setImage(new Image(POWER_UP_IMAGE));
		//game.getRoot().getChildren().add(this);
	}
	
	/**
	 * Triggers random positive effect when it hits the paddle. Always reverts controls to normal. Adds 200 points.
	 */
	public void trigger() {
		getGame().setScore(getGame().getScore() + 200);
		getGame().resetControls();
		int effect = ThreadLocalRandom.current().nextInt(0,3);
		if(effect == 0) getGame().getBalls().add(new Ball(getGame()));
		else if(effect == 1) for(Ball b : getGame().getBalls()) b.changeVelocity(-1);
		else if(effect == 2) getGame().getPaddle().changeWidth(1);
	}
}