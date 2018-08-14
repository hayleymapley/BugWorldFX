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
	protected int energy = 1000;
	private boolean dead = false;
	
	private int count = 0; //debugging

	public Bug(double radius) {
		super(radius);
		this.setRandomDirection();
//
//		int min = 750;
//		int max = 1000;
//		int range = max-min;
//		this.energy = (int)(Math.random() * range) + min;
	}

	// Check if the current bug is next to a plant, and if so, return that plant
//	public Plant checkPlantCollision(ArrayList<Plant> plants) {
//		for (Plant p : plants) {
//			if (this.getBoundsInParent().intersects(p.getBoundsInParent())) {
//				System.out.println("plant collision");
//				System.out.println("---------------");
//				return p;
//				
//			}
//		}
//		return null;
//	}

//		public Plant checkCollision(ArrayList<Plant> plants) {
////			boolean collisionDetected = false;
//			for (WorldObject w : plants) {
//				if (w != this) {
//					Shape intersect = Shape.intersect(this, w);
//					if (intersect.getBoundsInLocal().getWidth() != -1) {
////						collisionDetected = true;
//						return (Plant) w;
//					}
//				}
//			}
//			return null;
//		}

	public boolean checkCollision(double potentialX, double potentialY, ArrayList<WorldObject> allObjects) {
		for (WorldObject w : allObjects) {
			if (w != this) {
				if (this.calculateCollision(potentialX, potentialY, w)) {
					return true;
				}
			}
		}
		return false;
	}

	//Used by checkCollision method
	public boolean calculateCollision(double potentialX, double potentialY, WorldObject w) {
		//inspired by Oliver
		double diffX = w.getLayoutX() - potentialX;
		double diffY = w.getLayoutY() - potentialY;
		double distance = Math.sqrt((diffX*diffX)+(diffY*diffY));
		double minDistance = w.getRadius() + this.getRadius();
		return (distance < minDistance);
	}

	public void update(Bounds bounds, ArrayList<Bug> bugs, ArrayList<Plant> plants, ArrayList<WorldObject> allObjects) {
		//		Energy + death
		energy--;
		if (energy == 0) {
			dead = true;
			Image dead = new Image("/gravestone1.png");
			ImagePattern deadPattern = new ImagePattern(dead);
			this.setFill(deadPattern);
		}
		if (energy < 0) {
			energy = 0;
		}
		
		//		Movement
		if (!dead) {
			if (checkCollision(this.getLayoutX() + dX, this.getLayoutY() + dY, allObjects)) {
				//do nothing
			} else {
				this.setLayoutX(this.getLayoutX() + dX);
				this.setLayoutY(this.getLayoutY() + dY);
			}
			//			inspiration: https://stackoverflow.com/questions/20022889/how-to-make-the-ball-bounce-off-the-walls-in-javafx
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

			//Eating from plants
//			Plant p = this.checkCollision(plants);
//			if (p != null && p.getRadius() > 0 && energy < 500) {
//				dX = 0;
//				dY = 0;
//				energy += p.eatFrom();
//				System.out.println("eat");
//				count++;
//				System.out.println(count);
//			}

			// chance of change in direction and speed (distance moved)
			int directionChance = (int)Math.ceil(Math.random()*15);
			if (directionChance == 1) {
				this.setRandomDirection();
			}
			int speedChance = (int)Math.ceil(Math.random()*10);
			if (speedChance == 1) {
				int speed = (int)Math.ceil(Math.random()*2); //1=slower 2=faster
				if (speed == 1 && dX > -3 && dY > -3) {
					dX = dX - 0.5;
					dY = dY - 0.5;
				} else if (speed == 2 && dX < 3 && dY < 3) {
					dX = dX + 0.5;
					dY = dY + 0.5;
				}
			}

		}
	}

	public void setRandomDirection() {
		double x = Math.ceil(Math.random()*2);
		double y = Math.ceil(Math.random()*2);
		if (x > 1) {
			this.dX = -dX;
		}
		if (y > 1) {
			this.dY = -dY;
		}
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
