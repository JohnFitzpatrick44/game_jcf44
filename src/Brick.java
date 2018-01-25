
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle {
	

	
    /**
	 * Builds a brick with specified dimensions, durability, and type
	 * 
	 * @param width
	 * @param height
	 * @param durability How many hits it takes to break, 4 is permanent
	 * @param type 0 is neutral, 1 is powerUp, 2 is powerDown, 3 is top-only
	 */
	public Brick(int durability, int type, Breakout game) {	
		super((Breakout.XSIZE) / 11, Breakout.YSIZE / 20);
		this.durability = durability;
		this.type = type;
		rePaint();
		speed = 0;
		setArcHeight(Breakout.BRICK_CURVE);
    	setArcWidth(Breakout.BRICK_CURVE);
    	this.game = game;
	}
	
	private Breakout game;
	private int durability;
	private int type;
	private double speed;
	
	/**
	 * Reduces block durability, repaints it accordingly
	 * 
	 * @param b Which ball hit it
	 * @return Remaining durability
	 */
	public int reduceDurability(Ball b) {
		if(type == 3 && b.getCenterY() > getY() + getHeight()) return durability; // Top blocks unaffected when hit from bottom
		if(durability <= 3) {
			durability --;
			game.setScore(game.getScore() + 10);
		}
		if(durability != 0) {
			rePaint();
			speed = b.getSpeeds()[0];
		}
		else game.setScore(game.getScore() + 90);
		if(durability <= 0) {
			game.getRoot().getChildren().remove(this);
			game.getBricks().remove(this);
		}
		return durability;
	}
	
	/**
	 * Paints block according to its type and durability, adds gradient for top-only block
	 */
	public void rePaint() {
		if(type == 3) {		// Adds a gradient with UI color if top block
			Stop[] stops = new Stop[] { new Stop(0, Breakout.COLOR_PALETTE[durability-1]), new Stop(1, Breakout.COLOR_PALETTE[3])};
			LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
			this.setFill(lg1);
		} else {
    		this.setFill(Breakout.COLOR_PALETTE[Math.max(0,durability-1)]);	// Otherwise use palette for different block colors
		}
	}
	
	/**
	 * Checks for collisions with other bricks, transfers its speed over so they knock into each other
	 * Avoids overlapping glitches
	 */
	public void checkBrickCollisions() {
		for(Brick b : game.getBricks()) {
			if(b != this && b.getBoundsInParent().intersects(this.getBoundsInParent())) {
				b.setSpeed(this.getSpeed());	// Give colliding block its own speed
				if(this.getX() > b.getX() && this.getX() < b.getX() + b.getWidth()) this.setX(b.getX() + b.getWidth());	// Prevents overlapping
				if(this.getX() < b.getX() && this.getX() + this.getWidth() > b.getX()) this.setX(b.getX() - this.getWidth());
				this.setSpeed(0.9*this.getSpeed());	// Slightly reduces first block's speed, looks cooler/more natural
			}
		}
	}
	
	public int getDurability() {return durability;}
	
	public int getType() {return type;}
	
	/**
	 * Sets speed, but not for permanent (unmovable) blocks.
	 * 
	 * @param xs
	 */
	public void setSpeed(double xs) {
			if(durability >= 4) speed = 0;	// Permanent blocks do not move, even in movable mode
			else speed = xs;
		}
	
	public double getSpeed() {return speed;}	// Only x speed matters

}