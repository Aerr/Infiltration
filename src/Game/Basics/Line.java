package Game.Basics;

import org.newdawn.slick.Graphics;

public class Line
{

	public float x0;
	public float y0;
	public float x1;
	public float y1;
	public boolean isBound;

	public Line(float x0, float y0, float x1, float y1, boolean b)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.isBound = b;
		swap();
	}

	public void render(Graphics g)
	{
		g.drawLine(x0, y0, x1, y1);
	}

	private void swap()
	{
		if (this.x0 > this.x1)
		{
			float temp = x0;
			x0 = x1;
			x1 = temp;

			temp = y0;
			y0 = y1;
			y1 = temp;
		}
	}

	public Vector2 getCenter()
	{
		return (new Vector2((x1 + x0) / 2, (y1 - y0) / 2));
	}

	public double slope()
	{
		return (y1 - y0) / (x1 - x0);
	}

	public boolean isVertical()
	{
		return (x0 - x1 == 0);
	}

	public boolean isHorizontal()
	{
		return (y0 - y1 == 0);
	}
}
