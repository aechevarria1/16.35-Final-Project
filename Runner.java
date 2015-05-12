import java.lang.IllegalArgumentException;
//import static org.junit.Assert.*;
import java.lang.Object;
import java.util.Random;
// import java.util.concurrent.locks.ReentrantLock;

public class Runner extends Thread
{
    private double x, y, theta,approachTime;
    private double dx,dy,dtheta;
    private int teamID,legID;
    private boolean hasBaton,won;
    private static int totalNumVehicles = 0;
    private int vehicleID;

    private Simulator _s = null;

    private int _lastCheckedTime = 0;
    private int _lastCheckedMTime = 0;

    private Random r;

    // This lock is needed when you try resource-hierarchy solution
    // for deadlock prevention
    // private ReentrantLock mygvLock;

    public Runner (double pose[], double s, boolean hasBaton,int TID, int LID)
    {
	if (pose.length != 3)
	    throw new IllegalArgumentException("newPos must be of length 3");

	synchronized (Runner.class) {
	    vehicleID = totalNumVehicles;
	    totalNumVehicles++;
	}

	x = pose[0]; 
	y = pose[1]; 
	theta = pose[2];

	dx = s * Math.cos(theta);
	dy = s * Math.sin(theta);
	dtheta = 0;
    teamID = TID;
    legID = LID;
	//clampPosition();
	//clampVelocity();

	r = new Random();
    }

    public void addSimulator(Simulator sim)
    {
	_s = sim;
    }

    public int getVehicleID()
    {
	return vehicleID;
    }

    private void clampPosition() {
	x = Math.min(Math.max(x,0),100);
	y = Math.min(Math.max(y,0),100);
	theta = Math.min(Math.max(theta, -Math.PI), Math.PI);
	if (theta - Math.PI == 0 || Math.abs(theta - Math.PI) < 1e-6)
	    theta = -Math.PI;
    }

    private void clampVelocity() {

	double velMagnitude = Math.sqrt(dx*dx+dy*dy);
	if (velMagnitude > 10.0) {
	    /* Note: 
	 
	       I could also implement this as 

	       double direction = atan2(dy, dx);
	       dx = 10.0 * cos(direction);
	       dy = 10.0 * sin(direction);

	       but since 
	       cos(direction) = dx/velMagnitude;
	       sin(direction) = dy/velMagnitude; 
	 
	       I can save myself an atan2, a cos and a sin, in exchange for two
	       extra divisions. atan2, cos and sin are very expensive
	       computationally. 

	    */ 

	    dx = 10.0 * dx/velMagnitude;
	    dy = 10.0 * dy/velMagnitude;
	}

	if (velMagnitude < 5.0) {
	    /* Same logic as above. */ 

	    dx = 5.0 * dx/velMagnitude;
	    dy = 5.0 * dy/velMagnitude;
	}

	//dtheta = Math.min(Math.max(dtheta, -Math.PI/4), Math.PI/4);		
    }

    private boolean checkIfNoLock() {
	if (_s == null) {
	    return false;
	}
	try {
	    return DeadlockTester.testLock(this, _s);
	} catch (DeadlockTesterException e) {
	    e.printStackTrace();
	    Runtime.getRuntime().exit(1);
	}
	return false;
    }

    public double [] getPosition() {
	double[] position = new double[3];
	if (checkIfNoLock()) {
	    synchronized(this) {
		position[0] = x;
		position[1] = y;
		position[2] = theta;
    
		return position;
	    }
	}
	return position;
    }

    public double [] getVelocity() {
	double[] velocity = new double[3];
	if (checkIfNoLock()) {
	    synchronized(this) {
		velocity[0] = dx;
		velocity[1] = dy;
		velocity[2] = dtheta;
		
		return velocity;
	    }
	}
	return velocity;	
    }

    public synchronized void setPosition(double[] newPos) {
	if (newPos.length != 3)
	    throw new IllegalArgumentException("newPos must be of length 3");      

	x = newPos[0];
	y = newPos[1];
	theta = newPos[2];

	//clampPosition();
    }

    public synchronized void setVelocity(double[] newVel) {
	if (newVel.length != 3)
	    throw new IllegalArgumentException("newVel must be of length 3");      

	dx = newVel[0];
	dy = newVel[1];
	dtheta = newVel[2];		

	//clampVelocity();
    }

    public synchronized void controlRunner(Control c) {
	dx = c.getSpeed() * Math.cos(theta);
	dy = c.getSpeed() * Math.sin(theta);
	theta = c.getAngle();

	//clampVelocity();
    }

    public void run()
    {
	int currentTime = 0;
	int currentMTime = 0;
	
	while(currentTime < 100.0){
	    synchronized(_s){
		currentTime = _s.getCurrentSec();
		currentMTime = _s.getCurrentMSec();

		while(_lastCheckedTime == currentTime && _lastCheckedMTime == currentMTime){
		    try{
			// // DEBUG
			// System.out.printf("GV %d [%d,%d] waiting\n", vehicleID, currentTime, currentMTime);
			// // DEBUG
			_s.wait();
			currentTime = _s.getCurrentSec();
			currentMTime = _s.getCurrentMSec();
		    }
		    catch(java.lang.InterruptedException e){
			System.err.printf("Interupted " + e);
		    }
		}
		// // DEBUG
		// System.out.printf("GV %d [%d,%d] proceeding\n", vehicleID, currentTime, currentMTime);
		// // DEBUG
		_s.notifyAll();
	    }

	    // // DEBUG
	    // System.out.printf("GV %d [%d,%d] advancing\n", vehicleID, currentTime, currentMTime);
	    // // DEBUG

	    // advance(currentTime - _lastCheckedTime, 
	    // 	    currentMTime - _lastCheckedMTime);

	    advanceNoiseFree(currentTime - _lastCheckedTime, 
	                  currentMTime - _lastCheckedMTime);

	    _lastCheckedTime = currentTime;
	    _lastCheckedMTime = currentMTime;

	    synchronized(_s){
		if(_s.numVehicleToUpdate == 0) {
		    //this should not already be zero - something is wrong
		    System.err.println("ERROR: No of vehicles to update already 0\n");
		    System.exit(-1);
		}
		// // DEBUG
		// System.out.printf("GV %d [%d,%d] decrementing numVehicleToUpdate\n", vehicleID, currentTime, currentMTime);
		// // DEBUG
		_s.numVehicleToUpdate--;
		_s.notifyAll();
	    }	
	}

    }

    public static double normalizeAngle(double theta)
    {
	double rtheta = ((theta - Math.PI) % (2 * Math.PI));
	if (rtheta < 0) {	// Note that % in java is remainder, not modulo.
	    rtheta += 2*Math.PI;
	}
	return rtheta - Math.PI;
    }

   
    public synchronized void advanceNoiseFree(int sec, int msec)
    {
	double t = sec + msec * 1e-3;
	double s = Math.sqrt( dx * dx + dy * dy );

	/*if (Math.abs(dtheta) > 1e-3) { // The following model is not well defined when dtheta = 0
	    // Circle center and radius
	    double r = s/dtheta;

	    double xc = x - r * Math.sin(theta);
	    double yc = y + r * Math.cos(theta);

	    theta = theta + dtheta * t;

	    double rtheta = ((theta - Math.PI) % (2 * Math.PI));
	    if (rtheta < 0) {	// Note that % in java is remainder, not modulo.
		rtheta += 2*Math.PI;
	    }
	    theta = rtheta - Math.PI;

	    // Update    
	    x = xc + r * Math.sin(theta);
	    y = yc - r * Math.cos(theta);
	    dx = s * Math.cos(theta);
	    dy = s * Math.sin(theta);

	} else*/ {			// Straight motion. No change in theta.
	    x = x + dx * t;
	    y = y + dy * t;
	    /*try {
			sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	//clampPosition();
	//clampVelocity();
    }

    public boolean getHasBaton(){
    	return hasBaton;
    }
    
    public boolean getWon(){
    	return won;
    }
    
    public void setWon(boolean w){
    	won = w;
    }
    
    public double getApproachTime(){
    	return approachTime;
    }
    
    public int getTeamID(){
    	return teamID;
    }
    
    public int getLegID(){
    	return legID;
    }
}
