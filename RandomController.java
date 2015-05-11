import java.util.*;
import java.lang.IllegalArgumentException;

public class RandomController extends RunnerController
{
    private Random rng;

    public RandomController(Simulator s, Runner v){
	super(s,v);	
	rng = new Random();
    }

    public Control getControl(int sec, int msec) {
	// DEBUG
	System.out.printf("VC %d [%d,%d] get random control, vehicle: %d\n",
			  controllerID, sec, msec, v.getVehicleID());
	// DEBUG
	// avoid walls if we're too close
	Control a = avoidWalls(this.v.getPosition());
	if (a != null)
	    return a;
		
	// otherwise generate a random control
	Control c = new Control(rng.nextDouble() * 5 + 5,
				rng.nextDouble() * Math.PI / 2.0 - Math.PI / 4.0);
	return c;
    }

}