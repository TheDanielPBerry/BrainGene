package game.articles;

import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import game.Box;
import game.Model;

public class Dust extends Entity {
	
	int age;
	ImageFilter filter;
	
	public Dust(Model m, Box d) {
		super(m, d);
		filter = new RGBImageFilter() {
			public final int filterRGB(int x, int y, int rgb) {
				return (int) (rgb*0.5);
			}
		};
	}
	
	
	public void tick() {
		ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
		img = Toolkit.getDefaultToolkit().createImage(ip);
	}
	
}
