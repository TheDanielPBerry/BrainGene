package game;

import net.java.games.input.*;

/**
 * A convenience class for polling and handling controller inputs and integrating with keyobard controls.
 * @author Cantino
 *
 */
public class HandheldController implements Runnable {

	public Controller controller;
	
	/**
	 * Build the thread and get ready for event polling.
	 * @param c Passed in by the Roger setup method.
	 */
	public HandheldController(Controller c) {
		controller = c;
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
				Roger.keys[0] = ButtonClick(component.getPollData());
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
				Roger.keys[1] = true;
			}
			else if(data<-0.8f) {
				Roger.keys[3] = true;
			}else {
				Roger.keys[1] = Roger.keys[3] = false;
			}
			break;
		case Y:
			if(data>0.8f) {
				Roger.keys[2] = true;
			}
			else if(data<-0.8f) {
				Roger.keys[0] = true;
			}else {
				Roger.keys[0] = Roger.keys[2] = false;
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
		Roger.keys[0] = Roger.keys[1] = Roger.keys[1] = Roger.keys[2] = Roger.keys[3] = false;
		switch((int)(data*1000)) {
		case 125:
			Roger.keys[3] = Roger.keys[0] = true;
			break;
		case 250:
			Roger.keys[0] = true;
			break;
		case 375:
			Roger.keys[0] = Roger.keys[1] = true;
			break;
		case 500:
			Roger.keys[1] = true;
			break;
		case 625:
			Roger.keys[1] = Roger.keys[2] = true;
			break;
		case 750:
			Roger.keys[2] = true;
			break;
		case 875:
			Roger.keys[2] = Roger.keys[3] = true;
			break;
		case 1000:
			Roger.keys[3] = true;
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
		while(Roger.game) {
			PollController();
			try {
				Thread.sleep(1);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
