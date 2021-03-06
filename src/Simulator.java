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
	//System.out.printf("---------Adding Ground Vehicle-----------\n");
	for(int i=0;i < RunnerList.size(); i++){
	    Runner mgv = RunnerList.get(i);
	    double position[] = mgv.getPosition();
	  //  System.out.printf("%d : %f,%f,%f \n", mgv.getVehicleID(),
			   //   position[0], position[1], position[2]);
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

	while (curentSec < 100000) {
	    
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

	    //Check justPassed and check for winner
	    synchronized(this){
	    for (int i=0;i < RunnerList.size();i++){
	    	Runner cr = RunnerList.get(i);
	    	Runner nr = RunnerList.get(i);
	    	for (int j=0;j<RunnerList.size();j++){
	    		if (j!=i && RunnerList.get(j).getLegID()==cr.getLegID())
	    			nr = RunnerList.get(j);
	    		if(cr.getHasBaton() && nr.getHasBaton())  {
	    			if (cr.getPosition()[0]>nr.getPosition()[0] && cr.getJustPassed() == false){
	    				nr.setJustPassed(true);
	    		}
	    		}
	    	}
	    	if (cr.getWon()){
	    		System.out.println("Team "+cr.getTeamID()+" wins!");
	    		Thread.currentThread().interrupt();
	    		return;
	    	}
	    }
	    notifyAll();
	    }
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

    public static void main (String [] args) throws InterruptedException,IllegalArgumentException
    {
	if (args.length == 0) {
	    System.err.println("Usage: Java Simulator <noVehicles> <hostname>\n"+
			       "where <noVehicles> : total number of vehicles\n" +
			       "      <hostname>   : DisplayServer host address");
	    System.exit(-1);
	}
	double runner11s = Double.parseDouble(args[1]);
	double runner12s = Double.parseDouble(args[2]);
	double runner13s = Double.parseDouble(args[3]);
	double runner14s = Double.parseDouble(args[4]);
	double runner21s = Double.parseDouble(args[5]);
	double runner22s = Double.parseDouble(args[6]);
	double runner23s = Double.parseDouble(args[7]);
	double runner24s = Double.parseDouble(args[8]);
	if (runner11s+runner12s+runner13s+runner14s > 2)
		throw new IllegalArgumentException("Team 1 has inputted too much speed!");
	if (runner21s+runner22s+runner23s+runner24s > 2)
		throw new IllegalArgumentException("Team 2 has inputted too much speed!");
	int numberofVehicles = 1;
	String host = args[0];
	// The args are going to specify each runner's speed. here is what args will be
	//host,11,12,13,14,21,22,23,24
	//(where 11 is the speed of runner11, for example)
	//no commas
	
	DisplayClient dpClient = new DisplayClient(host);

	Simulator sim = new Simulator(dpClient);


	int team1y = 50;
	int team2y = 60;
	int runner1x = 10;
	int runner2x = 60;
	int runner3x = 110;
	int runner4x = 160;
    

   	double[] initialPos11 = {runner1x,team1y, 0};
	Runner runner11 = new Runner(initialPos11, runner11s, true, runner1x, false,0,1,0,false, false);

	double[] initialPos12 = {runner2x,team1y, 0};
	Runner runner12 = new Runner(initialPos12, runner12s, false, runner2x, false,0,2,0,false, false);

	
	double[] initialPos13 = {runner3x,team1y, 0};
	Runner runner13 = new Runner(initialPos13,runner13s, false, runner3x, false,0,3,0,false, false);

	
	double[] initialPos14 = {runner4x,team1y, 0};
	Runner runner14 = new Runner(initialPos14, runner14s, false, runner4x, false,0,4,0,false, false);

	
	double[] initialPos21 = {runner1x,team2y, 0};
	Runner runner21 = new Runner(initialPos21, runner21s, true, runner1x, false,1,1,0,false, false);
	
	
	double[] initialPos22 = {runner2x,team2y, 0};
	Runner runner22 = new Runner(initialPos22, runner22s, false, runner2x, false,1,2,0,false, false);

	
	double[] initialPos23 = {runner3x,team2y, 0};
	Runner runner23 = new Runner(initialPos23, runner23s, false, runner3x, false,1,3,0,false, false);

	
	double[] initialPos24 = {runner4x,team2y, 0};
	Runner runner24 = new Runner(initialPos24, runner24s, false, runner4x, false,1,4,0,false, false);

	
	
	FirstRunnerController c11 = new FirstRunnerController(sim, runner11, runner12);
	RunnerController c12 = new RunnerController(sim, runner12, runner13, runner11,runner22);
	RunnerController c13 = new RunnerController(sim, runner13, runner14, runner12,runner23);
	LastRunnerController c14 = new LastRunnerController(sim, runner14, runner13,runner12,runner24);
	FirstRunnerController c21 = new FirstRunnerController(sim, runner21, runner22);
	RunnerController c22 = new RunnerController(sim, runner22, runner23, runner21,runner12);
	RunnerController c23 = new RunnerController(sim, runner23, runner24, runner22,runner13);
	LastRunnerController c24 = new LastRunnerController(sim, runner24, runner23,runner22,runner14);

	
	
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
