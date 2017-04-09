package game;

import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class Neat {
	
	public static void main(String[] args) {
		
		Thread t = new Thread(new Roger(new int[0]));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Genome 2 Complete");
		
	}
	
	public static void Evolve() {
		
		Evolver evolver = new Evolver();
		try {
			Properties props = new Properties("res\\properties\\game.properties");
			evolver.init(props);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static float Fitness() {
		return 0f;
	}
	
}

