package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import Game.Main.GameState;
import Game.Basics.Line;
import Game.Basics.Sphere;
import Game.Basics.Vector2;

import org.newdawn.slick.*;

public class ObjectsHandler {
	private LinkedList<Sphere> spheres;
	private LinkedList<Line> lines;
	private LinkedList<Rectangle> rects;
	private Player player;
	private int selected;
	private float tempX0;
	private float tempY0;
	private int w, h;
	private boolean gravity;
	private Image texture;
	public int scale;
	private Level level;
	private LightManager lightManager;

	private boolean inEditor;

	public ObjectsHandler(int w, int h, Image img_sphere, Image img_perso,
			Image img_floor, Image img_wall, Image img_light) {
		this.spheres = new LinkedList<Sphere>();
		this.lines = new LinkedList<Line>();
		this.rects = new LinkedList<Rectangle>();

		this.w = w;
		this.h = h;
		// genBoundaries();
		texture = img_sphere;

		selected = -1;
		this.tempX0 = -1;
		this.tempY0 = -1;

		this.gravity = false;
		player = new Player(w, h, img_perso, img_sphere);
		level = new Level(img_wall);
		this.rects = level.Load("level.cfg");

		lightManager = new LightManager(w, h, img_light);
		//		lightManager.AddLight(60, 60, 2, 0);
		lightManager.AddLight(800, 0, 2, 0);
	}

	private void genBoundaries() {
		this.lines.add(new Line(1, 0, 1, h, true));
		this.lines.add(new Line(0, 1, w, 1, true));
		this.lines.add(new Line(w - 1, 0, w - 1, h, true));
		this.lines.add(new Line(0, h - 1, w, h - 1, true));
	}

	public double random(double x, double y) {
		return (Math.random() * (y - x)) + x;
	}

	public void render(GameContainer gc, Graphics g, GameState state, float x, float y)
			throws SlickException {

		level.render(g);

		for (int i = 0; i < spheres.size(); i++)
			spheres.get(i).render(gc, g);

		player.Render(gc, g, lightManager.lights);

		level.renderWalls(g);
		
		if (tempX0 != -1)
			g.drawLine(tempX0, tempY0, x, y);

		lightManager.render(g);
	}

	public void update(double dt, double x, double y, Input ip, Vector2 camPos)
			throws SlickException {

		level.Update(camPos);
		if (inEditor)
			level.UpdateEditor(new Vector2(x + camPos.X,y + camPos.Y), ip);
		
		player.HandleInput(ip, dt, lines, rects);
		
		for (int i = 0; i < spheres.size(); i++) {
			// We check for collisions for all other spheres
			collision(player, spheres.get(i));
			for (int j = i + 1; j < spheres.size(); j++)
				collision(spheres.get(i), spheres.get(j));

//			if (spheres.get(i).selected)
//				spheres.get(i).update(x, y, dt);
//			else
//				spheres.get(i).update(dt, lines, rects);
		}
	}

	// Returns true if two balls are moving towards each other
	private boolean movingToBall(Sphere A, Sphere B) {
		return ((B.pos.X - A.pos.X) * (A.vel.X - B.vel.X) + (B.pos.Y - A.pos.Y)
				* (A.vel.Y - B.vel.Y) > 0);
	}

	// Returns the distance between two objects
	private double distance(Sphere A, Sphere B) {
		double dX = Math.pow(((B.pos.X + B.size) - (A.pos.X + A.size)), 2);
		double dY = Math.pow(((B.pos.Y + B.size) - (A.pos.Y + A.size)), 2);
		// We don't root square because it's a very slow operation
		// We'd rather square the other side of the (in)equation
		return (dX + dY);
	}

	private double distance(Sphere A, double x, double y, double size) {
		double dX = Math.pow(((x + size) - (A.pos.X + A.size)), 2);
		double dY = Math.pow(((y + size) - (A.pos.Y + A.size)), 2);
		// We don't root square because it's a very slow operation
		// We'd rather square the other side of the (in)equation
		return (dX + dY);
	}

	// Collision between spheres
	private void collision(Sphere A, Sphere B) {
		// if balls are moving toward each other and they are close
		// the 10^-9 is here to compensate the eventual rounding error
		double dist = distance(A, B);
		double err = Math.pow(10, -9);
		if (movingToBall(A, B) && dist <= Math.pow(A.size + B.size + err, 2)) {
			// Calculation of the resulting impulse for each ball
			double nx = (A.pos.X - B.pos.X) / (A.size + B.size); // Normalized
			// vector in
			// X
			double ny = (A.pos.Y - B.pos.Y) / (A.size + B.size); // Normalized
			// vector in
			// Y
			double a1 = A.vel.X * nx + A.vel.Y * ny; // A's impulse
			double a2 = B.vel.X * nx + B.vel.Y * ny; // B's impulse
			double p = (a1 - a2) / (A.mass + B.mass); // Resultant impulse
			// ===================================================

			// Repositioning if the collision has gone too far
			// And if balls are overlapping
			double tomove = B.size + A.size - Math.sqrt(dist);
			if (tomove > err) {
				double angle = Math.atan2(B.pos.Y - A.pos.Y, B.pos.X - A.pos.X);
				B.pos.X += Math.cos(angle) * (tomove);
				B.pos.Y += Math.sin(angle) * (tomove);
			}
			// ===================================================

			A.vel.X -= (1 + A.e) * p * nx * B.mass;
			A.vel.Y -= (1 + A.e) * p * ny * B.mass;

			B.vel.X += (1 + B.e) * p * nx * A.mass;
			B.vel.Y += (1 + B.e) * p * ny * A.mass;
		}
	}

	// Basic public operation on list
	public int count() {
		return spheres.size();
	}

	public void addSphere(int x, int y, int WIDTH, int HEIGHT) {
		if (tempX0 == -1) {
			if (selected == -1) {
				double s = random(0.25f, 0.75f);
				double size = (96 * s) / 2;
				// Prevents from creating a sphere if it overlaps another one
				for (int i = 0; i < spheres.size(); i++) {
					if (distance(spheres.get(i), x - size, y - size, size) <= Math
							.pow(spheres.get(i).size + size, 2)) {
						spheres.get(i).selected = true;
						selected = i;
						return;
					}
				}

				Sphere p = new Sphere(x, y, WIDTH, HEIGHT, random(0.4f, 0.9f),
						(float) s, texture, gravity);
				this.spheres.add(p);
			} else {
				spheres.get(selected).selected = false;
				selected = -1;
			}
		} else
			addLine(x, y);
	}

//	public void addSquare(int x, int y) {
//		rects.add(new Rect(x - 50, y - 50, x + 50, y - 50, x - 50, y + 50,
//				x + 50, y + 50));
//	}

	public void addLine(float x, float y) {
		// First step : start point
		if (tempX0 == -1) {
			tempX0 = x;
			tempY0 = y;
		}
		// Second step : end point
		else {
			// float dx = x - tempX0; // delta x
			// float dy = y - tempY0; // delta y
			//
			// double linelength = Math.sqrt(dx * dx + dy * dy);
			// dx /= linelength;
			// dy /= linelength;
			//
			// // Ok, (dx, dy) is now a unit vector pointing in the direction of
			// // the line
			// int thickness = 25;
			// float px = thickness / 2 * -dy; // vector with length width/2
			// float py = thickness / 2 * dx;
			//
			// // top and bottom
			// this.rects.add(new Rectangle((int) (x + px + py),
			// (int) (y + py - px), (int) (tempX0 + px - py),
			// (int) (tempY0 + py + px), (int) (x - px + py),
			// (int) (y - (py + px)), (int) (tempX0 - (px + py)),
			// (int) (tempY0 - (py - px))));

			this.lines.add(new Line(tempX0, tempY0, x, y, false));
			tempX0 = -1;
			tempY0 = -1;
		}
	}

	public void reset() {
		tempX0 = -1;
		tempY0 = -1;
		this.spheres.clear();
		this.lines.clear();
		this.rects.clear();
		genBoundaries();
	}

	// ==============================
	public void gravityChanger() {
		gravity = !gravity;
		for (int i = 0; i < spheres.size(); i++)
			spheres.get(i).gravityChanger();
	}

	public Vector2 GetPlayerPos() {
		return (player.pos);
	}

	public void setInEditor(boolean b) {
		inEditor = b;
		level.setInEditor(b);
	}
}