
public class RunnerController extends FirstRunnerController {


	private Runner prev_runner;
	private Runner current_runner;
	private Runner next_runner;
    public RunnerController(Simulator s, Runner current_v, Runner next_runner, Runner prev_runner)
			throws IllegalArgumentException {
		super(s, current_v, next_runner);
		// TODO Auto-generated constructor stub
		   this.prev_runner = prev_runner;
		   this.current_runner = current_v;
		   this.next_runner = next_runner;
    }
 
    
    
    
    
    
    

	public synchronized Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	double x = current_runner.getPosition()[0];
	double y = current_runner.getPosition()[1];
	int TID = current_runner.getTeamID();
	Control nextControl = null;
	double prev_speed = 1;
	double dist_bw_next_runner = next_runner.getPosition()[0] - current_runner.getPosition()[0];

//if the previous runner still has the baton, don't move
	if (prev_runner.getHasBaton() == true && prev_runner.getJustRan() == false)
		nextControl = new Control(0,0);
	//if the previous runner is approaching with baton
	else if (prev_runner.getHasBaton() == true && prev_runner.getJustRan() == true){
    	if (TID == 0 && Math.abs(y-55)>1e-2){
    		nextControl= new Control(1,Math.PI/4);
    	}
    	else if (TID == 1 && Math.abs(y-55)>1e-2)
    		nextControl= new Control(1,-Math.PI/4);
    	//Runner slows down to make sure the previous one can catch
		else if (Math.abs(y-55)<1e-2)
			nextControl = new Control(prev_speed/2,0);
	}
	//once the baton is handed off
	else {
		current_runner.setHasBaton(true);
	}
    	//once you reach the stopping point, stop moving and set hasBaton to false and set justRan to true
	if (dist_bw_next_runner < 10){
		nextControl = new Control(0,0);
		current_runner.setHasBaton(false);
		current_runner.setJustRan(true);
		
	}
	if (current_runner.getHasBaton() == false && current_runner.getJustRan() == true){
		nextControl = new Control(0,0);
	}
	
	
	
	
	return nextControl;
    
	
	
    }

	public Control passVehicle(double x, double y, int TID, double nx, double ny, double s){
    	Control c = null;
    	if (TID == 1){
	    	if (nx>x && y<ny+1) //First maneuver up
	    		c = new Control(s,Math.PI/4);
	    	else if (y>ny+1 && x<nx+2)
	    		c = new Control(s,0);//second maneuver to move foward
	    	else if (x>nx+2 && Math.abs(y-ny)>1e-2)
	    		c = new Control(s,-Math.PI/4);//third maneuver to move back into the lane
	    	else 
	    		c = new Control(s,0);//continue to run normally
    	}
    	else {
    		if (nx>x && y>ny-1) 
	    		c = new Control(s,-Math.PI/4);
	    	else if (y<ny-1 && x<nx+2)
	    		c = new Control(s,0);
	    	else if (x>nx+2 && Math.abs(y-ny)>1e-2)
	    		c = new Control(s,Math.PI/4);
	    	else 
	    		c = new Control(s,0);
    	}
    	return c;
    }

}
