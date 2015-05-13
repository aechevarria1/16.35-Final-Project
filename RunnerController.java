
public class RunnerController extends FirstRunnerController {


	private Runner prev_runner;
	private Runner current_runner;
	private Runner next_runner;
	private Runner comp_runner;
	private int counter = 0;
    public RunnerController(Simulator s, Runner current_v, Runner next_runner, Runner prev_runner, Runner comp_runner) //Need to add the competing runner in the same leg
			throws IllegalArgumentException {
		super(s, current_v, next_runner);
		// TODO Auto-generated constructor stub
		   this.prev_runner = prev_runner;
		   this.current_runner = current_v;
		   this.next_runner = next_runner;
		   this.comp_runner = comp_runner;
    }
 
    
    
    
    
    
    

	public synchronized Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	double x = current_runner.getPosition()[0];
	double y = current_runner.getPosition()[1];
	double nx = next_runner.getPosition()[0]; //The next vehicles position
	double ny = next_runner.getPosition()[1];
	double cx = comp_runner.getPosition()[0];
	double cy = comp_runner.getPosition()[1];
	int TID = current_runner.getTeamID();
	Control nextControl = null;
	double prev_speed = prev_runner.getInputSpeed();
	double this_speed = current_runner.getInputSpeed();
	double dist_bw_next_runner = next_runner.getPosition()[0] - current_runner.getPosition()[0];

//if the previous runner still has the baton, don't move
	if (prev_runner.getHasBaton() == true && prev_runner.getJustRan() == false)
		nextControl = new Control(0,0);
	//if the previous runner is approaching with baton
	else if (prev_runner.getHasBaton() == true && prev_runner.getJustRan() == true){
    	if (TID == 0 && Math.abs(y-55)>1e-2){
    		nextControl= new Control(2*prev_speed,Math.PI/4);
    	}
    	else if (TID == 1 && Math.abs(y-55)>1e-2)
    		nextControl= new Control(1,-Math.PI/4);
    	//Runner slows down to make sure the previous one can catch
		else if (Math.abs(y-55)<1e-2)
			nextControl = new Control(prev_speed/2,0);
	}
	//once the baton is handed off
	else if (prev_runner.getHasBaton() == false && prev_runner.getJustRan() == true){
		nextControl = new Control(this_speed,0);
		current_runner.setHasBaton(true);
	}
	
	//Runner moving outside hand off zone
	if (current_runner.getHasBaton() == true && current_runner.getJustRan() == false) {
		nextControl = new Control(this_speed,0);
		//System.out.println(current_runner.getLegID() + "," + next_runner.getLegID());
		if (Math.abs(cx-x)<3){ //Making sure they are within distance
			//System.out.println("test");
			if (cx>x){
				//System.out.println("test");
				if (counter == 0){
					current_runner.setJustPassed(true);
					counter++;
				}
				if (current_runner.getJustPassed() == true)
					return nextControl = passVehicle(x,y,TID,cx,cy,2*comp_runner.getInputSpeed()); //Passing runner will be twice the speed of the passed runner
			}
			else if (cx<x){
				counter++;
			}
		}
		//System.out.println("test");
		
	}
    	//once you reach the stopping point, stop moving and set hasBaton to false and set justRan to true
	/*if (dist_bw_next_runner < 10){
		nextControl = new Control(0,0);
		current_runner.setHasBaton(false);
		current_runner.setJustRan(true);
		
	}
	if (current_runner.getHasBaton() == false && current_runner.getJustRan() == true){
		nextControl = new Control(0,0);
	}*/
	
	
	
	
	return nextControl;
    
	
	
    }

	public Control passVehicle(double x, double y, int TID, double cx, double cy, double s){
    	Control c = null;
    	//System.out.println("test");
    	if (TID == 1){
	    	if (cx>x && y<cy+1) //First maneuver up
	    		c = new Control(s,Math.PI/4);
	    	else if (y>cy+1 && x<cx+2){
	    		c = new Control(s,0);
	    		System.out.println(y-cy);
	    	}//second maneuver to move foward
	    	else if (x>cx+2 &&  Math.abs(y-cy)>1e-2){
	    		//System.out.println("test");
	    		c = new Control(s,-Math.PI/4);
	    	}//third maneuver to move back into the lane
	    	else 
	    		c = new Control(s,0);//continue to run normally
    	}
    	else {
    		if (cx>x && y>cy-1) 
	    		c = new Control(s,-Math.PI/4);
	    	else if (y<cy-1 && x<cx+2)
	    		c = new Control(s,0);
	    	else if (x>cx+2 && Math.abs(y-cy)>1e-2)
	    		c = new Control(s,Math.PI/4);
	    	else {
	    		c = new Control(s,0);
	    	}
    	}
    	return c;
    }

}
