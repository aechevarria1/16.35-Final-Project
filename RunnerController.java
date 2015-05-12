
public class RunnerController extends FirstRunnerController {


	private Runner previous_runner;
    public RunnerController(Simulator s, Runner v, Runner prev_runner)
			throws IllegalArgumentException {
		super(s, v);
		// TODO Auto-generated constructor stub
		   this.previous_runner = prev_runner;
    }
 
    
    
    
    
    
    

	public Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	double x = v.getPosition()[0];
	double y = v.getPosition()[1];
	Control nextControl = null;
	//Testing to make sure the runner can change direction properly. Every quadrant it will switch


	
	if (previous_runner.getPosition()[0] < v.getStart_x() ){
		nextControl = new Control(0,0);
    	return nextControl;    		
	}
	else{
	if (y<55){
		nextControl= new Control(1,3*Math.PI/8);
	}
	else if (y>55)
		nextControl= new Control(1,-3*Math.PI/8);
	else if (y == 55)
		nextControl = new Control(1,0);
	
	return nextControl;
	}
}


}
