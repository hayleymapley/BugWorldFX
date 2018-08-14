package application;

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

// 1/10 chance of bug randomly changing direction each time
//double chance = Math.ceil(Math.random()*10);
//if (chance == 1) {
//	this.setRandomDirection();
//}

//		double currentX = this.getCenterX();
//		double currentY = this.getCenterY();
//		int direction = (int) Math.ceil(Math.random()*4); //1=N, 2=S, 3=E, 4=W
//		switch (direction) {
//		case 1 :
//			//			double newUpY = currentY + dy;
//			if (this.getCenterY() + this.getTranslateY() < this.getRadius()) {
//				dy = -dy;
//			}
//			this.setTranslateY(this.getTranslateY() + dy);
//			break;
//		case 2 :
//			//			double newDownY = currentY - dy;
//			if (this.getCenterY() + this.getTranslateY() + this.getRadius() > canvas.getHeight()) {
//				dy = -dy;
//			}
//			this.setTranslateY(this.getTranslateY() - dy);
//			break;
//		case 3 :
//			//			double newRightX = currentX - dx;
//			if (this.getCenterX() + this.getTranslateX() + this.getRadius() > canvas.getWidth()) {
//				dx = -dx;
//			}
//			this.setTranslateX(this.getTranslateX() - dx);
//			break;
//		case 4 :
//			//			double newLeftX = currentX + dx;
//			if (this.getCenterX() + this.getTranslateX() < this.getRadius()) {
//				dx = -dx;
//			}
//			this.setTranslateX(this.getTranslateX() + dx);
//			break;
////		}

public class Bug extends WorldObject {

	private double dX = -1.5f;
	private double dY = -1.5f;
	protected int energy = 100;
	private boolean dead = false;

	public Bug(double radius) {
		super(radius);
		this.setFill(Color.web("red",0.0));
		this.setRandomDirection();

		int min = 750;
		int max = 1000;
		int range = max-min;
		this.energy = (int)(Math.random() * range) + min;
	}

	// Check if the current bug is next to a plant, and if so, return that plant
	public Plant checkPlantCollision(ArrayList<Plant> plants) {
		for (Plant p : plants) {
			if (this.getBoundsInParent().intersects(p.getBoundsInParent())) {
				return p;
			}
		}
		return null;
	}

//		public boolean checkCollision(ArrayList<WorldObject> allObjects) {
//			boolean collisionDetected = false;
//			for (WorldObject w : allObjects) {
//				if (w != this) {
//					Shape intersect = Shape.intersect(this, w);
//					if (intersect.getBoundsInLocal().getWidth() != -1) {
//						collisionDetected = true;
//					}
//				}
//			}
//			return collisionDetected;
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

		//		Movement
		if (!dead) {
			if (checkCollision(this.getLayoutX() + dX, this.getLayoutY() + dY, allObjects)) {
				//nothing
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

			//			Eating from plants
			Plant p = this.checkPlantCollision(plants);
			if (p != null && p.getRadius() > 0 && energy < 500) {
				dX = 0;
				dY = 0;
				energy += p.eatFrom();
			}

			// chance of change in direction and speed (distance moved)
			int directionChance = (int)Math.ceil(Math.random()*15);
			if (directionChance == 1) {
				this.setRandomDirection();
			}
			int speedChance = (int)Math.ceil(Math.random()*20);
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
