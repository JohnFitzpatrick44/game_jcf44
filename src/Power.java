import javafx.scene.image.ImageView;
	/**
     * Represents powers that fall from bricks in game. Base class that positive and negative powers can inherit.
     * @author Jack
     * @version %G%
     */
public class Power extends ImageView {
	/**
	 * Tracks owner brick, so power will fall when it breaks
	 * @param owner
	 */
	public Power(Brick owner) {
		super();
		this.owner = owner;
		speed = 0;
	}
	private Brick owner;
	private double speed;
	/**
	 * Update position if falling, sticks to owner brick if not. Must update position to track bricks due to movable brick mode.
	 * @param elapsedTime
	 */
	public void updatePos(double elapsedTime) {
		if(speed == 0) {
			setX(owner.getX() + owner.getWidth() / 2 - getBoundsInParent().getWidth() / 2);
			setY(owner.getY() + owner.getHeight() / 2 - getBoundsInParent().getHeight() / 2);
		} else setY(this.getY() + elapsedTime * speed);
	}
	public void startFall() {speed = Breakout.FALL_SPEED;}
	public double getSpeed() {return speed;}
	public Brick getOwner() {return owner;}
	public void trigger() {};
}