package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;

import Game.Basics.Line;
import Game.Basics.Rect;
import Game.Basics.Vector2;

public class Player
{
	private enum Move
	{
		Crouch, IdleCrouch, IdleStand, Sprint, Walk, Punch,
	}

	private static final int size = 72;
	private static final int bounds = 320;
	private static final int crouchSpeed = 2;
	private static final int normalSpeed = 5;
	private static final int sprintSpeed = 12;
	private static final float scale = 0.75f;
	private float angle;

	private Animation crouch_idle;
	private Animation crouch_walk;
	private Animation stand_idle;
	private Animation stand_walk;
	private Animation punch;

	private Move move;
	private int moveSpeed;
	private Vector2 speed;
	private Rectangle collision;
	private Rectangle intersect;
	public Vector2 pos;

	public Vector2 GetPos()
	{
		return (new Vector2(pos.X - size * scale, pos.Y - size * scale));
	}

	private float GetSize()
	{
		return (scale * bounds);
	}

	public Player(int w, int h, Image moves, Image fight)
	{
		this.pos = new Vector2(900, 600);
		this.speed = Vector2.Zero();
		move = Move.IdleStand;
		moveSpeed = normalSpeed;
		SpriteSheet spritesheet = new SpriteSheet(moves, bounds, bounds);

		collision = new Rectangle((int) pos.X, (int) pos.Y, (int) (size * 1.5f), (int) (size * 1.5f));

		stand_idle = new Animation(spritesheet, 0, 0, 13, 0, true, 100, true);
		stand_walk = new Animation(spritesheet, 0, 1, 13, 1, true, 100, true);
		crouch_idle = new Animation(spritesheet, 0, 2, 13, 2, true, 100, true);
		crouch_walk = new Animation(spritesheet, 0, 3, 13, 3, true, 100, true);

		spritesheet = new SpriteSheet(fight, 500, 529);
		punch = new Animation(spritesheet, 0, 0, 13, 0, true, 75, true);

	}

	public void HandleInput(Input ip, double dt) throws SlickException
	{
		if (ip.isMousePressed(0) || move == Move.Punch)
		{
			if (punch.getFrame() < 13)
			{
				move = Move.Punch;
				speed = Vector2.Zero();
			}
			else
			{
				move = Move.IdleStand;
				punch.restart();
			}
		}
		else 
		{
			// /** Movement Speed control
			if (ip.isKeyDown(Input.KEY_LSHIFT))
			{
				moveSpeed = sprintSpeed;
				move = Move.Sprint;
			}
			else if (ip.isKeyDown(Input.KEY_LCONTROL))
			{
				moveSpeed = crouchSpeed;
				move = Move.Crouch;
			}
			else
			{
				moveSpeed = normalSpeed;
				move = Move.Walk;
			}
			// **/
			// /** If moving
			if (ip.isKeyDown(Input.KEY_UP)
					|| ip.isKeyDown(Input.KEY_Z)
					|| ip.isKeyDown(Input.KEY_DOWN)
					|| ip.isKeyDown(Input.KEY_S)
					|| ip.isKeyDown(Input.KEY_RIGHT)
					|| ip.isKeyDown(Input.KEY_D)
					|| ip.isKeyDown(Input.KEY_LEFT)
					|| ip.isKeyDown(Input.KEY_Q))
			{
				// /** If sudden change in direction
				if (speed.Y != 0 && (ip.isKeyPressed(Input.KEY_UP) || ip.isKeyPressed(Input.KEY_DOWN)))
				{
					if (speed.Y < 0)
						speed.Y = -0.25;
					else
						speed.Y = 0.25;
				}
				if (speed.X != 0 && (ip.isKeyPressed(Input.KEY_RIGHT) || ip.isKeyPressed(Input.KEY_LEFT)))
				{
					if (speed.X < 0)
						speed.X = -0.25;
					else
						speed.X = 0.25;
				}
				// **/

				// /** Moves depending on inputs and acceleration
				if (ip.isKeyDown(Input.KEY_UP) || ip.isKeyDown(Input.KEY_Z) && speed.Y > -1)
				{
					speed.Y -= dt * 2;
					if (speed.Y < -1)
						speed.Y = -1;
				}
				else if (speed.Y < 0)
					speed.Y += dt * 4;

				if (ip.isKeyDown(Input.KEY_DOWN) || ip.isKeyDown(Input.KEY_S) && speed.Y < 1)
				{
					speed.Y += dt * 2;

					if (speed.Y > 1)
						speed.Y = 1;
				}
				else if (speed.Y > 0)
					speed.Y -= dt * 4;

				if (ip.isKeyDown(Input.KEY_RIGHT) || ip.isKeyDown(Input.KEY_D) && speed.X < 1)
				{
					speed.X += dt * 2;

					if (speed.X > 1)
						speed.X = 1;
				}
				else if (speed.X > 0)
					speed.X -= dt * 4;
				if (ip.isKeyDown(Input.KEY_LEFT) || ip.isKeyDown(Input.KEY_Q) && speed.X > -1)
				{
					speed.X -= dt * 2;

					if (speed.X < -1)
						speed.X = -1;
				}
				else if (speed.X < 0)
					speed.X += dt * 4;
			}
			// **/

			// /** If not moving
			else
			{
				double a = dt * 3;
				if (Math.abs(speed.X) < 0.1)
					speed.X = 0;
				else if (speed.X < 0)
					speed.X += a;
				else
					speed.X -= a;

				if (Math.abs(speed.Y) < 0.1)
					speed.Y = 0;
				else if (speed.Y < 0)
					speed.Y += a;
				else
					speed.Y -= a;

				if (Math.abs(speed.X) < 0.1 && Math.abs(speed.Y) < 0.1)
				{
					if (move == Move.Crouch)
						move = Move.IdleCrouch;
					else
						move = Move.IdleStand;
				}
			}
			// **/
		}
	}

	public void Update(LinkedList<Rectangle> rects)
	{
		if (move != Move.IdleStand && move != Move.IdleCrouch && move != Move.Punch)
			angle = (float) Math.toDegrees(Math.atan2(speed.X, -speed.Y));

		collision.x = (int) (pos.X + signOf(speed.X) * size * 0.5f);
		collision.y = (int) (pos.Y + signOf(speed.Y) * size * 0.5f);

		boolean colliding = false;
		for (int i = 0; i < rects.size(); i++)
		{
			if (collision.contains(rects.get(i)) || collision.intersects(rects.get(i)))
			{
				intersect = (Rectangle) collision.createIntersection(rects.get(i));
				double w = intersect.getWidth();
				double h = intersect.getHeight();
				if (h < 9 || Math.min(h, w) == h)
				{
					if (speed.Y > 0)
						pos.Y -= h;
					else if (speed.Y < 0)
						pos.Y += h;
				}
				else if (h < 20)
				{
					if (intersect.getY() + h > collision.getCenterY())
						pos.Y -= h;
					else
						pos.Y += h;
				}
				if (w < 9 || Math.min(h, w) == w)
				{
					if (speed.X > 0)
						pos.X -= w;
					else if (speed.X < 0)
						pos.X += w;
				}
				else if (w < 20)
				{
					if (intersect.getX() + w > collision.getCenterX())
						pos.X -= w;
					else
						pos.X += w;
				}
				colliding = true;
				break;
			}
		}

		Vector2 newVel = speed.GetMul(moveSpeed * 0.5);
		stand_walk.setSpeed((float) newVel.GetLength() * 0.55f);

		// Movements
		if (!colliding)
		{
			pos.X += (newVel.X);
			pos.Y += (newVel.Y);
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
		Color startCol = new Color(0, 0, 0, 0.25f);

		// DEBUG
		// Collisions' dummy
		g.drawRect((float) collision.getX(), (float) collision.getY(), (float) collision.getWidth(), (float) collision.getHeight());
		// Collisions' residues
		if (intersect != null)
			g.drawRect((float) intersect.getX(), (float) intersect.getY(), (float) intersect.getWidth(), (float) intersect.getHeight());
		// ---DEBUG

		// Shadow drawing
		for (int i = 0; i < lights.size(); i++)
		{
			Light curr = lights.get(i);
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
			if (pos.getDistance(new Vector2(curr.getX(), curr.getY())) < 64)
				startCol.a -= 0.2;

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
								startCol,
								(l.x1 - curr.getX() + l.x1 + l.x0 - curr.getX() + l.x0) / 2,
								(l.y1 - curr.getY() + l.y1 + l.y0 - curr.getY() + l.y0) / 2,
								new Color(0, 0, 0, 0f));

				g.fill(poly, fill);
			}
		}

		g.pushTransform();
		g.rotate((float) GetPos().X + GetSize() / 2, (float) GetPos().Y + GetSize() / 2, angle);
		switch (move)
		{
		case IdleStand:
			stand_idle.draw((float) GetPos().X, (float) GetPos().Y, GetSize(), GetSize());
			break;
		case Walk:
			stand_walk.draw((float) GetPos().X, (float) GetPos().Y, GetSize(), GetSize());
			break;
		case IdleCrouch:
			crouch_idle.draw((float) GetPos().X, (float) GetPos().Y, GetSize(), GetSize());
			break;
		case Crouch:
			crouch_walk.draw((float) GetPos().X, (float) GetPos().Y, GetSize(), GetSize());
			break;
		case Punch:
			punch.draw((float)pos.X - 185 * scale, (float)pos.Y - 265 * scale, 500 * 0.75f, 529 * 0.75f);
			break;
		default:
			stand_walk.draw((float) GetPos().X, (float) GetPos().Y, GetSize(), GetSize());
			break;
		}
		g.popTransform();
	}

	public Rectangle inRoom(LinkedList<Rectangle> rooms)
	{
		for (int i = 0; i < rooms.size(); i++)
		{
			if (rooms.get(i).contains(pos.X, pos.Y, 40, 40) || rooms.get(i).intersects(pos.X, pos.Y, 20, 20))
				return rooms.get(i);
		}
		return null;
	}
}
