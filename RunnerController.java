
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
	double comp_speed = comp_runner.getInputSpeed();
	double dist_bw_next_runner = next_runner.getPosition()[0] - current_runner.getPosition()[0];

//if the previous runner still has the baton, don't move
	if (prev_runner.getHasBaton() == true && prev_runner.getJustRan() == false){
		nextControl = new Control(0,0);}

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
	
	//Passing another runner
	//HERE'S WHERE THE ERROR WAS, it comes right back here as soon as the vehicle passes, so add the part to go back down in here.
	if (current_runner.getHasBaton() == true && current_runner.getJustRan() == false) {
		nextControl = new Control(this_speed,0);
    	
		if (x>cx+2 &&  Math.abs(y-cy)>1e-2 && TID == 1){
    		nextControl = new Control(this_speed,-Math.PI/4);}
		
		if (x>cx+2 && Math.abs(y-cy)<1e-2 && TID == 0)
    		nextControl = new Control(this_speed,Math.PI/4);
		
		
		if (Math.abs(cx-x)<3){ //Making sure they are within a good distance to be able to pass
			if (cx>x){
				if (counter == 0){
					current_runner.setJustPassed(true);
					counter++;
				}
				if (current_runner.getJustPassed() == true && nx-cx-20>0){ //Make sure the runner can have enought time to pass before the exchange zone
					nextControl = passVehicle(x,y,TID,cx,cy,2*comp_runner.getInputSpeed());
				}//Passing runner will be twice the speed of the passed runner
				else if (current_runner.getJustPassed() == true && nx-cx-20<0)
					nextControl = new Control(comp_speed,0);
			}
			
			else if (cx<x){
				counter++;
			}
		}
		
		if(dist_bw_next_runner < 10){
	   		//nextControl = new Control(0,0);
			//current_runner.setHasBaton(false);
			current_runner.setJustRan(true); //  runner is approaching next runner
			}	
		//System.out.println("test");
		
	}
	
	else if (current_runner.getJustRan() == true && next_runner.getHasBaton() == false){
		nextControl = new Control(this_speed,0);
		//System.out.println(current_runner.getHasBaton());
		if (dist_bw_next_runner < 3) {
			current_runner.setHasBaton(false);//runner is passing baton
			next_runner.setHasBaton(true);
			nextControl = new Control(0,0);
			System.out.println(current_runner.getHasBaton());
		}
	}
	
	else if (current_runner.getJustRan() == true && next_runner.getHasBaton() == true){
		nextControl = new Control(0,0);
		System.out.println(current_runner.getHasBaton());
	}
	
	
	return nextControl;
    
	
	
    }

	public synchronized Control passVehicle(double x, double y, int TID, double cx, double cy, double s){
    	Control c = null;
    //CONTROLS FOR TEAM 1	
    	if (TID == 1){
    		//competitor is ahead of you: maneuver up
	    	if (cx>x && y<cy+1){ //First maneuver up
	    		c = new Control(s,Math.PI/4);} //t = s/sqrt(2) * 2 for the other direction
	    	else if (cx>x+2 && y>cy+1){//then move forward
	    		c = new Control(s,0); //t = s/((sqrt(2)-1)+2)
	    	}//maneuver back down now that you're ahead of the competitor
	    		//moved this if statement above
	    	//third maneuver to move back into the lane
	    	else 
	    		c = new Control(s,0);//continue to run normally
    	}
    //CONTROLS FOR TEAM 2	
    	else {
    		if (cx>x && y>cy-1) 
	    		c = new Control(s,-Math.PI/4);
	    	else if (y<cy-1 && x<cx+2)
	    		c = new Control(s,0);
	    	//moved this if statement above
	    	else {
	    		c = new Control(s,0);
	    	}
    	}
    	return c;
    }

}