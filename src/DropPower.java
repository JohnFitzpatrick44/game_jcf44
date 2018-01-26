/**
 * Abstract class for powers that drop vertically from their bricks, triggering upon hitting the paddle
 * 
 * @author Jack
 * @version %G%
 */
public abstract class DropPower extends Power implements IPower {

	
	/**
	 * Fall speed for the power
	 */
    public static final double FALL_SPEED = 300;
	
	private boolean falling;
	
	/**
	 * Constructor to make a new falling power
	 * @param owner	Owner brick
	 * @param game	Relevant Breakout game
	 */
	public DropPower(Brick owner, Breakout game) {
		super(owner, game);
		this.falling = false;
	}
	
	/**
	 * Updates position of power, either on brick or falling straight down
	 */
	public void updatePos(double elapsedTime) {
		if(falling) {
			setX(getOwner().getX() + getOwner().getWidth() / 2 - getBoundsInParent().getWidth() / 2);
			setY(getOwner().getY() + getOwner().getHeight() / 2 - getBoundsInParent().getHeight() / 2);
		} else setY(this.getY() + elapsedTime * getYSpeed());
	}
	
	/**
	 * Starts the power falling
	 */
	public void startFalling() {
		falling = true;
		setSpeedY(FALL_SPEED);
	}
	
	/**
	 * Returns if the power is falling
	 * @return State of falling
	 */
	public boolean getFalling() {return falling;}
}
