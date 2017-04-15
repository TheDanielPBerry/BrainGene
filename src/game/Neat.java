package game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Neat {
	
	

	static DatagramSocket socket;
	
	public static boolean[] GetMove(byte[] screen) {
		String message = "GET_MOVE||" + new String(screen) + "||";
		String data=null;
		for(byte i=0; i<4 && data==null; i++) {
			data = ProcessCommand(SendMessage(message));
		}
		byte[] result = data.getBytes();
		boolean moves[] = new boolean[result.length];
		for(byte i=0; i<moves.length; i++) {
			moves[i] = result[i] == 1;
		}
		return moves;
	}
	
	public static float StartGame(int id) {
		System.out.println("NEW GAME " + id);
		Roger game = new Roger(null);
		Thread t = new Thread(game);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return game.getFitness();
	}
	
	public static void Listen() {
		try {
			while(true) {
	            byte inBuffer[] = new byte[512];
	            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
	            socket.receive(inPacket);
	            String input = BufferToString(inPacket.getData());
	            ProcessCommand(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String ProcessCommand(String command) {
		String args[] = command.split("\\|\\|");
		switch(args[0]) {
		case "START":
			StartGame(Integer.parseInt(args[1]));
			break;
		case "RETURN_MOVE":
			return args[1];
		}
		return "";
	}
	

	public static String SendMessage(String data) {
		try {
            InetAddress destAddress = InetAddress.getByName("localhost");
            byte outBuffer[] = data.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destAddress, 9001);
            socket.send(outPacket);
            
            
            byte inBuffer[] = new byte[512];
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(inPacket);
            String back = BufferToString(inPacket.getData());
            
            return back;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	
	public static String BufferToString(byte[] buffer) {
		short i=0;
		String result = "";
		while(buffer[i]!=0) {
			result += (char)buffer[i];
			i++;
		}
		return new String(result);
	}
	
	

	public static void main(String[] args) {
//		try {
//			socket = new DatagramSocket(9002);
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
//		Listen();
		
		//args[];
		
	}
	
	
	
	
	
}

