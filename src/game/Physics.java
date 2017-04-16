package game;

public class Physics {
	
	
	/**
	 * The move method will perform AABB checking on all the bounding boxes within the current world.
	 * If collisions at set velocities are found collision handling is handled by both the respective classes and an internal 
	 * collision handling function referenced: collide(Box topBox, Box bottomBox, Vector2f v) - float
	 */
	public static boolean move(boolean jump) {
		for(Article top : Roger.articles) {
			if(top.boxes!=null) {
				for(Article bottom : Roger.articles) {
					if(top!=bottom && bottom.boxes!=null) {
						for(Box topBox : top.boxes) {
							Box nextX = topBox.add(top.dst).add(top.velocity.X());
							for(Box bottomBox : bottom.boxes) {
								bottomBox = bottomBox.add(bottom.dst);
								if(nextX.intersects(bottomBox)) {
									top.velocity.x = Physics.collide(topBox.add(top.dst), bottomBox, top.velocity.X());
									top.collide(Direction.X, bottom);
									bottom.hit(Direction.X, top);
									nextX = topBox.add(top.dst).add(top.velocity.X());
								}
							}
						}
					}
				}
				top.dst.x += top.velocity.x;
				for(Article bottom : Roger.articles) {
					if(top!=bottom && bottom.boxes!=null) {
						for(Box topBox : top.boxes) {
							Box nextY = topBox.add(top.dst).add(top.velocity.Y());
							for(Box bottomBox : bottom.boxes) {
								bottomBox = bottomBox.add(bottom.dst);
								if(nextY.intersects(bottomBox)) {
									if(top==Roger.getCam() && top.velocity.y>0) {
										jump = true;
									}
									top.velocity.y = Physics.collide(topBox.add(top.dst), bottomBox, top.velocity.Y());
									top.collide(Direction.Y, bottom);
									bottom.hit(Direction.Y, top);
									nextY = topBox.add(top.dst).add(top.velocity.Y());
								}
							}
						}
					}
				}
				top.dst.y += top.velocity.y;
			}
		}
		return jump;
	}
	
	/**
	 * This is called by the move method when a possible collision will occur. It can do many things but primarily
	 * it returns float of the distance between the colliding objects that will update into the top.
	 * @param topBox The primary box that is moving.
	 * @param bottomBox The box that is collided with and hit by the top box.
	 * @param v This is the velocity that the top box is moving in order to collide.
	 * @return The distance between the objects. This is used in move to update the velocity of the top article.
	 */
	public static float collide(Box topBox, Box bottomBox, Vector2f v) {
		float top = 0;
		float bottom = 0;
		if(v.y!=0) {
			if(v.y<0) {
				bottom = topBox.y;
				top = bottomBox.y+bottomBox.h;
			}
			else {
				top = bottomBox.y;
				bottom = topBox.y+topBox.h;
			}
		} else if(v.x!=0) {
			if(v.x<0) {
				bottom = topBox.x;
				top = bottomBox.x+bottomBox.w;
			}
			else {
				bottom = topBox.x+topBox.w;
				top = bottomBox.x;
			}
		}
		return top-bottom;
	}
	
	
	/**
	 * This will perform a quick O^2 lookthrough of wheter a provided box collides with any other box that exists within
	 * the game world.
	 * @param b A box to check if it is inside another box
	 * @param self This should be an article that is avoided when checking. This mostly refers to if an article is checking ahead in it's script and wants to ignore itself.
	 * @return true if b is inside a box in the game world of Roger.articles.
	 */
	public static boolean clearBox(Box b, Article self) {
		for(Article top : Roger.articles) {
			if(top!=self) {
				if(top.boxes!=null) {
					for(Box topBox : top.boxes) {
						Box nextX = topBox.add(top.dst);
						if(b.intersects(nextX)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
