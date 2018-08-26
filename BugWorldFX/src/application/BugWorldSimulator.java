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

/**
 * Entry point class - the program must be run from here. Extends Application class. 
 * @author mapleyhayl
 *
 */
public class BugWorldSimulator extends Application {

	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 500;
	
	// Panes
	private BorderPane canvas = new BorderPane();
	private Pane worldPane = new Pane();
	private HBox controls = new HBox();

	// Used to pass information to bugs/plants
	private ArrayList<Bug> bugs = new ArrayList<>();
	private ArrayList<Plant> plants = new ArrayList<>();
	private ArrayList<WorldObject> allObjects = new ArrayList<>();

	// Start-up defaults
	private int currentNumBugs = 0;
	private int currentNumPlants = 0;
	private int currentNumObstacles = 3;

	//	Load world object images
	private Image bug = new Image("/beetlecartoon.png");
	private ImagePattern bugPattern = new ImagePattern(bug);
	private Image plant = new Image("/bush.png");
	private ImagePattern plantPattern = new ImagePattern(plant);
	private Image obstacle = new Image("/obstacle.png");
	private ImagePattern obstaclePattern = new ImagePattern(obstacle); 
	
	private BackgroundImage geometric = new BackgroundImage(new Image("/menurotate.jpg"),
			BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
			BackgroundSize.DEFAULT);
	private BackgroundImage grass = new BackgroundImage(new Image("/grasstexture.jpg"),
			BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
			BackgroundSize.DEFAULT);

	//Load button images
	private Image imgPlayPause = new Image("/play-pause.png");		//Play/pause button images (has three states)
	private ImageView playPauseView = new ImageView(imgPlayPause);
	private Image imgPlay = new Image("/play.png");
	private ImageView playView = new ImageView(imgPlay);
	private Image imgPause = new Image("/pause.png");
	private ImageView pauseView = new ImageView(imgPause);
	private Image imgQuit = new Image(getClass().getResourceAsStream("exit (1).png")); // Quit button image
	private ImageView quitView = new ImageView(imgQuit);
	private Image imgReset = new Image("/reset3.png");		// Reset button image
	private ImageView resetView = new ImageView(imgReset);
	
	// Create buttons
	private Button play = new Button("");
	private Button quit = new Button("");
	private Button reset = new Button("");
	private Button generate = new Button("Generate world");
	
	//Create layout elements
	private VBox menuPane = new VBox();
	private Slider bugSlider = new Slider();
	private Label numBugs = new Label("\nNumber of bugs: " + (int)bugSlider.getValue());
	private Slider plantSlider = new Slider();
	private Label numPlants = new Label("\nNumber of plants: " + (int)plantSlider.getValue());

	/**
	 * Application initialising method.
	 */
	@Override
	public void start(final Stage primaryStage) {

		setControlButtonAttributes();
		initialiseMenuPane();
		setWorldPaneBackground();

		// Listener for bugs slider
		bugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				numBugs.setText("\nNumber of bugs: " + newValue.intValue());
				currentNumBugs = newValue.intValue();
			}
		});

		// Listener for plants slider
		plantSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				numPlants.setText("\nNumber of plants: " + newValue.intValue());
				currentNumPlants = newValue.intValue();
			}
		});

		// Handler for generate button
		generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reset();
				addBugs(currentNumBugs, bugPattern);
				addPlants(currentNumPlants, plantPattern);
				addObstacles(currentNumObstacles, obstaclePattern);
			}
		});

		// Set BorderPane properties and align menuPane and worldPane
		canvas.setCenter(worldPane);
		canvas.setLeft(menuPane);

		// Create scene
		final Scene scene = new Scene(canvas, DEFAULT_WIDTH+(DEFAULT_WIDTH/3), DEFAULT_HEIGHT);

		// Create frame and animation handler
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

		// Create Timeline
		final Timeline timeline = new Timeline(frame);
		timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);

		// Set control buttons handlers
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

		// Configure Stage
		primaryStage.setTitle("Bug World");
		primaryStage.setScene(scene);
		primaryStage.setMaxWidth(DEFAULT_WIDTH+(DEFAULT_WIDTH/3)+100);
		primaryStage.setMaxHeight(DEFAULT_HEIGHT+125);
		primaryStage.show();
	}

	/**
	 * Sets control buttons style and images (play/pause, reset, quit)
	 */
	public void setControlButtonAttributes() {
		// Play/pause button
		// Set image widths and heights (play/pause button has three states/images)
		playPauseView.setFitWidth(20);
		playPauseView.setFitHeight(20);
		playView.setFitWidth(20);
		playView.setFitHeight(20);
		pauseView.setFitWidth(20);
		pauseView.setFitHeight(20);
		// Set style and image of play button
		play.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000");
		play.setGraphic(playPauseView);

		// Quit button
		// Set image width, height
		quitView.setFitWidth(20);
		quitView.setFitHeight(20);
		// Set style and image of quit button
		quit.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		quit.setGraphic(quitView);

		// Reset button
		resetView.setFitWidth(20);
		resetView.setFitHeight(20);
		// Set style and image of reset button
		reset.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		reset.setGraphic(resetView);
	}

	/**
	 * Initialises the menu pane - adds the controls, titles, and other buttons/sliders
	 */
	public void initialiseMenuPane() {
		// Set controls panel attributes and add buttons
		controls.setPadding(new Insets(25,25,25,25));
		controls.setSpacing(5);
		controls.getChildren().addAll(play, reset, quit);

		// Add title
		Text title = new Text("BUG WORLD\n");
		title.setFont(Font.font(30));
		title.setFill(Color.WHITE);
		title.setTextAlignment(TextAlignment.CENTER);

		// Set slider for bug input attributes
		bugSlider.setMin(0);
		bugSlider.setMax(15);
		bugSlider.setValue(0);
		bugSlider.setShowTickLabels(true);
		bugSlider.setShowTickMarks(true);
		bugSlider.setMajorTickUnit(5);
		bugSlider.setMinorTickCount(1);
		bugSlider.setBlockIncrement(1);
		// Set label colour
		numBugs.setTextFill(Color.WHITE);
		
		// Set slider for plant input attributes
		plantSlider.setMin(0);
		plantSlider.setMax(15);
		plantSlider.setValue(0);
		plantSlider.setShowTickLabels(true);
		plantSlider.setShowTickMarks(true);
		plantSlider.setMajorTickUnit(5);
		plantSlider.setMinorTickCount(1);
		plantSlider.setBlockIncrement(1);
		// Set label colour
		numPlants.setTextFill(Color.WHITE);

		// Set style of generate button
		generate.setStyle("-fx-background-color: #ffffff;-fx-border-color: #000000");
		
		// Set menuPane attributes
		menuPane.setPrefSize(DEFAULT_WIDTH/2, DEFAULT_HEIGHT);
		menuPane.setPadding(new Insets(25, 25, 25, 25));
		menuPane.setBackground(new Background(geometric));
		menuPane.getChildren().addAll(title, numBugs, bugSlider, numPlants, plantSlider, generate, controls);
		menuPane.setAlignment(Pos.CENTER);
		menuPane.setSpacing(10);
	}

	/**
	 * Sets background of worldPane
	 */
	public void setWorldPaneBackground() {
		worldPane.setBackground(new Background(grass));
	}

	
	/**
	 * Resets worldPane and add bugs and plants according to fields
	 */
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

	/**
	 * Adds bugs
	 * @param num
	 * @param bugPattern
	 */
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

	/**
	 * Adds plants
	 * @param num
	 * @param plantPattern
	 */
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

	/**
	 * Adds obstacles
	 * @param num
	 * @param obstaclePattern
	 */
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

	/**
	 * Generate radius for plant & obstacle
	 * @return integer (radius)
	 */
	public double getRandomRadius() {
		int min = 5;
		int max = 15;
		int range = (max-min);
		return (Math.random() * range) + min;
	}

	/**
	 * Get random X or Y coordinate
	 * @param coord
	 * @return coordinate
	 */
	public double getRandomCoordinate(String coord) {
		double min;
		double max;
		double range;		
		switch (coord) {
		case "x" :
			min = 10; 
			max = DEFAULT_WIDTH-(DEFAULT_WIDTH/4);
			range = (max - min);
			return (Math.random() * range) + min;
		case "y" :
			min = 10;
			max = DEFAULT_HEIGHT - 50;
			range = (max - min);
			return (Math.random() * range) + min; 
		}
		return 0;
	}

	/**
	 * Check collision for when bugs and plants spawn
	 * @param o
	 * @param potentialX
	 * @param potentialY
	 * @param allObjects
	 * @return true if collision will occur, false if not
	 */
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

	/**
	 * Used by checkCollision method
	 * @param o
	 * @param potentialX
	 * @param potentialY
	 * @param w
	 * @return true or false
	 */
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
