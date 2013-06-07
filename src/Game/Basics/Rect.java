package Game.Basics;

import org.newdawn.slick.Graphics;

public class Rect
{

	public Line[] edges;

	public int getY()
	{
		return (int) edges[0].y0;
	}

	public int getX()
	{
		return (int) edges[0].x0;
	}

	public int getW()
	{
		return (int) (edges[0].x1 - edges[0].x0);
	}

	public int getH()
	{
		return (int) (edges[2].y1 - edges[0].y0);
	}

	public Rect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4)
	{
		edges = new Line[4];
		// 0 : Top line
		edges[0] = new Line(x1, y1, x2, y2, false);
		// 1 : Bottom line
		edges[1] = new Line(x3, y3, x4, y4, false);
		// 2 : Left line
		edges[2] = new Line(x1, y1, x3, y3, true);
		// 3 : Right line
		edges[3] = new Line(x2, y2, x4, y4, true);
	}

	public Rect(int x1, int y1, int width, int height)
	{
		edges = new Line[4];
		// 0 : Top line
		edges[0] = new Line(x1, y1, x1 + width, y1, false);
		// 1 : Bottom line
		edges[1] = new Line(x1, y1 + height, x1 + width, y1 + height, false);
		// 2 : Left line
		edges[2] = new Line(x1, y1, x1, y1 + height, false);
		// 3 : Right line
		edges[3] = new Line(x1 + width, y1, x1 + width, y1 + height, false);
	}

	public void render(Graphics g)
	{
		for (int i = 0; i < 4; i++)
		{
			g.drawLine(edges[i].x0, edges[i].y0, edges[i].x1, edges[i].y1);
		}
	}

	public Line FindClosestEdge(Vector2 v)
	{
		double min = v.getDistance(new Vector2((edges[0].x0 + edges[0].x1) / 2, edges[0].y0));
		int id = 0;
		for (int i = 1; i < 4; i++)
		{
			double d = v.getDistance(new Vector2((edges[i].x0 + edges[i].x1) / 2, (edges[i].y0 + edges[i].y1) / 2));
			if (d < min)
			{
				min = d;
				id = i;
			}
		}
		return edges[id];
	}
}
