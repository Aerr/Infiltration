package Game;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

import Game.Basics.Vector2;

public class Level
{
	private enum Mode
	{
		Floor(0), Wall(1), Light(2), End(3);
		private int id;

		private Mode(int i)
		{
			id = i;
		}

		public int i()
		{
			return id;
		}
	}

	private LinkedList<Obj> positions;
	private LinkedList<GroupedRooms> rooms;

	public LinkedList<Obj> getPositions()
	{
		return positions;
	}

	private SpriteSheet sprite;

	LinkedList<Rectangle> walls;
	private Rectangle rect;

	private static final int[] gridW = new int[] { 268, 67, 1920, };
	private static final int[] gridH = new int[] { 178, 67, 1080, };

	private boolean inEditor;
	private int mode;

	private Vector2 temp;
	private Vector2 mouse;
	private int currentId;
	private float currentIntensity;

	public boolean isInEditor()
	{
		return inEditor;
	}

	public void setInEditor(boolean inEditor)
	{
		// this.Init();
		this.inEditor = inEditor;
	}

	public GroupedRooms getCurrentRoom(Vector2 pos)
	{
		int i = 0;
		for (GroupedRooms room : rooms)
		{
			for (Room r : room.getRooms())
			{
				if (r.contains(pos.X, pos.Y))
					return rooms.get(i);
			}
			i++;
		}
		return null;
	}

	public LinkedList<Light> getCurrentLights(Vector2 pos)
	{
		int i = 0;
		for (GroupedRooms room : rooms)
		{
			for (Room r : room.getRooms())
			{
				if (r.contains(pos.X, pos.Y))
					return rooms.get(i).getLights();
			}
			i++;
		}
		return null;
	}

	public Level(Image wall)
	{
		this.Init();

		this.sprite = new SpriteSheet(wall, 1, 1);

		rect = new Rectangle(0, 0, 1920, 1080);

		inEditor = false;
		mode = Mode.Floor.i();

	}

	public void Init()
	{
		positions = new LinkedList<Obj>();
		walls = new LinkedList<Rectangle>();

		rooms = new LinkedList<GroupedRooms>();

		temp = Vector2.Zero();

		currentId = 0;
		currentIntensity = 1;
	}

	public void Update(Vector2 pos)
	{
		rect.x = ((int) pos.X);
		rect.y = ((int) pos.Y);
	}

	public void UpdateEditor(Vector2 pos, Input ip)
	{
		mouse = pos;
		// Clicking : Placing Elements
		if (ip.isMouseButtonDown(0) || ip.isMouseButtonDown(1))
		{
			if (mode == Mode.Light.i())
			{
				if (ip.isMousePressed(0))
				{
					while (currentId > rooms.size() - 1)
						rooms.add(new GroupedRooms());
					rooms.get(currentId).addLight(new Light(mouse.X, mouse.Y, currentIntensity, 0));
				}
				else if (ip.isMouseButtonDown(1))
				{

					for (GroupedRooms room : rooms)
					{
						for (Light l : room.getLights())
						{
							if (mouse.getDistance(new Vector2(l.getX(), l.getY())) < 100)
							{
								room.removeLight(l);
								break;
							}
						}
					}
				}
			}
			else
			{
				mouse = SnapToGrid(mouse);
				if (ip.isMouseButtonDown(0))
					positions.add(new Obj(new Vector2(mouse.X, mouse.Y), mode, 0));
				else
				{
					Clean_List();
					for (int i = 0; i < positions.size(); i++)
					{
						if (positions.get(i).getPos().Equals(new Vector2(mouse.X, mouse.Y)))
						{
							positions.remove(i);
							break;
						}
					}
				}
			}
		}
		// Saving
		else if (ip.isKeyPressed(Input.KEY_F5))
			Save_Level();
		// Pressing X : Changing mode (Floor, Wall, Props)
		else if (ip.isKeyPressed(Input.KEY_X))
		{
			temp = Vector2.Zero();
			mode = ++mode % Mode.End.i();
		}
		// Pressing Enter : creating rectangle (walls' collision ; defining rooms)
		else if (ip.isKeyPressed(Input.KEY_ENTER) && !ip.isKeyDown(Input.KEY_X)) // To correct odd bug...
		{
			if (mode == Mode.Wall.i())
				mouse = SnapToGrid(mouse);
			if (temp.isZero())
				temp = mouse;
			else
			{
				if (temp.X > mouse.X || temp.Y > mouse.Y)
				{
					Vector2 v = temp;
					temp = mouse;
					mouse = v;
				}
				if (mode == Mode.Wall.i())
					mouse.Add(new Vector2(gridW[mode], gridH[mode]));
				if (temp.X != mouse.X && temp.Y != mouse.Y)
				{
					if (mode == Mode.Wall.i())
						walls.add(new Rectangle((int) temp.X, (int) temp.Y, (int) Math.abs(mouse.X - temp.X), (int) Math.abs(mouse.Y
								- temp.Y)));
					else if (mode == Mode.Floor.i())
					{
						while (currentId > rooms.size() - 1)
							rooms.add(new GroupedRooms());
						rooms.get(currentId).addRoom(
								new Room((int) temp.X, (int) temp.Y, (int) Math.abs(mouse.X - temp.X), (int) Math.abs(mouse.Y - temp.Y)));
					}
					temp = Vector2.Zero();
				}
			}
		}
		else if (ip.isKeyPressed(Input.KEY_DELETE))
		{
			temp = Vector2.Zero();
			if (mode == Mode.Wall.i())
			{
				for (Rectangle w : walls)
				{
					if (w.contains(mouse.X, mouse.Y, 30, 30) || w.intersects(mouse.X, mouse.Y, 30, 30))
						walls.remove(w);
				}
			}
			else if (mode == Mode.Floor.i())
			{

				for (GroupedRooms room : rooms)
				{
					for (Room r : room.getRooms())
					{
						if (r.contains(mouse.X, mouse.Y))
						{
							room.removeRoom(r);
							break;
						}
					}
				}
			}
		}
		else if (ip.isKeyPressed(Input.KEY_NEXT))
		{
			if (mode == (Mode.Floor).i())
				currentId++;
			else if (mode == (Mode.Light).i())
				currentIntensity += 0.2f;
		}
		else if (ip.isKeyPressed(Input.KEY_PRIOR))
		{
			if (mode == (Mode.Floor).i())
				currentId--;
			else if (mode == (Mode.Light).i() && currentIntensity > 0.2f)
				currentIntensity -= 0.2f;
		}
	}

	public void render(Graphics g)
	{
		for (Obj curr : positions)
		{
			Rectangle currRect = new Rectangle((int) curr.getPos().X, (int) curr.getPos().Y, gridW[curr.getT()], gridH[curr.getT()]);
			if (rect.contains(currRect) || rect.intersects(currRect))
			{
				int h = 0;
				for (int j = 0; j < curr.getT(); j++)
				{
					h += gridH[j];
				}
				Image tmp = sprite.getSubImage(curr.getId() * gridW[curr.getId()], h, gridW[curr.getT()], gridH[curr.getT()]);
				g.drawImage(tmp, (float) curr.getPos().X, (float) curr.getPos().Y);
			}
		}

		if (inEditor)
		{
			g.setColor(Color.darkGray);

			for (int i = rect.y / gridH[mode]; i * gridH[mode] <= 1080 + (rect.y); i++)
				g.drawLine(rect.x, i * gridH[mode], rect.x + 1920, i * gridH[mode]);

			for (int i = rect.x / gridW[mode]; i * gridW[mode] <= 1920 + (rect.x); i++)
				g.drawLine(i * gridW[mode], rect.y, i * gridW[mode], rect.y + 1080);

			if (!temp.isZero())
			{
				g.setColor(new Color(255, 43, 43, 128));
				g.fillRect(
						(float) Math.min(temp.X, mouse.X),
						(float) Math.min(temp.Y, mouse.Y),
						(float) Math.abs(temp.X - mouse.X),
						(float) Math.abs(temp.Y - mouse.Y));
			}
		}
	}

	public void renderWalls(Graphics g)
	{
		int h = 0;
		for (int j = 0; j < (Mode.Wall).i(); j++)
		{
			h += gridH[j];
		}
		for (Obj curr : positions)
		{
			if (curr.getT() == Mode.Wall.i())
			{
				Rectangle currRect = new Rectangle((int) curr.getPos().X, (int) curr.getPos().Y, gridW[curr.getT()], gridH[curr.getT()]);
				if (rect.contains(currRect) || rect.intersects(currRect))
				{
					Image tmp = sprite.getSubImage(curr.getId() * gridW[curr.getId()], h, gridW[curr.getT()], gridH[curr.getT()]);
					g.drawImage(tmp, (float) curr.getPos().X, (float) curr.getPos().Y);
				}
			}
		}
		if (inEditor)
		{
			if (mode == Mode.Wall.i())
			{
				g.setColor(Color.white);
				for (Rectangle w : walls)
				{
					g.drawRect((float) w.getX(), (float) w.getY(), (float) w.getWidth(), (float) w.getHeight());
				}
			}
			else if (mode == Mode.Floor.i())
			{
				for (GroupedRooms room : rooms)
				{
					for (Room r : room.getRooms())
					{
						g.setColor(new Color((r.width), r.y, r.x, 128));
						g.fillRect(r.x, r.y, r.width, r.height);
						g.setColor(Color.black);
						g.drawRect(r.x, r.y, r.width, r.height);
					}
				}
			}
			else if (mode == Mode.Light.i())
			{
				g.setColor(Color.magenta);
				for (GroupedRooms room : rooms)
				{
					for (Light l : room.getLights())
					{
						// if (room != null && room.contains((int)curr.GetX(), (int)curr.GetY()))
						g.fillOval(l.getX() - 15, l.getY() - 15, 30, 30);
					}
				}
			}
		}
	}

	private Vector2 SnapToGrid(Vector2 mouse)
	{
		if (mouse.X % gridW[mode] != 0)
		{
			if (mouse.X < 0)
				mouse.X -= gridW[mode];
			mouse.X = ((int) (mouse.X / gridW[mode])) * gridW[mode];
		}
		if (mouse.Y % gridH[mode] != 0)
		{
			if (mouse.Y < 0)
				mouse.Y -= gridH[mode];
			mouse.Y = ((int) (mouse.Y / gridH[mode])) * gridH[mode];
		}
		return mouse;
	}

	public LinkedList<Rectangle> Load(String file)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;

			int i = 0;
			reader.readLine();
			// Reading rectangles for rooms
			do
			{
				reader.readLine();
				rooms.add(new GroupedRooms());
				while ((line = reader.readLine()) != null)
				{
					if (!line.equalsIgnoreCase(""))
					{
						if (line.charAt(0) == '#')
							break;
						String[] p = line.split(" ");
						Room r = new Room(Integer.valueOf(p[0]), Integer.valueOf(p[1]), Integer.valueOf(p[2]), Integer.valueOf(p[3]));
						rooms.get(i).addRoom(r);
					}
				}
				// Reading positions and intensity and id for lights
				while ((line = reader.readLine()) != null)
				{
					if (!line.equalsIgnoreCase(""))
					{
						if (line.charAt(0) == '#')
							break;
						String[] p = line.split(" ");
						Light l = new Light(Integer.valueOf(p[0]), Integer.valueOf(p[1]), Float.parseFloat(p[2]), Integer.valueOf(p[3]));
						rooms.get(i).addLight(l);
					}
				}
				i++;
			} while (line != null && line.charAt(1) != '#');
			// Reading rectangles for wall collisions
			while ((line = reader.readLine()) != null)
			{
				if (!line.equalsIgnoreCase(""))
				{
					if (line.charAt(0) == '#')
						break;
					String[] p = line.split(" ");
					Rectangle r = new Rectangle(Integer.valueOf(p[0]), Integer.valueOf(p[1]), Integer.valueOf(p[2]), Integer.valueOf(p[3]));
					walls.add(r);
				}
			}
			// Reading texture positions
			while ((line = reader.readLine()) != null)
			{
				if (!line.equalsIgnoreCase(""))
				{
					String[] parts = line.split(" ");
					Vector2 v = new Vector2(Double.valueOf(parts[2]), Double.valueOf(parts[3]));
					positions.add(new Obj(v, Integer.valueOf(parts[0]), Integer.valueOf(parts[1])));
				}
			}
			reader.close();
		}
		catch (IOException ioe)
		{
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
		return walls;
	}

	public void Save_Level()
	{
		Clean_List();
		try
		{
			FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/" + "level.cfg", false);
			BufferedWriter output = new BufferedWriter(fw);

			int i = 1;

			for (GroupedRooms room : rooms)
			{
				output.write(String.format("#--ROOM %d--#\n", i++));
				output.write("#--Rectangles--#\n");
				for (Room r : room.getRooms())
				{
					output.write(String.format("%d %d %d %d\n", r.x, r.y, r.width, r.height));
				}

				output.write("#--Lights--#\n");
				for (Light l : room.getLights())
				{
					output.write(String.format(
							"%d %d %s %d\n",
							(int) l.getX(),
							(int) l.getY(),
							Float.toString(l.getIntensity()),
							l.getType()));
				}
				output.write(String.format("\n"));
			}

			output.write(String.format("\n"));
			output.write("##--WALLS--##\n");

			for (Rectangle curr : walls)
			{
				output.write(String.format(
						"%d %d %d %d\n",
						(int) curr.getX(),
						(int) curr.getY(),
						(int) curr.getWidth(),
						(int) curr.getHeight()));
			}

			output.write(String.format("\n"));
			output.write(String.format("\n"));
			output.write("##--TEXTURES--##\n");

			for (Obj curr : positions)
			{
				output.write(String.format("%d %d %d %d\n", curr.getT(), curr.getId(), (int) curr.getPos().X, (int) curr.getPos().Y));
			}

			output.flush();
			output.close();
		}
		catch (IOException ioe)
		{
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
	}

	private void Clean_List()
	{
		for (int i = 0; i < positions.size(); i++)
		{
			for (int j = 1; j < positions.size(); j++)
			{
				if (i != j && positions.get(i).getPos().Equals(positions.get(j).getPos()))
					positions.remove(j--);
			}
		}

	}

	public void printInfo(Graphics g, Vector2 playerPos)
	{
		g.setColor(Color.red);
		String[] m = new String[] { "Floor", "Wall", "Light", };
		g.drawString("Mode : " + m[mode], rect.x + 10, rect.y + 45);
		g.drawString("(Id: " + currentId + ")", rect.x + 125, rect.y + 45);
		if (mode == (Mode.Light).i())
			g.drawString("(Intensity: " + currentIntensity + ")", rect.x + 10, rect.y + 65);
		g.fillOval((int) playerPos.X - 15, (int) playerPos.Y - 15, 30, 30);
		g.drawString(String.format("Pos : %d , %d", (int) rect.getCenterX(), (int) rect.getCenterY()), rect.x + 10, rect.y + 95);
	}
	// **/
}
