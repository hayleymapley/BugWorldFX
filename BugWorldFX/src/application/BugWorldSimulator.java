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

//TODO: add obstacles
//TODO: Comment and tidy up code

public class BugWorldSimulator extends Application {

	private BorderPane canvas = new BorderPane();
	Pane worldPane = new Pane();
	private static final int defaultWidth = 600;
	private static final int defaultHeight = 500;

	private ArrayList<Bug> bugs = new ArrayList<>();
	private ArrayList<Plant> plants = new ArrayList<>();
	private ArrayList<WorldObject> allObjects = new ArrayList<>();

	private int currentNumBugs = 0;
	private int currentNumPlants = 0;
	private int currentNumObstacles = 3;

	//	Load bug and plant images
	private Image bug = new Image("/beetlecartoon.png");
	private ImagePattern bugPattern = new ImagePattern(bug);

	private Image plant = new Image("/bush.png");
	private ImagePattern plantPattern = new ImagePattern(plant);
	
	private Image obstacle = new Image("/obstacle.png");
	private ImagePattern obstaclePattern = new ImagePattern(obstacle); 

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

		// Add buttons to controls panel
		controls.getChildren().addAll(play, reset, quit);

		// Add world pane attributes
		BackgroundImage grass = new BackgroundImage(new Image("/grasstexture.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		worldPane.setBackground(new Background(grass));

		//Add title
		Text title = new Text("BUG WORLD\n");
		title.setFont(Font.font(30));
		title.setFill(Color.WHITE);
		title.setTextAlignment(TextAlignment.CENTER);

		//Add slider for bug input
		Slider bugSlider = new Slider();
		bugSlider.setMin(0);
		bugSlider.setMax(15);
		bugSlider.setValue(0);
		bugSlider.setShowTickLabels(true);
		bugSlider.setShowTickMarks(true);
		bugSlider.setMajorTickUnit(5);
		bugSlider.setMinorTickCount(1);
		bugSlider.setBlockIncrement(1);
		Label numBugs = new Label("\nNumber of bugs: " + (int)bugSlider.getValue());
		numBugs.setTextFill(Color.WHITE);
		bugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				numBugs.setText("\nNumber of bugs: " + newValue.intValue());
				currentNumBugs = newValue.intValue();
			}
		});

		//Add slider for plant input
		Slider plantSlider = new Slider();
		plantSlider.setMin(0);
		plantSlider.setMax(15);
		plantSlider.setValue(0);
		plantSlider.setShowTickLabels(true);
		plantSlider.setShowTickMarks(true);
		plantSlider.setMajorTickUnit(5);
		plantSlider.setMinorTickCount(1);
		plantSlider.setBlockIncrement(1);
		Label numPlants = new Label("\nNumber of plants: " + (int)plantSlider.getValue());
		numPlants.setTextFill(Color.WHITE);
		plantSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				numPlants.setText("\nNumber of plants: " + newValue.intValue());
				currentNumPlants = newValue.intValue();
			}
		});

		//Add generate world button
		Button generate = new Button("Generate world");
		generate.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reset();
				addBugs(currentNumBugs, bugPattern);
				addPlants(currentNumPlants, plantPattern);
				addObstacles(currentNumObstacles, obstaclePattern);
			}
		});

		//Add menu pane
		VBox menu = new VBox();
		menu.setPrefSize(defaultWidth/2, defaultHeight);
		menu.setPadding(new Insets(25, 25, 25, 25));
		BackgroundImage geometric = new BackgroundImage(new Image("/menurotate.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		menu.setBackground(new Background(geometric));
		menu.getChildren().addAll(title, numBugs, bugSlider, numPlants, plantSlider, generate, controls);
		menu.setAlignment(Pos.CENTER);
		menu.setSpacing(10);

		//Set canvas properties and align controls and worldPane
		canvas.setCenter(worldPane);
		canvas.setLeft(menu);

		//Create scene
		final Scene scene = new Scene(canvas, defaultWidth+(defaultWidth/3), defaultHeight);

		//Create frame and animation handler
		KeyFrame frame = new KeyFrame(Duration.millis(30), new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				for (Bug b : bugs) {
					final Bounds bounds = worldPane.getBoundsInLocal();
					b.update(bounds, bugs, plants, allObjects);
				}
				for (Plant p : plants) {
					p.update(allObjects);
				}
			}
		});

		//Create Timeline
		final Timeline timeline = new Timeline(frame);
		timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);

		//Set control buttons handlers
		controls.setAlignment(Pos.CENTER);
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

		//Configure Stage
		primaryStage.setTitle("Bug World");
		primaryStage.setScene(scene);
		primaryStage.setMaxWidth(defaultWidth+(defaultWidth/3)+100);
		primaryStage.setMaxHeight(defaultHeight+125);
		primaryStage.show();
	}

	//Reset worldPane and add bugs and plants according to fields
	public void reset() {
		//Clear everything
		bugs.clear();
		plants.clear();
		allObjects.clear();
		worldPane.getChildren().clear();
		//Add everything
		addBugs(currentNumBugs, bugPattern);
		addPlants(currentNumPlants, plantPattern);
		addObstacles(currentNumObstacles, obstaclePattern);
		worldPane.getChildren().addAll(allObjects);
	}

	//Add bugs
	public void addBugs(int num, ImagePattern bugPattern) {
		for (int i=0; i<num; i++) {
			double x = getRandomCoordinate("x");
			double y = getRandomCoordinate("y");
			Bug bug = new Bug(10);
			bug.relocate(x, y);
			while (checkSpawnCollision(bug, x, y, allObjects)) {
				x = getRandomCoordinate("x");
				y = getRandomCoordinate("y");
				bug.relocate(x, y);
			}
			bug.setFill(bugPattern);
			bugs.add(bug);
			allObjects.add(bug);
		}
	}

	//Add plants
	public void addPlants(int num, ImagePattern plantPattern) {
		for (int i=0; i<num; i++) {
			Plant plant = new Plant(getRandomRadius());
			double x = getRandomCoordinate("x");
			double y = getRandomCoordinate("y");
			plant.relocate(x, y);
			while (checkSpawnCollision(plant, x, y, allObjects)) {
				x = getRandomCoordinate("x");
				y = getRandomCoordinate("y");
				plant.relocate(x, y);
			}
			plant.setFill(plantPattern);
			plants.add(plant);
			allObjects.add(plant);
		}
	}
	
	//Add obstacles
	public void addObstacles(int num, ImagePattern obstaclePattern) {
		for (int i=0; i<num; i++) {
			Obstacle obstacle = new Obstacle(getRandomRadius());
			double x = getRandomCoordinate("x");
			double y = getRandomCoordinate("y");
			obstacle.relocate(x, y);
			while (checkSpawnCollision(obstacle, x, y, allObjects)) {
				x = getRandomCoordinate("x");
				y = getRandomCoordinate("y");
				obstacle.relocate(x, y);
			}
			obstacle.setFill(obstaclePattern);
			allObjects.add(obstacle);
		}
	}

	// Generate radius for plant & obstacle
	public double getRandomRadius() {
		int min = 5;
		int max = 15;
		int range = (max-min);
		return (Math.random() * range) + min;
	}

	//Get random X or Y coordinate
	public double getRandomCoordinate(String coord) {
		double min;
		double max;
		double range;		
		switch (coord) {
		case "x" :
			min = 10; 
			max = defaultWidth-(defaultWidth/4);
			range = (max - min);
			return (Math.random() * range) + min;
		case "y" :
			min = 10;
			max = defaultHeight - 50;
			range = (max - min);
			return (Math.random() * range) + min; 
		}
		return 0;
	}

	//Check collision for when bugs and plants spawn
	public boolean checkSpawnCollision(WorldObject o, double potentialX, double potentialY, ArrayList<WorldObject> allObjects) {
		for (WorldObject w : allObjects) {
			if (w != o) {
				if (this.calculateCollision(o, potentialX, potentialY, w)) {
					return true;
				}
			}
		}
		return false;
	}

	//Used by checkCollision method
	public boolean calculateCollision(WorldObject o, double potentialX, double potentialY, WorldObject w) {
		//inspired by Oliver - uses pythagoras' theorum
		double diffX = w.getLayoutX() - potentialX;
		double diffY = w.getLayoutY() - potentialY;
		double distance = Math.sqrt((diffX*diffX)+(diffY*diffY));
		double minDistance = (w.getRadius()*2) + (o.getRadius()*2);
		return (distance < minDistance);
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
