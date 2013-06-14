package Game;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class WaypointManager
{
	private LinkedList<Waypoint> waypoints;

	public LinkedList<Waypoint> getWaypoints()
	{
		return waypoints;
	}

	public WaypointManager(String file)
	{
		
		this.waypoints = new LinkedList<Waypoint>();
		Load(file);
	}

	private void Load(String file)
	{
		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null)
			{
				if (!line.equalsIgnoreCase(""))
				{
					String[] p = line.split(" ");
					Waypoint w = new Waypoint(Integer.valueOf(p[0]), Integer.valueOf(p[1]));
					
					line = reader.readLine();
					if (!line.equalsIgnoreCase(""))
					{
						p = line.split(" ");
						for (String s: p)
							w.addLink(Integer.valueOf(s));
					}
					waypoints.add(w);
				}
			}
			
			reader.close();
		}
		catch (IOException ioe)
		{
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
	}
}

