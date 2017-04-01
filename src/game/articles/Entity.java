package game.articles;

import game.Article;
import game.Box;
import game.Model;
import game.Vector2f;


public class Entity extends Article {
	
	public boolean liftable = false;
	
	public Entity() {
		super();
		staticElement = false;
	}
	
	public Entity(Model m, Box dest) {
		super(m, dest);
		staticElement = false;
	}
	
	public Entity(Model m, Vector2f pos) {
		super(m, pos);
		staticElement = false;
	}
	
	public void tick() {
		super.tick();
	}
	
}
