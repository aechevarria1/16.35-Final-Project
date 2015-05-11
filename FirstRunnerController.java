
public class FirstRunnerController extends RunnerController {

	public FirstRunnerController(Simulator s, Runner v)
			throws IllegalArgumentException {
		super(s, v);
		// TODO Auto-generated constructor stub
	}
	
	
    public Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	double x = v.getPosition()[0];
	double y = v.getPosition()[1];
	Control nextControl = null;
	//Testing to make sure the runner can change direction properly. Every quadrant it will switch
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
