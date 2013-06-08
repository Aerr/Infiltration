package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import Game.Main.GameState;
import Game.Basics.Vector2;

import org.newdawn.slick.*;

public class ObjectsHandler
{
	private LinkedList<Rectangle> rects;
	private Player player;
	private float tempX0;
	private float tempY0;
	public int scale;
	private Level level;
	private LightManager lightManager;
	private boolean lightEnabled;

	private boolean inEditor;

	public ObjectsHandler(int w, int h, Image img_perso, Image img_floor, Image img_wall, Image img_light)
	{
		this.rects = new LinkedList<Rectangle>();

		this.tempX0 = -1;
		this.tempY0 = -1;

		player = new Player(w, h, img_perso);

		level = new Level(img_wall);
		this.rects = level.Load("level.cfg");

		lightEnabled = true;
		lightManager = new LightManager(w, h, img_light);
		lightManager.AddLight(1200, -600, 2, 0);
	}

	public double random(double x, double y)
	{
		return (Math.random() * (y - x)) + x;
	}

	public void render(GameContainer gc, Graphics g, GameState state, float x, float y) throws SlickException
	{

		level.render(g);

		player.Render(gc, g, lightManager.lights);

		level.renderWalls(g);

		if (tempX0 != -1)
			g.drawLine(tempX0, tempY0, x, y);
		

		if (lightEnabled)
			lightManager.render(g, player.inRoom(level.getRooms()));
	}

	public void update(double dt, double x, double y, Input ip, Vector2 camPos) throws SlickException
	{

		level.Update(camPos);
		if (inEditor)
			level.UpdateEditor(new Vector2(x + camPos.X, y + camPos.Y), ip);

		player.HandleInput(ip, dt);
		player.Update(rects);
		
		if (ip.isKeyPressed(Input.KEY_I))
			lightEnabled = !lightEnabled;
	}

	public Vector2 GetPlayerPos()
	{
		return (player.pos);
	}

	public void setInEditor(boolean b)
	{
		inEditor = b;
		level.setInEditor(b);
	}
	
	public void printInfos(Graphics g)
	{
		if (inEditor)
			level.printInfo(g);
	}
}