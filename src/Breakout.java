import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Breakout extends Application {
	
	public static final String TITLE = "Breakout";
    public static final int XSIZE = 600;
    public static final int YSIZE = 400;
    public static final int UI_SIZE = 100;
    public static final int FRAMES_PER_SECOND = 100;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.rgb(243, 203, 111);
    public static final int MAX_PADDLE_SPEED = 300;
    public static final int PADDLE_DRAG = 3000;
    public static final double INIT_BALL_SPEED = 300;
    public static final int NUM_BRICKS = 40;
    public static final int BALL_RADIUS = 5;
    public static final int BRICK_CURVE = 2;
    public static final String HEART_IMAGE = "heart.png";
    public static final String POWER_UP_IMAGE = "extraballpower.gif";
    public static final String POWER_DOWN_IMAGE = "sizepower.gif";
    public static final String LEVEL_FILE = "LevelFile.txt";
    public static final double FALL_SPEED = 100;
    public static final Color BRICK_COLOR_1 = Color.rgb(251, 139, 76);
    public static final Color BRICK_COLOR_2 = Color.rgb(224, 98, 29);
    public static final Color BRICK_COLOR_3 = Color.rgb(193, 68, 49);
    public static final Color UI_COLOR = Color.rgb(87, 87, 91);

    
    private double paddleSpeed = 0;
    private boolean leftKeyHeld = false;    
    private boolean rightKeyHeld = false;
    private Scene myScene;
    private Rectangle paddle;
    private ArrayList<Ball> balls;
    private ArrayList<Brick> bricks;
    private Group root;
    private double pi = Math.PI;
	private Label score;
	private Image heart = new Image(getClass().getClassLoader().getResourceAsStream(HEART_IMAGE));
	private Image powerUp = new Image(getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE));
	private Image powerDown = new Image(getClass().getClassLoader().getResourceAsStream(POWER_DOWN_IMAGE));
	
	private Life[] hearts = new Life[3];
	private int scoreValue = 0;
	private boolean sticky = false;
	private ArrayList<Power> powers;
	private KeyCode left = KeyCode.LEFT;
	private KeyCode right = KeyCode.RIGHT;
	private BufferedReader br;
	private int lives = 3;
	
	@Override
	public void start (Stage stage) {
        myScene = setupGame(XSIZE + UI_SIZE, YSIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();
        score.setLayoutX(XSIZE + UI_SIZE / 2 - score.getWidth() / 2);
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
                                      e -> step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }
	
	private Scene setupGame (int width, int height, Paint background) {
        root = new Group();
        Scene scene = new Scene(root, width, height, background);
        width -= UI_SIZE;
        
        try{ 
        	FileInputStream fstream = new FileInputStream("LevelFile.txt");
        	DataInputStream in = new DataInputStream(fstream);
        	br = new BufferedReader(new InputStreamReader(in));
        } catch(FileNotFoundException e) {e.printStackTrace();}
        paddle = new Rectangle((XSIZE) / 5, 10);
        paddle.setArcHeight(10);	//Globalify
        paddle.setArcWidth(20);
        paddle.setFill(UI_COLOR);
        
        balls = new ArrayList<Ball>();
                
        root.getChildren().add(paddle);

        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        
        Rectangle uiPane = new Rectangle(width, 0, UI_SIZE, height);
        uiPane.setFill(UI_COLOR);
        score = new Label("Score\n0");
        score.setTextAlignment(TextAlignment.CENTER);
        score.setTextFill(Color.WHITE);
        score.setLayoutY(height / 20);
        score.setStyle("-fx-font: 20 arial;");
        
        Rectangle splash = new Rectangle(width, height);
        splash.setFill(UI_COLOR);
        splash.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent me) {
        		splash.setOpacity(0);
        	}
        });
        
        
        root.getChildren().add(uiPane);
        root.getChildren().add(score);
        Life heart1 = new Life(1);
        Life heart2 = new Life(2);
        Life heart3 = new Life(3);
        hearts[0] = heart1;
        hearts[1] = heart2;
        hearts[2] = heart3;
        root.getChildren().addAll(heart1, heart2, heart3);
        setField();

        root.getChildren().add(splash);

        return scene;
    }

    private void step (double elapsedTime) {
    	
    	double w = myScene.getWidth() - UI_SIZE;
    	double h = myScene.getHeight();
    	double xp = paddle.getX();
    	
    	
    	
        if(paddleSpeed > 0 && !rightKeyHeld) {
        	paddleSpeed -= PADDLE_DRAG * elapsedTime;
        	paddleSpeed = Math.max(0, paddleSpeed);
        } else if(paddleSpeed < 0 && !leftKeyHeld) {
        	paddleSpeed += PADDLE_DRAG * elapsedTime;
        	paddleSpeed = Math.min(0, paddleSpeed);
        }
    	if((xp > w - paddle.getBoundsInParent().getWidth())) {
    		paddle.setX(w - paddle.getBoundsInParent().getWidth());
    	} else if(xp < 0) {
    		paddle.setX(0);
    	} else {
    		paddle.setX(xp + elapsedTime*paddleSpeed);
    	}
    	
    	
    	for(int k = 0; k < balls.size(); k++) {
        	
    		Ball active = balls.get(k);
    		double x = active.getCenterX();
        	double y = active.getCenterY();
        	double r = BALL_RADIUS;
    		
        	if(x + r >= w) active.bounceX(-1); 
        	else if(x - r < 0) active.bounceX(1);
            
        	if(y - r >= h) {
        		balls.remove(k);
        		if(balls.isEmpty()) {
        			if(loseLife()) System.exit(1);		// LOSE LIFE
        			balls.add(new Ball());
        		}
        	} else if(y - r < 0) {
        		active.bounceY();
        	}
        	
        	if(active.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
        		if(y + r > paddle.getY() || y > paddle.getY() + paddle.getHeight()/2) active.setAngle((((x-(paddle.getX()+paddle.getWidth()/2))/paddle.getWidth()-0.5)*pi*.6)-0.2*pi);
        		if(sticky) active.setStuck(true);
        	}
        	
        	for(int kk = 0; kk < bricks.size(); kk++) {
        		Brick b = bricks.get(kk);
        		
        		if(b.getBoundsInParent().intersects(active.getBoundsInParent())) {
        			int durability = b.reduceDurability();
        			scoreValue += 10;
        			if(durability == 0) {
        				root.getChildren().remove(b);
        				bricks.remove(kk);
        				scoreValue += 90;
        			}
        			if(x > b.getX() + b.getWidth()) active.bounceX(1);
        			else if(x < b.getX()) active.bounceX(-1);
        			if(y > b.getY() + b.getHeight()) active.bounceY(1);
        			else if(y < b.getY()) active.bounceY(-1);
        			
        		}
        		
        	}
        	
        	if(bricks.size() == 0) nextLevel();
        	
        	
        	
        	if(active.getStuck()) active.stick();
        	
        	
        	active.setCenterX(x + active.getSpeeds()[0] * elapsedTime);
            active.setCenterY(y + active.getSpeeds()[1] * elapsedTime);
            
    	}
    	for(int k = 0; k < powers.size(); k++) {
    		Power p = powers.get(k);
    		if(p.getOwner().getDurability() == 0 && p.getSpeed() == 0) {p.startFall();}
    		p.updatePos(elapsedTime);
    		if(paddle.getBoundsInParent().intersects(p.getBoundsInParent())) {
    			root.getChildren().remove(p);
    			powers.remove(k);
    			if(p.getType() < 4) scoreValue += 200;
    			else scoreValue -= 200;
    			if(p.getType() == 1) balls.add(new Ball());
    			else if(p.getType() == 2) {
    					for(Ball b : balls) {
    						b.setVelocity(b.getVelocity() * .75);
    						if(b.getVelocity() < INIT_BALL_SPEED / 5) b.setVelocity(INIT_BALL_SPEED / 5);
    						b.setSpeeds();
    				    }
    			} else if(p.getType() == 3) {
    				double paddleXPos = paddle.getX() + paddle.getWidth()/2;
    				paddle.setWidth(paddle.getWidth()*1.25);
    				if(paddle.getWidth() > (XSIZE)/2) paddle.setWidth((XSIZE)/2);
    				paddle.setX(paddleXPos - paddle.getWidth()/2);
    			} else if(p.getType() == 4) {
    				double paddleXPos = paddle.getX() + paddle.getWidth()/2;
    				paddle.setWidth(paddle.getWidth()*.75);
    				if(paddle.getWidth() < (XSIZE)/10) paddle.setWidth((XSIZE)/10);
    				paddle.setX(paddleXPos - paddle.getWidth()/2);
    			} else if(p.getType() == 5) {
					for(Ball b : balls) {
						b.setVelocity(b.getVelocity() * 1.25);
						if(b.getVelocity() > INIT_BALL_SPEED * 3) b.setVelocity(INIT_BALL_SPEED * 3);
						b.setSpeeds();
				    }
    			} else if(p.getType() == 6) {
    				switchControls();
    			}
    		}
    	}
    	
    	score.setText("Score\n" + scoreValue);


    }

    private void handleKeyInput (KeyCode code) {
        if (code == right) {
        	paddleSpeed = MAX_PADDLE_SPEED;
        	rightKeyHeld = true;
        } else if (code == left) {
        	paddleSpeed = -MAX_PADDLE_SPEED;
        	leftKeyHeld = true;
        } else if (code == KeyCode.SPACE) {
        	sticky = true;
        } else if (code == KeyCode.UP) {
        } else if (code == KeyCode.DOWN) {
        }
    }
    
    private void handleKeyRelease(KeyCode code) {
    	if(code == right) {
    		rightKeyHeld = false;
    	} else if(code == left) {
    		leftKeyHeld = false;
    	} else if(code == KeyCode.SPACE) {
    		for(Ball b : balls) {
    			if(b.getStuck()) b.bounceY();
    			b.setStuck(false);
    			sticky = false;
    		}
    	}
	}

    private void handleMouseInput (double x, double y) {
        
    }

    private void switchControls() {
    	left = KeyCode.RIGHT;
    	right = KeyCode.LEFT;
    }
    
    private void resetControls() {
    	left = KeyCode.LEFT;
    	right = KeyCode.RIGHT;
    }
    
    private void readLevel() throws IOException{
    	String str;
    	while((str = br.readLine()) != null) {
    		String[] brickCodes = str.split(" ");
    		if(brickCodes[0].equals("-")) return;
    		for(int k = 0; k < brickCodes.length; k++) {
    			char[] code = brickCodes[k].toCharArray();
    			int type = 0;
    			if(code[0] == 'p') type = ThreadLocalRandom.current().nextInt(1, 4);
    			else if(code[0] == 'd') type = ThreadLocalRandom.current().nextInt(4, 7);
    			Brick toAdd = new Brick((XSIZE) / 11, YSIZE / 20, Character.getNumericValue(code[1]), type);
    			bricks.add(toAdd);
    			root.getChildren().add(toAdd);		// 0 is normal type, 1-6 are powers, -1 is top only, -2 is permanent
            	toAdd.setArcHeight(BRICK_CURVE);
            	toAdd.setArcWidth(BRICK_CURVE);
            	
            	if(toAdd.getType() > 0) powers.add(new Power(toAdd.getType(), toAdd));
    		}
    	}
    }
    
    private boolean loseLife() {		// returns true when game is over
    	resetControls();
    	for(int k = 2; k >= 0; k--) {
    		if(hearts[k].isActive()) {
    			hearts[k].setActive(false);
    			root.getChildren().remove(hearts[k]);
    			
    			for(int kk = 0; kk < powers.size(); kk++) {
    				if(powers.get(kk).getSpeed() != 0) {
    					root.getChildren().remove(powers.get(kk));
    					powers.remove(kk);
    				}
    			}
    			paddle.setWidth((XSIZE)/5);
    			return false;
    		}
    	}
    	return true;
    }
    
    private void setField() {

    	
        paddle.setY(YSIZE - 2*paddle.getBoundsInParent().getHeight());
        paddle.setX(XSIZE/2 - paddle.getBoundsInParent().getWidth()/2);
        paddle.setWidth((XSIZE) / 5);
        paddle.setHeight(10);
        balls.add(new Ball());
        powers = new ArrayList<Power>();
        bricks = new ArrayList<Brick>();
        try {
			readLevel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        for(int k = 0; k < bricks.size(); k++) {
        	bricks.get(k).setX(k%10 * (XSIZE)/10 + (XSIZE) / 132);
        	bricks.get(k).setY((int) k/10 * YSIZE / 12 + YSIZE / 60);
        }
        
        
        if(bricks.size() == 0) System.exit(1);
        
    }

    private void nextLevel() {
    	for(int k = balls.size()-1; k >= 0; k--) {
    		root.getChildren().remove(balls.get(k));
    		balls.remove(k);
    	}
    	for(int k = powers.size()-1; k >= 0; k--) {
    		root.getChildren().remove(powers.get(k));
    		powers.remove(k);
    	}
    	setField();
    }
    
    public static void main (String[] args) {
        launch(args);
    }
    
    private class Brick extends Rectangle {
    	public Brick(int width, int height, int durability, int type) {
    		super(width, height);
    		this.durability = durability;
    		this.type = type;
    		rePaint();
    		//this.setStrokeWidth(durability-1);
    		//this.setStroke(Color.BLACK);
    	}
    	
    	int durability;
    	int type;
    	
    	public int getType() {
    		return type;
    	}
    	
    	public int reduceDurability() {
    		durability --;
    		if(durability != 0) rePaint();
    		return durability;
    	}
    	
    	public void rePaint() {
    		if(durability == 1) this.setFill(BRICK_COLOR_1);
    		else if(durability == 2) this.setFill(BRICK_COLOR_2);
    		else if(durability == 3) this.setFill(BRICK_COLOR_3);

    	}
    	
    	public int getDurability() {return durability;}
    	
    	
    }
    
    private class Ball extends Circle {
    	public Ball() {
    		super(BALL_RADIUS, UI_COLOR);
    		velocity = INIT_BALL_SPEED;
    		angle = 3*pi/2;
    		speeds = new double[2];
    		setSpeeds();
    		setCenterX(paddle.getX() + paddle.getWidth() / 2);
    		setCenterY(paddle.getY()-getRadius());
            root.getChildren().add(this);
            stick();
            stuck = true;
    	}
    	
    	private double velocity;
    	private double angle;
    	private double[] speeds;
    	private boolean stuck;
    	
    	public void bounceX() {
    		angle = pi - angle;					// Reflects angle across x axis		
    		while(angle < 0) angle += 2*pi;		// Loop normalizes angle to between 0 and 2*pi
    		angle = angle%(2*pi);
    		setSpeeds();
    	}
    	
    	public void bounceY() {
    		angle = 2*pi - angle;				// Reflects across y axis
    		while(angle < 0) angle += 2*pi;
    		angle = angle%(2*pi);
    		setSpeeds();
    	}
    	
    	public void bounceX(int direction) {			// Ensures a bounce in specified (+ or -) direction
    		if(angle >= pi/2 && angle <= 3*pi/2) {
    			if(direction > 0) bounceX();
    			else return;
    		} else {
    			if(direction < 0) bounceX();
    			else return;
    		}
    	}
    	
    	public void bounceY(int direction) {
    		if(angle > pi) {
    			if(direction > 0) bounceY();
    			else return;
    		} else {
    			if(direction < 0) bounceY();
    			else return;
    		}
    	}
    	
    	public void setAngle(double ang) {
    		angle = ang;
    		while(angle < 0) angle += 2*pi;
    		angle = angle%(2*pi);
    		setSpeeds();
    	}
    	
    	public void stick() {
    		speeds[0] = paddleSpeed;
    		speeds[1] = 0;
    		stuck = true;
    	}
    	
    	public void setVelocity(double vel) {velocity = vel;}
    	
    	public double getVelocity() {return velocity;}
    	
    	public double[] getSpeeds() {return speeds;}
    	
    	public void setSpeeds() {					// When speeds are reset (i.e., a bounce is called), the ball is no longer stuck
    		speeds[0] = velocity*Math.cos(angle);
    		speeds[1] = velocity*Math.sin(angle);
    		stuck = false;
    	}
    	
    	public boolean getStuck() {return stuck;}
    	
    	public void setStuck(boolean b) {stuck = b;}
    	
    }    
    
    private class Life extends ImageView {
    	public Life(int pos) {
    		super(heart);
    		setFitWidth(UI_SIZE / 4);
            setFitHeight(UI_SIZE / 4);
            setX(XSIZE + pos * UI_SIZE / 4 - this.getFitWidth() / 2);
            setY(YSIZE / 5);
            active = true;
    	}
    	
    	public boolean isActive() {return active;}
    	public void setActive(boolean b) {active = b;}
    	
    	private boolean active;
    	
    }
    
    private class Power extends ImageView {    	
    	public Power(int type, Brick owner) {		// 1 is extra ball, 2 is slower ball, 3 is bigger paddle, 4 is smaller paddle, 5 is faster ball, 6 is reverse controls
    		super();
    		if(type < 4) this.setImage(powerUp);
    		else this.setImage(powerDown);
    		this.type = type;
    		this.owner = owner;
    		root.getChildren().add(this);
    		speed = 0;
    	}
    	
    	private int type;
    	private Brick owner;
    	private double speed;
    	
    	public void updatePos(double elapsedTime) {
    		if(speed == 0) {
    			setX(owner.getX() + owner.getWidth() / 2 - this.getBoundsInParent().getWidth() / 2);
    			setY(owner.getY() + owner.getHeight() / 2 - this.getBoundsInParent().getHeight() / 2);
    		} else {
    			setY(this.getY() + elapsedTime * speed);
    		}
    	}
    	
    	public void startFall() {speed = FALL_SPEED;}
    	
    	public double getSpeed() {return speed;}
    	
    	public int getType() {return type;}
    	
    	public Brick getOwner() {return owner;}
    }
    
    
}



