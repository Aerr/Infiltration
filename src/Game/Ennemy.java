package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

import Game.Basics.Line;
import Game.Basics.Vector2;

public class Ennemy
{
	private enum Move
	{
		IdleStand, Sprint, Walk
	}

	private enum State
	{
		Normal, Investigating, Suspicious, Alerted
	}

	private static final int fovW = 600;
	private static final int fovH = 900;

	private static final int size = 72;
	private static final int bounds = 320;
	private static final int normalSpeed = 5;
	// private static final int sprintSpeed = 12;
	private static final float scale = 0.75f;
	private float angle;

	private Animation stand_idle;
	private Animation stand_walk;

	private Move move;
	private int moveSpeed;
	private Vector2 speed;
	private Rectangle collision;
	public Vector2 pos;
	private Vector2 old_sign;

	private Vector2 direction;
	private boolean onPath;
	private Waypoint startingWaypoint;
	private Waypoint finalWaypoint;
	private Waypoint destination;
	private State state;
	private Polygon fov;
	private LinkedList<Polygon> ps;

	public Vector2 getPos()
	{
		return (new Vector2(collision.getCenterX(), collision.getCenterY()));
	}

	public Vector2 drawPos()
	{
		return (new Vector2(pos.X - size * scale, pos.Y - size * scale));
	}

	private float GetSize()
	{
		return (scale * bounds);
	}

	public Ennemy(Image moves, LinkedList<Rectangle> walls)
	{
		ps = new LinkedList<Polygon>();
		state = State.Normal;

		this.pos = new Vector2(900, 600);
		this.speed = Vector2.Zero();
		move = Move.IdleStand;
		moveSpeed = normalSpeed;
		SpriteSheet spritesheet = new SpriteSheet(moves, bounds, bounds);

		collision = new Rectangle((int) pos.X, (int) pos.Y, (int) (size * 1.15f), (int) (size * 1.15f));

		stand_idle = new Animation(spritesheet, 0, 0, 13, 0, true, 100, true);
		stand_walk = new Animation(spritesheet, 0, 1, 13, 1, true, 100, true);

		old_sign = Vector2.Zero();
		direction = Vector2.Zero();

		UpdateFOV(walls);
	}

	public void HandleMoves(double dt, Waypoint dest, LinkedList<Waypoint> waypoints)
	{
		if (state == State.Suspicious)
		{
			this.destination = dest;
		}

		if ((state == State.Suspicious || state == State.Investigating) && waypoints != null && destination != null)
		{
			if (startingWaypoint != waypoints.get(0))
			{
				startingWaypoint = waypoints.get(0);
				onPath = false;
			}
			waypoints.remove(0);

			if (destination.equals(startingWaypoint))
			{
				if (state == State.Investigating)
					state = State.Normal;
				direction = Vector2.Zero();
			}
			else if (pos.getDistance(new Vector2(startingWaypoint.getX(), startingWaypoint.getY())) > 1500 && !onPath)
				direction = new Vector2(startingWaypoint.getX() - pos.X, startingWaypoint.getY() - pos.Y);
			else if (waypoints.size() > 0)
			{
				onPath = true;
				finalWaypoint = waypoints.get(0);

				double min = destination.getPos().getDistance(new Vector2(finalWaypoint.getX(), finalWaypoint.getY()));
				waypoints.remove(0);
				for (Waypoint w : waypoints)
				{
					double tmp = destination.getPos().getDistance(new Vector2(w.getX(), w.getY()));
					if (tmp < min)
					{
						finalWaypoint = w;
						min = tmp;
					}
				}

				direction = new Vector2(finalWaypoint.getX() - getPos().X, finalWaypoint.getY() - getPos().Y);
			}
		}
		else
			direction = Vector2.Zero();

		if (!direction.isZero())
		{
			move = Move.Walk;
			speed = direction.GetNormalized();
		}
		else
		{
			move = Move.IdleStand;
		}

	}

	public void Update(LinkedList<Rectangle> rects, LinkedList<Rectangle> walls, Rectangle playerRect, double visibility)
	{
		// No need to update when not moving
		// -> No collisions, no change of angles, no change of position
		if (move != Move.IdleStand)
		{
			angle = (float) Math.toDegrees(Math.atan2(speed.X, -speed.Y));

			Vector2 sign = new Vector2(1, 1);
			sign.X = signOf(speed.X);
			if (sign.X == 0)
				sign.X = old_sign.X;
			sign.Y = signOf(speed.Y);
			if (sign.Y == 0)
				sign.Y = old_sign.Y;

			collision.x = (int) (pos.X + sign.X * size * 0.5f);
			collision.y = (int) (pos.Y + sign.Y * size * 0.5f);

			old_sign = sign;

			Vector2 newVel = speed.GetMul(moveSpeed * 0.5);

			stand_walk.setSpeed((float) newVel.GetLength() * 0.55f);

			// Movements
			pos.X += (newVel.X);
			pos.Y += (newVel.Y);

		}

		UpdateFOV(walls);

		if (fov.contains(playerRect.x, playerRect.y))
		{
			if (visibility >= 0.8f)
				// "PLAYER IS FULLY VISIBLE (guards alerted on sight)";
				state = State.Alerted;
			else if (visibility >= 0.35f)
				// "YOU ARE VISIBLE (guards will investigate on sight)";
				state = State.Suspicious;
			else if (visibility >= 0.15f)
				// "PLAYER IS PARTIALLY VISIBLE (guards will investigate if moving)";
				state = State.Suspicious;
			else if (visibility >= 0.055f)
				// "PLAYER IS ALMOST INVISIBLE (noise and movements will locate you and guards might investigate)";
				state = State.Suspicious;
			else
				// "YOU'RE INVISIBLE"
				state = State.Normal;
		}

		// if (visibility >= 0.35f &&
		// else if (state == State.Alerted)
		// state = State.Investigating;
		// else if (state != State.Investigating)
		// state = State.Normal;

	}

	private void UpdateFOV(LinkedList<Rectangle> walls)
	{
		ps.clear();
		fov = new Polygon();
		float tX = (float) (getPos().X - 50 * old_sign.X);
		float tY = (float) (getPos().Y - 50 * old_sign.Y);
		fov.addPoint(tX, tY);
		fov.addPoint((float) (tX - fovW), (float) (tY + fovH));
		fov.addPoint((float) (tX + fovW), (float) (tY + fovH));
		fov = (Polygon) fov.transform(Transform.createRotateTransform((float) ((Math.atan2(-speed.X, speed.Y))), tX, tY));

		for (Rectangle w : walls)
		{
			Shape s = new org.newdawn.slick.geom.Rectangle(w.x, w.y, w.width, w.height);

			if (fov.intersects(s) || fov.contains(s))
			{
				Line[] lines = new Line[4];
				lines[0] = new Line(w.x, w.y, w.x + w.width, w.y);
				lines[1] = new Line(w.x, w.y, w.x, w.y + w.height);
				lines[2] = new Line(w.x + w.width, w.y, w.x + w.width, w.y + w.height);
				lines[3] = new Line(w.x, w.y + w.height, w.x + w.width, w.y + w.height);

				Polygon p = null;
				for (Line l : lines)
				{
					Vector2 startToEnd = new Vector2(l.x1, l.y1);
					startToEnd.Sub(new Vector2(l.x0, l.y0));

					Vector2 normal = new Vector2(startToEnd.Y, -startToEnd.X);

					Vector2 posToStart = new Vector2(l.x0, l.y0);
					posToStart.Sub(new Vector2(tX, tY));

					if (normal.getDot(posToStart) < 0)
					{
						Polygon poly = new Polygon();
						float xB, yB;
						float ratio = 4;
						poly.addPoint(l.x0, l.y0);
						poly.addPoint(l.x1, l.y1);

						xB = l.x1 - tX + l.x1;
						yB = l.y1 - tY + l.y1;

						xB = ratio * xB + (1 - ratio) * l.x1;
						yB = ratio * yB + (1 - ratio) * l.y1;

						poly.addPoint(xB, yB);

						xB = l.x0 - tX + l.x0;
						yB = l.y0 - tY + l.y0;

						xB = ratio * xB + (1 - ratio) * l.x0;
						yB = ratio * yB + (1 - ratio) * l.y0;

						// yB = 1000 * (yB - l.y0) + l.y0 - ((yB - l.y0) / (xB - l.x0)) * l.x0;
						// xB = 1000 * (xB - l.y0);

						poly.addPoint(xB, yB);
						//
						// if (p == null)
						// p = poly;
						// else
						// p = (Polygon)p.union(poly)[0];
						ps.add(poly);
						fov = (Polygon) fov.subtract(poly)[0];
						
						// break;
					}
				}
				// ps.add(p);
				// if (p != null)
				// fov = (Polygon)fov.subtract(p)[0];
			}
		}
	}

	private int signOf(double x)
	{
		if (x > 0.1)
			return 1;
		else if (x < -0.1)
			return -1;
		else
			return 0;
	}

	public void Render(GameContainer gc, Graphics g, LinkedList<Light> lights) throws SlickException
	{
		// Shadow drawing
		// if (lights != null)
		// {
		// for (Light curr : lights)
		// {
		// Color tmp = new Color(0, 0, 0, 0.75f / (lights.size() + 1));
		// double d = pos.getDistance(new Vector2(curr.getX(), curr.getY()));
		// if (curr.isSwitched_on() && curr.isOn())
		// {
		// Rect rect =
		// new Rect(
		// (int) pos.X + 32,
		// (int) pos.Y + 32,
		// (int) pos.X + 96,
		// (int) pos.Y + 32,
		// (int) pos.X + 32,
		// (int) pos.Y + 96,
		// (int) pos.X + 96,
		// (int) pos.Y + 96);
		// tmp.a -= d / 1500000f;
		//
		// for (int j = 0; j < 4; j++)
		// {
		// Line l = rect.edges[j];
		// Polygon poly = new Polygon();
		// poly.addPoint(l.x0, l.y0);
		// poly.addPoint(l.x1, l.y1);
		// poly.addPoint(l.x1 - curr.getX() + l.x1, l.y1 - curr.getY() + l.y1);
		// poly.addPoint(l.x0 - curr.getX() + l.x0, l.y0 - curr.getY() + l.y0);
		//
		// ShapeFill fill =
		// new GradientFill(
		// (l.x0 + l.x1) / 2,
		// (l.y1 + l.y0) / 2,
		// tmp,
		// (l.x1 - curr.getX() + l.x1 + l.x0 - curr.getX() + l.x0) / 2,
		// (l.y1 - curr.getY() + l.y1 + l.y0 - curr.getY() + l.y0) / 2,
		// new Color(0, 0, 0, 0f));
		//
		// g.fill(poly, fill);
		// }
		// }
		// }
		// }

		g.pushTransform();
		g.rotate((float) drawPos().X + GetSize() / 2, (float) drawPos().Y + GetSize() / 2, angle);
		switch (move)
		{
		case IdleStand:
			stand_idle.draw((float) drawPos().X, (float) drawPos().Y, GetSize(), GetSize());
			break;
		case Walk:
			stand_walk.draw((float) drawPos().X, (float) drawPos().Y, GetSize(), GetSize());
			break;
		default:
			stand_walk.draw((float) drawPos().X, (float) drawPos().Y, GetSize(), GetSize());
			break;
		}
		g.popTransform();

		// DEBUG
		// Collisions' dummy
		// g.drawRect((float) collision.getX(), (float) collision.getY(), (float) collision.getWidth(), (float) collision.getHeight());
		// // Collisions' residues
		// if (intersect != null)
		// g.drawRect((float) intersect.getX(), (float) intersect.getY(), (float) intersect.getWidth(), (float) intersect.getHeight());

		// g.fillOval((float) collision.getCenterX() - 15, (float) collision.getCenterY() - 15, 30,30);
		g.setColor(new Color(1f, 0f, 0f, 0.3f));
		g.fill(fov);

		g.setColor(Color.cyan);
		for (Polygon p : ps)
			g.fill(p);

		if (startingWaypoint != null && finalWaypoint != null)
		{
			g.setColor(Color.green);
			g.fillOval(startingWaypoint.drawX() - 30, startingWaypoint.drawY() - 30, 60, 60);
			g.setColor(Color.red);
			g.fillOval(finalWaypoint.drawX() - 30, finalWaypoint.drawY() - 30, 60, 60);
			g.setColor(Color.cyan);
			g.fillOval(destination.drawX() - 30, destination.drawY() - 30, 60, 60);
		}
		// ---DEBUG

	}
}
