package game.articles;

import game.Article;
import game.Box;
import game.Direction;
import game.Model;

public class Clara extends Mob {
	
	
	public Direction hDir = Direction.LEFT;
	public boolean motion = false;
	
	public Clara(Model m, Box d) {
		super(m,d);
		refreshRate = 0;
	}
	
	@Override
	public void collide(Direction dir, Article bottom) {
		
	}
	@Override
	public void hit(Direction dir, Article top) {
		
	}
	/**This will flip the src of the image and allow for the image to drawn in reverse as a mirror along the x axis.*/
	public void setXScreen(boolean left) {
		if((left && hDir!=Direction.LEFT) || (!left && hDir!=Direction.RIGHT)) {
			srcOrigin = srcOrigin.mirrorX();
			src = srcOrigin.copy();
		}
	}

	@Override
	public void tick() {
		super.tick();
		if(!motion) {
			srcOrigin.y = 0;
			srcOrigin.h = 256;
			src = srcOrigin.copy();
		} else if(velocity.y!=0) {
			srcOrigin.y = 512;
			src.y = 512;
			src.h = 768;
		}else if(hDir == Direction.RIGHT || hDir==Direction.LEFT) {
			srcOrigin.y = 256;
			src.y = 256;
			src.h = 512;
		}
	}
}
