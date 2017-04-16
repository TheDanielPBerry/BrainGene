package game;

public class Vector2f {
	
	public float x, y;
	
	
	public Vector2f() {
		
	}
	
	public Vector2f(float X, float Y) {
		x = X;
		y = Y;
	}

	public Vector2f X() {
		return new Vector2f(x,0);
	}
	public Vector2f Y() {
		return new Vector2f(0,y);
	}
	
	public Vector2f neg() {
		return new Vector2f(-x,-y);
	}

	public Vector2f add(Vector2f v) {
		return new Vector2f(x+v.x, y+v.y);
	}
	public Vector2f multiply(Vector2f v) {
		return new Vector2f(x*v.x, y*v.y);
	}
	public Vector2f multiply(float a) {
		return new Vector2f(x*a, y*a);
	}
	public String toString() {
		return "(" + x + "," + y +  ")";
	}
}
