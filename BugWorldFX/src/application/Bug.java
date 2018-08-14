package application;

import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

public class Bug extends WorldObject {

	private double dX = -1.5f;
	private double dY = -1.5f;
	private int energy = 1000;
	private boolean isHungry = false;
	private boolean isDead = false;

	public Bug(double radius) {
		super(radius);
		this.randomiseDirection();
	}
	
	public void update(Bounds bounds, ArrayList<Bug> bugs, ArrayList<Plant> plants, ArrayList<WorldObject> allObjects) {
		//Energy + death
		this.updateEnergy();

		//Movement
		this.move(bounds, allObjects);

		//Eating from plants
		this.eat(plants);	
		
		// randomise direction (chance of changing direction and distance moved)
		this.randomiseDirection();
		}
	

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
	
	public void move(Bounds bounds, ArrayList<WorldObject> allObjects) {
		if (!isDead) {
			if (!collisionDetected(this.getLayoutX() + dX, this.getLayoutY() + dY, allObjects)) {
				//move
				this.setLayoutX(this.getLayoutX() + dX);
				this.setLayoutY(this.getLayoutY() + dY);
			}
			//Border collision logic
			//inspiration: https://stackoverflow.com/questions/20022889/how-to-make-the-ball-bounce-off-the-walls-in-javafx
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

	public void eat(ArrayList<Plant> plants) {
		Plant p = collisionWithPlantDetected(getLayoutX(), getLayoutY(), plants);
		if (p != null && p.getRadius() > 0 && isHungry) {
			dX = 0;
			dY = 0;
			energy += p.eatFrom();
		}
		// set Plant p to null so that the bugs don't continue to eat from it
		p = null;
	}
	
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
	
	//Check for collision with plants
	public Plant collisionWithPlantDetected(double potentialX, double potentialY, ArrayList<Plant> plants) {
		for (Plant w : plants) {
			if (calculatePlantCollision(potentialX, potentialY, w) && w instanceof Plant) {
				return w;
			}
		}
		return null;
	}
	
	//Used by collisionWithPlantDetected
	public boolean calculatePlantCollision(double potentialX, double potentialY, WorldObject w) {
		//inspired by Oliver
		double diffX = w.getLayoutX() - potentialX;
		double diffY = w.getLayoutY() - potentialY;
		double distance = Math.sqrt((diffX*diffX)+(diffY*diffY));
		double minDistance = w.getRadius() + getRadius() + 3;
		return (distance < minDistance);
	}

	//Check for collision
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

	//Used by collisionDetected method
	public boolean calculateCollision(double potentialX, double potentialY, WorldObject w) {
		//inspired by Oliver
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
