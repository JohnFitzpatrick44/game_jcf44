import javafx.scene.image.ImageView;

	/**
     * Abstract class that represents powers in game.
     * 
     * @author Jack
     * @version %G%
     */
public abstract class Power extends ImageView {
	
	/**
	 * Tracks owner brick, so power will start "falling" when it breaks. Initializes x and y speeds to 0.
	 * 
	 * @param owner Brick the power is tied to
	 */
	public Power(Brick owner, Breakout game) {
		super();
		this.owner = owner;
		xSpeed = 0;
		ySpeed = 0;
		this.game = game;
	}
	
	private Brick owner;
	private double xSpeed;
	private double ySpeed;
	private Breakout game;
	
	/**
	 * Gets relevant Breakout game
	 * @return Breakout game
	 */
	public Breakout getGame() {return game;}
	
	/**
	 * Sets x speed of power
	 * @param x New x speed
	 */
	public void setSpeedX(double x) {xSpeed = x;}
	
	/**
	 * Sets y speed of power
	 * @param y New y speed
	 */
	public void setSpeedY(double y) {ySpeed = y;}
	
	/**
	 * @return x speed
	 */
	public double getXSpeed() {return xSpeed;}
	
	/**
	 * @return y speed
	 */
	public double getYSpeed() {return ySpeed;}
	
	/**
	 * @return owner Brick
	 */
	public Brick getOwner() {return owner;}
}