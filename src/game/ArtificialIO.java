package game;

import java.awt.Dimension;
import java.util.ArrayList;

public class ArtificialIO {

	public static Box WIN_DIM = new Box(new Dimension(400,300));
	
	public static byte[][] PollScreen(ArrayList<Article> articles) {
		final Vector2f arraySize  = new Vector2f(20,15);
		byte screen[][] = new byte[(int) arraySize.x][(int) arraySize.y];
		Box scanner = new Box(Roger.getCam().dst.x+(Roger.getCam().dst.w/2)-WIN_DIM.w/2, Roger.getCam().dst.y+(Roger.getCam().dst.h/2)-WIN_DIM.h/2,(int)(WIN_DIM.w/arraySize.x), (int)(WIN_DIM.h/arraySize.y));
		
		
		for(short x=0; x<arraySize.x; x++) {
			scanner.x += x*scanner.w;
			for(short y=0; y<arraySize.y; y++) {
				scanner.y += y*scanner.h;
				for(Article article : articles) {
					if(article.boxes != null) {
						for(Box box : article.boxes) {
							if(box.add(article.dst).intersects(scanner)) {
								screen[x][y] = (byte) (article.staticElement?2:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     1);
							}
						}
					}
				}
				scanner.y -= y*scanner.h;
			}
			scanner.x -= x*scanner.w;
		}
		return screen;
	}
	

	public static byte[] Vectorize(byte[][] data) {
		byte result[] = new byte[data[0].length*data.length];
		for(int y=0; y<data.length; y++) {
			for(int x=0; x<data[0].length; x++) {
				result[(y*data[0].length)+x] = data[y][x];
			}
		}
		return result;
	}
	
}
