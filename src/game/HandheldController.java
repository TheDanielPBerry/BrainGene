package game;

import net.java.games.input.*;

/**
 * A convenience class for polling and handling controller inputs and integrating with keyobard controls.
 * @author Cantino
 *
 */
public class HandheldController implements Runnable {

	public Controller controller;
	public Roger roger;
	
	/**
	 * Build the thread and get ready for event polling.
	 * @param c Passed in by the Roger setup method.
	 */
	public HandheldController(Controller c, Roger r) {
		controller = c;
		roger = r;
	}
	
	
	/**Do a check of what's happened on the controller and handle the events.*/
	public void PollController() {
		controller.poll();
		EventQueue queue = controller.getEventQueue();
		Event evt = new Event();
		while(queue.getNextEvent(evt)) {
			Component component = evt.getComponent();
			switch(component.getName()) {
			case "Button 2":
				roger.keys[0] = ButtonClick(component.getPollData());
				break;
			case "Z Axis":
				break;
			case "X Axis": 
				Joystick(Direction.X, component.getPollData());
				break;
			case "Y Axis":
				Joystick(Direction.Y, component.getPollData());
				break;
			case "Hat Switch":
				HatSwitch(component.getPollData());
				break;
			case "Button 9":
				System.exit(0);
				break;
			default:
				//System.out.println(component.getName());
				break;
			}
		}
	}
	
	
	/**
	 * Up down left right the joystick controls the character movement.
	 * @param dir Duh it's direction
	 * @param data It's a threshold value of how much the joystick has moved in either direction.
	 */
	public void Joystick(final Direction dir, final float data) {
		switch(dir) {
		case X:
			if(data>0.8f) {
				roger.keys[1] = true;
			}
			else if(data<-0.8f) {
				roger.keys[2] = true;
			}else {
				roger.keys[1] = roger.keys[2] = false;
			}
			break;
		case Y:
			if(data>0.8f) {
				roger.keys[3] = true;
			}
			else if(data<-0.8f) {
				roger.keys[0] = true;
			}else {
				roger.keys[0] = roger.keys[3] = false;
			}
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * The dpad increments by 0.125 for every eighth of a circle of which rocker buttons are pushed and edits Roger 
	 * key boolean values accordingly. 
	 * @param data
	 */
	public void HatSwitch(final float data) {
		roger.keys[0] = roger.keys[1] = roger.keys[1] = roger.keys[2] = roger.keys[3] = false;
		switch((int)(data*1000)) {
		case 125:
			roger.keys[2] = roger.keys[0] = true;
			break;
		case 250:
			roger.keys[0] = true;
			break;
		case 375:
			roger.keys[0] = roger.keys[1] = true;
			break;
		case 500:
			roger.keys[1] = true;
			break;
		case 625:
			roger.keys[1] = roger.keys[3] = true;
			break;
		case 750:
			roger.keys[3] = true;
			break;
		case 875:
			roger.keys[3] = roger.keys[2] = true;
			break;
		case 1000:
			roger.keys[3] = true;
			break;
		}
	}
	
	/**
	 * Handle a button click on the controller. (Duh)
	 * @param data 1 if down, 0 if up
	 * @return true if button down, false if up.
	 */
	public boolean ButtonClick(final float data) {
		return data==1f;
	}
	
	/**
	 * A nice little thread loop for polling the controller if it even exists. Whoa meta!
	 */
	@Override
	public void run() {
		while(roger.game) {
			PollController();
			try {
				Thread.sleep(1);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
