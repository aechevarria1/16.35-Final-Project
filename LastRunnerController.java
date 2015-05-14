
public class LastRunnerController extends FirstRunnerController {

	Runner current_runner;
	Runner prev_runner;
	Runner sec_prev_runner;
	Runner comp_runner;
	
	public LastRunnerController(Simulator s, Runner v, Runner prev_runner, Runner sec_prev_runner, Runner comp_runner)
			throws IllegalArgumentException {
		super(s, v, prev_runner);
		// TODO Auto-generated constructor stub
		   this.prev_runner = prev_runner;
		   this.current_runner = v;
		   this.prev_runner = prev_runner;
		   this.sec_prev_runner = sec_prev_runner;
		   this.comp_runner = comp_runner;
	}

	  public synchronized Control getControl(int sec, int msec)
	    {
			double x = current_runner.getPosition()[0];
			double y = current_runner.getPosition()[1];
			double this_speed = current_runner.getInputSpeed();
			double prev_speed = prev_runner.getInputSpeed();
			double cx = comp_runner.getPosition()[0];
			double cy = comp_runner.getPosition()[1];
			int TID = current_runner.getTeamID();
			Control nextControl = null;

		  //start off not moving
	    	/*if (current_runner.getJustRan() == false && current_runner.getHasBaton() == false){
	    		nextControl = new Control(0,0);
	    	}*/
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
	    		else if (Math.abs(y-55)<1e-2 && current_runner.getHasBaton() == false)
	    			nextControl = new Control(prev_speed/2,0);
	    			//System.out.println("test1");
	    	}
	    	//once the baton is handed off
	    	/*else if (current_runner.getHasBaton() == true /*&& prev_runner.getJustRan() == true){
	    		System.out.println("test");
	    		if (TID == 1 && y<cy+1 && cx>x)
	    			nextControl = new Control(this_speed,Math.PI/4);
	    		else if (TID == 0 && y>cy-1 && cx>x)
	    			nextControl = new Control(this_speed,-Math.PI/4);
	    		nextControl = new Control(this_speed,0);
	    		current_runner.setHasBaton(true);
	    		sec_prev_runner.setHasBaton(false);
	    		sec_prev_runner.setJustRan(true);
	    		//prev_runner.setHasBaton(false);
	    		//prev_runner.setJustRan(true);
	    	}*/
	    	System.out.println("prev:"+prev_runner.getHasBaton()+" cur"+current_runner.getHasBaton());
	    	//System.out.println("test1");
			return nextControl;
	    }
	  
	
}
