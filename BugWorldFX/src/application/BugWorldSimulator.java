package application;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

// issue: resizing stage does not affect bounds and therefore bug movement
// solution: declare Bounds (property used by update()) inside KeyFrame handler

//issue: plants spawn inside each other and bugs move through each other and plants

//issue: plants can sometimes continue to be eaten when the bug is out of range
//
//issue: white circles behind bug objects

//TODO: move objects to groups
//TODO: allow user input for number of bugs/plants
//TODO: fix collision
//TODO: Comment and tidy up code

public class BugWorldSimulator extends Application {

	private static BorderPane canvas;
	private static int defaultWidth = 400;
	private static int defaultHeight = 300;
	
	private ArrayList<Bug> bugs = new ArrayList<>();
	private ArrayList<Plant> plants = new ArrayList<>();
	private ArrayList<WorldObject> allObjects = new ArrayList<>();

	@Override
	public void start(final Stage primaryStage) {
		//	Load bug and plant images
		Image bug = new Image("/beetlecartoon.png");
		ImagePattern bugPattern = new ImagePattern(bug);
		
		Image plant = new Image("/bush.png");
		ImagePattern plantPattern = new ImagePattern(plant);

		// Create 10 bug and plant objects
		this.addBugs(10, bugPattern);
		this.addPlants(10, plantPattern);

		// Create controls
		HBox controls = new HBox();
		controls.setPadding(new Insets(0,5,0,5));
		controls.setSpacing(5);
		// Add Play/Pause button		
		Image imgPlayPause = new Image("/play-pause.png");
		ImageView playPauseView = new ImageView(imgPlayPause);
		playPauseView.setFitWidth(20);
		playPauseView.setFitHeight(20);
		Image imgPlay = new Image("/play.png");
		ImageView playView = new ImageView(imgPlay);
		playView.setFitWidth(20);
		playView.setFitHeight(20);
		Image imgPause = new Image("/pause.png");
		ImageView pauseView = new ImageView(imgPause);
		pauseView.setFitWidth(20);
		pauseView.setFitHeight(20);
		Button play = new Button("");
		play.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000");
		play.setGraphic(playPauseView);
		// Add Quit button
		Image imgQuit = new Image(getClass().getResourceAsStream("exit (1).png"));
		ImageView quitView = new ImageView(imgQuit);
		quitView.setFitWidth(20);
		quitView.setFitHeight(20);
		Button quit = new Button("");
		quit.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		quit.setGraphic(quitView);
		// Add reset button
		Image imgReset = new Image("/reset3.png");
		ImageView resetView = new ImageView(imgReset);
		resetView.setFitWidth(20);
		resetView.setFitHeight(20);
		Button reset = new Button("");
		reset.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		reset.setGraphic(resetView);
		// Add to panel
		controls.getChildren().addAll(play, reset, quit);

		// Add bug pane
		Pane pane = new Pane();
//		pane.setStyle("-fx-background-color: #999999");
		pane.getChildren().addAll(bugs);
		pane.getChildren().addAll(plants);
		BackgroundImage grass = new BackgroundImage(new Image("/grasstexture.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		pane.setBackground(new Background(grass));

		//sets canvas properties and aligns controls
		canvas = new BorderPane();
		canvas.setCenter(pane);
		canvas.setBottom(controls);	

		//sets button attributes
		controls.setAlignment(Pos.CENTER);
		play.setAlignment(Pos.CENTER);

		//creates scene
		final Scene scene = new Scene(canvas, defaultWidth, defaultHeight);
		
		//creates frame and animation handler
		KeyFrame frame = new KeyFrame(Duration.millis(30), new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				for (Bug b : bugs) {
					final Bounds bounds = pane.getBoundsInLocal();
					b.update(bounds, bugs, plants);
				}
				for (Plant p : plants) {
					p.update();
				}
			}
		});

		final Timeline timeline = new Timeline(frame);
		timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);

		//sets control buttons
		play.setOnAction(new EventHandler<ActionEvent>() {
			int count = 0;
			@Override
			public void handle(ActionEvent event) {
				if (count%2 == 0) {
					timeline.play();
					play.setGraphic(pauseView);
				} else {
					timeline.pause();
					play.setGraphic(playView);
				}
				count++;
			}
		});
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				primaryStage.close();
			}
		});
		reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//clear all groups
				bugs.clear();
				plants.clear();
				allObjects.clear();
				pane.getChildren().clear();
				// Create 10 bug objects
				addBugs(10, bugPattern);
				// Create 10 plant objects
				addPlants(10, plantPattern);
				pane.getChildren().addAll(bugs);
				pane.getChildren().addAll(plants);
			}
		});

		primaryStage.setTitle("Bug World");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void addBugs(int num, ImagePattern bugPattern) {
		for (int i=0; i<num; i++) {
			Bug bug = new Bug(getRandomX(), getRandomY(), 10);
			bug.setFill(bugPattern);
			bugs.add(bug);
			allObjects.add(bug);
		}
	}

	public void addPlants(int num, ImagePattern plantPattern) {
		for (int i=0; i<num; i++) {
			Plant plant = new Plant(getRandomX(), getRandomY(), getRandomRadius());
			plant.setFill(plantPattern);
			plants.add(plant);
			allObjects.add(plant);
		}
	}

	// Generate radius for plant
	public double getRandomRadius() {
		int min = 10;
		int max = 20;
		int range = (max-min);
		return (Math.random() * range) + min;
	}

	public double getRandomX() {
		double min = 10; 
		double max = 380;
		double range = (max - min);
		return (Math.random() * range) + min;
	}

	public double getRandomY() {
		double min = 10;
		double max = 235;
		double range = (max - min);
		return (Math.random() * range) + min; 
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
