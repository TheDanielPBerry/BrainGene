package game;

import game.articles.Clara;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Roger extends JPanel implements Runnable, KeyListener {
	
	
	private static final long serialVersionUID = 6870272608626600492L;
	//public static Box WIN_DIM = new Box(0,0,1000,600);
	public static Box WIN_DIM = new Box(Toolkit.getDefaultToolkit().getScreenSize());
	/**A double buffered image to smooth the graphics of the surface*/
	public BufferedImage buffer;
	/**A context for performing bitmap graphical operations on the buffer.*/
	public static Graphics g3;
	/**A nice variable to turn off and close out the main threads.*/
	public static boolean game = true;
	/**A counter for graphical throttling to prevent too many screen refreshs.*/
	public byte refresh = 0;
	/**The refresh counter must increment over this before a repaint is made.*/
	public final byte refreshRate = 10;
	/**A counter similar to refresh but for IO and AI and other calculations of physics and individual articles.*/
	public byte tick = 0;
	/**The tick counter must go over this in order to do a game tick.*/
	public final byte tickRate = 10;
	/**A dynamic arraylist of everything that exists within the game.*/
	public static ArrayList<Article> articles;
	/**A mapping of model articles that maintain lightweight representations of every article on the screen.*/
	public static HashMap<String, Model> models;
	/**This individual number represents the index within the articles arraylist of the active focused article. (The index of the character)*/
	public static int cameraId = 0;
	/**A way to keep track of key values during the tick and move from key event interrunpts.*/
	public static boolean[] keys = {false,false,false,false,false,false};
	/**A useful variable to determine for gameplay if the camera is currently in freefall.*/
	public static boolean jump = false;
	/**The controller is used for the 3rd party ps2 controller design to make gameplay more fun.*/
	public static Controller controller = null;
	/**The frame of the window*/
	private JFrame frame;
	/**0 is play, 1 is AI*/
	private byte gameMode = 0;
	
	
	/**
	 * Perform a single second of computation for my physics engine and do computations for all the articles in the game world.
	 * */
	public void tick() {
		
		Clara camera = (Clara) Roger.getCam();
		if(Roger.keys[1]) {
			camera.setXScreen(false);
			camera.hDir = Direction.RIGHT;
			camera.motion = true;
			camera.velocity.x += 0.8f;
		} else if(Roger.keys[3]) {
			camera.setXScreen(true);
			camera.hDir = Direction.LEFT;
			camera.motion = true;
			camera.velocity.x -= 0.8f;
		} else {
			camera.motion = false;
		}
		if(jump && camera.motion) {
			//articles.add(new Dust(Roger.models.get("Dust"), new Box(100,100,64,64)));
		}
		camera.velocity.x*=0.9;
		if(Roger.keys[0] && Roger.jump) {
			jump = false;
			camera.velocity.y = -24f;
		}else if(camera.velocity.y!=0) {
			jump = false;
		}
		
		Physics.move();
		if(camera.velocity.y != 0) {
			camera.motion = true;
		}
		Iterator<Article> iter = Roger.articles.iterator();
		while(iter.hasNext()) {
			Article a = iter.next();
			if(a.markForDeath) {
				iter.remove();
				continue;
			}
			a.tick();
			if(!a.staticElement) {
				a.velocity.y += 1f;
			}
			if(Math.abs(a.velocity.x)<0.1) {
				a.velocity.x = 0f;
			}
		}
		
		if(camera.dst.y>1000) {
			die();
		}
		if(gameMode==1 && camera.velocity.x == 0 && camera.velocity.y == 0) {
			
		}
	}
	
	
	/**Draw everything to the double buffer before performing a repaint.*/
	public void make() {
		//Clip the drawing to the screen Dimensions
		g3.setClip(new Rectangle(-Roger.WIN_DIM.w/2, -Roger.WIN_DIM.h/2, Roger.WIN_DIM.w, Roger.WIN_DIM.h));
		//Clear the scree9n and set the background to orange simulataenously.
		g3.setColor(Color.ORANGE);
		g3.fillRect(-Roger.WIN_DIM.w/2, -Roger.WIN_DIM.h/2, Roger.WIN_DIM.w, Roger.WIN_DIM.h);
		
		if(gameMode==0) {
			//Center the graphics context around the camera.
			g3.translate(-Roger.getCam().dst.x-Roger.getCam().dst.w/2, -Roger.getCam().dst.y-Roger.getCam().dst.h/2);
			g3.setColor(Color.RED);
			final Vector2f offset = Roger.getCam().dst.getPos2f().neg().add(WIN_DIM.getDim2f().multiply(0.5f)).add(Roger.getCam().dst.getDim2f().multiply(0.5f).neg());
			for(Article a : Roger.articles) {
				if(a.dst.add(offset).intersects(WIN_DIM)) {
					g3.drawImage(a.img, a.dst.x, a.dst.y, a.dst.x+a.dst.w, a.dst.y+a.dst.h, a.src.x, a.src.y, a.src.w, a.src.h, null);
					/*for(Box b : a.boxes) {
						b = b.add(a.dst);
						g3.drawRect(b.x,b.y,b.w,b.h);
					}*/
				}
			}
			g3.translate(Roger.getCam().dst.x+Roger.getCam().dst.w/2, Roger.getCam().dst.y+Roger.getCam().dst.w/2);
		} else {
			g3.translate(-WIN_DIM.w/2, -WIN_DIM.h/2);
			byte screen[][] = ArtificialIO.PollScreen(articles);
			for(short y=0; y<screen.length; y++) {
				for(short x=0; x<screen[y].length; x++) {
					//g3.setColor(screen[y][x]==0?new Color(0,0,0,100): new Color(255,255,255,100));
					g3.setColor(screen[y][x]==0? Color.BLACK : screen[y][x]==1? Color.RED : Color.GREEN);
					g3.fillRect(y * WIN_DIM.w/100,x * WIN_DIM.h/50, WIN_DIM.w/100, WIN_DIM.h/50);
				}
			}
			g3.translate(WIN_DIM.w/2, WIN_DIM.h/2);
		}
		repaint();
	}
	
	/**A quick routine to load the map and controller and setup other important variables.*/
	public static void setup() {
		articles = new ArrayList<Article>();
		MapUtil.LoadMap(articles, "assets/maps/map.xml");
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(Controller c : controllers) {
			if(c.getName().contains("Generic")) {
				Thread t = new Thread(new HandheldController(c));
				t.start();
				break;
			}
		}
	}
	
	/**Kill the player*/
	public void die() {
		game = false;
	}
	
	
	/**The main loop and timer where refreshes of the screen and physics are performed.*/
	public void run() {
		while(Roger.game) {
			if(refresh>refreshRate) {
				make();
				refresh = 0;
			}refresh++;
			
			
			if(tick>tickRate) {
				tick();
				tick = 0;
			}tick++;
			
			try {
				Thread.sleep(1);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(gameMode==1) {
			frame.setVisible(false);
			frame.dispose();
		}
	}
	
	/**A convenience method to retrieve the focus article. AKA as the camera or character.*/
	public static Article getCam() {
		return articles.get(cameraId);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	
	public void keyPressed(KeyEvent e) {
		final boolean flag = true;
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			Roger.keys[0] = flag;
			break;
		case KeyEvent.VK_RIGHT:
			Roger.keys[1] = flag;
			break;
		case KeyEvent.VK_DOWN:
			Roger.keys[2] = flag;
			break;
		case KeyEvent.VK_LEFT:
			Roger.keys[3] = flag;
			break;
		case KeyEvent.VK_SPACE:
			Roger.keys[4] = flag;
			break;
		case KeyEvent.VK_P:
			Roger.keys[5] = flag;
			break;
		case KeyEvent.VK_ESCAPE:
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		final boolean flag = false;
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			Roger.keys[0] = flag;
			break;
		case KeyEvent.VK_RIGHT:
			Roger.keys[1] = flag;
			break;
		case KeyEvent.VK_DOWN:
			Roger.keys[2] = flag;
			break;
		case KeyEvent.VK_LEFT:
			Roger.keys[3] = flag;
			break;
		case KeyEvent.VK_SPACE:
			Roger.keys[4] = flag;
			break;
		case KeyEvent.VK_P:
			Roger.keys[5] = flag;
			
			byte screen[][] = ArtificialIO.PollScreen(articles);
			for(short y=0; y<screen.length; y++) {
				for(short x=0; x<screen[y].length; x++) {
					System.out.print(screen[x][y]);
				}
				System.out.println();
			}
			break;
		case KeyEvent.VK_ESCAPE:
			Roger.game = false;
			if(gameMode==0) {
				System.exit(0);
			}
			break;
		}
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	
	public static void main(String[] args) {
		//
		//Build the frame, buffer and input for the game
		//
		JFrame frame = new JFrame("Roger Rabbit");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIN_DIM.getDim());
		//f.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		Roger r = new Roger();
		frame.add(r);
		frame.addKeyListener(r);
		
		r.buffer = new BufferedImage(WIN_DIM.w, WIN_DIM.h, BufferedImage.TYPE_INT_ARGB);
		g3 = r.buffer.getGraphics();
		g3.translate(WIN_DIM.w/2, WIN_DIM.h/2);
		
		setup();
		
		Thread t = new Thread(r);
		t.start();
		
	}
	
	public Roger() {
		
	}
	
	public Roger(int[] weights) {
		//
		//Build the frame, buffer and input for the game
		//
		frame = new JFrame("Roger Rabbit");
		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIN_DIM.getDim());
		//f.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.add(this);
		frame.addKeyListener(this);
		
		buffer = new BufferedImage(WIN_DIM.w, WIN_DIM.h, BufferedImage.TYPE_INT_ARGB);
		g3 = buffer.getGraphics();
		g3.translate(WIN_DIM.w/2, WIN_DIM.h/2);
		
		setup();
		gameMode = 1;
		
	}
	
	
	

}
