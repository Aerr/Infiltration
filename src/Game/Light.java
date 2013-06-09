package Game;

import Game.Basics.Vector2;

public class Light
{

	private int intensity;
	private Vector2 pos;
	private int type;

	public int getIntensity()
	{
		return this.intensity;
	}

	public int getType()
	{
		return this.type;
	}

	public float getX()
	{
		return (float) pos.X;
	}

	public float getY()
	{
		return (float) pos.Y;
	}

	public Light(double x, double y, int intensity, int type)
	{
		pos = new Vector2(x, y);
		this.intensity = intensity;
		this.type = type;
	}
}
