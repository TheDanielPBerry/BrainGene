package game.articles;

import game.*;

public class Bug extends Mob {
	
	public byte dx = -3;
	
	public Bug(Model m, Box d) {
		super(m,d);
		mirrorX();
	}
	
	@Override
	public void collide(Direction dir, Article bottom) {
		if(dir==Direction.X) {
			mirrorX();
			dx*=-1;
		}
	}
	@Override
	public void hit(Direction dir, Article top) {
		if(dir==Direction.Y && top.velocity.y>0) {
			kill();
		}
	}

	@Override
	public void tick() {
		super.tick();
		velocity.x=dx;
		if(((dx>0 && !Physics.clearBox(dst.add(new Vector2f(frameWidth, 10)), this))) || ((dx<0 && !Physics.clearBox(dst.add(new Vector2f(-frameWidth,15)), this)))) {
			dx*=-1;
			mirrorX();
		}
	}
	
}
