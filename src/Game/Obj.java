package Game;

import Game.Basics.Vector2;

public class Obj {
	private Vector2 pos;
	private int id;
	private int t;

	public int getId() {
		return id;
	}
	
	public Vector2 getPos() {
		return pos;
	}

	public int getT() {
		return t;
	}

	public Obj(Vector2 pos, int type, int id) {
		this.pos = pos;
		this.id = id;
		this.t = type;
	}
}
