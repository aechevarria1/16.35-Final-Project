
public class FirstVehicleController extends VehicleController {

	public FirstVehicleController(Simulator s, Runner v)
			throws IllegalArgumentException {
		super(s, v);
		// TODO Auto-generated constructor stub
	}

//the vehicle goes in at an angle and then straightens out once it reaches the y value of the track.
	  public Control getControl(int sec, int msec)
	    {
		double controlTime = sec+msec*1E-3;
		Control nextControl = null;
		double speed = 5;
		double omega = Math.PI;
		nextControl = new Control(speed, omega);
		return nextControl;
	    }
	

}
