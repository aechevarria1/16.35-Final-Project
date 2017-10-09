import java.util.*;
import java.lang.IllegalArgumentException;

public class FirstRunnerController extends Thread
{

	
	protected Simulator s;
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
	    Control nextControl = this.getControl();

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

    public synchronized Control getControl()
    {


    	double x = current_runner.getPosition()[0];
    	double y = current_runner.getPosition()[1];
    	double nx = next_runner.getPosition()[0]; //The next vehicles position
    	double ny = next_runner.getPosition()[1];
    	double dx = current_runner.getVelocity()[0];
    	double dy = current_runner.getVelocity()[1];
    	double ndx = current_runner.getVelocity()[0];// The next vehicles velocity
    	double ndy = current_runner.getVelocity()[1];
    	double s = current_runner.getInputSpeed();
    	double ns = Math.sqrt(ndx*ndx + ndy*ndy);
    	int TID = current_runner.getTeamID();
    	Control nextControl = null;
    	double dist_bw_runners = next_runner.getPosition()[0] - current_runner.getPosition()[0];

    	//the first runner never has to pass another runner.
    	
    	//first runner starts off with the baton and runs
    	//Testing to make sure the runner can change direction properly. Every quadrant it will switch
    	//The top is team 1, the bottom is team 0
    	if (current_runner.getHasBaton() == true){
    		if (x<=20)
    			nextControl= new Control(s,0);
    		else if (x>20 && x<=25){
    			if (TID == 1)
    				nextControl = new Control(s,-Math.PI/4);
    			else
    				nextControl = new Control(s,Math.PI/4);
    		}
    		else if (x>25) {
    			nextControl = new Control(s,0);
    			/*if (TID == 1){
    				if (Math.abs(nx-x)<3){
    					System.out.println(ns);
    					nextControl = passVehicle(x,y,TID,nx,ny,2*ns); //Passing runner will be twice the speed of the passed runner
    				}
    				else 
    					nextControl = new Control(1,0);
    			}
    			else {
    				if (Math.abs(nx-x)<3){
    					//System.out.println("test");
    					nextControl = passVehicle(x,y,TID,nx,ny,2*ns);
    				}
    				else 
    					nextControl = new Control(1,0);
    			}*/
			}
    	//  runner is approaching next runner
	    	if(dist_bw_runners < 10 && dist_bw_runners > 3){
	       		//nextControl = new Control(0,0);
	    		//current_runner.setHasBaton(false);
	    		current_runner.setJustRan(true);
	    		}	
	    	//runner is passing baton
	    	else if (dist_bw_runners < 3) {
	    		current_runner.setHasBaton(false);
	    		
	    		nextControl = new Control(0,0);
	    	}
    	}
    	
    	//don't move anymore once this runner hands off the baton
    	if (current_runner.getHasBaton() == false){
    		nextControl = new Control(0,0);
    	}
    	return nextControl;
    
    }
    

}
