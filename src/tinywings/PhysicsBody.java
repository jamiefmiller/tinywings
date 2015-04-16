package tinywings;

public class PhysicsBody {
	private double x;
	private double y;
	private double direction;
	private double speed;
	private double gravity;
	private int r;
	
	public PhysicsBody(int x, int y, int r) {
		this.x = x;
		this.y = y;
		this.direction = 0.0;
		this.speed = 1.0;
		this.gravity = -0.02;
		this.r = r;
	}
	
	public int getX() {
		return (int) Math.round(x);
	}
	
	public int getY() {
		return (int) Math.round(y);
	}
	
	public int getR() {
		return r;
	}
	
	public double getAngle() {
		return direction;
	}
	
	/**
	 * Bird deliberately falling.
	 */
	public void press() {
		gravity = -0.08;
	}
	
	/**
	 * Bird stopped deliberately falling.
	 */
	public void unpress() {
		gravity = -0.02;
	}
	
	/**
	 * Called each frame.
	 * @param floor Height of the ground at current x.
	 * @param tangent Angle of tangent line to the floor in radians.
	 * @param region The physics region to use if the object is in collision with the terrain.
	 */
	public void update(int floor, double floorAngle, Region region) {
		//System.out.println(direction);
		if (y-r <= floor) {
			double angleSimilarity = 1-((Math.abs(floorAngle-direction) % (Math.PI/2)) / (Math.PI/2));
			//System.out.println("floor"+floorAngle+"direction"+direction+"similarity"+angleSimilarity);
			//if (angleSimilarity < 0.8) {
			//	System.out.println("bounce");
			//}
			y = floor+r;
			speed *=angleSimilarity*angleSimilarity;
			speed += 10*gravity*(Math.sin(floorAngle));//angleSimilarity +
			direction = floorAngle;
		} else {
			double xvec = speed*Math.cos(direction);
			double yvec = speed*Math.sin(direction) + gravity;
			speed = Math.sqrt(xvec*xvec + yvec*yvec);
			direction = Math.atan2(yvec, xvec);
		}
		
		if (speed < 1.0) {
			speed = 1.0;
		}
		direction %= Math.PI*2;
		if (direction > Math.PI/2-0.1 && direction <= Math.PI) {
			direction = Math.PI/2-0.1;
		}
		if (direction > Math.PI && direction < Math.PI*1.5+0.1) {
			direction = Math.PI*1.5+0.1;
		}
		
		x += speed*Math.cos(direction);
		y += speed*Math.sin(direction);
		
		
		/*if (getY()-r <= floor) {
			// Collision with ground.
			y_pos = floor+r;
			//y_vel = 0;
		} else {
			region = Region.OPEN;
		}
		switch (region) {
			case HILLTOP:
				// http://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
				double dnorm2 = dheight*dheight + 1;
				x_vel = x_vel - 2*x_vel*(-dheight)*(-dheight)/dnorm2;
				y_vel = y_vel - 2*y_vel/dnorm2;
			case DOWNHILL:
				double vi2 = x_vel*x_vel + y_vel*y_vel;
				double vf = Math.sqrt(2.0*y_acc*Math.abs(dheight) + vi2);
				double angle = Math.atan2(dheight, 1.0);
				x_vel = Math.cos(angle)*vf;
				y_vel = Math.sin(angle)*vf;
			case UPHILL:
				y_pos = floor+r; // Placeholder
			default:
				// Projectile Motion
				x_pos += x_vel + 0.5*x_acc;
				x_vel += x_acc;
				y_pos += y_vel + 0.5*y_acc;
				y_vel += y_acc;
		}*/
	}
}
