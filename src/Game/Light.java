package Game;

import Game.Basics.Vector2;

public class Light
{

	private float intensity;
	private Vector2 pos;
	private int type;
	private boolean switched_on;

	public boolean isSwitched_on()
	{
		return switched_on;
	}

	public void setSwitched_on(boolean switched_on)
	{
		this.switched_on = switched_on;
	}

	private boolean on;

	public boolean isOn()
	{
		return on;
	}

	public void setOn(boolean on)
	{
		this.on = on;
	}

	public float getIntensity()
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

	public Light(double x, double y, float intensity, int type)
	{
		pos = new Vector2(x, y);
		this.intensity = intensity;
		this.type = type;

		this.switched_on = true;
		this.on = true;
	}
}
