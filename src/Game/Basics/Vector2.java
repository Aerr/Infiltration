package Game.Basics;

public class Vector2 {
	
	public static Vector2 Zero() { return (new Vector2(0,0)); }
	public double X, Y;

	public Vector2 (double x, double y)
	{
		this.X = x;
		this.Y = y;
	}

	public void Add(double a)
	{
		X += a;
		Y += a;
	}
	public void Add(Vector2 v)
	{
		X += v.X;
		Y += v.Y;
	}

	public void Sub(double a)
	{
		X -= a;
		Y -= a;
	}
	public void Sub(Vector2 v)
	{
		X -= v.X;
		Y -= v.Y;
	}
	public Vector2 GetSub(Vector2 v)
	{
		return (new Vector2(X - v.X, Y - v.Y));
	}
	
	public boolean isZero()
	{
		return (this.X == 0 && this.Y == 0);		
	}
	public Vector2 GetMul(double a)
	{
		return (new Vector2(X * a, Y * a));
	}
	public void Mul(double a)
	{
		X *= a;
		Y *= a;
	}
	public Vector2 Mul(Vector2 v)
	{
		return (new Vector2(X / v.X, Y / v.Y));
	}
	public double GetLength() {
		return Math.sqrt(X*X + Y*Y);
	}
	public Vector2 GetNormalized() {
		double length = Math.sqrt(X*X + Y*Y);
		if (length != 0.0) {
			float s = 1.0f / (float)length;
			double newX = X * s, newY = Y * s;
			if (newX > 1)
			{
				newX = 1;
				newY = 0;
			}
			else if (newY > 1)
			{
				newY = 1;
				newX = 0;
			}
			return (new Vector2(newX, newY));
		}
		else
			return (Vector2.Zero());
	}

	public boolean Equals(Vector2 v)
	{
		return (v.X == X && v.Y == Y);
	}
	
	public double getDistance(Vector2 v) 
	{
		double dX = Math.pow((X - v.X), 2);
		double dY = Math.pow((Y - v.Y), 2);
		// We don't root square because it's a very slow operation 
		// We'd rather square the other side of the (in)equation
		return (dX + dY);
	}
}
