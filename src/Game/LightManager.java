package Game;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.renderer.SGL;

public class LightManager
{

	private LinkedList<Light> lights;
	private static final float ambient_lum = 0.05f;
	private static final float ambient_dark = 0.01f;

	public LinkedList<Light> getLights()
	{
		return lights;
	}

	public void setLights(LinkedList<Light> lights)
	{
		this.lights = lights;
	}

	private int resX;
	private int resY;
	private Image texture;
	private LinkedList<Room> rooms;
	private LinkedList<Room> old_rooms;
	private Color color;
	private Color old_color;

	public LightManager(int w, int h, Image texture)
	{
		lights = new LinkedList<Light>();
		this.resX = w;
		this.resY = h;
		this.texture = texture;

		color = Color.black;
		old_color = Color.black;
	}

	public void render(Graphics g, LinkedList<Room> current_rooms)
	{
		// When room just changed
		if (current_rooms != null && rooms != null && !rooms.equals(current_rooms))
		{
			color = new Color(0, 0, 0, 0.00f);
			old_color = new Color(0, 0, 0, ambient_lum);
			old_rooms = rooms;
		}

		// We assign
		rooms = current_rooms;

		// We clear alphamap to update it
		g.clearAlphaMap();
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		GL11.glEnable(SGL.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		if (rooms != null)
		{
			// We draw each light's alphamap
			for (int i = 0; i < lights.size(); i++)
			{
				Light curr = lights.get(i);
				for(Room room: rooms) {
					if (curr.isSwitched_on() && room.contains((int) curr.getX(), (int) curr.getY()))
					{
						curr.setOn(true);
						// We take it to the right scale
						Image tmp = texture.getScaledCopy(curr.getIntensity());
						// Top-Left corner of the image (where it should be drawn)
						int srcX = (int) (curr.getX() - tmp.getWidth() / 2);
						int srcY = (int) (curr.getY() - tmp.getHeight() / 2);
						// We find whether it exceeds right/top limits of the room
						int x = Math.max(0, room.x - srcX);
						int y = Math.max(0, room.y - srcY);
						// We adjust the width/height depending on previous result
						// and so that it does not exceed the room's bottom/right limits
						int w = Math.min(room.width, Math.min(room.x + room.width - srcX, tmp.getWidth() - x));
						int h = Math.min(room.height, Math.min(room.y + room.height - srcY, tmp.getHeight() - y));
						// We get the subimage depending on the value we previously found
						tmp = tmp.getSubImage(x, y, w, h);
						// We place it where it should be :
						// If it exceeded the right/top limits, it must be drawn at the room's top-left corner
						// Else, simply at the starting point (srcX, srcY)
						if (x != 0)
							srcX = room.x;
						if (y != 0)
							srcY = room.y;
						// We finally draw the mask
						tmp.draw(srcX, srcY);
					}
					else
						curr.setOn(false);
				}
			}

			// For transitions
			for(Room room: rooms) {
				g.setColor(color);
				g.fillRect(room.x, room.y, room.width, room.height);
				if (color.a != ambient_lum)
				{
					if (color.a < ambient_lum)
						color.a += 0.0003;
					else if (color.a > ambient_lum)
						color.a = ambient_lum;
				}	
			}
		}
		if (old_rooms != null)
		{
			for(Room room: old_rooms) {
				g.setColor(old_color);
				g.fillRect(room.x, room.y, room.width, room.height);

				if (old_color.a > 0)
					old_color.a -= 0.0002;
			}
		}

		g.resetTransform();
		// We let a very reduced visibility of the neighbouring rooms
		g.setColor(new Color(0, 0, 0, ambient_dark));
		g.fillRect(0, 0, resX, resY);

		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);

		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_ALPHA, GL11.GL_DST_ALPHA);

		// Here, we draw a black mask onto the whole screen
		g.setColor(Color.black);
		g.fillRect(0, 0, resX, resY);
	}
}
