package game;

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
	
	
}
