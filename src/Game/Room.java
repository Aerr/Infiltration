package Game;

public class Room
{
	public int x, y, width, height;

	public Room(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Find if a point is contained in a room *
	 * 
	 * @param posX
	 *            The X coordinate of the point
	 * @param posY
	 *            The Y coordinate of the point
	 * 
	 * @return Whether or not the point is contained in the room.
	 */
	public boolean contains(double posX, double posY)
	{
		return (posX >= (x - 5) && posX <= (x + width + 5) && posY >= (y - 5) && posY <= (y + height + 5));
	}
}
