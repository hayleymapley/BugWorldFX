package application;

import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

/**
 * Class extending WorldObject that moves (or doesn't move), decreases in energy,
 * eats from plants, and randomises direction each update.
 * @author mapleyhayl
 *
 */
public class Bug extends WorldObject {

	private double dX = -1.5f;
	private double dY = -1.5f;
	private int energy = 1000;
	private boolean isHungry = false;
	private boolean isDead = false;

	/**
	 * Constructor
	 * @param radius
	 */
	public Bug(double radius) {
		super(radius);
		this.randomiseDirection();
	}
	
	/**
	 * Method that handles energy decrementing and death, movement and feeding
	 * @param bounds - for collision detection with walls
	 * @param bugs - for collision detection
	 * @param plants - for collision detection and feeding
	 * @param allObjects - for collision detection
	 */
	public void update(Bounds bounds, ArrayList<Bug> bugs, ArrayList<Plant> plants, ArrayList<WorldObject> allObjects) {
		this.updateEnergy();

		this.move(bounds, allObjects);

		this.eat(plants);	
		
		this.randomiseDirection();
		}
	
	/**
	 * Decrements energy. If energy is less than 600, boolean isHungry is set to true.
	 * If energy is equal to 0, it remains at 0 and boolean isDead is set to true. The bug
	 * then changes image to  be a gravestone, indicating death.
	 */
	public void updateEnergy() {
		energy--;
		if (energy < 600) {
			isHungry = true;
		}
		if (energy == 0) {
			isDead = true;
			Image dead = new Image("/gravestone1.png");
			ImagePattern deadPattern = new ImagePattern(dead);
			this.setFill(deadPattern);
		}else if (energy < 0) {
			energy = 0;
		}
	}
	
	/**
	 * Handles bug movement. If the bug is not dead, it will check for collision. If no
	 * collision will happen when the bug takes its next move, it moves. If collision is
	 * detected, the bug will go in the opposite direction.
	 * @param bounds - used for collision with walls.
	 * @param allObjects - used for collision with worldObjects.
	 */
	public void move(Bounds bounds, ArrayList<WorldObject> allObjects) {
		if (!isDead) {
			if (!collisionDetected(this.getLayoutX() + dX, this.getLayoutY() + dY, allObjects)) {
				this.setLayoutX(this.getLayoutX() + dX);
				this.setLayoutY(this.getLayoutY() + dY);
			}
			final boolean atRightBorder = this.getLayoutX() >= (bounds.getMaxX() - this.getRadius());
			final boolean atLeftBorder = this.getLayoutX() <= (bounds.getMinX() + this.getRadius());
			final boolean atBottomBorder = this.getLayoutY() >= (bounds.getMaxY() - this.getRadius());
			final boolean atTopBorder = this.getLayoutY() <= (bounds.getMinY() + this.getRadius());
			if (atRightBorder || atLeftBorder) {
				dX *= -1;
			}
			if (atBottomBorder || atTopBorder) {
				dY *= -1;
			}
		}
	}

	/**
	 * Handles bug feeding. If collision is detected with a plant and the
	 * bug is hungry, the bug will stop and eat from the plant, gaining 100
	 * energy for each feeding action.
	 * @param plants - to detect collision with plants in the world
	 */
	public void eat(ArrayList<Plant> plants) {
		Plant p = collisionWithPlantDetected(getLayoutX(), getLayoutY(), plants);
		if (p != null && p.getRadius() > 0 && isHungry) {
			dX = 0;
			dY = 0;
			energy += p.eatFrom();
		}
		p = null;
	}
	
	/**
	 * Randomises direction - there is a 1/15 chance of changing direction and 
	 * a 1/10 chance of increasing "speed" (distance moved per update). This is capped at -3/3.
	 */
	public void randomiseDirection() {
		int directionChance = (int)Math.ceil(Math.random()*15);
		if (directionChance == 1) {
			double x = Math.ceil(Math.random()*2);
			double y = Math.ceil(Math.random()*2);
			if (x > 1) {
				this.dX = -dX;
			}
			if (y > 1) {
				this.dY = -dY;
			}
		}
		int distanceChance = (int)Math.ceil(Math.random()*10);
		if (distanceChance == 1) {
			int distance = (int)Math.ceil(Math.random()*2); //1=slower 2=faster
			if (distance == 1 && dX > -3 && dY > -3) {
				dX = dX - 0.5;
				dY = dY - 0.5;
			} else if (distance == 2 && dX < 3 && dY < 3) {
				dX = dX + 0.5;
				dY = dY + 0.5;
			}
		}
	}
	
	/**
	 * Checks for collision with all plants in the world. If a collision is detected, a plant is returned.
	 * @param potentialX
	 * @param potentialY
	 * @param plants
	 * @return plant - returned plant is then eaten from.
	 */
	public Plant collisionWithPlantDetected(double potentialX, double potentialY, ArrayList<Plant> plants) {
		for (Plant w : plants) {
			if (calculatePlantCollision(potentialX, potentialY, w) && w instanceof Plant) {
				return w;
			}
		}
		return null;
	}
	
	/**
	 * Used by the collisionWithPlantDetected method. Takes the potentialX and potentialY of the intended movement
	 * and calculates whether a collision will occur.
	 * @param potentialX
	 * @param potentialY
	 * @param w
	 * @return true if there will be a collision or false if there will not
	 */
	public boolean calculatePlantCollision(double potentialX, double potentialY, WorldObject w) {
		//inspired by Oliver
		double diffX = w.getLayoutX() - potentialX;
		double diffY = w.getLayoutY() - potentialY;
		double distance = Math.sqrt((diffX*diffX)+(diffY*diffY));
		double minDistance = w.getRadius() + getRadius() + 3;
		return (distance < minDistance);
	}

	/**
	 * Checks for collision with all WorldObjects. 
	 * @param potentialX
	 * @param potentialY
	 * @param allObjects
	 * @return true if a collision will occur, and false if not
	 */
	public boolean collisionDetected(double potentialX, double potentialY, ArrayList<WorldObject> allObjects) {
		for (WorldObject w : allObjects) {
			if (w != this) {
				if (calculateCollision(potentialX, potentialY, w)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Used by collisionDetected method. Takes the potentialX and potentialY of the intended movement
	 * and calculates whether a collision will occur.
	 * @param potentialX
	 * @param potentialY
	 * @param w
	 * @return true if a collision will occur, and false if not.
	 */
	public boolean calculateCollision(double potentialX, double potentialY, WorldObject w) {
		double diffX = w.getLayoutX() - potentialX;
		double diffY = w.getLayoutY() - potentialY;
		double distance = Math.sqrt((diffX*diffX)+(diffY*diffY));
		double minDistance = w.getRadius() + getRadius();
		return (distance < minDistance);
	}

	//ACCESSORS
	public double getdX() {
		return dX;
	}
	public void setdX(double dX) {
		this.dX = dX;
	}

	public double getdY() {
		return dY;
	}
	public void setdY(double dY) {
		this.dY = dY;
	}

}
