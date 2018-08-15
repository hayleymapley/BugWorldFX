package application;

import java.util.ArrayList;

import javafx.scene.shape.Shape;

public class Plant extends WorldObject {

	private double radius;

	public Plant(double radius) {
		super(radius);
		this.radius = radius;
	}

	public int eatFrom() {
		if (radius >= 1) {
			radius--;
			return 100; // returns 100 food for every point subtracted from radius
		}
		return 0;
	}

	public void update(ArrayList<WorldObject> allObjects) {
		if (radius < 1) {
			this.setVisible(false);
		}
		// Chance of growth
		int chance = (int)(Math.ceil(Math.random()*70));
		if (chance == 1 && !checkCollision(allObjects)) { //Stops the plant from growing if it is surrounded by an object
			radius += 1;
		}
		if (radius > 30) { //Stop growth at 30
			radius = 30;
		}
		this.setRadius(radius);
	}

	//Used to check collision with other objects
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
