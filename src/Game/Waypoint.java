package Game;

import java.util.LinkedList;

import Game.Basics.Vector2;

public class Waypoint
{
	private int ID;

	public int getID()
	{
		return ID;
	}

	public void setID(int iD)
	{
		ID = iD;
	}

	private int x, y;
	private LinkedList<Integer> links;

	public LinkedList<Integer> getLinks()
	{
		return links;
	}

	public int getX()
	{
		return x - 15;
	}

	public int getY()
	{
		return y - 15;
	}

	public Vector2 getPos()
	{
		return (new Vector2(getX(), getY()));
	}
	
	public Waypoint(double x, double y)
	{
		this.x = (int) x;
		this.y = (int) y;

		this.links = new LinkedList<Integer>();
	}

	public void addLink(int i)
	{
		boolean exists = false;
		for (Integer a : links)
		{
			if (a == i)
			{
				exists = true;
				break;
			}
		}
		if (!exists)
			this.links.add(i);
	}

	public void removeLink(Integer i)
	{
		links.remove(i);
		for (int j = 0; j < links.size(); j++)
		{
			Integer x = links.get(j);
			if (x >= i)
				links.set(j, x - 1);
		}
	}
}
