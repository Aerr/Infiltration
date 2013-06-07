package Game;

import Game.Basics.Vector2;

public class Light
{

	private int intensity;
	private Vector2 pos;
	private int type;

	public int GetIntensity()
	{
		return this.intensity;
	}

	public int GetType()
	{
		return this.type;
	}

	public float GetX()
	{
		return (float) pos.X;
	}

	public float GetY()
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
