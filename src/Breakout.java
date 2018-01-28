import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

/**
 * Breakout game superclass, run to play.	
 * 
 * @author Jack Fitzpatrick
 *	@version %G%
 *
 */

public class Breakout  {
	/**
	 * Current level file
	 */
    public static final String LEVEL_FILE = "LevelFile.txt";
	public static final String TITLE = "Breakout - by Jack Fitzpatrick";
	
    public static final int BRICK_CURVE = 2;

	/**
	 * x size of window
	 */
    public static final int XSIZE = 800;
    /**
     * y size of window
     */
    public static final int YSIZE = 600;
    public static final double INIT_BALL_SPEED = 400;
    public static final int BALL_RADIUS = 5;
    /**
     * x size of ui frame
     */
    public static final int UI_SIZE = 100;
    public static final int FRAMES_PER_SECOND = 100;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.rgb(243, 203, 111);
    public static final int MAX_PADDLE_SPEED = 400;
    /**
     * How fast paddle speed decays
     */
    public static final int PADDLE_DRAG = MAX_PADDLE_SPEED * 10;
    /**
     * How fast brick speed decays
     */
    public static final int BRICK_DRAG = 300;
    public static final int PADDLE_ACCELERATION = 1000;		// Downward acceleration, for jumping paddle
    public static final String HEART_IMAGE = "heart.png";
    public static final String POWER_UP_IMAGE = "extraballpower.gif";
    public static final double FALL_SPEED = 300;
    public static final Color[] COLOR_PALETTE = new Color[] {Color.rgb(251, 139, 76), Color.rgb(224, 98, 29), 
    		Color.rgb(193, 68, 49), Color.rgb(87, 87, 91)}; // In order: Brick 1 color, brick 2 color, brick 3 color, UI color
    public static final double pi = Math.PI;
    
    
    private boolean leftKeyHeld = false;    
    private boolean rightKeyHeld = false;
	private KeyCode left = KeyCode.LEFT;
	private KeyCode right = KeyCode.RIGHT;
    
    private ArrayList<Ball> balls;
    private ArrayList<Brick> bricks;
    private ArrayList<Power> powers;

	private Life[] hearts = new Life[3];
	private Paddle paddle;

    private Group root;
    
	private Label score;
	private Label level;
	
	private Image heart = new Image(getClass().getClassLoader().getResourceAsStream(HEART_IMAGE));
	
	private int numPermanents = 0;
	private int scoreValue = 0;
	private int currentLevel = 0;
	private boolean movable = false;
	private boolean gameDone = false;

	private BufferedReader br;
	
	
	
	/**
	 * Initializes permanent variables, calls setUI and setSplash to set their respective areas.
	 * 
	 * @param width The width of the screen
	 * @param height The height of the screen
	 * @param background The background color
	 * @return The game's scene
	 */
	public Scene setupGame (int width, int height, Paint background) {
		root = new Group();
        paddle = new Paddle();
        root.getChildren().add(paddle);
        balls = new ArrayList<Ball>();
        Scene scene = new Scene(root, width, height, background);
        resetBR();
                
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        
        setUI();
        setSplash();
        
        return scene;
    }
	
	/**
	 * Initializes UI and related labels, calls setField to set up level 1.
	 */
	private void setUI() {
		Rectangle uiBackground = new Rectangle(XSIZE, 0, UI_SIZE, YSIZE);
        uiBackground.setFill(COLOR_PALETTE[3]);
        root.getChildren().add(uiBackground);
        
        score = new Label("Score\n0");
        processLabel(score, 32, UI_SIZE);
        score.setLayoutY(YSIZE/40);	// These values are for positioning labels along UI bar
        
        level = new Label("Level\n0");
        processLabel(level, 32, UI_SIZE);
        level.setLayoutY(YSIZE*4/9);

        Label livesText = new Label("Lives");
        processLabel(livesText, 32, UI_SIZE);
        livesText.setLayoutY(YSIZE/4);
       
        Label moveButtonText = new Label("Moving\nBlocks");
        processLabel(moveButtonText, 28, UI_SIZE);
        moveButtonText.setLayoutY(YSIZE*2/3);
        
        for(int k = 0; k < hearts.length; k++) {
        	hearts[k] = new Life(k+1);
        	root.getChildren().add(hearts[k]);
        }

        setField();

        Button movableButton = new Button();	// Button that triggers movable block mode
        movableButton.setText("OFF");
        movableButton.setStyle("-fx-font: 28 courier;");
        movableButton.setPrefWidth(UI_SIZE*.9);
        movableButton.setLayoutX(XSIZE + UI_SIZE/2 - movableButton.getPrefWidth()/2);
        movableButton.setLayoutY(YSIZE*.85);
        movableButton.setOnMousePressed(new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent me) {
        		movable = !movable;
        		if(movable) movableButton.setText("ON");
        		else movableButton.setText("OFF");
        		root.requestFocus();	// Sets focus back to root, rather than on button 
        	}
        });	
        
        root.getChildren().addAll(movableButton, score, level, livesText, moveButtonText);
        root.requestFocus();
	}
	
	/**
	 * Initializes splash screen with instructions.
	 */
	private void setSplash() {
		ArrayList<Node> uiNodes = new ArrayList<Node>(); // To easily add / remove all ui nodes at once
        Rectangle splash = new Rectangle(XSIZE+UI_SIZE, YSIZE);
        splash.setFill(COLOR_PALETTE[3]);
        
        Label howTo = new Label("How to Play");
        Label instructions = new Label("Bounce the ball off the paddle to break the bricks!\n"
        		+ "Press space to start.\n"
        		+ "Use the left and right arrows to move the paddle.\n"
        		+ "Hold space to make the paddle sticky.\n"
        		+ "Release space to release the ball.\n"
        		+ "Try pressing the up arrow key as well!\n"
        		+ "Break all the bricks to win! You have 3 lives.\n"
        		+ "Check out the README for cheats.\n"
        		+ "Have Fun!");
        
        processLabel(howTo, 64, XSIZE);
        howTo.setLayoutY(YSIZE/20);
        howTo.setLayoutX((XSIZE+UI_SIZE)/2-howTo.getPrefWidth()/2);
        
        processLabel(instructions, 32, XSIZE);
        instructions.setLayoutY(YSIZE/4);
        instructions.setLayoutX((XSIZE+UI_SIZE)/2 - instructions.getPrefWidth()/2);

        uiNodes.add(splash);
        uiNodes.add(howTo);
        uiNodes.add(instructions);
        
        root.setOnKeyTyped(new EventHandler<KeyEvent>() {	// Removes splash when any key is typed
        	@Override
        	public void handle(KeyEvent ke) {
        		root.getChildren().removeAll(uiNodes);
        	}
        });
        
        root.getChildren().addAll(uiNodes);
	}
	
	
	/**
	 * Updates every new frame, updates paddle, balls, bricks, and powers in that order.
	 * 
	 * @param elapsedTime Amount of time passed in one step
	 */
    public void step (double elapsedTime) {
    	if(gameDone) return;
    	
    	updatePaddle(elapsedTime);
    	updateBalls(elapsedTime);
    	if(movable) updateBricks(elapsedTime);
    	updatePowers(elapsedTime);
    	
    	score.setText("Score\n" + scoreValue);
    	level.setText("Level\n" + currentLevel);
    }

    
    /**
     * Updates paddle position, both in the x and y directions.
     * 
     * @param elapsedTime
     */
    private void updatePaddle(double elapsedTime) {
        if(paddle.getXSpeed() > 0 && !rightKeyHeld) {	// rightKeyHeld and leftKeyHeld included to smooth paddle movement
        	paddle.setXSpeed(paddle.getXSpeed() - PADDLE_DRAG * elapsedTime);	// Equation to decelerate paddle, stop at 0
        	paddle.setXSpeed(Math.max(0, paddle.getXSpeed()));
        } else if(paddle.getXSpeed() < 0 && !leftKeyHeld) {	// Otherwise, the paddle would jerkily move between 2 speeds
        	paddle.setXSpeed(paddle.getXSpeed() + PADDLE_DRAG * elapsedTime);
        	paddle.setXSpeed(Math.min(0, paddle.getXSpeed()));
        }
        
    	if((paddle.getX() > XSIZE - paddle.getWidth())) {	// Keeps paddle in bounds
    		paddle.setX(XSIZE - paddle.getWidth());
    	} else if(paddle.getX() < 0) {
    		paddle.setX(0);
    	} else {
    		paddle.setX(paddle.getX() + elapsedTime*paddle.getXSpeed());
    	}
    	if(paddle.getYSpeed() != 0 || paddle.getY() < YSIZE - 2*paddle.getHeight()) {	// Handles jumping
    		paddle.setYSpeed(paddle.getYSpeed() + PADDLE_ACCELERATION*elapsedTime);
    		if(paddle.getY() > YSIZE - 2*paddle.getHeight())  {		// If paddle is below its starting position, reset it vertically
    			paddle.setYSpeed(0);
    			paddle.setY(YSIZE - 2*paddle.getHeight());
    		} else paddle.setY(paddle.getY() + elapsedTime*paddle.getYSpeed());
    	}
    }
    
    
    /**
     * Updates the position and speeds of the balls in the level. Checks for paddle stickiness, and updates brick durability.
     * 
     * @param elapsedTime
     */
    private void updateBalls(double elapsedTime) {
    	for(int k = 0; k < balls.size(); k++) {
    		Ball active = balls.get(k);
    		double x = active.getCenterX();
        	double y = active.getCenterY();
    		
        	if(!active.getStuck()) {
        		if(x + BALL_RADIUS >= XSIZE) active.bounceX(-1); // Forces ball to bounce away from edge, so it doesn't get stuck
        		else if(x - BALL_RADIUS < 0) active.bounceX(1);
            
        		if(y - BALL_RADIUS >= YSIZE) {	// When ball is lost
        			balls.remove(k);
	        		if(balls.isEmpty()) {
	        			if(loseLife()) gameOver();		// Lose life, if all lives are gone, game over
	        			balls.add(new Ball(this));
	        		}
	        	} else if(y - BALL_RADIUS < 0) {
	        		active.bounceY();
	        	}
        		if(active.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            		if(y < paddle.getY() + paddle.getHeight()/2)
            			active.setAngle((((x-(paddle.getX()+paddle.getWidth()/2))/paddle.getWidth()-0.5)*pi*.6)-0.2*pi);
            		if(paddle.getSticky()) active.setStuck(true);
            	}
        	}
        	
        	active.checkBrickCollisions();
        	
        	if(bricks.size() - numPermanents == 0) nextLevel();		// Checks if all breakable bricks are gone
        	
        	if(active.getStuck()) active.stick();	// Sticks ball to paddle
        	
        	active.setCenterX(x + active.getSpeeds()[0] * elapsedTime);
            active.setCenterY(y + active.getSpeeds()[1] * elapsedTime);
            if(active.getStuck() && y + BALL_RADIUS > paddle.getY()) active.setCenterY(paddle.getY() - BALL_RADIUS);
            		// This line makes sure a jumping paddle does not "go through" a falling ball
    	}
    }
    
    
    /**
     * Only used when bricks are movable. Updates their x positions.
     * 
     * @param elapsedTime
     */
    private void updateBricks(double elapsedTime) {
    	for(Brick b : bricks) {
    		if(b.getSpeed() > 0) {	// Updates sliding bricks
            	b.setSpeed(b.getSpeed() - BRICK_DRAG * elapsedTime);
            	b.setSpeed(Math.max(0, b.getSpeed()));
            } else if(b.getSpeed() < 0) {
            	b.setSpeed(b.getSpeed() + BRICK_DRAG * elapsedTime);
            	b.setSpeed(Math.min(0, b.getSpeed()));
            }
    		if((b.getX() > XSIZE - b.getWidth())) {
        		b.setX(XSIZE - b.getWidth());
        	} else if(b.getX() < 0) {
        		b.setX(0);
        	} else {
        		b.setX(b.getX() + elapsedTime*b.getSpeed());
        	}
    		if(b.getSpeed() != 0) b.checkBrickCollisions();		// Collisions between two bricks
    	}
    	
    }
    
    
    /**
     * Updates power positions, trigger their effects if they touch a paddle.
     * 
     * @param elapsedTime
     */
    private void updatePowers(double elapsedTime) {
    	for(int k = 0; k < powers.size(); k++) {
    		Power p = powers.get(k);
    		p.toFront();
    		if(p.getOwner().getDurability() == 0 && !p.getFalling()) p.startFalling();
    		p.updatePos(elapsedTime);
    		if(paddle.getBoundsInParent().intersects(p.getBoundsInParent())) {	// Triggers effect
    			root.getChildren().remove(p);
    			powers.remove(k);
    			p.trigger();
    		}
    	}
    }
    
   
    /**
     * Handles key inputs, including directional commands, stickiness, and cheat codes. Works in tandem with handleKeyRelease to 
     * make movement smoother. If key is being held down, the paddle will slide across the screen smoothly.
     * Cheats simply trigger powerUp or down effects, or calls jumpLevel.
     * 
     * @param code KeyCode for input
     */
    private void handleKeyInput (KeyCode code) {
        if (code == right) {
        	paddle.setXSpeed(MAX_PADDLE_SPEED);
        	rightKeyHeld = true;
        } else if (code == left) {
        	paddle.setXSpeed(-MAX_PADDLE_SPEED);
        	leftKeyHeld = true;
        } else if (code == KeyCode.SPACE) {
        	paddle.setSticky(true);
        } else if (code == KeyCode.UP) {
        	if(paddle.getYSpeed() == 0) paddle.setYSpeed(-2*FALL_SPEED);
        } else if (code == KeyCode.L) {
        	resetLives();
        } else if (code == KeyCode.P) {
        	if(!gameDone) nextLevel();
        } else if (code == KeyCode.EQUALS) {
        	for(Ball b : balls) b.changeVelocity(1);
        } else if (code == KeyCode.MINUS) {
        	for(Ball b : balls) b.changeVelocity(-1);
        } else if (code == KeyCode.OPEN_BRACKET) {
        	paddle.changeWidth(-1);
        } else if (code == KeyCode.CLOSE_BRACKET) {
        	paddle.changeWidth(1);
        } else if (code == KeyCode.O) {
        	balls.add(new Ball(this));
        } else if (code.isDigitKey()) {
        	String name = code.name();
        	jumpLevel(Character.getNumericValue(name.charAt(name.length()-1)));	// Jumps to level specified by key code
        }
    }
    
    
    /**
     * Handles paddle stickiness release, and when control keys are no longer held down.
     * 
     * @param code
     */
    private void handleKeyRelease(KeyCode code) {
    	if(code == right) {
    		rightKeyHeld = false;
    	} else if(code == left) {
    		leftKeyHeld = false;
    	} else if(code == KeyCode.SPACE) {
    		for(Ball b : balls) {
    			if(b.getStuck()) b.bounceY();
    			b.setStuck(false);
    			paddle.setSticky(false);
    		}
    	}
	}
    
    
    /**
     * Switches left and right controls, as a power down effect.
     */
    public void switchControls() { // Separate methods, so effects aren't cancelled out
    	left = KeyCode.RIGHT;
    	right = KeyCode.LEFT;
    }
    
    /**
     * Undoes switchControls
     */
    public void resetControls() {
    	left = KeyCode.LEFT;
    	right = KeyCode.RIGHT;
    }
    
    
    /**
     * Reads a level and sets it up. Does this every time a new level is loaded.
     */
    private void setField() {	// Only paddle is not reset
        balls.add(new Ball(this));
        powers = new ArrayList<Power>();
        bricks = new ArrayList<Brick>();
        try {
			readLevel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        for(int k = 0; k < bricks.size(); k++) {	// Spaces them out evenly
        	bricks.get(k).setX(k%10 * (XSIZE)/10 + (XSIZE) / 264);
        	bricks.get(k).setY((int) k/10 * YSIZE / 12 + YSIZE / 60);
        }
        for(int k = bricks.size() - 1; k >= 0; k--) {
        	if(bricks.get(k).getDurability() == 0) {
        		root.getChildren().remove(bricks.get(k));
        		bricks.remove(k);
        	}
        }
    }
    
    
    /**
     * Reads a new level from signified level file.
     * 
     * @throws IOException
     */
    private void readLevel() throws IOException{
    	String str;
    	numPermanents = 0;
    	while((str = br.readLine()) != null) {
    		String[] brickCodes = str.split(" ");	// Follow level file format specified in README
    		if(brickCodes[0].equals("-")) {
    			currentLevel ++;
    			return;
    		}
    		for(int k = 0; k < brickCodes.length; k++) {
    			char[] code = brickCodes[k].toCharArray();
    			int type = 0;
    			if(code[0] == 'p') type = 1;	// Always a random power up/down
    			else if(code[0] == 'd') type = 2;
    			else if(code[0] == 't') type = 3;
    			Brick toAdd = new Brick(Character.getNumericValue(code[1]), type, this);
    			bricks.add(toAdd);
    			root.getChildren().add(toAdd);		// 0 is normal type, 1-6 are powers, 7 is top only, durability 4 is permanent
    			if(toAdd.getDurability() >= 4 && toAdd.getType() < 3) numPermanents += 1;
    			DropPower p = null;
            	if(toAdd.getType() == 1) p = new PowerUp(toAdd, this);
            	else if(toAdd.getType() == 2) p = new PowerDown(toAdd, this);
            	if(p!=null) {
            		root.getChildren().add(p);
            		powers.add(p);
            	}
    		}
    	}
    	gameOver();	// Player wins if no more levels can be read
    }
    
    public int getCurrentLevel() {return currentLevel;}
    
    /**
     * Resets bufferedReader so level file can be re-read.
     */
    private void resetBR() {	// Resets BufferedReader if a previous level needs to be reloaded
    	try{ 
        	FileInputStream fstream = new FileInputStream(LEVEL_FILE);
        	DataInputStream in = new DataInputStream(fstream);
        	br = new BufferedReader(new InputStreamReader(in));
        	currentLevel = 0;
        } catch(FileNotFoundException e) {e.printStackTrace();}
    }
    
    /**
     * Removes hearts, checks for death
     * 
     * @return Whether or not player has died (true if dead)
     */
    private boolean loseLife() {	// returns true when dead
    	resetControls();
    	for(int k = 2; k >= 0; k--) {	// Iterates backwards to remove last heart first
    		if(hearts[k].isActive()) {
    			hearts[k].setActive(false);
    			root.getChildren().remove(hearts[k]);
    			for(int kk = 0; kk < powers.size(); kk++) {
    				if(powers.get(kk).getFalling()) {
    					root.getChildren().remove(powers.get(kk));	// Removes falling powers at lost life
    					powers.remove(kk);
    				}
    			}
    			paddle.setWidth((XSIZE)/5);		// Returns paddle to original size
    			return false;
    		}
    	}
    	return true;
    }
    
    public ArrayList<Ball> getBalls() {return balls;}
    
    public ArrayList<Brick> getBricks() {return bricks;}

    
    public Paddle getPaddle() {return paddle;}
    
    
    /**
     * Resets all hearts as a cheat
     */
    private void resetLives() {
    	for(Life heart : hearts) {
    		if(!heart.isActive()) {
    			heart.setActive(true);
    			root.getChildren().add(heart);
    		}
    	}
    }
    
    /**
     * Ends game
     */
    private void gameOver() {
    	gameDone = true;
    	setGameOverUI();
        
        for(Life l : hearts) {	// Removes all active hearts
        	if(l.isActive()) root.getChildren().remove(l);
        	l.setActive(false);
        }
    }
    
    /**
     * Goes to next level in file, builds it
     */
    private void nextLevel() {	// Iterates backwards to avoid index out of bounds
    	for(int k = balls.size()-1; k >= 0; k--) {
    		root.getChildren().remove(balls.get(k));
    		balls.remove(k);
    	}
    	for(int k = powers.size()-1; k >= 0; k--) {
    		root.getChildren().remove(powers.get(k));
    		powers.remove(k);
    	}
    	
    	for(int k = bricks.size()-1; k >= 0; k--) {
    		root.getChildren().remove(bricks.get(k));
    		bricks.remove(k);
    	}
    	setField();
    }
    
    /** 
     * Jumps to the specified level
     * 
     * @param levelNumber Desired level to jump to
     */
    private void jumpLevel(int levelNumber) {
    	if(levelNumber > currentLevel) {
    		while(currentLevel != levelNumber) {
    			nextLevel();
    		}
    	} else {
    		resetBR();	// Need to reset br to reload previous levels
    		for(int k = 0; k < levelNumber; k++) {
    			nextLevel();
    		}
    	}
    }
    
    /**
     * Sets UI for game over screen, exit and retry buttons, and text whether player won or lost
     */
    private void setGameOverUI() {
    	ArrayList<Node> gameOverNodes = new ArrayList<Node>();
    	Rectangle gameOverScreen = new Rectangle(XSIZE + UI_SIZE, YSIZE);
        gameOverScreen.setFill(COLOR_PALETTE[3]);
        
        Button retryButton = new Button("Retry");
        Button exitButton = new Button("Exit");
        
        boolean won = false;	// Checks if player won
        for(Life l : hearts) won = won || l.isActive();
        Label endText = new Label();
        if(won) endText.setText("You won!");
        else endText.setText("You lost!");
        processLabel(endText, 64, XSIZE);
        endText.setLayoutX((XSIZE+UI_SIZE)/2 - endText.getPrefWidth()/2);
        endText.setLayoutY(YSIZE / 4);
        
        Label endScore = new Label("Final Score: " + scoreValue);
        processLabel(endScore, 48, XSIZE);
        endScore.setLayoutX((XSIZE+UI_SIZE)/2 - endText.getPrefWidth()/2);
        endScore.setLayoutY(YSIZE / 2.5);
        
        retryButton.setStyle("-fx-font: 48 courier;");
        retryButton.setPrefWidth(XSIZE/4);
        retryButton.setLayoutX(3*(UI_SIZE+XSIZE)/8-retryButton.getPrefWidth()/2);
        retryButton.setLayoutY(YSIZE*3/5);

        exitButton.setStyle("-fx-font: 48 courier;");
        exitButton.setPrefWidth(XSIZE/4);
        exitButton.setLayoutX(5*(UI_SIZE+XSIZE)/8-exitButton.getPrefWidth()/2);
        exitButton.setLayoutY(YSIZE*3/5);
        
        retryButton.setOnMouseClicked(new EventHandler<MouseEvent> () {
        	@Override		// Resets everything for a retry: hearts, gameDone, score, and jumps to level 1
        	public void handle(MouseEvent ae) {
        		scoreValue = 0;
        		jumpLevel(1);
        		gameDone = false; 
        		root.getChildren().removeAll(gameOverNodes);
        		root.requestFocus();
        		resetLives();
        	}
        });
        
        exitButton.setOnMouseClicked(new EventHandler<MouseEvent> () {
        	@Override		// Exits app
        	public void handle(MouseEvent ae) {
        		System.exit(1);
        	}
        });
    	
    	gameOverNodes.add(gameOverScreen);
        gameOverNodes.add(retryButton);
        gameOverNodes.add(exitButton);
        gameOverNodes.add(endText);
        gameOverNodes.add(endScore);
        
        root.getChildren().addAll(gameOverNodes);
        endText.toFront();
    }
    
    /**
     * Quickly sets up text label to be used in UI
     * 
     * @param l Label to set
     * @param size size of label text
     * @param width x size of label
     */
    private void processLabel(Label l, int size, int width) {	// Used to quickly format UI and other labels
    	l.setTextAlignment(TextAlignment.CENTER);
    	l.setAlignment(Pos.CENTER);
        l.setTextFill(Color.WHITE);
        l.setStyle("-fx-font: "+ Integer.toString(size) + " courier;");
        l.setPrefWidth(width);
        l.setLayoutX(XSIZE + UI_SIZE / 2 - l.getPrefWidth() / 2);
    }
    
    public Group getRoot() {return root;}
    
    /**
     * Represents a heart in game
     * 
     * @author Jack
     *	@version %G%
     */
    private class Life extends ImageView {
    	public Life(int pos) {
    		super(heart);
    		setFitWidth(UI_SIZE / 4);
            setFitHeight(UI_SIZE / 4);
            setX(XSIZE + pos * UI_SIZE / 4 - this.getFitWidth() / 2);
            setY(YSIZE / 3);
            active = true;
    	}
    	
    	private boolean active;
    	
    	public boolean isActive() {return active;}
    	public void setActive(boolean b) {active = b;}
    	
    }
    
    
    
    
    
    public int getScore() {return scoreValue;}
    public void setScore(int sv) {scoreValue = sv;}
    
   
    

}



