import javafx.scene.shape.Circle;
	
public class Ball extends Circle {
    private double velocity;
	private double angle;		// Angle measured from positive x axis
	private double[] speeds;	// x and y speeds are included separately from angle, velocity to explicitly stick ball to paddle
	private boolean stuck;
	private Breakout game;
	
	public Ball(Breakout game) {
		super(Breakout.BALL_RADIUS, Breakout.COLOR_PALETTE[3]);		// Uses UI color for ball
		velocity = Breakout.INIT_BALL_SPEED + (game.getCurrentLevel()-1) * 25;		// Increase in speed as levels progress
		angle = 3*Math.PI/2;		// Initial angle straight up ('up' is negative y direction), all angles in radians
		speeds = new double[2];		// Speeds[0] is x speed, speeds[1] is y speed
		setSpeeds();
		setCenterX(game.getPaddle().getX() + game.getPaddle().getWidth() / 2);
		setCenterY(game.getPaddle().getY()-getRadius());
	    game.getRoot().getChildren().add(this);
	    stuck = true;
	    this.game = game;
	}
	
	/**
	 * Bounces ball in relative x direction
	 */
	public void bounceX() {
		angle = Math.PI - angle;					// Reflects angle across x axis		
		while(angle < 0) angle += 2*Math.PI;		// Loop normalizes angle to between 0 and 2*pi
		angle = angle%(2*Math.PI);
		setSpeeds();
	}
	
	/** 
	 * Bounces ball in relative y direction
	 */
	public void bounceY() {
		angle = 2*Math.PI - angle;				// Reflects across y axis
		while(angle < 0) angle += 2*Math.PI;
		angle = angle%(2*Math.PI);
		setSpeeds();
	}
	
	/**
	 * Bounces ball in specified x direction. Used to guarantee a certain bounce in the case of significantly overlapping boundaries.
	 * @param direction Either positive or negative, ball will always bounce that way. Magnitude irrelevant.
	 */
	public void bounceX(int direction) {			// Ensures a bounce in specified (+ or -) direction, any + or - int will do
		if(angle >= Math.PI/2 && angle <= 3*Math.PI/2) {
			if(direction > 0) bounceX();
		} else {
			if(direction < 0) bounceX();
		}
	}
	
	/**
	 * Bounces ball in specified y direction. Used to guarantee a certain bounce, as with bounceX(int).
	 * @param direction Either positive or negative, ball will always bounce that way. Magnitude irrelevant.
	 */
	public void bounceY(int direction) {
		if(angle > Math.PI) {
			if(direction > 0) bounceY();
		} else {
			if(direction < 0) bounceY();
		}
	}
	
	/**
	 * Sets ball angle in radians and resets ball speeds accordingly, used when ball bounces off paddle.
	 * @param ang Angle to be set in radians
	 */
	public void setAngle(double ang) {
		angle = ang;
		while(angle < 0) angle += 2*Math.PI;
		angle = angle%(2*Math.PI);
		setSpeeds();
	}
	
	/**
	 * Keeps ball stuck to paddle by matching ball speeds with paddle speeds.
	 */
	public void stick() {
		speeds[1] = game.getPaddle().getYSpeed();
		if(game.getPaddle().getX() <= 0 || game.getPaddle().getX() + game.getPaddle().getWidth() >= Breakout.XSIZE) speeds[0] = 0;	
		else speeds[0] = game.getPaddle().getXSpeed(); 	// Line above makes sure ball doesn't move when paddle hits wall
		stuck = true;	
	}
	
	/**
	 * Checks for collisions on bricks, gives bricks ball's x speed. Bounces ball depending on where it hit brick.
	 */
	public void checkBrickCollisions() {
		for(int k = 0; k < game.getBricks().size(); k++) {
			Brick b = game.getBricks().get(k);
			if(b.getBoundsInParent().intersects(getBoundsInParent())) {
				b.reduceDurability(this);
				if(getCenterX() > b.getX() + b.getWidth()) bounceX(1);		// Do this explicitly as the ball could come from any direction
				else if(getCenterX() < b.getX()) bounceX(-1);
				if(getCenterY() > b.getY() + b.getHeight()) bounceY(1);
				else if(getCenterY() < b.getY()) bounceY(-1);
			}
		}
	}
	
	/**
	 * Increases or decreases ball velocity by 25%, based on sign of direction. Magnitude does not matter.
	 * @param direction	Signifies either positive or negative ball speed change. 0 does nothing.
	 */
	public void changeVelocity(int direction) {
		if(direction != 0) direction = direction/Math.abs(direction);
		setVelocity(getVelocity()*(1 + 0.25*direction));
		if(getVelocity() > Breakout.INIT_BALL_SPEED * 3) setVelocity(Breakout.INIT_BALL_SPEED * 3);
		else if(getVelocity() < Breakout.INIT_BALL_SPEED / 5) setVelocity(Breakout.INIT_BALL_SPEED / 5);
		setSpeeds();
	}
	
	public void setVelocity(double vel) {velocity = vel;}
	public double getVelocity() {return velocity;}
	public double[] getSpeeds() {return speeds;}
	
	/**
	 * Resets ball speeds based on velocity and angle, and unsticks the ball from the paddle.
	 */
	public void setSpeeds() {
		speeds[0] = velocity*Math.cos(angle);
		speeds[1] = velocity*Math.sin(angle);
		stuck = false;
	}
	
	public boolean getStuck() {return stuck;}
	public void setStuck(boolean b) {stuck = b;}
}