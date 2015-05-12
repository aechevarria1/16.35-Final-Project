import java.util.*;
import java.lang.IllegalArgumentException;

public class FirstRunnerController extends Thread
{

	
	private Simulator s;
    protected Runner v;
    protected Runner prev_runner;
    
    private int _lastCheckedTime = 0;
    private int _lastCheckedMTime = 0;

    protected static int totalNumControllers = 0;
    protected int controllerID = 0;


    
    
    public FirstRunnerController(Simulator s, Runner v) throws IllegalArgumentException
    {
	if (s == null) {
	    throw new IllegalArgumentException("No simulator specified.");
	}
	if (v == null) {
	    throw new IllegalArgumentException("No vehicle specified.");
	}
	this.s = s;
	this.v = v;

	synchronized (RunnerController.class) {
	    controllerID = totalNumControllers;
	    totalNumControllers++;
	}
    }

    public void run()
    {
	int currentTime = 0;
	int currentMTime = 0;
	
	while(currentTime < 100.0) {

	    synchronized(s) {
		currentTime = s.getCurrentSec();
		currentMTime = s.getCurrentMSec();

		while (_lastCheckedTime == currentTime && _lastCheckedMTime == currentMTime) {
		    try {
			s.wait(); // Wait for the simulator to notify
			currentTime = s.getCurrentSec();
			currentMTime = s.getCurrentMSec();
		    } catch (java.lang.InterruptedException e) {
			System.err.printf("Interrupted " + e);
			System.exit(0);
		    }
		}
		s.notifyAll();
	    }

	    // Generate a new control
	    Control nextControl = this.getControl(currentTime, currentMTime);

	    if (nextControl != null) {
		v.controlRunner(nextControl); 
	    }

	    //update the time of the last control
	    _lastCheckedTime = currentTime;
	    _lastCheckedMTime = currentMTime;

	    synchronized(s){
		if(s.numControlToUpdate == 0 ) {
		    //this should not already be zero - something is wrong
		    System.err.println("ERROR: No of controllers to update already 0.\n");
		    System.exit(-1);
		}
		s.numControlToUpdate--;
		s.notifyAll();
	    }
	}
    }

    public Control getControl(int sec, int msec)
    {

    	double controlTime = sec+msec*1E-3;
    	double x = v.getPosition()[0];
    	double y = v.getPosition()[1];
    	Control nextControl = null;


    	if (y<55){
    		nextControl= new Control(1,Math.PI/4);
    	}
    	else if (y>55)
    		nextControl= new Control(1,-Math.PI/4);
    	else if (y == 55)
    		nextControl = new Control(1,0);
    	
    	return nextControl;
        }
    
   

   

}