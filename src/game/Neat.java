package game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Neat {
	
	

	static DatagramSocket Socket;
	static int Port;
	
	public static boolean[] GetMove(byte[] screen) {
		String message = "GET_MOVE|" + new String(screen) + "|";
		String data=null;
		for(byte i=0; i<10 && data==null; i++) {
			data = SendMessage(message);
		}
		data = data.split("\\|")[1];
		byte[] result = data.getBytes();
		boolean moves[] = new boolean[result.length];
		for(byte i=0; i<moves.length; i++) {
			moves[i] = result[i] == 49;
		}
		return moves;
	}
	
	
	public static float StartGame(int id) {
		Roger game = new Roger(1);
		Thread t = new Thread(game);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return game.getFitness();
	}
	
	
	public static String ProcessCommand(String command) {
		String args[] = command.split("\\|");
		switch(args[0]) {
		case "START":
			StartGame(Integer.parseInt(args[1]));
			break;
		}
		return "";
	}
	

	public static String SendMessage(String data) {
		try {
            InetAddress destAddress = InetAddress.getByName("localhost");
            byte outBuffer[] = data.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destAddress, Neat.Port);
            Socket.send(outPacket);
            
            
            byte inBuffer[] = new byte[512];
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            Socket.receive(inPacket);
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
		Neat.Port = Integer.parseInt(args[0]);
		System.out.println("New Game: UDP Port: " + Port);
		try {
			Socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		float fitness = StartGame(Neat.Port);
		if(fitness>4000) {
			SendMessage("WIN|" + fitness + "|");
		} else {
			SendMessage("DEAD|" + fitness + "|");
		}
		Socket.close();
		System.exit(0);
	}
	
	
	
	
	
}

