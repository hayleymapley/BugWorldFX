package application;

import javafx.scene.shape.Circle;

/**
 * Class extending Circle that serves as a superclass to Bug, Obstacle
 * and Plant classes.
 * @author mapleyhayl
 *
 */
public class WorldObject extends Circle {
	
	public WorldObject(double radius) {
		super(radius);
	}
}
