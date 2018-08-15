package application;

import java.util.ArrayList;

import javafx.scene.shape.Shape;

/**
 * Class extending WorldObject that updates each frame. There is a
 * chance of growth each update, but a Plant will not grow if something
 * is next to it. Plants can also be eaten from.
 * @author mapleyhayl
 *
 */
public class Plant extends WorldObject {

	private double radius;

	/**
	 * Constructor - radius
	 */
	public Plant(double radius) {
		super(radius);
		this.radius = radius;
	}

	/**
	 * If the Plant's growth is not 0, the plant reduces
	 * in radius by 1.
	 * @return 100 food for every point subtracted from radius.
	 */
	public int eatFrom() {
		if (radius >= 1) {
			radius--;
			return 100;
		}
		return 0;
	}

	/**
	 * Plant has a 1/70 chance of growth, and does not grow if it detects collision
	 * Plant stops growth once radius reaches 30
	 * @param allObjects -  used to check for collision with other objects in world
	 */
	public void update(ArrayList<WorldObject> allObjects) {
		if (radius < 1) {
			this.setVisible(false);
		}
		int chance = (int)(Math.ceil(Math.random()*70));
		if (chance == 1 && !checkCollision(allObjects)) {
			radius += 1;
		}
		if (radius > 30) {
			radius = 30;
		}
		this.setRadius(radius);
	}

	/**
	 * Checks for collision with other objects in world
	 * @param allObjects - used to check for collision with other objects
	 * @return collisionDetected boolean
	 */
	public boolean checkCollision(ArrayList<WorldObject> allObjects) {
		boolean collisionDetected = false;
		for (WorldObject w : allObjects) {
			if (w != this) {
				Shape intersect = Shape.intersect(this, w);
				if (intersect.getBoundsInLocal().getWidth() != -1) {
						collisionDetected = true;
				}
			}
		}
		return collisionDetected;
	}
}
