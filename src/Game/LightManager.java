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

	public LightManager(int w, int h, Image texture)
	{
		lights = new LinkedList<Light>();
		this.w = w;
		this.h = h;
		this.texture = texture;
	}

	public void render(Graphics g, Rectangle room)
	{
//		for (int i = 0; i < lights.size(); i++)
//		{
//			Light curr = lights.get(i);
//			if (room != null && room.contains((int)curr.GetX(), (int)curr.GetY()))
//					g.fillOval(curr.GetX() - 15, curr.GetY() - 15, 30, 30);
//		}
		g.clearAlphaMap();
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		GL11.glEnable(SGL.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		for (int i = 0; i < lights.size(); i++)
		{
			Light curr = lights.get(i);
			if (room != null && room.contains((int)curr.GetX(), (int)curr.GetY()))
			{
			texture.draw(
					curr.GetX() - (texture.getWidth() * curr.GetIntensity() / 2),
					curr.GetY() - (texture.getWidth() * curr.GetIntensity() / 2),
					curr.GetIntensity());

			g.fillOval(curr.GetX(), curr.GetY(), 30, 30);
			}
		}

		g.setColor(new Color(0, 0, 0, 0.05f));
		if (room != null)
			g.fillRect(room.x, room.y, room.width, room.height);
		
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
