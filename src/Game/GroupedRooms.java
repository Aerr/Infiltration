package Game;

import java.util.LinkedList;

import Game.Basics.Vector2;

public class GroupedRooms
{
	public class ComputedPlace
	{
		private int srcX, srcY;
		public int getSrcX()
		{
			return srcX;
		}
		public int getSrcY()
		{
			return srcY;
		}
		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
		public int getW()
		{
			return w;
		}
		public int getH()
		{
			return h;
		}
		private int x, y;
		private int w, h;
		public ComputedPlace(int srcX, int srcY, int x, int y, int w, int h)
		{
			this.srcX = srcX;
			this.srcY = srcY;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}
	private LinkedList<Room> rooms;
	private LinkedList<Light> lights;
	private ComputedPlace places[][];

	public ComputedPlace[][] getPlaces()
	{
		return places;
	}

	public LinkedList<Room> getRooms()
	{
		return rooms;
	}

	public LinkedList<Light> getLights()
	{
		return lights;
	}

	public GroupedRooms()
	{
		this.lights = new LinkedList<Light>();
		this.rooms = new LinkedList<Room>();
	}

	public void addLight(Light l)
	{
		this.lights.add(l);
	}

	public void addRoom(Room r)
	{
		this.rooms.add(r);
	}

	public void removeRoom(Room r)
	{
		rooms.remove(r);
	}

	public void removeLight(Light l)
	{
		lights.remove(l);
	}

	public void InitalizeNewPositions()
	{
		places = new ComputedPlace[rooms.size()][lights.size()];
	}
	/**
	 * @param i : index of the room containing the light
	 * @param j : index of the light contained in the room
	 * @param src : computed position
	 */
	public void AssignPosition(int i, int j, int srcX, int srcY, int x, int y, int w, int h)
	{
		places[i][j] = new ComputedPlace(srcX, srcY, x, y, w, h); 
	}
	public void AssignPosition(int i, int j)
	{
		places[i][j] = null; 
	}

	public Vector2 GetLightPos(int j)
	{
		int i = 0;
		ComputedPlace p = null;
		while ((p = places[i++][j]) == null);
		double max = p.h * p.w;
		for (; i < places.length; i++)
		{
			ComputedPlace t1 = places[i][j];
			if (t1 != null)
			{
				double t2 = t1.h * t1.w;
				if (t2 > max)
				{
					max = t2;
					p = t1;
				}
			}
		}
		return (new Vector2(p.getX(), p.getY()));
	}
}
