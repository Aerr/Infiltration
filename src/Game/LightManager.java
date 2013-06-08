package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.renderer.SGL;

public class LightManager
{

	LinkedList<Light> lights;
	private int w;
	private int h;
	private Image texture;
	private Rectangle room;
	private Rectangle old_room;
	private Color color;
	private Color old_color;

	public LightManager(int w, int h, Image texture)
	{
		lights = new LinkedList<Light>();
		this.w = w;
		this.h = h;
		this.texture = texture;
		color = new Color(0, 0, 0, 0.05f);
		old_color = new Color(0, 0, 0, 0.05f);
	}

	public void render(Graphics g, Rectangle current_room)
	{
		g.setColor(Color.pink);
		for (int i = 0; i < lights.size(); i++)
		{
			Light curr = lights.get(i);
			//			if (room != null && room.contains((int)curr.GetX(), (int)curr.GetY()))
			g.fillOval(curr.GetX() - 15, curr.GetY() - 15, 30, 30);
		}

		if (current_room != null && room != null && !room.equals(current_room))
		{
			color = new Color(0, 0, 0, 0.00f);
			old_color = new Color(0, 0, 0, 0.05f);
			old_room = room;
		}

		room = current_room;

		g.clearAlphaMap();
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		GL11.glEnable(SGL.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		for (int i = 0; i < lights.size(); i++) 
		{
			Light curr = lights.get(i);
			if (room != null && room.contains((int)curr.GetX(), (int)curr.GetY()))
			{
				Image tmp = texture.getScaledCopy(curr.GetIntensity());
				int w,h;
				int x = (int)(curr.GetX() - tmp.getWidth() / 2);
				int y = (int)(curr.GetY() - tmp.getHeight() / 2);

				x = Math.max(0, room.x - x);
				y = Math.max(0, room.y - y);
				w = Math.min(tmp.getWidth() - x, room.width);
				h = Math.min(tmp.getHeight() - y, room.height);
				
				tmp = tmp.getSubImage(x, y, w, h);
						
				tmp.draw(room.x, room.y);
				
//				texture.drawCentered(curr.GetX(), curr.GetY());
			}
		}

		if (room != null)
		{
			g.setColor(color);
			g.fillRect(room.x, room.y, room.width, room.height);
			if (color.a < 0.05f)
				color.a += 0.0003;
			else if (color.a > 0.05f)
				color.a = 0.05f;
		}
		if (old_room != null)
		{
			g.setColor(old_color);
			g.fillRect(old_room.x, old_room.y, old_room.width, old_room.height);
			if (old_color.a > 0)
				old_color.a -= 0.0002;
		}

		g.resetTransform();
		g.setColor(new Color(0, 0, 0, .001f));
		g.fillRect(0, 0, w, h);

		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);

		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_ALPHA, GL11.GL_DST_ALPHA);

		g.setColor(Color.black);
		g.fillRect(0, 0, w, h);
	}

	public void AddLight(double x, double y, int intensity, int type)
	{
		lights.add(new Light(x, y, intensity, type));
	}
}
