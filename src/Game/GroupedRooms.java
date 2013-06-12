package Game;

import java.util.LinkedList;

public class GroupedRooms
{

	private LinkedList<Room> rooms;
	private LinkedList<Light> lights;

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
}
