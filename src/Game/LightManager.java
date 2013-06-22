package Game;

import java.awt.Rectangle;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.renderer.SGL;

import Game.GroupedRooms.ComputedPlace;

public class LightManager
{
	private static final float ambient_lum = 1f;
	private static final float ambient_dark = 0.001f;

	private int resX;
	private int resY;
	private Image texture;
	private GroupedRooms rooms;
	private GroupedRooms old_rooms;
	private Color color;
	private Color old_color;

	public LightManager(int w, int h, Image texture, LinkedList<GroupedRooms> all_rooms, GroupedRooms current_rooms)
	{
		this.resX = w;
		this.resY = h;
		this.texture = texture;

		color = new Color(0, 0, 0, 0f);
		old_color = new Color(0, 0, 0, 1f);

		rooms = current_rooms;

		for (GroupedRooms groupedRooms : all_rooms)
			computeLights(groupedRooms);
	}

	public void init(LinkedList<GroupedRooms> all_rooms)
	{
		for (GroupedRooms groupedRooms : all_rooms)
			computeLights(groupedRooms);
	}

	public void update(double dt, GroupedRooms current_rooms)
	{
		if (color.a != ambient_lum)
		{
			if (color.a < ambient_lum)
				color.a += dt;
			else if (color.a > ambient_lum)
				color.a = ambient_lum;
		}

		if (old_color.a > 0)
		{
			old_color.a -= dt * 4f;
			if (old_color.a < 0.0001)
			{
				old_rooms = null;
				old_color.a = 0f;
			}
		}

		// When room just changed
		if (current_rooms != null && rooms != null && !rooms.equals(current_rooms))
		{
			color = new Color(0, 0, 0, 0f);
			old_color = new Color(0, 0, 0, 1f);
			old_rooms = rooms;
			// We assign
			rooms = current_rooms;
		}
	}

	public void render(Graphics g)
	{
		// We clear alphamap to update it
		g.clearAlphaMap();
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		GL11.glEnable(SGL.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		drawLights(g, rooms, color);

		drawLights(g, old_rooms, old_color);

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

	private void drawLights(Graphics g, GroupedRooms gp, Color c)
	{
		if (gp != null)
		{
			// We draw each light's alphamap
			boolean first = true;
			ComputedPlace[][] places = gp.getPlaces();
			int i = 0, j = 0;
			for (Light l : gp.getLights())
			{
				for (Room room : gp.getRooms())
				{
					ComputedPlace computed = places[i][j];
					if (places[i][j] != null)
					{
						Image tmp = texture.getScaledCopy(l.getIntensity());
						tmp = tmp.getSubImage(computed.getX(), computed.getY(), computed.getW(), computed.getH());
						tmp.draw(computed.getSrcX(), computed.getSrcY(), c);
					}

					if (first)
					{
						// For transitions
						g.setColor(new Color(0, 0, 0, c.a / 25f));
						g.fillRect(room.x, room.y, room.width, room.height);
					}
					i++;
				}
				first = false;
				i = 0;
				j++;
			}
		}
	}

	private void computeLights(GroupedRooms gp)
	{
		if (gp != null)
		{
			gp.InitalizeNewPositions();
			int i = 0, j = 0;
			for (Light l : gp.getLights())
			{
				for (Room room : gp.getRooms())
				{
					Image tmp = texture.getScaledCopy(l.getIntensity());
					// We take it to the right scale
					// Top-Left corner of the image (where it should be drawn)
					int srcX = (int) (l.getX() - tmp.getWidth() / 2);
					int srcY = (int) (l.getY() - tmp.getHeight() / 2);
					Rectangle r1 = new Rectangle(room.x, room.y, room.width, room.height);
					Rectangle r2 = new Rectangle(srcX, srcY, tmp.getWidth(), tmp.getHeight());

					if (r1.contains(r2) || r1.intersects(r2))
					{
						// We find whether it exceeds right/top limits of the room
						int x = Math.max(0, room.x - srcX);
						int y = Math.max(0, room.y - srcY);
						// We adjust the width/height depending on previous result
						// and so that it does not exceed the room's bottom/right limits
						int w = Math.min(room.width, Math.min(room.x + room.width - srcX, tmp.getWidth() - x));
						int h = Math.min(room.height, Math.min(room.y + room.height - srcY, tmp.getHeight() - y));
						// We get the subimage depending on the value we previously found
						// tmp = tmp.getSubImage(x, y, w, h);
						// We place it where it should be :
						// If it exceeded the right/top limits, it must be drawn at the room's top-left corner
						// Else, simply at the starting point (srcX, srcY)
						if (x != 0)
							srcX = room.x;
						if (y != 0)
							srcY = room.y;

						gp.AssignPosition(i, j, srcX, srcY, x, y, w, h);
					}
					else
						gp.AssignPosition(i, j);
					i++;
				}
				i = 0;
				j++;
			}
		}
	}
}
