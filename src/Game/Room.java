package Game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Room
{
	public int x, y, width, height;
	private Rectangle rect;
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
		rect = new Rectangle(x, y, width, height);
		this.id = id;
	}
	
	public Rectangle r()
	{
		return rect;
	}
	
	public void render(Graphics g)
	{
		
	}
}
