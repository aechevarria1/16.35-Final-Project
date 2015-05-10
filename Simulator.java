import java.util.*;

public class Simulator extends Thread
{
    private int _currentSec = 0;
    private int _currentMSec = 0;

    //List of Runner
    protected List<Runner> RunnerList;
    public int numControlToUpdate = 8;
    public int numVehicleToUpdate = 8;
    public int numberofVehicles = 8;

    private DisplayClient _displayClient;

    public Simulator(){
	RunnerList = new ArrayList<Runner>();	
    }

    public Simulator(DisplayClient displayClient){
	this();
	
	if(displayClient ==null){
	    throw new IllegalArgumentException("Invalid Display client object");
	}
	_displayClient = displayClient;
    }


    public int getCurrentSec() {
	return _currentSec;
    }
	
    public int getCurrentMSec() {
	return _currentMSec;
    }

    public void advanceClock() {
	_currentMSec += 10;
	if (_currentMSec >= 1e3) {
	    _currentMSec -= 1e3;
	    _currentSec ++;
	}
    }

    public synchronized void addRunner(Runner gv){
	RunnerList.add(gv);
	System.out.printf("---------Adding Ground Vehicle-----------\n");
	for(int i=0;i < RunnerList.size(); i++){
	    Runner mgv = RunnerList.get(i);
	    double position[] = mgv.getPosition();
	    System.out.printf("%d : %f,%f,%f \n", mgv.getVehicleID(),
			      position[0], position[1], position[2]);
	}
	numVehicleToUpdate++;
	numControlToUpdate++;
    }

    public void run()
    {

	// We're going to need these to know how much time has elapsed since the
	// last call to vehicle.updateState(). We could leave this out, and always
	// call vehicle.updateState() with arguments of 0 and 10, but for a
	// real-time implementation in a later assignment, we're actually going to
	// need to measure the elapsed time. 

	int _lastUpdateSec = _currentSec;
	int _lastUpdateMSec = _currentMSec;

	_displayClient.clear();
	double gvX[] = new double[RunnerList.size()];
	double gvY[] = new double[RunnerList.size()];
	double gvTheta[] = new double[RunnerList.size()];
	_displayClient.traceOn();

	while (_currentSec < 100) {
	    
	    int deltaSec = _currentSec - _lastUpdateSec;
	    int deltaMSec = _currentMSec - _lastUpdateMSec;

	    if (deltaMSec < 0) {
		deltaMSec += 1e3;
		deltaSec -= 1;
	    }

	    // Update display
	    for(int i=0;i < RunnerList.size(); i++){
		Runner currVehicle = RunnerList.get(i);
		double [] position = currVehicle.getPosition();	
		double [] velocity = currVehicle.getVelocity();	
		gvX[i] = position[0];
		gvY[i] = position[1];
		gvTheta[i] = position[2];
	    }
	    _displayClient.update(RunnerList.size(),gvX,gvY,gvTheta);

	    // Advance the clock
	    _lastUpdateSec = _currentSec;
	    _lastUpdateMSec = _currentMSec;
	    synchronized(this) {
		// // DEBUG
		// System.out.printf("Sim [%d,%d] clock advancing, controllers: %d, vehicles : %d\n",
		// 		  _currentSec, _currentMSec, 
		// 		  numControlToUpdate, numVehicleToUpdate);
		// System.out.printf("--------------------------------------------\n");
		// // DEBUG
		advanceClock();
		notifyAll();
	    }

	    // Wait while everything is updated
	    synchronized(this) {
		try {
		    while (numVehicleToUpdate > 0 || numControlToUpdate > 0) {
			wait();
			// // DEBUG
			// System.out.printf("Sim [%d,%d] waiting for updating, controllers: %d, vehicles:%d\n",
			// 		  _currentSec, _currentMSec,
			// 		  numControlToUpdate, numVehicleToUpdate);
			// // DEBUG
		    }
		} catch (java.lang.InterruptedException e) {
		    System.err.printf("Interrupted " + e);
		}

		// // DEBUG
		// System.out.printf("Sim [%d,%d] all updated\n", _currentSec, _currentMSec);
		// // DEBUG

		numControlToUpdate = RunnerList.size();
		numVehicleToUpdate = RunnerList.size();

		// // DEBUG
		// System.out.printf("Sim [%d,%d] resetting sizes, controllers: %d, vehicles: %d\n",
		// 		  _currentSec, _currentMSec,
		// 		  numControlToUpdate, numVehicleToUpdate);
		// // DEBUG
		
		notifyAll();
	    }

	}
	_displayClient.traceOff();
    }

    public static void main (String [] args) throws InterruptedException
    {
	if (args.length == 0) {
	    System.err.println("Usage: Java Simulator <noVehicles> <hostname>\n"+
			       "where <noVehicles> : total number of vehicles\n" +
			       "      <hostname>   : DisplayServer host address");
	    System.exit(-1);
	}
	
	//the number of vehicles is 8, so the user doesn't need to specify this
	//int numberofVehicles = Integer.parseInt(args[0]);
	
	String host = args[0];

	DisplayClient dpClient = new DisplayClient(host);

	Simulator sim = new Simulator(dpClient);

	//Random r = new Random();

	double team1y = 50;
	double team2y = 60;
	double runner1x = 10;
	double runner2x = 60;
	double runner3x = 110;
	double runner4x = 160;
	
	double vel[] = {0,0,0};
	double vel_moving11[] = {20,5,0};
	double vel_moving21[] = {20,5,0};
	//create all 8 vehicles and vehicleControllers, set their initial speed, omega, and pos
	
	
	double[] initialPos11 = {runner1x,team1y, 0};
	double initialS11 = 5;
	double initialOmega11 = Math.PI;
	Runner runner11 = new Runner(initialPos11, vel_moving11,false,0);
	VehicleController c11 = new FirstVehicleController(sim, runner11);
	
	double[] initialPos12 = {runner2x,team1y, 0};
	double initialS12 = 0;
	double initialOmega12 = 0;
	Runner runner12 = new Runner(initialPos12, vel,false,0);
	VehicleController c12 = new VehicleController(sim, runner12);
	
	double[] initialPos13 = {runner3x,team1y, 0};
	double initialS13 = 0;
	double initialOmega13 = 0;
	Runner runner13 = new Runner(initialPos13, vel,false,0);
	VehicleController c13 = new VehicleController(sim, runner13);
	
	double[] initialPos14 = {runner4x,team1y, 0};
	double initialS14 = 0;
	double initialOmega14 = 0;
	Runner runner14 = new Runner(initialPos14, vel,false,0);
	VehicleController c14 = new VehicleController(sim, runner14);
	
	double[] initialPos21 = {runner1x,team2y, 0};
	double initialS21 = 5;
	double initialOmega21 = Math.PI;
	Runner runner21 = new Runner(initialPos21, vel_moving21,false,0);
	VehicleController c21 = new FirstVehicleController(sim, runner21);
	
	double[] initialPos22 = {runner2x,team2y, 0};
	double initialS22 = 0;
	double initialOmega22 = 0;
	Runner runner22 = new Runner(initialPos22, vel,false,0);
	VehicleController c22 = new VehicleController(sim, runner22);
	
	double[] initialPos23 = {runner3x,team2y, 0};
	double initialS23 = 0;
	double initialOmega23 = 0;
	Runner runner23 = new Runner(initialPos23, vel,false,0);
	VehicleController c23 = new VehicleController(sim, runner23);
	
	double[] initialPos24 = {runner4x,team2y, 0};
	double initialS24 = 0;
	double initialOmega24 = 0;
	Runner runner24 = new Runner(initialPos24, vel,false,0);
	VehicleController c24 = new VehicleController(sim, runner24);
	
	
	//there should be 4 equidistant vehicles to start
	
	
	sim.addRunner(runner11);
	sim.addRunner(runner12);
	sim.addRunner(runner13);
	sim.addRunner(runner14);
	sim.addRunner(runner21);
	sim.addRunner(runner22);
	sim.addRunner(runner23);
	sim.addRunner(runner24);
	runner11.addSimulator(sim);
	runner12.addSimulator(sim);
	runner13.addSimulator(sim);
	runner14.addSimulator(sim);
	runner21.addSimulator(sim);
	runner22.addSimulator(sim);
	runner23.addSimulator(sim);
	runner24.addSimulator(sim);
    c11.start();
    c12.start();
    c13.start();
    c14.start();
    c21.start();
    c22.start();
    c23.start();
    c24.start();
    runner11.start();
    runner12.start();
    runner13.start();
    runner14.start();
    runner21.start();
    runner22.start();
    runner23.start();
    runner24.start();
	sim.start();
    }
}
