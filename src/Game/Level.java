package Game;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

import Game.Basics.Vector2;

public class Level {
	private enum Mode
	{
		Floor(0),
		Wall(1);
		private int id;
		private Mode(int i){id = i;}
		public int i(){return id;}
	}
	private LinkedList<Obj> positions;
	public LinkedList<Obj> getPositions() {
		return positions;
	}

	private SpriteSheet sprite;

	LinkedList<Rectangle> rectangles;
	private Rectangle rect;

	private static final int[] gridW = new int[] { 268, 67 };
	private static final int[] gridH = new int[] { 178, 67 };

	private boolean inEditor;
	private Mode mode;

	private Vector2 temp;

	public boolean isInEditor() {
		return inEditor;
	}

	public void setInEditor(boolean inEditor) {
		this.inEditor = inEditor;
	}

	public Level(Image wall) {
		positions = new LinkedList<Obj>();
		rectangles = new LinkedList<Rectangle>();

		this.sprite = new SpriteSheet(wall, 1, 1);

		rect = new Rectangle(0,0,1920,1080);
		inEditor = false;

		mode = Mode.Floor; 
		temp = Vector2.Zero();
	}

	public LinkedList<Rectangle> Load(String file)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.equalsIgnoreCase(""))
				{
					if (line.equalsIgnoreCase("#"))
						break;
					String[] p = line.split(" ");
					Rectangle r = new Rectangle(Integer.valueOf(p[0]), Integer.valueOf(p[1]), Integer.valueOf(p[2]),Integer.valueOf(p[3]));
					rectangles.add(r);
				}
			}
			while ((line = reader.readLine()) != null) {
				if (!line.equalsIgnoreCase(""))
				{
					String[] parts = line.split(" ");
					Vector2 v = new Vector2(Double.valueOf(parts[1]), Double.valueOf(parts[2]));
					positions.add(new Obj(v, Integer.valueOf(parts[0]), 0));
				}
			}
			reader.close();
		} catch (IOException ioe) {
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
		return rectangles;
	}

	public void render(Graphics g)
	{		
		for (int i = 0; i < positions.size(); i++) {
			Obj curr = positions.get(i);
			Rectangle currRect = new Rectangle((int)curr.getPos().X, (int)curr.getPos().Y, gridW[curr.getT()], gridH[curr.getT()]);
			if (rect.contains(currRect) || rect.intersects(currRect))
			{
				int h = 0;
				for (int j = 0; j < curr.getT(); j++) {
					h += gridH[j];
				}
				Image tmp = sprite.getSubImage(curr.getId() * gridW[curr.getId()], h, gridW[curr.getT()], gridH[curr.getT()]);
				g.drawImage(tmp, (float)curr.getPos().X, (float)curr.getPos().Y);
			}
		}

		if (inEditor)
		{
			g.setColor(Color.darkGray);

			for (int i = (int)(rect.y / gridH[mode.i()]); i * gridH[mode.i()] <= 1080 + (int)(rect.y); i++)
				g.drawLine(rect.x, i * gridH[mode.i()], rect.x + 1920, i * gridH[mode.i()]);

			for (int i = (int)(rect.x / gridW[mode.i()]); i * gridW[mode.i()] <= 1920 + (int)(rect.x); i++)
				g.drawLine(i * gridW[mode.i()], rect.y, i * gridW[mode.i()], rect.y + 1080);
		}
	}

	public void renderWalls(Graphics g)
	{		
		int h = 0;
		for (int j = 0; j < (Mode.Wall).i(); j++) {
			h += gridH[j];
		}
		for (int i = 0; i < positions.size(); i++) {
			Obj curr = positions.get(i);
			if (curr.getT() == Mode.Wall.i())
			{
				Rectangle currRect = new Rectangle((int)curr.getPos().X, (int)curr.getPos().Y, gridW[curr.getT()], gridH[curr.getT()]);
				if (rect.contains(currRect) || rect.intersects(currRect))
				{
					Image tmp = sprite.getSubImage(curr.getId() * gridW[curr.getId()], h, gridW[curr.getT()], gridH[curr.getT()]);
					g.drawImage(tmp, (float)curr.getPos().X, (float)curr.getPos().Y);
				}
			}
		}
		if (inEditor)
		{
			g.setColor(Color.white);
			for (int i = 0; i < rectangles.size(); i++) {
				g.drawRect((float)rectangles.get(i).getX(), (float)rectangles.get(i).getY(),(float)rectangles.get(i).getWidth(), (float)rectangles.get(i).getHeight());
			}

			g.setColor(Color.red);
			g.drawString("Mode : " + mode.toString(), rect.x + 10,  rect.y + 15);
			g.drawString(String.format("Pos : %d , %d", (int)rect.getCenterX(), (int)rect.getCenterY()), rect.x + 10,  rect.y + 50);
		}
	}

	public void Update(Vector2 pos) {
		rect.x = ((int)pos.X);
		rect.y = ((int)pos.Y);
	}

	public void UpdateEditor(Vector2 mouse, Input ip)
	{
		if (ip.isMouseButtonDown(0) || ip.isMouseButtonDown(1)) {
			mouse = SnapToGrid(mouse);
			if (ip.isMouseButtonDown(0))
				positions.add(new Obj(new Vector2(mouse.X, mouse.Y), mode.i(), 0));
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
		else if (ip.isKeyPressed(Input.KEY_F5))
			Save_Level();
		else if (ip.isKeyPressed(Input.KEY_X))
		{
			temp = Vector2.Zero();

			if (mode == Mode.Floor)
				mode = Mode.Wall;
			else
				mode = Mode.Floor;
		}
		else if (mode == Mode.Wall && ip.isKeyPressed(Input.KEY_ENTER) && !ip.isKeyDown(Input.KEY_X)) // To correct odd bug...
		{	
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
				mouse.Add(new Vector2(gridW[mode.i()],gridH[mode.i()]));
				if (temp.X != mouse.X && temp.Y != mouse.Y)
					rectangles.add(new Rectangle((int)temp.X, (int)temp.Y, (int)Math.abs(mouse.X - temp.X), (int)Math.abs(mouse.Y - temp.Y)));
				temp = Vector2.Zero();
			}
		}
	}

	private Vector2 SnapToGrid(Vector2 mouse)
	{
		if (mouse.X % gridW[mode.i()] != 0) {
			if (mouse.X < 0)
				mouse.X -= gridW[mode.i()];
			mouse.X = ((int) (mouse.X / gridW[mode.i()])) * gridW[mode.i()];
		}
		if (mouse.Y % gridH[mode.i()] != 0) {
			if (mouse.Y < 0)
				mouse.Y -= gridH[mode.i()];
			mouse.Y = ((int) (mouse.Y / gridH[mode.i()])) * gridH[mode.i()];
		}
		return mouse;
	}

	// /** Editor
	private void Clean_List() {
		for (int i = 0; i < positions.size(); i++) {
			for (int j = 1; j < positions.size(); j++) {
				if (i != j && positions.get(i).getPos().Equals(positions.get(j).getPos()))
					positions.remove(j--);
			}
		}

	}

	private void Save_Level() {
		Clean_List();
		try {
			FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/"
					+ "level.cfg", false);
			BufferedWriter output = new BufferedWriter(fw);
			for (int i = 0; i < rectangles.size(); i++) {
				Rectangle curr = rectangles.get(i);
				output.write(String.format("%d %d %d %d\n", (int)curr.getX(), (int)curr.getY(), (int)curr.getWidth(), (int)curr.getHeight()));
			}

			output.write("#\n");

			for (int i = 0; i < positions.size(); i++) {
				Obj curr = positions.get(i);
				output.write(String.format("%d %d %d\n", curr.getT(),(int) curr.getPos().X,(int)curr.getPos().Y));
			}

			output.flush();
			output.close();
		} catch (IOException ioe) {
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
	}
	// **/
}
