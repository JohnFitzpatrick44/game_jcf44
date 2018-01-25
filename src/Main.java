import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application{
	private Breakout breakgame;
	public static void main(String[] args) {launch(args);}
	
	@Override
	public void start (Stage stage) {
		breakgame = new Breakout();
        Scene myScene = breakgame.setupGame(Breakout.XSIZE + Breakout.UI_SIZE, Breakout.YSIZE, Breakout.BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(Breakout.TITLE);
        stage.getIcons().add(new Image(Breakout.POWER_UP_IMAGE));
        stage.show();
        KeyFrame frame = new KeyFrame(Duration.millis(Breakout.MILLISECOND_DELAY),
        					e -> breakgame.step(Breakout.SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }
}