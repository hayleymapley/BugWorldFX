package application;

public class Plant extends WorldObject {

	private int radius;

	public Plant(double x, double y, double radius) {
		super(radius);
		this.relocate(x, y);
		this.radius = (int) radius;
	}

	public int eatFrom() {
		if (radius >= 1) {
			radius--;
			return 100; // returns 10 food for every point subtracted from radiu
		}
		return 0;
	}

	public void update() {
		if (radius == 0) {
			this.setVisible(false);
		}
		// Chance of growth
		int chance = (int)(Math.ceil(Math.random()*100));
		if (chance == 1) {
			radius++;
		}
		if (radius > 30) {
			radius = 30;
		}
		this.setRadius(radius);
	}
}
