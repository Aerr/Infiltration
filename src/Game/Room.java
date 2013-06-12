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
	
	public boolean contains(double posX, double posY)
	{
		return (posX >= (x - 1) && posX <= (x + width + 1) && posY >= (y - 1) && posY <= (y + height + 1));
	}
}
