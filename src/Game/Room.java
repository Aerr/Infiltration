package Game;

public class Room
{
	public int x, y, width, height;
	private int id;

	public int getId()
	{
		return id;
	}
	
	public Room(int x, int y, int width, int height, int id)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.id = id;
	}
	
	public boolean contains(double posX, double posY)
	{
		return (posX >= x && posX <= (x + width) && posY >= y && posY <= (y + height));
	}
}
