package game;

import java.awt.Dimension;

public class Box {
	public int x,y,w,h;
	
	public Box() {
		
	}

	public Box(int a, int b, int c, int d) {
		x=a;
		y=b;
		w=c;
		h=d;
	}
	public Box(Dimension d) {
		x=0;
		y=0;
		w=d.width;
		h=d.height;
	}
	

	public Box add(Box b) {
		return new Box(x+b.x, y+b.y, w, h);
	}
	public Box add(Vector2f v) {
		return new Box((int)(v.x+x), (int)(v.y+y), w, h);
	}
	public Box translate(Vector2f v) {
		return new Box((int)(v.x+x), (int)(v.y+y), (int)(w + v.x), (int)(h + v.y));
	}
	
	public Box copy() {
		return new Box(x,y,w,h);
	}
	public Box mirrorX() {
		return new Box(w, y, x, h);
	}
	public Box mirrorY() {
		return new Box(x, h, w, y);
	}
	
	
	public boolean intersects(Box b) {
		return (x<(b.x+b.w)) && ((x+w)>b.x) && (y<(b.y+b.h)) && ((y+h)>b.y);
	}

	public Vector2f getPos2f() {
		return new Vector2f(x,y);
	}
	public Vector2f getDim2f() {
		return new Vector2f(w,h);
	}
	
	public Dimension getDim() {
		return new Dimension(w,h);
	}
}
