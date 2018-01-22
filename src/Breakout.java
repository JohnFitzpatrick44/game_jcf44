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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Breakout extends Application {
	
	public static final String TITLE = "Breakout";
    public static final int XSIZE = 800;
    public static final int YSIZE = 600;
    public static final int UI_SIZE = 100;
    public static final int FRAMES_PER_SECOND = 100;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.rgb(243, 203, 111);
    public static final int MAX_PADDLE_SPEED = 400;
    public static final int PADDLE_DRAG = MAX_PADDLE_SPEED * 10;
    public static final int BRICK_DRAG = 300;
    public static final int PADDLE_ACCELERATION = 1000;
    public static final double INIT_BALL_SPEED = 500;
    public static final int BALL_RADIUS = 5;
    public static final int BRICK_CURVE = 2;
    public static final String HEART_IMAGE = "heart.png";
    public static final String POWER_UP_IMAGE = "extraballpower.gif";
    public static final String POWER_DOWN_IMAGE = "sizepower.gif";
    public static final String LEVEL_FILE = "LevelFile.txt";
    public static final double FALL_SPEED = 300;
    //public static final Color BRICK_COLOR_1 = Color.rgb(251, 139, 76);
    //public static final Color BRICK_COLOR_2 = Color.rgb(224, 98, 29);
    //public static final Color BRICK_COLOR_3 = Color.rgb(193, 68, 49);
    //public static final Color UI_COLOR = Color.rgb(87, 87, 91);
    public static final Color[] COLOR_PALETTE = new Color[] {Color.rgb(251, 139, 76), Color.rgb(224, 98, 29), Color.rgb(193, 68, 49), Color.rgb(87, 87, 91)};
    public static final double pi = Math.PI;
    //UI, B1, B2, B3
    
    private boolean leftKeyHeld = false;    
    private boolean rightKeyHeld = false;
    private ArrayList<Ball> balls;
    private ArrayList<Brick> bricks;
    private Group root;
	private Label score;
	private Label level;
	private Image heart = new Image(getClass().getClassLoader().getResourceAsStream(HEART_IMAGE));
	private Image powerUp = new Image(getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE));
	private Image powerDown = new Image(getClass().getClassLoader().getResourceAsStream(POWER_DOWN_IMAGE));
	private int numPermanents = 0;
	private Life[] hearts = new Life[3];
	private int scoreValue = 0;
	private ArrayList<Power> powers;
	private KeyCode left = KeyCode.LEFT;
	private KeyCode right = KeyCode.RIGHT;
	private BufferedReader br;
	private int currentLevel = 0;
	private Paddle paddle;
	private boolean movable = false;
	private boolean gameDone = false;
	
	
	@Override
	public void start (Stage stage) {
        Scene myScene = setupGame(XSIZE + UI_SIZE, YSIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();
        
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
                                      e -> step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }
	
	private Scene setupGame (int width, int height, Paint background) {
		
		root = new Group();
        paddle = new Paddle();
        balls = new ArrayList<Ball>();
        Scene scene = new Scene(root, width, height, background);
        
        resetBR();        
                
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        
        setUI(scene);
        
        return scene;
    }

	private void setSplash(Scene scene) {
		ArrayList<Node> uiNodes = new ArrayList<Node>();
        
        Rectangle splash = new Rectangle(XSIZE+UI_SIZE, YSIZE);
        Label howTo = new Label("How to Play");
        Label instructions = new Label("Bounce the ball off the paddle to break the bricks!\n"
        		+ "Press space to start.\n"
        		+ "Use the left and right arrows to move the paddle.\n"
        		+ "Hold space to make the paddle sticky.\n"
        		+ "Release space to release the ball.\n"
        		+ "Break all the bricks to win! You have 3 lives.\n"
        		+ "Check out the README for cheats.\n\nHave Fun!");
        howTo.setTextFill(Color.WHITE);
        instructions.setTextFill(Color.WHITE);
        splash.setFill(COLOR_PALETTE[3]);
        
        howTo.setAlignment(Pos.CENTER);
        instructions.setAlignment(Pos.CENTER);
        howTo.setTextAlignment(TextAlignment.CENTER);
        instructions.setTextAlignment(TextAlignment.CENTER);
        
        howTo.setStyle("-fx-font: 64 courier;");
        instructions.setStyle("-fx-font: 32 courier;");
        
        howTo.setPrefWidth((XSIZE+UI_SIZE)/2);
        howTo.setLayoutY(YSIZE/20);
        howTo.setLayoutX((XSIZE+UI_SIZE)/2-howTo.getPrefWidth()/2);
        
        instructions.setPrefWidth(XSIZE);
        instructions.setLayoutY(YSIZE/4);
        instructions.setLayoutX((XSIZE+UI_SIZE)/2 - instructions.getPrefWidth()/2);

        uiNodes.add(splash);
        uiNodes.add(howTo);
        uiNodes.add(instructions);
        
        scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
        	@Override
        	public void handle(KeyEvent ke) {
        		root.getChildren().removeAll(uiNodes);
        	}
        });
        
        root.getChildren().addAll(uiNodes);

        
        
        
	}
	
	private void setUI(Scene scene) {
		
		Rectangle uiBackground = new Rectangle(XSIZE, 0, UI_SIZE, YSIZE);
        uiBackground.setFill(COLOR_PALETTE[3]);
        root.getChildren().add(uiBackground);
        
        score = new Label("Score\n0");
        processLabel(score, 32, UI_SIZE);
        score.setLayoutY(YSIZE / 40);
        
        level = new Label("Level\n0");
        processLabel(level, 32, UI_SIZE);
        level.setLayoutY(YSIZE *4 / 9);

        Label livesText = new Label("Lives");
        processLabel(livesText, 32, UI_SIZE);
        livesText.setLayoutY(YSIZE / 4);
       
        Label moveButtonText = new Label("Moving\nBlocks");
        processLabel(moveButtonText, 28, UI_SIZE);
        moveButtonText.setLayoutY(YSIZE *2/3);
        
        for(int k = 0; k < hearts.length; k++) {
        	hearts[k] = new Life(k+1);
        	root.getChildren().add(hearts[k]);
        }

        setField();

        Button movableButton = new Button();
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
        		root.requestFocus();
                
        	}
        });	
        
        root.getChildren().addAll(movableButton, score, level, livesText, moveButtonText);
        root.requestFocus();
        setSplash(scene);
	}
	
	
    private void step (double elapsedTime) {
    	
    	if(gameDone) return;
    	
    	updatePaddle(elapsedTime);
    	
    	updateBalls(elapsedTime);
    	
    	if(movable) updateBricks(elapsedTime);
    	
    	updatePowers(elapsedTime);
    	
    	score.setText("Score\n" + scoreValue);
    	
    	level.setText("Level\n" + currentLevel);


    }

    private void updatePaddle(double elapsedTime) {
        if(paddle.getXSpeed() > 0 && !rightKeyHeld) {
        	paddle.setXSpeed(paddle.getXSpeed() - PADDLE_DRAG * elapsedTime);
        	paddle.setXSpeed(Math.max(0, paddle.getXSpeed()));
        } else if(paddle.getXSpeed() < 0 && !leftKeyHeld) {
        	paddle.setXSpeed(paddle.getXSpeed() + PADDLE_DRAG * elapsedTime);
        	paddle.setXSpeed(Math.min(0, paddle.getXSpeed()));
        }
    	if((paddle.getX() > XSIZE - paddle.getWidth())) {
    		paddle.setX(XSIZE - paddle.getWidth());
    	} else if(paddle.getX() < 0) {
    		paddle.setX(0);
    	} else {
    		paddle.setX(paddle.getX() + elapsedTime*paddle.getXSpeed());
    	}
    	if(paddle.getYSpeed() != 0 || paddle.getY() < YSIZE - 2*paddle.getHeight()) {
    		
    		paddle.setYSpeed(paddle.getYSpeed() + PADDLE_ACCELERATION*elapsedTime);
    		if(paddle.getY() > YSIZE - 2*paddle.getHeight())  {
    			paddle.setYSpeed(0);
    			paddle.setY(YSIZE - 2*paddle.getHeight());
    		} else paddle.setY(paddle.getY() + elapsedTime*paddle.getYSpeed());
    	}
    }
    
    private void updateBalls(double elapsedTime) {

    	for(int k = 0; k < balls.size(); k++) {
        	
    		Ball active = balls.get(k);
    		double x = active.getCenterX();
        	double y = active.getCenterY();
        	double r = BALL_RADIUS;
    		
        	if(!active.getStuck()) {
        		if(x + r >= XSIZE) active.bounceX(-1); 
        		else if(x - r < 0) active.bounceX(1);
            
        		if(y - r >= YSIZE) {
        			balls.remove(k);
	        		if(balls.isEmpty()) {
	        			if(loseLife()) gameOver();		// LOSE LIFE
	        			balls.add(new Ball());
	        		}
	        	} else if(y - r < 0) {
	        		active.bounceY();
	        	}
        		if(active.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            		if(y + r > paddle.getY() || y > paddle.getY() + paddle.getHeight()/2) active.setAngle((((x-(paddle.getX()+paddle.getWidth()/2))/paddle.getWidth()-0.5)*pi*.6)-0.2*pi);
            		if(paddle.getSticky()) active.setStuck(true);
            	}
        	}
        	
        	active.checkBrickCollisions();
        	
        	if(bricks.size() - numPermanents == 0) nextLevel();
        	
        	if(active.getStuck()) active.stick();
        	
        	active.setCenterX(x + active.getSpeeds()[0] * elapsedTime);
            active.setCenterY(y + active.getSpeeds()[1] * elapsedTime);
            if(active.getStuck() && y + r > paddle.getY()) active.setCenterY(paddle.getY() - r);
            
    	}
    }
    
    private void updateBricks(double elapsedTime) {
    	for(Brick b : bricks) {
    		if(b.getSpeed() > 0) {
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
    		if(b.getSpeed() != 0) b.checkBrickCollisions();
    	}
    	
    }
    
    private void updatePowers(double elapsedTime) {
    	for(int k = 0; k < powers.size(); k++) {
    		Power p = powers.get(k);
    		if(p.getOwner().getDurability() == 0 && p.getSpeed() == 0) {p.startFall();}
    		p.updatePos(elapsedTime);
    		if(paddle.getBoundsInParent().intersects(p.getBoundsInParent())) {
    			root.getChildren().remove(p);
    			powers.remove(k);
    			if(p.getType() < 4) scoreValue += 200;
    			else scoreValue -= 200;
    			p.trigger();
    		}
    	}
    }
    
    
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
        	if(paddle.getYSpeed() == 0) paddle.setYSpeed(-FALL_SPEED);
        } else if (code == KeyCode.L) {
        	resetLives();
        } else if (code == KeyCode.P) {
        	nextLevel();
        } else if (code == KeyCode.EQUALS) {
        	for(Ball b : balls) {
				b.setVelocity(b.getVelocity() * 1.25);
				if(b.getVelocity() > INIT_BALL_SPEED * 3) b.setVelocity(INIT_BALL_SPEED * 3);
				b.setSpeeds();
		    }
        } else if (code == KeyCode.MINUS) {
        	for(Ball b : balls) {
				b.setVelocity(b.getVelocity() * .75);
				if(b.getVelocity() < INIT_BALL_SPEED / 5) b.setVelocity(INIT_BALL_SPEED / 5);
				b.setSpeeds();
		    }
        } else if (code == KeyCode.OPEN_BRACKET) {
        	double paddleXPos = paddle.getX() + paddle.getWidth()/2;
			paddle.setWidth(paddle.getWidth()*.75);
			if(paddle.getWidth() < (XSIZE)/10) paddle.setWidth((XSIZE)/10);
			paddle.setX(paddleXPos - paddle.getWidth()/2);
        } else if (code == KeyCode.CLOSE_BRACKET) {
        	double paddleXPos = paddle.getX() + paddle.getWidth()/2;
			paddle.setWidth(paddle.getWidth()*1.25);
			if(paddle.getWidth() > (XSIZE)/2) paddle.setWidth((XSIZE)/2);
			paddle.setX(paddleXPos - paddle.getWidth()/2);
        } else if (code == KeyCode.O) {
        	balls.add(new Ball());
        } else if (code.isDigitKey()) {
        	String name = code.name();
        	jumpLevel(Character.getNumericValue(name.charAt(name.length()-1)));
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
    			paddle.setSticky(false);
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
    
    
    private void setField() {
        balls.add(new Ball());
        powers = new ArrayList<Power>();
        bricks = new ArrayList<Brick>();
        try {
			readLevel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        for(int k = 0; k < bricks.size(); k++) {
        	bricks.get(k).setX(k%10 * (XSIZE)/10 + (XSIZE) / 264);
        	bricks.get(k).setY((int) k/10 * YSIZE / 12 + YSIZE / 60);
        }
                
    }
    
    private void readLevel() throws IOException{
    	String str;
    	numPermanents = 0;
    	while((str = br.readLine()) != null) {
    		String[] brickCodes = str.split(" ");
    		if(brickCodes[0].equals("-")) {
    			currentLevel ++;
    			return;
    		}
    		for(int k = 0; k < brickCodes.length; k++) {
    			char[] code = brickCodes[k].toCharArray();
    			int type = 0;
    			if(code[0] == 'p') type = ThreadLocalRandom.current().nextInt(1, 4);
    			else if(code[0] == 'd') type = ThreadLocalRandom.current().nextInt(4, 7);
    			else if(code[0] == 't') type = 7;
    			Brick toAdd = new Brick((XSIZE) / 11, YSIZE / 20, Character.getNumericValue(code[1]), type);
    			bricks.add(toAdd);
    			root.getChildren().add(toAdd);		// 0 is normal type, 1-6 are powers, -1 is top only, durability 4 is permanent
    			if(toAdd.getDurability() >= 4 && toAdd.getType() < 7) numPermanents += 1;
            	toAdd.setArcHeight(BRICK_CURVE);
            	toAdd.setArcWidth(BRICK_CURVE);
            	
            	if(toAdd.getType() > 0 && toAdd.getType() < 7) powers.add(new Power(toAdd.getType(), toAdd));
    		}
    	}
    	gameOver();
    }
    
    private void resetBR() {
    	try{ 
        	FileInputStream fstream = new FileInputStream(LEVEL_FILE);
        	DataInputStream in = new DataInputStream(fstream);
        	br = new BufferedReader(new InputStreamReader(in));
        	currentLevel = 0;
        } catch(FileNotFoundException e) {e.printStackTrace();}
    }
    
    
    private boolean loseLife() {
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
    
    private void resetLives() {
    	for(Life heart : hearts) {
    		if(!heart.isActive()) {
    			heart.setActive(true);
    			root.getChildren().add(heart);
    		}
    	}
    }
    
    private void gameOver() {
    	gameDone = true;
    	setGameOverUI();
        
        
        for(Life l : hearts) {
        	if(l.isActive()) root.getChildren().remove(l);
        	l.setActive(false);
        }

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
    	for(int k = bricks.size()-1; k >= 0; k--) {
    		root.getChildren().remove(bricks.get(k));
    		bricks.remove(k);
    	}
    	setField();
    }
    
    private void jumpLevel(int levelNumber) {
    	if(levelNumber > currentLevel) {
    		for(int k = 0; k < levelNumber - currentLevel; k++) {
    			nextLevel();
    		}
    	} else {
    		resetBR();
    		for(int k = 0; k < levelNumber; k++) {
    			nextLevel();
    		}
    	}
    	
    }
    
    private void setGameOverUI() {
    	
    	ArrayList<Node> gameOverNodes = new ArrayList<Node>();
    	Rectangle gameOverScreen = new Rectangle(XSIZE + UI_SIZE, YSIZE);
        gameOverScreen.setFill(COLOR_PALETTE[3]);
        Button retryButton = new Button("Retry");
        Button exitButton = new Button("Exit");
        
        boolean won = false;
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
        	@Override
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
        	@Override
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
    
    private void processLabel(Label l, int size, int width) {
    	l.setTextAlignment(TextAlignment.CENTER);
    	l.setAlignment(Pos.CENTER);
        l.setTextFill(Color.WHITE);
        l.setStyle("-fx-font: "+ Integer.toString(size) + " courier;");
        l.setPrefWidth(width);
        l.setLayoutX(XSIZE + UI_SIZE / 2 - l.getPrefWidth() / 2);
    }
    

    public static void main (String[] args) {
        launch(args);
    }
    
    
    
    private class Brick extends Rectangle {
    	public Brick(int width, int height, int durability, int type) {	//Type of 0 is neutral, 1-3 is power up, 4-6 is down, 7 is top block
    		super(width, height);
    		this.durability = durability;
    		if(this.durability > 4) this.durability = 4;
    		this.type = type;
    		rePaint();
    		speed = 0;
    	}
    	
    	int durability;
    	int type;
    	double speed;
    	
    	
    	public int getType() {
    		return type;
    	}
    	
    	public int reduceDurability(Ball b) {
    		if(type == 7 && b.getCenterY() > getY() + getHeight()) return durability;
    		if(durability <= 3) {
    			durability --;
    			scoreValue += 10;
    		}
    		if(durability != 0) rePaint();
    		return durability;
    	}
    	
    	public void rePaint() {
    		if(type == 7) {
    			Stop[] stops = new Stop[] { new Stop(0, COLOR_PALETTE[durability-1]), new Stop(1, COLOR_PALETTE[3])};
    			LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
    			this.setFill(lg1);
    		} else {
	    		this.setFill(COLOR_PALETTE[Math.max(0,durability-1)]);
    		}
    	}
    	
    	public void checkBrickCollisions() {
    		for(Brick b : bricks) {
    			if(b != this && b.getBoundsInParent().intersects(this.getBoundsInParent())) {
    				b.setSpeed(this.getSpeed());
    				if(this.getX() > b.getX() && this.getX() < b.getX() + b.getWidth()) this.setX(b.getX() + b.getWidth());
    				if(this.getX() < b.getX() && this.getX() + this.getWidth() > b.getX()) this.setX(b.getX() - this.getWidth());
    				this.setSpeed(0.9*this.getSpeed());
    			}
    		}
    	}
    	
    	public int getDurability() {return durability;}
    	
    	public void setSpeed(double xs) {
    			if(durability >= 4) speed = 0;
    			else speed = xs;
    		}
    	public double getSpeed() {return speed;}

    }
    
    private class Ball extends Circle {
    	public Ball() {
    		super(BALL_RADIUS, COLOR_PALETTE[3]);
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
    		speeds[0] = paddle.getXSpeed();
    		speeds[1] = paddle.getYSpeed();
    		if(paddle.getX() <= 0 || paddle.getX() + paddle.getWidth() >= XSIZE) speeds[0] = 0;
    		stuck = true;
    	}
    	
    	public void checkBrickCollisions() {
    		for(int k = 0; k < bricks.size(); k++) {
        		Brick b = bricks.get(k);
        		
        		if(b.getBoundsInParent().intersects(getBoundsInParent())) {
        			if(b.reduceDurability(this) == 0) scoreValue+=90;
        			b.setSpeed(this.getSpeeds()[0]);
        			if(getCenterX() > b.getX() + b.getWidth()) bounceX(1);
        			else if(getCenterX() < b.getX()) bounceX(-1);
        			if(getCenterY() > b.getY() + b.getHeight()) bounceY(1);
        			else if(getCenterY() < b.getY()) bounceY(-1);
        			
        		}
        		if(b.getDurability() <= 0) {
    				root.getChildren().remove(b);
    				bricks.remove(k);
    			}
        		
        	}
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
            setY(YSIZE / 3);
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
    	
    	public void trigger() {
    		if(type == 1) balls.add(new Ball());
			else if(type == 2) {
					for(Ball b : balls) {
						b.setVelocity(b.getVelocity() * .75);
						if(b.getVelocity() < INIT_BALL_SPEED / 5) b.setVelocity(INIT_BALL_SPEED / 5);
						b.setSpeeds();
				    }
			} else if(type == 3) {
				double paddleXPos = paddle.getX() + paddle.getWidth()/2;
				paddle.setWidth(paddle.getWidth()*1.25);
				if(paddle.getWidth() > (XSIZE)/2) paddle.setWidth((XSIZE)/2);
				paddle.setX(paddleXPos - paddle.getWidth()/2);
			} else if(type == 4) {
				double paddleXPos = paddle.getX() + paddle.getWidth()/2;
				paddle.setWidth(paddle.getWidth()*.75);
				if(paddle.getWidth() < (XSIZE)/10) paddle.setWidth((XSIZE)/10);
				paddle.setX(paddleXPos - paddle.getWidth()/2);
			} else if(type == 5) {
				for(Ball b : balls) {
					b.setVelocity(b.getVelocity() * 1.25);
					if(b.getVelocity() > INIT_BALL_SPEED * 3) b.setVelocity(INIT_BALL_SPEED * 3);
					b.setSpeeds();
			    }
			} else if(type == 6) {
				switchControls();
			}
    	}
    	
    	public void startFall() {speed = FALL_SPEED;}
    	
    	public double getSpeed() {return speed;}
    	
    	public int getType() {return type;}
    	
    	public Brick getOwner() {return owner;}
    	
    }
    
    private class Paddle extends Rectangle {
    	public Paddle() {
    		super(XSIZE/5,YSIZE/60);
    		xSpeed = 0;
    		ySpeed = 0;
    		sticky = true;
    		root.getChildren().add(this);
    		resetPosition();
    		this.setArcHeight(10);	//Globalify
            this.setArcWidth(20);
            this.setFill(COLOR_PALETTE[3]);
    	}
    	
    	double xSpeed;
    	double ySpeed;
    	boolean sticky;
    	
    	public void resetPosition() {
    		this.setX(XSIZE/2 - this.getWidth()/2);
    		this.setY(YSIZE - 2*this.getHeight());
    	}
    	
    	
    	public void setXSpeed(double xs) {this.xSpeed = xs;}
    	public double getXSpeed() {return xSpeed;}
    	public void setYSpeed(double ys) {this.ySpeed = ys;}
    	public double getYSpeed() {return ySpeed;}
    	public void setSticky(boolean s) {this.sticky = s;}
    	public boolean getSticky() {return sticky;}
    }
    
}



