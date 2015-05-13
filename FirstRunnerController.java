import java.util.ArrayList;
import java.util.List;


public class FirstRunnerController extends RunnerController {

	public FirstRunnerController(Simulator s, Runner v, Runner pv)
			throws IllegalArgumentException {
		super(s, v, pv); //pv is actually the next vehicle in this case
		// TODO Auto-generated constructor stub
	}
	
    public Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
	double x = v.getPosition()[0];
	double y = v.getPosition()[1];
	double nx = pv.getPosition()[0];
	double ny = pv.getPosition()[1];
	double dx = v.getVelocity()[0];
	double dy = v.getVelocity()[1];
	double ndx = pv.getVelocity()[0];
	double ndy = pv.getVelocity()[1];
	double s = Math.sqrt(dx*dx + dy*dy);
	double ns = Math.sqrt(ndx*ndx + ndy*ndy);
	//System.out.println(s);
	int TID = v.getTeamID();
	//System.out.println(TID);
	//List<Runner> list = s.RunnerList;
	//System.out.println(nx);
	Control nextControl = null;
	//Testing to make sure the runner can change direction properly. Every quadrant it will switch
	//The top is team 1, the bottom is team 0
	//First straight is 10m long
	//Merge is 5m long, needs to start at y = 10 and y = -10
	if (x<=20){
		nextControl= new Control(.5,0);
	}
	else if (x>20 && x<=25){
		if (TID == 1)
			nextControl = new Control(.5,-Math.PI/4);
		else
			nextControl = new Control(.5,Math.PI/4);
	}
	else if (x>25) {
		if (TID == 1){
			if (Math.abs(nx-x)<3){
				System.out.println(ns);
				nextControl = passVehicle(x,y,TID,nx,ny,2*ns); //Passing runner will be twice the speed of the passed runner
			}
			else 
				nextControl = new Control(1,0);
		}
		else {
			if (Math.abs(nx-x)<3){
				//System.out.println("test");
				nextControl = passVehicle(x,y,TID,nx,ny,2*ns);
			}
			else 
				nextControl = new Control(1,0);
		}
		}
	//else if (TID == 0 && x>25)
	//	nextControl = new Control(.5,0);
	//Code that will be implemented for regular Runner Controllers
	return nextControl;
    }
/*
 * if (Math.abs(nx-x)<5){
				deltaTime = controlTime - prevTime;
				if ((Math.abs(ny-y))<5)
					if (nx>x)
						nextControl = new Control(1,Math.PI/4);
					else
						nextControl = new Control(1,-Math.PI/4);
				else {
					nextControl = new Control(1,0);
					if (x>nx+2)
						nextControl = new Control(1,-Math.PI/4);
				}
			prevTime = controlTime;
			}
			else 
				nextControl = new Control(.6,0);
 */
    public Control passVehicle(double x, double y, int TID, double nx, double ny, double s){
    	Control c = null;
    	if (TID == 1){
	    	if (nx>x && y<ny+1) 
	    		c = new Control(s,Math.PI/4);
	    	else if (y>ny+1 && x<nx+2)
	    		c = new Control(s,0);
	    	else if (x>nx+2 && Math.abs(y-ny)>1e-2)
	    		c = new Control(s,-Math.PI/4);
	    	else 
	    		c = new Control(s,0);
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
 
