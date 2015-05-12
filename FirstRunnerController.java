import java.util.ArrayList;
import java.util.List;


public class FirstRunnerController extends RunnerController {

	public FirstRunnerController(Simulator s, Runner v, Runner pv)
			throws IllegalArgumentException {
		super(s, v, pv);
		// TODO Auto-generated constructor stub
	}
	
	public FirstRunnerController(Simulator s, Runner v){
		super(s,v);
	}
    public Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	double x = v.getPosition()[0];
	double y = v.getPosition()[1];
	int TID = v.getTeamID();
	List<Runner> list = s.RunnerList;
	//System.out.println(TID);
	Control nextControl = null;
	//Testing to make sure the runner can change direction properly. Every quadrant it will switch
	//The top is team 1, the bottom is team 0
	//First straight is 10m long
	//Merge is 5m long, needs to start at y = 10 and y = -10
	if (x<=20)
		nextControl= new Control(.5,0);
	/*else if (TID == 0)
		try {
			v.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	else if (TID == 1 && x>20 && x<=25)
		nextControl = new Control(.5,-Math.PI/4);
	else if (TID == 0 && x>20 && x<=25)
		nextControl = new Control(.5,Math.PI/4);
	else if (TID == 1 && x>25)
		nextControl = new Control(.5,0);
	else if (TID == 0 && x>25)
		nextControl = new Control(.5,0);
	//Code that will be implemented for regular Runner Controllers
	return nextControl;
    }

}
