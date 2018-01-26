import javafx.scene.shape.Rectangle;

/**
     * Represents paddle in game
     * 
     * @author Jack
     *	@version %G%
     */




public class Paddle extends Rectangle {
	

	private double xSpeed;
	private double ySpeed;
	private boolean sticky;
	
	
	public Paddle() {
		super(Breakout.XSIZE/5,Breakout.YSIZE/60);
		xSpeed = 0;
		ySpeed = 0;
		sticky = true;
		resetPosition();
		this.setArcHeight(10);
        this.setArcWidth(20);
        this.setFill(Breakout.COLOR_PALETTE[3]);
	}
	
	public void resetPosition() {
		this.setX(Breakout.XSIZE/2 - this.getWidth()/2);
		this.setY(Breakout.YSIZE - 2*this.getHeight());
	}
	
	/**
	 * Increases or decreases paddle length by 25%
	 * 
	 * @param direction
	 */
	public void changeWidth(int direction) {
		if(direction != 0) direction = direction/Math.abs(direction);
		double paddleXPos = getX() + getWidth()/2;
		setWidth(getWidth()*( 1 + 0.25 * direction));
		if(getWidth() > (Breakout.XSIZE)/2) setWidth((Breakout.XSIZE)/2);
		else if(getWidth() < (Breakout.XSIZE)/10) setWidth((Breakout.XSIZE)/10);
		setX(paddleXPos - getWidth()/2);
	}
	
	public void setXSpeed(double xs) {this.xSpeed = xs;}
	public double getXSpeed() {return xSpeed;}
	public void setYSpeed(double ys) {this.ySpeed = ys;}
	public double getYSpeed() {return ySpeed;}
	public void setSticky(boolean s) {this.sticky = s;}
	public boolean getSticky() {return sticky;}
}