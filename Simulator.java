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
	
	int numberofVehicles = Integer.parseInt(args[0]);
	String host = args[1];

	DisplayClient dpClient = new DisplayClient(host);

	Simulator sim = new Simulator(dpClient);

	Random r = new Random();

	Runner leader = null;

	int leaderType = 1; // 0 - RandomController, 1 - LeadingController

	RunnerController fc = null; // First controller	
	
	for (int i = 0; i < numberofVehicles; i++) {
	    double[] initialPos = { r.nextDouble() * 100, r.nextDouble() * 100,
				    r.nextDouble() * 2 * Math.PI - Math.PI };
	    double initialS = r.nextDouble() * 5.0 + 5;
	    double initialOmega = r.nextDouble() * Math.PI / 2.0 - Math.PI / 4.0;

	    Runner gvf = new Runner(initialPos, initialS,false,0,0, initialOmega);
	    RunnerController c = null;

	    if (i == 0) {
		if (leaderType == 0 ) {
		    c = new RandomController(sim, gvf);
		} else if (leaderType == 1) {
		    c = new LeadingController(sim, gvf);
		}
		fc = c;
		leader = gvf;
	    } else {
		if (leader != null) {
		    c = new FollowingController(sim, gvf, leader);
		    if (leaderType == 1) {
			((LeadingController)fc).addFollower(gvf);
		    }
		} else {
		    System.err.println("ERROR: no leader vehicle defined.");
		    System.exit(-1);
		}
	    }
	    sim.addRunner(gvf);
	    gvf.addSimulator(sim);
	    c.start();
	    gvf.start();
	}
	sim.start();
    }
}
