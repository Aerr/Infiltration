package Game.Basics;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.newdawn.slick.*;

public class Sphere {

	public double size;
	private int HEIGHT, WIDTH;
	public double mass;
	public Vector2 pos;
	protected Vector2 old_pos;
	public Vector2 vel;
	public double e;
	private double scale;
	private Image texture;
	public boolean selected;
	private boolean gravity;

	public Sphere(double x, double y, int w, int h, double rest, double sc, Image t, boolean g) 
	{			
		this.WIDTH = w;
		this.HEIGHT = h;
		this.scale = sc;
		this.size = (96 * scale) / 2;

		if (x <= 0)
			x = 0;
		else if (x >= WIDTH - (size * 2))
			x = WIDTH - (size * 2);

		if (y <= 0)
			y = 0;
		else if (y >= HEIGHT - (size * 2))
			y = HEIGHT - (size * 2);
		
		this.pos = new Vector2(x - size,y - size);
		this.old_pos = new Vector2(x - size,y - size);
		this.vel = new Vector2(0,0);

		this.mass = sc * 100;
		this.texture = t;
		this.e = rest;
		this.scale = sc;
		this.selected = false;
		this.gravity = g;
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		if (this.texture != null)
			this.texture.draw((float)pos.X, (float)pos.Y, (float)scale);
	}
	// Normal update
	public void update(double dt, LinkedList<Line> lines, LinkedList<Rect> rects) throws SlickException {
		// Gravity
		if (gravity)
			vel.Y += 9.8 * dt * 100;

		// Line collision
		for (int i = 0; i < lines.size(); i++)
			collisionL2S(lines.get(i), vel);
		// Rectangle collision
		for (int i = 0; i < rects.size(); i++)
			collisionR2S(rects.get(i), vel);

		checkBounds();
		if (Math.abs(vel.X) - 0.5 < 0)
			vel.X = 0;
		if (Math.abs(vel.Y) - 0.5 < 0)
			vel.Y = 0;
		// Movements
		pos.X += vel.X * dt;
		pos.Y += vel.Y * dt;

	}
	// Update for selected mode
	public void update(double x, double y, double dt) throws SlickException {
		pos.X = x - size;
		pos.Y = y - size;

		vel.X = (pos.X - old_pos.X) / (dt * mass * 0.35f);
		vel.Y = (pos.Y - old_pos.Y) / (dt * mass * 0.35f);

		old_pos = pos;
	}

	private boolean inRange(double x, double y, double range) 
	{
		double dX = Math.pow(((x) - (pos.X + size)), 2);
		double dY = Math.pow(((y) - (pos.Y + size)), 2);
		// We don't root square because it's a very slow operation 
		// We'd rather square the other side of the (in)equation
		return (dX + dY <= Math.pow(range, 2));
	}

	private double dot(double uX, double uY, double vX, double vY)
	{
		return (uX * vX + uY * vY);
	}
	private boolean collisionEndpoint(float x1, float y1)
	{
		// Calculation of the resulting impulse for each ball
		final double endMass = mass;
		double collisiondist = Math.sqrt(Math.pow(x1 - (pos.X + size), 2) + Math.pow(y1 - (pos.Y + size), 2));
		double n_x = (x1 - (pos.X + size)) / collisiondist;
		double n_y = (y1 - (pos.Y + size)) / collisiondist;
		double p = 2 * (vel.X * n_x + vel.Y * n_y) / (mass + endMass);
		double w_x = vel.X - p * mass * n_x - p * endMass * n_x;
		double w_y = vel.Y - p * mass * n_y - p * endMass * n_y;
		vel.X = w_x;
		vel.Y = w_y;
		return true;// ;
	}
	protected boolean collisionR2S(Rect r, Vector2 newVel)
	{
		for (int i = 0; i < 4; i++)
		{
			if (collisionL2S(r.edges[i], newVel))
				return true;
		}
		return false;
	}
	protected boolean collisionL2S(Line l, Vector2 newVel) { 
		if (!l.isBound && movingToPoint(l.x0, l.y0, newVel) && inRange(l.x0, l.y0, size))
			return collisionEndpoint(l.x0, l.y0);
		else if (!l.isBound && movingToPoint(l.x1, l.y1, newVel) && inRange(l.x1, l.y1, size))
			return collisionEndpoint(l.x1, l.y1);
		else
		{
			int t = 0;
			double vX = (l.x1 + t) - (l.x0 - t);
			double vY = l.y1 - l.y0;

			double wX = (pos.X + size) - (l.x0 - t);
			double wY = (pos.Y + size) - l.y0;

			double c1 = dot(wX,wY,vX,vY);
			double c2 = dot(vX,vY,vX,vY);

			double b = c1 / c2;
			double newX = (l.x0 - t) + b * vX;
			double newY = l.y0 + b * vY;

			if (c1 > 0 && c2 > c1)
			{
				if (inRange(newX, newY, size + 1))
				{
					double l0 = Math.sqrt(Math.pow((l.x1 + t) - (l.x0 - t), 2) + Math.pow(l.y1 - l.y0, 2));

					short signX = (short) Math.signum(l.y0 - l.y1);
					if (signX == 0)
						signX = 1;
					double sY = -signX * ((l.y1 - l.y0) / l0);
					double sX = signX * (((l.x1 + t) - (l.x0 - t)) / l0);
					
					if (inRange(newX, newY, size))
					{
						if (l.isVertical())
						{
							if (newVel.X >= 0)
								newX -= size * 2;
							else
								newX += 2;

							newY -= size;
						}
						else if (l.isHorizontal())
						{
							if (newVel.Y >= 0)
								newY -= size * 2;
							else
									newY += 2;

							newX -= size;
						}

						pos.X = newX;
						pos.Y = newY;
						reflectionV(sY,sX);
						return  false;
					}
					
					reflectionV(sY,sX);
					return  movingToPoint(newX, newY, newVel);
				}
			}
		}
		return false;
	}
	
	private boolean movingToPoint(double x, double y, Vector2 newVel) {
		return ((x - pos.X) * newVel.X + (y - pos.Y) * newVel.Y > 0);
	}
	
	// Calculates the reflection vector
	private void reflectionV(double a, double b)
	{
		double x = ((1+e)*(a*(vel.X * a + vel.Y * b))) / (1 / mass);
		double y = ((1+e)*(b*(vel.X * a + vel.Y * b))) / (1 / mass);

		vel.X -= x * (1 / mass);
		vel.Y -= y * (1 / mass);
	}

	// We check if the ball is outside the bounds
	protected void checkBounds()
	{
		if (pos.X < 0)
			pos.X = 2;
		if (pos.X + size * 2 > WIDTH)
			pos.X = WIDTH - 2 - size * 2;
		if (pos.Y < 0)
			pos.Y = 2;
		if (pos.Y + size * 2 > HEIGHT)
			pos.Y = HEIGHT - 2 - size * 2;
	}

	public void gravityChanger()
	{
		this.gravity = !this.gravity;
	}
	
}