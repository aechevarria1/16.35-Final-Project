import java.util.*;

public class Simulator extends Thread
{
    private int curentSec = 0;
    private int curentMSec = 0;

    //List of Runner
    protected List<Runner> RunnerList;
    public int numControlToUpdate = 0;
    public int numVehicleToUpdate = 0;

    private DisplayClient displayClient;

    public Simulator(){
	RunnerList = new ArrayList<Runner>();	
    }

    public Simulator(DisplayClient displayClient){
	this();
	
	if(displayClient ==null){
	    throw new IllegalArgumentException("Invalid Display client object");
	}
	this.displayClient = displayClient;
    }


    public int getCurrentSec() {
	return curentSec;
    }
	
    public int getCurrentMSec() {
	return curentMSec;
    }
    
    

    public void advanceClock() {
	curentMSec += 10;
	if (curentMSec >= 1e3) {
	    curentMSec -= 1e3;
	    curentSec ++;
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

	int _lastUpdateSec = curentSec;
	int _lastUpdateMSec = curentMSec;

	displayClient.clear();
	double gvX[] = new double[RunnerList.size()];
	double gvY[] = new double[RunnerList.size()];
	double gvTheta[] = new double[RunnerList.size()];
	displayClient.traceOn();

	while (curentSec < 100) {
	    
	    int deltaSec = curentSec - _lastUpdateSec;
	    int deltaMSec = curentMSec - _lastUpdateMSec;

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
	    displayClient.update(RunnerList.size(),gvX,gvY,gvTheta);

	    // Advance the clock
	    _lastUpdateSec = curentSec;
	    _lastUpdateMSec = curentMSec;
	    synchronized(this) {
		// // DEBUG
		// System.out.printf("Sim [%d,%d] clock advancing, controllers: %d, vehicles : %d\n",
		// 		  curentSec, curentMSec, 
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
			// 		  curentSec, curentMSec,
			// 		  numControlToUpdate, numVehicleToUpdate);
			// // DEBUG
		    }
		} catch (java.lang.InterruptedException e) {
		    System.err.printf("Interrupted " + e);
		}

		// // DEBUG
		// System.out.printf("Sim [%d,%d] all updated\n", curentSec, curentMSec);
		// // DEBUG

		numControlToUpdate = RunnerList.size();
		numVehicleToUpdate = RunnerList.size();

		// // DEBUG
		// System.out.printf("Sim [%d,%d] resetting sizes, controllers: %d, vehicles: %d\n",
		// 		  curentSec, curentMSec,
		// 		  numControlToUpdate, numVehicleToUpdate);
		// // DEBUG
		
		notifyAll();
	    }

	}
	displayClient.traceOff();
    }

    public static void main (String [] args) throws InterruptedException
    {
	if (args.length == 0) {
	    System.err.println("Usage: Java Simulator <noVehicles> <hostname>\n"+
			       "where <noVehicles> : total number of vehicles\n" +
			       "      <hostname>   : DisplayServer host address");
	    System.exit(-1);
	}
	
	int numberofVehicles = 1;
	String host = args[0];

	DisplayClient dpClient = new DisplayClient(host);

	Simulator sim = new Simulator(dpClient);


	int team1y = 50;
	int team2y = 60;
	int runner1x = 10;
	int runner2x = 60;
	int runner3x = 110;
	int runner4x = 160;
    
   	double[] initialPos11 = {runner1x,team1y, 0};
	Runner runner11 = new Runner(initialPos11, 0, false, runner1x);
	FirstRunnerController c11 = new FirstRunnerController(sim, runner11);

	double[] initialPos12 = {runner2x,team1y, 0};
	Runner runner12 = new Runner(initialPos12, 0, false, runner2x);
	RunnerController c12 = new RunnerController(sim, runner12, runner11);
	
	double[] initialPos13 = {runner3x,team1y, 0};
	Runner runner13 = new Runner(initialPos13, 0, false, runner3x);
	RunnerController c13 = new RunnerController(sim, runner13, runner12);
	
	double[] initialPos14 = {runner4x,team1y, 0};
	Runner runner14 = new Runner(initialPos14, 0, false, runner4x);
	RunnerController c14 = new RunnerController(sim, runner14, runner13);
	
	double[] initialPos21 = {runner1x,team2y, 0};
	Runner runner21 = new Runner(initialPos21, 0, false, runner1x);
	FirstRunnerController c21 = new FirstRunnerController(sim, runner21);
	
	double[] initialPos22 = {runner2x,team2y, 0};
	Runner runner22 = new Runner(initialPos22, 0, false, runner2x);
	RunnerController c22 = new RunnerController(sim, runner22, runner21);
	
	double[] initialPos23 = {runner3x,team2y, 0};
	Runner runner23 = new Runner(initialPos23, 0, false, runner3x);
	RunnerController c23 = new RunnerController(sim, runner23, runner22);
	
	double[] initialPos24 = {runner4x,team2y, 0};
	Runner runner24 = new Runner(initialPos24, 0, false, runner4x);
	RunnerController c24 = new RunnerController(sim, runner24, runner23);
	
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
