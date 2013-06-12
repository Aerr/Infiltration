package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import Game.Main.GameState;
import Game.Basics.Vector2;

public class ObjectsHandler
{
	private LinkedList<Rectangle> rects;
	private Player player;
	public int scale;
	private Level level;
	private LightManager lightManager;
	private boolean lightEnabled;

	private boolean inEditor;

	public ObjectsHandler(int w, int h, Image img_perso, Image img_floor, Image img_wall, Image img_light, Image img_fight)
	{
		this.rects = new LinkedList<Rectangle>();

		player = new Player(w, h, img_perso, img_fight);

		level = new Level(img_wall);
		this.rects = level.Load("level.cfg");

		lightEnabled = true;
		lightManager = new LightManager(w, h, img_light, level.getCurrentRoom(player.getPos()));
	}

	public double random(double x, double y)
	{
		return (Math.random() * (y - x)) + x;
	}

	public void render(GameContainer gc, Graphics g, GameState state, float x, float y) throws SlickException
	{

		level.render(g);

		player.Render(gc, g, level.getCurrentLights(player.getPos()));

		level.renderWalls(g);

		if (lightEnabled)
			lightManager.render(g);
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
		else if (ip.isKeyPressed(Input.KEY_F12) && inEditor)
		{
			level.Init();
			this.rects = level.Load("level.cfg");
		}

		if (lightEnabled)
			lightManager.update(dt, level.getCurrentRoom(player.getPos()));
	}

	public Vector2 GetPlayerPos()
	{
		return (player.pos);
	}

	public void setInEditor(boolean b)
	{
		if (inEditor)
			level.Save_Level();
		inEditor = b;
		level.setInEditor(b);
	}

	public void printInfos(Graphics g)
	{
		if (inEditor)
			level.printInfo(g, player.getPos());
	}
}