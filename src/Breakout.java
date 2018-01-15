import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
    public static final Paint BACKGROUND = Color.AZURE;
    public static final int MAX_PADDLE_SPEED = 200;
    public static final int PADDLE_DRAG = 800;
    public static final double INIT_BALL_SPEED = 250;
    public static final int NUM_BRICKS = 40;
    public static final int BALL_RADIUS = 5;
    public static final int BRICK_CURVE = 5;
    public static final String HEART_IMAGE = "heart.png";
    
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
	private Life[] hearts = new Life[3];
    
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
        
        balls = new ArrayList<Ball>();
        balls.add(new Ball());
        paddle = new Rectangle(width / 5, 10);
        

        root.getChildren().add(paddle);
        
        paddle.setY(height - 2*paddle.getBoundsInParent().getHeight());
        paddle.setX(width/2 - paddle.getBoundsInParent().getWidth()/2);
        paddle.setArcHeight(10);	//Globalify
        paddle.setArcWidth(20);
        
        
        	
       
        bricks = new ArrayList<Brick>();
        for(int k = 0; k < NUM_BRICKS; k++) {
        	bricks.add(new Brick(width / 12, 20, 2, 0)); 			//Can add different colors/types here, for now, just base type w/ 1 durability
        	root.getChildren().add(bricks.get(k));
        	bricks.get(k).setX(k%10 * width/10 + width / 120);
        	bricks.get(k).setY((int) k/10 * height / 15);
        	bricks.get(k).setArcHeight(BRICK_CURVE);
        	bricks.get(k).setArcWidth(BRICK_CURVE);
        }
        
        
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        
        Rectangle uiPane = new Rectangle(width, 0, UI_SIZE, height);
        uiPane.setFill(Color.DARKBLUE);
        score = new Label("Score\n0");
        score.setTextAlignment(TextAlignment.CENTER);
        score.setTextFill(Color.WHITE);
        score.setLayoutY(height / 20);
        score.setStyle("-fx-font: 20 arial;");
        
        Life heart1 = new Life(1);
        Life heart2 = new Life(2);
        Life heart3 = new Life(3);
        hearts[0] = heart1;
        hearts[1] = heart2;
        hearts[2] = heart3;
        
        
        root.getChildren().add(uiPane);
        root.getChildren().add(score);
        root.getChildren().addAll(heart1, heart2, heart3);
        
                
        
        
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
        	}
        	
        	for(int kk = 0; kk < bricks.size(); kk++) {
        		Brick b = bricks.get(kk);
        		
        		if(b.getBoundsInParent().intersects(active.getBoundsInParent())) {
        			int durability = b.reduceDurability();
        			if(durability == 0) {
        				root.getChildren().remove(b);
        				bricks.remove(kk);
        				
        			}
        			if(x > b.getX() + b.getWidth()) active.bounceX(1);
        			else if(x < b.getX()) active.bounceX(-1);
        			if(y > b.getY() + b.getHeight()) active.bounceY(1);
        			else if(y < b.getY()) active.bounceY(-1);
        			
        			
        		}
        	}
        	
        	active.setCenterX(x + active.getSpeeds()[0] * elapsedTime);
            active.setCenterY(y + active.getSpeeds()[1] * elapsedTime);
    	}

    }

    private void handleKeyInput (KeyCode code) {
        if (code == KeyCode.RIGHT) {
        	paddleSpeed = MAX_PADDLE_SPEED;
        	rightKeyHeld = true;
        } else if (code == KeyCode.LEFT) {
        	paddleSpeed = -MAX_PADDLE_SPEED;
        	leftKeyHeld = true;
        } else if (code == KeyCode.UP) {
        } else if (code == KeyCode.DOWN) {
        }
    }
    
    private void handleKeyRelease(KeyCode code) {
    	if(code == KeyCode.RIGHT) {
    		rightKeyHeld = false;
    	} else if(code == KeyCode.LEFT) {
    		leftKeyHeld = false;
    	}
	}

    private void handleMouseInput (double x, double y) {
        
    }

    private boolean loseLife() {		// returns true when game is over
    	for(int k = 2; k >= 0; k--) {
    		if(hearts[k].isActive()) {
    			hearts[k].setActive(false);
    			root.getChildren().remove(hearts[k]);
    			if(k == 0) return true;
    			return false;
    		}
    	}
    	return true;
    }
    
    
    
    public static void main (String[] args) {
        launch(args);
    }
    
    public class Brick extends Rectangle {
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
    		this.setFill(Color.rgb(ThreadLocalRandom.current().nextInt(256-80*durability,256-80*(durability-1)),ThreadLocalRandom.current().nextInt(256-80*durability,256-80*(durability-1)),ThreadLocalRandom.current().nextInt(256-80*durability,256-80*(durability-1))));
    	}
    	
    	
    	
    }
    
    public class Ball extends Circle {
    	public Ball() {
    		super(BALL_RADIUS, Color.FORESTGREEN);
    		velocity = INIT_BALL_SPEED;
    		angle = 4*pi/3;
    		speeds = new double[2];
    		setSpeeds();
    		setCenterY((YSIZE*2/3));
            setCenterX((XSIZE - UI_SIZE)/2);
            root.getChildren().add(this);
    	}
    	
    	double velocity;
    	double angle;
    	double[] speeds;
    	
    	public void bounceX() {//int direction) {		// -1 Guarantees negative, +1 guarantees positive, 0 is a reversal
    		
    		angle = pi - angle;
    		while(angle < 0) angle += 2*pi;
    		angle = angle%(2*pi);
    		setSpeeds();
    	}
    	
    	public void bounceY() {//int direction) {		// -1 Guarantees negative, +1 guarantees positive, 0 is a reversal

    		angle = 2*pi - angle;
    		while(angle < 0) angle += 2*pi;
    		angle = angle%(2*pi);
    		setSpeeds();
    	}
    	
    	public void bounceX(int direction) {
    		if(angle >= pi/2 && angle <= 3*pi/2) {
    			if(direction > 0) bounceX();
    			else return;
    		} else {
    			if(direction < 0) bounceX();
    			else return;
    		}
    		setSpeeds();
    	}
    	
    	public void bounceY(int direction) {
    		if(angle > pi) {
    			if(direction > 0) bounceY();
    			else return;
    		} else {
    			if(direction < 0) bounceY();
    			else return;
    		}
    		setSpeeds();
    	}
    	
    	public void setAngle(double ang) {
    		angle = ang;
    		while(angle < 0) angle += 2*pi;
    		angle = angle%(2*pi);
    		setSpeeds();
    	}
    	
    	public double[] getSpeeds() {
    		return speeds;
    	}
    	
    	private void setSpeeds() {
    		speeds[0] = velocity*Math.cos(angle);
    		speeds[1] = velocity*Math.sin(angle);
    	}
    }

    
    
    public class Life extends ImageView {
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
    	
    	boolean active;
    	
    }
    
    
    
}



