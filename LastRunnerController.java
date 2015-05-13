
public class LastRunnerController extends FirstRunnerController {

	Runner current_runner;
	Runner prev_runner;
	
	public LastRunnerController(Simulator s, Runner v, Runner prev_runner)
			throws IllegalArgumentException {
		super(s, v, prev_runner);
		// TODO Auto-generated constructor stub
		   this.prev_runner = prev_runner;
		   this.current_runner = v;
		   this.prev_runner = prev_runner;

	}

	  public synchronized Control getControl(int sec, int msec)
	    {
		  double controlTime = sec+msec*1E-3;
			double x = current_runner.getPosition()[0];
			double y = current_runner.getPosition()[1];
			Control nextControl = null;

		  //start off not moving
	    	if (current_runner.getJustRan() == false){
	    		nextControl = new Control(0,0);
	    	}
	    	//once the previous runner just ran and gives up the baton, get the baton and run
	    	if (prev_runner.getJustRan() == true && prev_runner.getHasBaton() == false){
	    		if (y<55)
	    			nextControl= new Control(1,3*Math.PI/8);
	    		if (y>55)
	    			nextControl= new Control(1,-3*Math.PI/8);
	    		if (y == 55)
	    			nextControl = new Control(1,0);
	    		//once you uncomment this, the code goes nuts, but you need to do it eventually
	    		//current_runner.setJustRan(true);
	    	}
	    	
	    	
			return nextControl;
	    }
	
}
