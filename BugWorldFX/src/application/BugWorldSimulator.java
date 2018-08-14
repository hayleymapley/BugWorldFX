package application;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// issue: resizing stage does not affect bounds and therefore bug movement
// solution: declare Bounds (property used by update()) inside KeyFrame handler

//issue: plants spawn inside each other and bugs move through each other and plants
//solution: added collision checking methods using pythagoras

//issue: bugs can still spawn inside of plants

//issue: plants can sometimes continue to be eaten when the bug appears out of range (because of circle)
//
//issue: white circles behind bug objects
//solution: use .png file

//TODO: move objects to groups
//TODO: fix collision
//TODO: Comment and tidy up code
//TODO: Add dropshadow

public class BugWorldSimulator extends Application {

	private static BorderPane canvas;
	Pane worldPane = new Pane();
	private static int defaultWidth = 600;
	private static int defaultHeight = 500;

	private ArrayList<Bug> bugs = new ArrayList<>();
	private Group bugsGroup = new Group();
	private ArrayList<Plant> plants = new ArrayList<>();
	private Group plantsGroup = new Group();
	private ArrayList<WorldObject> allObjects = new ArrayList<>();
	private Group worldObjectsGroup = new Group();
	
	private int currentNumBugs = 0;
	private int currentNumPlants = 0;

	//	Load bug and plant images
	Image bug = new Image("/beetlecartoon.png");
	ImagePattern bugPattern = new ImagePattern(bug);

	Image plant = new Image("/bush.png");
	ImagePattern plantPattern = new ImagePattern(plant);

	@Override
	public void start(final Stage primaryStage) {
		// Create controls
		HBox controls = new HBox();
		controls.setPadding(new Insets(25,25,25,25));
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
		worldPane.getChildren().addAll(bugs);
		worldPane.getChildren().addAll(plants);
		BackgroundImage grass = new BackgroundImage(new Image("/grasstexture.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		worldPane.setBackground(new Background(grass));

		//Add menu pane
		VBox menu = new VBox();
		menu.setPrefSize(defaultWidth/2, defaultHeight);
		menu.setPadding(new Insets(25, 25, 25, 25));
		BackgroundImage geometric = new BackgroundImage(new Image("/menurotate.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		menu.setBackground(new Background(geometric));

		Text title = new Text("BUG WORLD\n");
		title.setFont(Font.font(30));
		title.setFill(Color.WHITE);
		title.setTextAlignment(TextAlignment.CENTER);

		Slider bugSlider = new Slider();
		bugSlider.setMin(0);
		bugSlider.setMax(30);
		bugSlider.setValue(0);
		bugSlider.setShowTickLabels(true);
		bugSlider.setShowTickMarks(true);
		bugSlider.setMajorTickUnit(10);
		bugSlider.setMinorTickCount(1);
		bugSlider.setBlockIncrement(1);
		//		bugSlider.setSnapToTicks(true);
		Label numBugs = new Label("\nNumber of bugs: " + (int)bugSlider.getValue());
		numBugs.setTextFill(Color.WHITE);
		bugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				numBugs.setText("\nNumber of bugs: " + newValue.intValue());
				currentNumBugs = newValue.intValue();
			}
		});

		Slider plantSlider = new Slider();
		plantSlider.setMin(0);
		plantSlider.setMax(30);
		plantSlider.setValue(0);
		plantSlider.setShowTickLabels(true);
		plantSlider.setShowTickMarks(true);
		plantSlider.setMajorTickUnit(10);
		plantSlider.setMinorTickCount(1);
		plantSlider.setBlockIncrement(1);
		//		plantSlider.setSnapToTicks(true);
		Label numPlants = new Label("\nNumber of plants: " + (int)plantSlider.getValue());
		numPlants.setTextFill(Color.WHITE);
		plantSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				numPlants.setText("\nNumber of plants: " + newValue.intValue());
				currentNumPlants = newValue.intValue();
			}
		});

		//needs gap here
		Button generate = new Button("Generate world");
		generate.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reset();
				addBugs(currentNumBugs, bugPattern);
				addPlants(currentNumPlants, plantPattern);
			}
		});

		menu.getChildren().addAll(title, numBugs, bugSlider, numPlants, plantSlider, generate, controls);
		menu.setAlignment(Pos.CENTER);
		menu.setSpacing(10);

		//sets canvas properties and aligns controls
		canvas = new BorderPane();
		canvas.setCenter(worldPane);
		canvas.setLeft(menu);

		//sets button attributes
		controls.setAlignment(Pos.CENTER);

		//creates scene
		final Scene scene = new Scene(canvas, defaultWidth+(defaultWidth/3), defaultHeight);

		//creates frame and animation handler
		KeyFrame frame = new KeyFrame(Duration.millis(30), new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				for (Bug b : bugs) {
					final Bounds bounds = worldPane.getBoundsInLocal();
					b.update(bounds, bugs, plants, allObjects);
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
				reset();
			}
		});

		primaryStage.setTitle("Bug World");
		primaryStage.setScene(scene);
		primaryStage.setMaxWidth(defaultWidth+(defaultWidth/3)+100);
		primaryStage.setMaxHeight(defaultHeight+125);
		primaryStage.show();
	}

	public void reset() {
		bugs.clear();
		plants.clear();
		allObjects.clear();
		worldPane.getChildren().clear();
		
		addBugs(currentNumBugs, bugPattern);
		addPlants(currentNumPlants, plantPattern);
		worldPane.getChildren().addAll(bugs);
		worldPane.getChildren().addAll(plants);
	}

	public void addBugs(int num, ImagePattern bugPattern) {
		for (int i=0; i<num; i++) {
			double x = getRandomX();
			double y = getRandomY();
			Bug bug = new Bug(10);
			while (checkCollision(bug, x, y, allObjects)) {
				x = getRandomX();
				y = getRandomY();
			}
			bug.relocate(x, y);
			bug.setFill(bugPattern);
			bugs.add(bug);
			allObjects.add(bug);
		}
	}

	public void addPlants(int num, ImagePattern plantPattern) {
		for (int i=0; i<num; i++) {
			Plant plant = new Plant(getRandomRadius());
			double x = getRandomX();
			double y = getRandomY();
			while (checkCollision(plant, x, y, allObjects)) {
				x = getRandomX();
				y = getRandomY();
			}
			plant.relocate(x, y);
			plant.setFill(plantPattern);
			plants.add(plant);
			allObjects.add(plant);
		}
	}
	
//	public boolean checkSpawnCollision(WorldObject w, ArrayList<WorldObject> allObjects) {
//		boolean collisionDetected = false;
//		for (WorldObject o : allObjects) {
//			if (o != w) {
//				Shape intersect = Shape.intersect(w, o);
//				if (intersect.getBoundsInLocal().getWidth() != -1) {
//					collisionDetected = true;
//				}
//			}
//		}
//		return collisionDetected;
//	}
	
	// Generate radius for plant
	public double getRandomRadius() {
		int min = 5;
		int max = 25;
		int range = (max-min);
		return (Math.random() * range) + min;
	}

	//Get random X coordinate
	public double getRandomX() {
		double min = 10; 
		double max = defaultWidth-(defaultWidth/3)-30;
		double range = (max - min);
		return (Math.random() * range) + min;
	}

	//Get random Y coordinate
	public double getRandomY() {
		double min = 10;
		double max = defaultHeight - 30;
		double range = (max - min);
		return (Math.random() * range) + min; 
	}
	
	public boolean checkCollision(WorldObject o, double potentialX, double potentialY, ArrayList<WorldObject> allObjects) {
		for (WorldObject w : allObjects) {
			if (w != o) {
				if (this.calculateCollision(o, potentialX, potentialY, w)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean calculateCollision(WorldObject o, double potentialX, double potentialY, WorldObject w) {
		//inspired by Oliver
		double diffX = w.getLayoutX() - potentialX;
		double diffY = w.getLayoutY() - potentialY;
		double distance = Math.sqrt((diffX*diffX)+(diffY*diffY));
		double minDistance = w.getRadius() + o.getRadius();
		return (distance < minDistance);
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
