import java.util.*;
import java.lang.IllegalArgumentException;

public class VehicleController extends Thread
{
    private Simulator _s;
    protected Runner _v;
    
    private int _lastCheckedTime = 0;
    private int _lastCheckedMTime = 0;

    protected static int totalNumControllers = 0;
    protected int controllerID = 0;


    
    public VehicleController(Simulator s, Runner v) throws IllegalArgumentException
    {
	if (s == null) {
	    throw new IllegalArgumentException("No simulator specified.");
	}
	if (v == null) {
	    throw new IllegalArgumentException("No vehicle specified.");
	}
	_s = s;
	_v = v;

	synchronized (VehicleController.class) {
	    controllerID = totalNumControllers;
	    totalNumControllers++;
	}
    }

    public void run()
    {
	int currentTime = 0;
	int currentMTime = 0;
	
	while(currentTime < 100.0) {

	    synchronized(_s) {
		currentTime = _s.getCurrentSec();
		currentMTime = _s.getCurrentMSec();

		while (_lastCheckedTime == currentTime && _lastCheckedMTime == currentMTime) {
		    try {
			// // DEBUG
			// System.out.printf("VC %d [%d,%d] waiting\n", controllerID, currentTime, currentMTime);
			// // DEBUG
			_s.wait(); // Wait for the simulator to notify
			currentTime = _s.getCurrentSec();
			currentMTime = _s.getCurrentMSec();
		    } catch (java.lang.InterruptedException e) {
			System.err.printf("Interrupted " + e);
			System.exit(0);
		    }
		}
		// // DEBUG
		// System.out.printf("VC %d [%d,%d] proceeding\n", controllerID, currentTime, currentMTime);
		// // DEBUG
		_s.notifyAll();
	    }

	    // // DEBUG
	    // System.out.printf("VC %d [%d,%d] generating control\n", controllerID, currentTime, currentMTime);
	    // // DEBUG
	    
	    // Generate a new control
	    Control nextControl = this.getControl(currentTime, currentMTime);

	    if (nextControl != null) {
		_v.controlRunner(nextControl); 
		// // DEBUG
		// System.out.printf("VC %d [%d,%d] next control applied\n", controllerID, currentTime, currentMTime);
		// // DEBUG
	    }

	    //update the time of the last control
	    _lastCheckedTime = currentTime;
	    _lastCheckedMTime = currentMTime;

	    synchronized(_s){
		if(_s.numControlToUpdate == 0 ) {
		    //this should not already be zero - something is wrong
		    System.err.println("ERROR: No of controllers to update already 0.\n");
		    System.exit(-1);
		}
		// // DEBUG
		// System.out.printf("VC %d [%d,%d] decrementing numControlToUpdate\n", controllerID, currentTime, currentMTime);
		// // DEBUG
		_s.numControlToUpdate--;
		_s.notifyAll();
	    }
	}
    }

    public Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	Control nextControl = null;
	double speed = 5;
	double omega = 0;
	nextControl = new Control(speed, omega);
	return nextControl;
    }




}