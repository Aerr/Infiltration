package Game.Basics;

public class Camera {
	private Vector2 pos;

	public Vector2 getPos() {
		return pos;
	}
	private static final Vector2 VIEWPORT_SIZE = new Vector2(1920,1080);
//	private static final Vector2 WORLD_SIZE = new Vector2(100000,100000);
//	private static final Vector2 offsetMax = WORLD_SIZE.GetSub(VIEWPORT_SIZE);
//	private static final Vector2 offsetMin = new Vector2();
	
	public Camera(float x, float y)
	{
		pos = new Vector2(x,y);
	}
	public void update(Vector2 PlayerPos) {
		pos = PlayerPos.GetSub(VIEWPORT_SIZE.GetMul(0.5));
		
//		Limits
//		if (pos.X > offsetMax.X)
//		    pos.X = offsetMax.X;
//		else if (pos.X < offsetMin.X)
//			pos.X = offsetMin.X;
//		
//		if (pos.Y > offsetMax.Y)
//		    pos.Y = offsetMax.Y;
//		else if (pos.Y < offsetMin.Y)
//			pos.Y = offsetMin.Y;
	}
}
