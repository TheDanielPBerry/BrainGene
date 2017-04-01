package game.articles;
import game.Article;
import game.Box;
import game.Direction;
import game.Model;

public class Spring extends Article {
	
	public Spring(Model m, Box d) {
		super(m,d);
	}
	
	@Override
	public void collide(Direction dir, Article bottom) {
		
	}
	@Override
	public void hit(Direction dir, Article top) {
		if(dir == Direction.Y) {
			top.velocity.y = -32f;
			//stretch = 1;
			//kill();
		}
	}
	
	public byte stretch = 0;
	@Override
	public void tick() {
		/*if(stretch!=0) {
			src.h -= stretch;
			src.y += stretch;
			stretch++;
		}
		if(stretch>=2) {
			stretch*=-1;
		}*/
		super.tick();
	}
}
