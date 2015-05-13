
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
	Control nextControl = null;
	double dist_bw_next_runner = next_runner.getPosition()[0] - current_runner.getPosition()[0];

//if the previous runner still has the baton, don't move
	if (prev_runner.getHasBaton() == true)
		nextControl = new Control(0,0);
	//if the previous runner doesn't have the baton and just ran, start moving
	else if (prev_runner.getHasBaton() == false && prev_runner.getJustRan() == true){
    	if (y<55){
    		nextControl= new Control(1,Math.PI/4);
    	}
    	else if (y>55)
    		nextControl= new Control(1,-Math.PI/4);
    	else if (y == 55)
    		nextControl = new Control(1,0);
    	//once you reach the stopping point, stop moving and set hasBaton to false and set justRan to true
	if (dist_bw_next_runner < 10){
		nextControl = new Control(0,0);
		current_runner.setHasBaton(false);
		current_runner.setJustRan(true);
		
	}
	if (current_runner.getHasBaton() == false && current_runner.getJustRan() == true){
		nextControl = new Control(0,0);
	}
	
	}
	
	
	
	return nextControl;
    
	
	
    }


}
