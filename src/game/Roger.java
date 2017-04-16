package game;

import game.articles.Clara;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
	public static Box WIN_DIM = new Box(new Dimension(400,300));
	/**A double buffered image to smooth the graphics of the surface*/
	public BufferedImage buffer;
	/**A context for performing bitmap graphical operations on the buffer.*/
	public Graphics g3;
	/**A nice variable to turn off and close out the main threads.*/
	public boolean game = true;
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
	public boolean[] keys = {false,false,false,false,false,false};
	/**A useful variable to determine for gameplay if the camera is currently in freefall.*/
	public boolean jump = false;
	/**The controller is used for the 3rd party ps2 controller design to make gameplay more fun.*/
	public Controller controller = null;
	/**The frame of the window*/
	private JFrame frame;
	/**0 is play, 1 is AI*/
	private byte gameMode = 0;
	/**A byte that tracks how many frames the player has not moved.*/
	private byte frozenCounter = 0;
	/**A byte that tracks how many frames the game has gone*/
	private int stuckCounter = 0;
	/**Track the player in between frames.*/
	private Box playerTracker = new Box(0,0,0,0);
	/**String path to local file system*/
	public static final String relativeFilePath = "C:/Users/Cantino/Documents/Java/RogerRabbit/";
	/**A boolean to determine if you won.*/
	public boolean win = false;
	
	
	
	/**
	 * Perform a single second of computation for my physics engine and do computations for all the articles in the game world.
	 * */
	public void tick() {
		System.out.print(".");
		
		Clara camera = (Clara) Roger.getCam();
		if(keys[1]) {
			camera.setXScreen(false);
			camera.hDir = Direction.RIGHT;
			camera.motion = true;
			camera.velocity.x += 0.8f;
		} else if(keys[2]) {
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
		if(keys[0] && jump) {
			jump = false;
			camera.velocity.y = -24f;
		}else if(camera.velocity.y!=0) {
			jump = false;
		}
		
		jump = Physics.move(jump);
		if(camera.velocity.y != 0) {
			camera.motion = true;
		}
		
		if(gameMode==1 && stuckCounter>1000) {
			System.out.println("Timeout");
			die();
		}
		stuckCounter++;
		
		if(gameMode==1 && playerTracker.x == camera.dst.x) {
			frozenCounter++;
			if(frozenCounter>20) {
				System.out.println("Timeout Sit");
				die();
			}
		} else {
			frozenCounter=0;
		}
		playerTracker = camera.dst.copy();
		
		if(camera.dst.x>=3950) {
			win();
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
		if(camera.dst.y>512) {
			System.out.println("Fall to Death");
			die();
		}
	}
	
	
	/**Draw everything to the double buffer before performing a repaint.*/
	public void make() {
		//Clip the drawing to the screen Dimensions
		g3.setClip(new Rectangle(-WIN_DIM.w/2, -WIN_DIM.h/2, WIN_DIM.w, WIN_DIM.h));
		//Clear the scree9n and set the background to orange simulataenously.
		g3.setColor(Color.ORANGE);
		g3.fillRect(-WIN_DIM.w/2, -WIN_DIM.h/2, WIN_DIM.w, WIN_DIM.h);
		
		if(gameMode!=1) {
			//Center the graphics context around the camera.
			g3.translate(-Roger.getCam().dst.x-Roger.getCam().dst.w/2, -Roger.getCam().dst.y-Roger.getCam().dst.h/2);
			g3.setColor(Color.RED);
			final Vector2f offset = Roger.getCam().dst.getPos2f().neg().add(WIN_DIM.getDim2f().multiply(0.5f)).add(Roger.getCam().dst.getDim2f().multiply(0.5f).neg());
			for(Article a : Roger.articles) {
				if(a.dst.add(offset).intersects(Roger.WIN_DIM)) {
					g3.drawImage(a.img, a.dst.x, a.dst.y, a.dst.x+a.dst.w, a.dst.y+a.dst.h, a.src.x, a.src.y, a.src.w, a.src.h, null);
					/*for(Box b : a.boxes) {
						b = b.add(a.dst);
						g3.drawRect(b.x,b.y,b.w,b.h);
					}*/
				}
			}
			g3.translate(Roger.getCam().dst.x+Roger.getCam().dst.w/2, Roger.getCam().dst.y+Roger.getCam().dst.w/2);
		} 
			g3.translate(-Roger.WIN_DIM.w/2, -Roger.WIN_DIM.h/2);
			byte screen[][] = ArtificialIO.PollScreen(articles);
			for(short y=0; y<screen.length; y++) {
				for(short x=0; x<screen[y].length; x++) {
					//g3.setColor(screen[y][x]==0?new Color(0,0,0,100): new Color(255,255,255,100));
					g3.setColor(screen[y][x]==0? Color.BLACK : screen[y][x]==1? Color.RED : Color.BLUE);
					g3.fillRect(y * Roger.WIN_DIM.w/100,x * Roger.WIN_DIM.h/50, Roger.WIN_DIM.w/100, Roger.WIN_DIM.h/50);
				}
			}
			g3.translate(WIN_DIM.w/2, WIN_DIM.h/2);
			if(win) {
				g3.setColor(Color.BLACK);
				g3.setFont(new Font("Helvetica", Font.BOLD, 50));
				g3.drawString("Congratulations!", -WIN_DIM.w/5, -WIN_DIM.h/3);
			}
		repaint();
	}
	
	/**A quick routine to load the map and controller and setup other important variables.*/
	public void setup() {
		articles = new ArrayList<Article>();
		MapUtil.LoadMap(articles, Roger.relativeFilePath + "assets/maps/map.xml");
		if(gameMode==0) {
			Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
			for(Controller c : controllers) {
				if(c.getName().contains("Generic")) {
					Thread t = new Thread(new HandheldController(c, this));
					t.start();
					break;
				}
			}
		}
	}
	
	/**Kill the player*/
	public void die() {
		System.out.println(stuckCounter);
		game = false;
	}

	
	/**Kill the player and win*/
	public void win() {
		win = true;
		//game = false;
	}
	
	/**The main loop and timer where refreshes of the screen and physics are performed.*/
	public void run() {
		setup();
		while(game) {
			if(gameMode!=1) {
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
			if(gameMode>0) {
				byte screen[] = ArtificialIO.Vectorize(ArtificialIO.PollScreen(articles));
				keys = Neat.GetMove(screen);
				if(gameMode==1) tick();
			}
		}
		if(gameMode==0) {
			System.out.println(getFitness());
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
			keys[0] = flag;
			break;
		case KeyEvent.VK_RIGHT:
			keys[1] = flag;
			break;
		case KeyEvent.VK_DOWN:
			keys[3] = flag;
			break;
		case KeyEvent.VK_LEFT:
			keys[2] = flag;
			break;
		case KeyEvent.VK_SPACE:
			keys[4] = flag;
			break;
		case KeyEvent.VK_P:
			keys[5] = flag;
			break;
		case KeyEvent.VK_ESCAPE:
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		final boolean flag = false;
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			keys[0] = flag;
			break;
		case KeyEvent.VK_RIGHT:
			keys[1] = flag;
			break;
		case KeyEvent.VK_DOWN:
			keys[3] = flag;
			break;
		case KeyEvent.VK_LEFT:
			keys[2] = flag;
			break;
		case KeyEvent.VK_SPACE:
			keys[4] = flag;
			break;
		case KeyEvent.VK_P:
			keys[5] = flag;
			
			byte screen[][] = ArtificialIO.PollScreen(articles);
			for(short y=0; y<screen.length; y++) {
				for(short x=0; x<screen[y].length; x++) {
					System.out.print(screen[x][y]);
				}
				System.out.println();
			}
			break;
		case KeyEvent.VK_ESCAPE:
			game = false;
			if(gameMode==0) {
				System.exit(0);
			}
			break;
		}
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	
	public static void main(String[] args) {
		WIN_DIM = new Box(Toolkit.getDefaultToolkit().getScreenSize());
		//
		//Build the frame, buffer and input for the game
		//
		Roger r = new Roger();
		r.frame = new JFrame("Roger Rabbit");
		r.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		r.frame.setSize(Roger.WIN_DIM.getDim());
		//f.setUndecorated(true);
		r.frame.setLocationRelativeTo(null);
		r.frame.setVisible(true);
		
		r.frame.add(r);
		r.frame.addKeyListener(r);
		
		r.buffer = new BufferedImage(Roger.WIN_DIM.w, Roger.WIN_DIM.h, BufferedImage.TYPE_INT_ARGB);
		r.g3 = r.buffer.getGraphics();
		r.g3.translate(Roger.WIN_DIM.w/2, Roger.WIN_DIM.h/2);
		
		
		Thread t = new Thread(r);
		t.start();
		
	}
	
	public float getFitness() {
		float fitness = getCam().dst.x;
		if(fitness<0) {
			fitness = 0;
		}
		System.out.println(getCam().velocity.toString() + "|" + getCam().dst.getPos2f());
		return fitness;
	}

	public Roger() {
		
	}
	
	public Roger(int i) {
		Roger.WIN_DIM = new Box(Toolkit.getDefaultToolkit().getScreenSize());
		//
		//Build the frame, buffer and input for the game
		//
		frame = new JFrame("Roger Rabbit");
		frame.setSize(Roger.WIN_DIM.getDim());
		//f.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.add(this);
		
		buffer = new BufferedImage(Roger.WIN_DIM.w, Roger.WIN_DIM.h, BufferedImage.TYPE_INT_ARGB);
		g3 = buffer.getGraphics();
		g3.translate(Roger.WIN_DIM.w/2, Roger.WIN_DIM.h/2);
//		
		gameMode = 2;
		
	}
	
	public Roger(int[] weights) {
		gameMode = 1;
		
	}
	
	
	

}
