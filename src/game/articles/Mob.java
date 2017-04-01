package game.articles;

import game.Animation;
import game.Box;
import game.Model;
import game.Vector2f;

public class Mob extends Entity implements Animation {
	
	/**A useful dimension data about the source image.*/
	public Box imageDim;
	/**A copy of the original source image for translation.*/
	public Box srcOrigin;
	/**A refresh counter for animation. Determines frame distance between static animation frames.*/
	public byte refresh = 0;
	/**A set rate of how many ticks should occur between each animation frame.*/
	public byte refreshRate = 10;
	/**A way to keep track of src width within the context of horizontal frame animation.*/
	public int frameWidth;
	
	public Mob() {
		super();
		imageDim = new Box(0, 0, img.getWidth(null), img.getHeight(null));
		srcOrigin = src.copy();
		frameWidth = src.w;
	}
	public Mob(Model m, Box dest) {
		super(m, dest);
		imageDim = new Box(0, 0, img.getWidth(null), img.getHeight(null));
		srcOrigin = src.copy();
		frameWidth = src.w;
	}
	
	public Mob(Model m, Vector2f pos) {
		super(m, pos);
		imageDim = new Box(0, 0, img.getWidth(null), img.getHeight(null));
		srcOrigin = src.copy();
		frameWidth = src.w;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(refresh>refreshRate) {
			refresh=0;
			src = src.translate(new Vector2f(frameWidth, 0));
			if(src.w>imageDim.w || src.x>imageDim.w) {
				src = srcOrigin.copy();
			}
		}refresh++;
	}
	
	
	/**This will flip the src of the image and allow for the image to drawn in reverse as a mirror along the x axis.*/
	public void mirrorX() {
		int temp = src.x;
		src.x = src.w;
		src.w = temp;
		temp = srcOrigin.x;
		srcOrigin.x = srcOrigin.w;
		srcOrigin.w = temp;
	}
	/**This will flip the src of the image and allow for the image to drawn in reverse as a mirror along the y axis.*/
	public void mirrorY() {
		int temp = src.y;
		src.y = src.h;
		src.h = temp;
		temp = srcOrigin.y;
		srcOrigin.y = srcOrigin.h;
		srcOrigin.h = temp;
	}
	
	/**Change the animation frame to allow for different y horizontal animations.*/
	@Override
	public void setAnimation(Box b) {
		src = b;
	}
	
	/**Servers barely any damn purpose*/
	@Override
	public Box getAnimation() {
		return src;
	}
	
}
