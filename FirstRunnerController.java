import java.util.*;
import java.lang.IllegalArgumentException;

public class FirstRunnerController extends Thread
{

	
	private Simulator s;
    protected Runner current_runner;
    protected Runner next_runner;
    
    private int _lastCheckedTime = 0;
    private int _lastCheckedMTime = 0;

    protected static int totalNumControllers = 0;
    protected int controllerID = 0;


    
    
    public FirstRunnerController(Simulator s, Runner current_v, Runner next_runner) throws IllegalArgumentException
    {
    	this.s = s;
    	this.current_runner = current_v;
    	this.next_runner = next_runner;
	
	synchronized (RunnerController.class) {
	    controllerID = totalNumControllers;
	    totalNumControllers++;
	}
    }

    public void run()
    {
	int currentTime = 0;
	int currentMTime = 0;
	
	while(currentTime < 100000.0) {

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
		current_runner.controlRunner(nextControl); 
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

    public synchronized Control getControl(int sec, int msec)
    {

    	double controlTime = sec+msec*1E-3;
    	double x = current_runner.getPosition()[0];
    	double y = current_runner.getPosition()[1];
    	Control nextControl = null;
    	double dist_bw_runners = next_runner.getPosition()[0] - current_runner.getPosition()[0];

    	//first runner starts off with the baton and runs
    	if (current_runner.getHasBaton() == true){
        	if (y<55){
        		nextControl= new Control(1,Math.PI/4);
        	}
        	else if (y>55)
        		nextControl= new Control(1,-Math.PI/4);
        	else if (y == 55)
        		nextControl = new Control(1,0);
        	
    	//  stop if you are within a certain distance of the next runner
    	if(dist_bw_runners < 10){  // && y == 55) {
       		nextControl = new Control(0,0);
    		current_runner.setHasBaton(false);
    		current_runner.setJustRan(true);
    		}	
    	}
    	
    	//don't move anymore once this runner hands off the baton
    	if (current_runner.getHasBaton() == false){
    		nextControl = new Control(0,0);
    	}
    	return nextControl;
    
    }
    


}