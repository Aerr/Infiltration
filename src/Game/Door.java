package Game;

import java.awt.Rectangle;

import Game.Basics.Vector2;

public class Door
{
	private boolean isOn;
	private Vector2 pos;
	private int w, h;
	private Rectangle r;

	public int getW()
	{
		return w;
	}

	public int getH()
	{
		return h;
	}

	public Vector2 getPos()
	{
		return pos;
	}

	public boolean IsOn()
	{
		return isOn;
	}

	public void setIsOn(boolean isOn)
	{
		this.isOn = isOn;
	}

	public Door(Vector2 pos, int width, int height)
	{
		this.pos = pos;
		isOn = true;
		w = width;
		h = height;
		r = new Rectangle((int)pos.X,(int)pos.Y, w, h);
	}

	public Rectangle getR()
	{
		return r;
	}
}
