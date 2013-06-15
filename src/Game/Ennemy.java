package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;

import Game.Basics.Line;
import Game.Basics.Rect;
import Game.Basics.Vector2;

public class Ennemy
{
	private enum Move
	{
		IdleStand, Sprint, Walk
	}

	private static final int size = 72;
	private static final int bounds = 320;
	private static final int normalSpeed = 5;
	private static final int sprintSpeed = 12;
	private static final float scale = 0.75f;
	private float angle;

	private Animation stand_idle;
	private Animation stand_walk;

	private Move move;
	private int moveSpeed;
	private Vector2 speed;
	private Rectangle collision;
	private Rectangle intersect;
	public Vector2 pos;
	private Vector2 old_sign;

	private Vector2 direction;
	private Waypoint dir;
	private boolean onPath;
	private Waypoint start;

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

	public Ennemy(Image moves)
	{
		this.pos = new Vector2(750, 650);
		this.speed = Vector2.Zero();
		move = Move.IdleStand;
		moveSpeed = normalSpeed;
		SpriteSheet spritesheet = new SpriteSheet(moves, bounds, bounds);

		collision = new Rectangle((int) pos.X, (int) pos.Y, (int) (size * 1.15f), (int) (size * 1.15f));

		stand_idle = new Animation(spritesheet, 0, 0, 13, 0, true, 100, true);
		stand_walk = new Animation(spritesheet, 0, 1, 13, 1, true, 100, true);

		old_sign = Vector2.Zero();
		direction = Vector2.Zero();
	}

	public void HandleMoves(double dt, Vector2 playerPos, LinkedList<Waypoint> closests)
	{		
		if (closests != null)
		{
			if (start != closests.get(0))
			{
				start = closests.get(0);
				onPath = false;
			}
			closests.remove(0);
			if (pos.getDistance(new Vector2(start.getX(), start.getY())) > 100 && !onPath)
				direction = new Vector2(start.getX() - pos.X, pos.Y - start.getY());
			else
			{
				onPath = true;
				Waypoint dest = closests.get(0);
				double min = playerPos.getDistance(new Vector2(dest.getX(), dest.getY()));
				closests.remove(0);			
				for (Waypoint w : closests)
				{
					double tmp = playerPos.getDistance(new Vector2(w.getX(), w.getY()));
					if (tmp < min)
					{
						dest = w;
						min = tmp;
					}
				}
				direction = new Vector2(dest.getX() - pos.X, pos.Y - dest.getY());
			}
		}
		else
			direction = Vector2.Zero();

		if (!direction.isZero())
		{
			move = Move.Walk;
			if (direction.Y > 0f && speed.Y > -1)
			{
				speed.Y -= dt * 2;
				if (speed.Y < -1)
					speed.Y = -1;
			}
			else if (speed.Y < 0)
				speed.Y += dt * 4;

			if (direction.Y < 0f && speed.Y < 1)
			{
				speed.Y += dt * 2;

				if (speed.Y > 1)
					speed.Y = 1;
			}
			else if (speed.Y > 0)
				speed.Y -= dt * 4;

			if (direction.X > 0f && speed.X < 1)
			{
				speed.X += dt * 2;

				if (speed.X > 1)
					speed.X = 1;
			}
			else if (speed.X > 0)
				speed.X -= dt * 4;
			if (direction.X < 0f && speed.X > -1)
			{
				speed.X -= dt * 2;

				if (speed.X < -1)
					speed.X = -1;
			}
			else if (speed.X < 0)
				speed.X += dt * 4;
		}
		else
		{
			move = Move.IdleStand;
		}

	}

	public void Update(LinkedList<Rectangle> rects, Rectangle playerRect)
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

			boolean colliding = false;
			if (!(colliding = getColliding(playerRect)))
			{
				for (Rectangle r : rects)
				{
					colliding = getColliding(r);
					if (colliding)
					{
						if (pos.Y >= r.y && pos.Y <= r.y + r.height + 300)
						{
							if (pos.Y <= r.y + r.height)
								speed = new Vector2(0, 1);
							else
								speed = new Vector2(0, Math.signum(playerRect.getCenterY() - pos.Y));
						}
						else if (pos.X >= r.x && pos.X <= r.x + r.width + 300)
						{

							if (pos.X <= r.x + r.width)
								speed = new Vector2(1, 0);
							else
								speed = new Vector2(Math.signum(playerRect.getCenterX() - pos.X), 0);
						}
					}
				}
			}

			Vector2 newVel = speed.GetMul(moveSpeed * 0.5);

			stand_walk.setSpeed((float) newVel.GetLength() * 0.55f);

			// Movements
			// if (!colliding)
			{
				pos.X += (newVel.X);
				pos.Y += (newVel.Y);
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

		// DEBUG
		// Collisions' dummy
		g.drawRect((float) collision.getX(), (float) collision.getY(), (float) collision.getWidth(), (float) collision.getHeight());
		// // Collisions' residues
		//		 if (intersect != null)
		//		 g.drawRect((float) intersect.getX(), (float) intersect.getY(), (float) intersect.getWidth(), (float) intersect.getHeight());

		// g.fillOval((float) collision.getCenterX() - 15, (float) collision.getCenterY() - 15, 30,30);
		// ---DEBUG

		// Shadow drawing
		if (lights != null)
		{
			for (Light curr : lights)
			{
				Color tmp = new Color(0, 0, 0, 0.75f / (lights.size() + 1));
				double d = pos.getDistance(new Vector2(curr.getX(), curr.getY()));
				if (curr.isSwitched_on() && curr.isOn())
				{
					Rect rect =
							new Rect(
									(int) pos.X + 32,
									(int) pos.Y + 32,
									(int) pos.X + 96,
									(int) pos.Y + 32,
									(int) pos.X + 32,
									(int) pos.Y + 96,
									(int) pos.X + 96,
									(int) pos.Y + 96);
					tmp.a -= d / 1500000f;

					for (int j = 0; j < 4; j++)
					{
						Line l = rect.edges[j];
						Polygon poly = new Polygon();
						poly.addPoint(l.x0, l.y0);
						poly.addPoint(l.x1, l.y1);
						poly.addPoint(l.x1 - curr.getX() + l.x1, l.y1 - curr.getY() + l.y1);
						poly.addPoint(l.x0 - curr.getX() + l.x0, l.y0 - curr.getY() + l.y0);

						ShapeFill fill =
								new GradientFill(
										(l.x0 + l.x1) / 2,
										(l.y1 + l.y0) / 2,
										tmp,
										(l.x1 - curr.getX() + l.x1 + l.x0 - curr.getX() + l.x0) / 2,
										(l.y1 - curr.getY() + l.y1 + l.y0 - curr.getY() + l.y0) / 2,
										new Color(0, 0, 0, 0f));

						g.fill(poly, fill);
					}
				}
			}
		}

		// g.drawString("Coucou", (float) drawPos().X, (float) drawPos().Y);
		//		if (start != null && dest != null)
		//		{
		//			g.fillRect(start.x, start.y, start.width, start.height);
		//			g.fillRect(dest.x, dest.y, dest.width, dest.height);
		//		}
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
	}

	private boolean getColliding(Rectangle r)
	{
		if (collision.contains(r) || collision.intersects(r))
		{
			intersect = (Rectangle) collision.createIntersection(r);
			double w = intersect.getWidth();
			double h = intersect.getHeight();
			if (h < 12 || Math.min(h, w) == h)
			{
				if (speed.Y > 0)
					pos.Y -= h + 1;
				else if (speed.Y < 0)
					pos.Y += h + 1;
			}
			else if (h < 50)
			{
				if (intersect.getY() + h > collision.getCenterY())
					pos.Y -= h;
				else
					pos.Y += h;
			}
			if (w < 12 || Math.min(h, w) == w)
			{
				if (speed.X > 0)
					pos.X -= w + 1;
				else if (speed.X < 0)
					pos.X += w + 1;
			}
			else if (w < 50)
			{
				if (intersect.getX() + w > collision.getCenterX())
					pos.X -= w;
				else
					pos.X += w;
			}
			return true;
		}
		return false;
	}
}
