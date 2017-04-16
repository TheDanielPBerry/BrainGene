package game;

import java.awt.Image;

import javax.swing.ImageIcon;



/**
 * An article is any object that can exist in the game world. Children of articles can have more complex behaviour.
 * @author Daniel Berry
 */
public class Article {
	
	/**A recognizable name to identify the type of article. This comes from the xml node name.*/
	public String name;
	/**This box specifies where from the source image is the article to be drawn. This is used for animation and other cool shit.*/
	public Box src;
	/**The destination is the box that the article is drawn to in the game world.*/
	public Box dst;
	/**These boxes are all relative to the dst and map where the article should collide with other collidable articles.*/
	public Box[] boxes;
	/**This component of the physics implementation tracks how fast an article moves per tick.*/
	public Vector2f velocity;
	/**This is the source image of what shall be painted to the surface.*/
	public Image img;
	/**Determine if an article is affected by global forces. (wind, gravity, etc.)*/
	public boolean staticElement;
	/**This flag is marked by the kill method and is handled later to remove an article from the game world.*/
	boolean markForDeath = false;
	
	
	public Article() {
		velocity = new Vector2f();
	}
	
	public Article(int x, int y, int w, int h) {
		velocity = new Vector2f();
	}
	/**
	 * Instantiate an article that will exist in the game.
	 * @param m A summary of the article data that will be copied.
	 * @param dest Where the article will be displayed on the plane.
	 */
	public Article(Model m, Box dest) {
		src = m.src.copy();
		if(dest.w == -1) {
			dest.w = m.src.w;
		}
		if(dest.h == -1) {
			dest.h = m.src.h;
		}
		dst = dest;
		img = new ImageIcon(Roger.relativeFilePath + m.img).getImage();
		boxes = m.boxes;
		name = m.name;
		velocity = new Vector2f();
	}
	public Article(Model m, Vector2f pos) {
		src = m.src.copy();
		dst = new Box((int)pos.x, (int)pos.y, m.src.w, m.src.h);
		img = new ImageIcon(Roger.relativeFilePath + m.img).getImage();
		boxes = m.boxes;
		name = m.name;
		velocity = new Vector2f();
	}
	
	public void tick() {
		
	}
	
	/**
	 * If an article collides with another article then, this method will be called.
	 * This pertains to this active instant colliding with another article.
	 * This method is designed to be overridden by subclasses.
	 * @param d The direction that article was moving when it collided with something else.
	 * @param bottom The article that it hit.
	 */
	public void collide(Direction d, Article bottom) {
		
	}
	
	/**
	 * If an article is collided into by another article then this method will be called.
	 * This pertains to when an active instant is hit by another moving article.
	 * This is designed for overriding by subclasses.
	 * @param d The direction that the article that collides with the instant is going
	 * @param top The article that collides with the instant
	 */
	public void hit(Direction d, Article top) {
		
	}
	
	
	/**
	 * This will kill an article and remove it from the game world.
	 */
	public void kill() {
		markForDeath = true;
	}
	
	
	public Article clone(Vector2f v) {
		return new Article();
	}
	
}
