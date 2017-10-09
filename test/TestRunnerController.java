import static org.junit.Assert.*;

public class TestRunnerController {

  

    /**
     * Test adding runner and simulator 
     * with the VehicleController
     */
    @org.junit.Test
	public void addGVTest() {
	double[] pose = { 0, 0, 0 };
	 double input_speed = 0;
	    int start_x = 50;
	    int teamID = 1;
	    int legID = 1;
	    double s = 0;
	    boolean hasBaton = false;
	    boolean justRan = false;
	    boolean justPassed = false;
	    boolean doneRunning = false;
	    boolean hasWon = false;
	    Runner current_v = new Runner(pose, input_speed, hasBaton, start_x, justRan, 0, 1, s, justPassed, doneRunning);
	    Runner next_runner = new Runner(pose, input_speed, hasBaton, start_x, justRan, 0, 2, s, justPassed, doneRunning);
	    Runner prev_runner = new Runner(pose, input_speed, hasBaton, start_x, justRan, 0, 0, s, justPassed, doneRunning);
	    Runner comp_runner = new Runner(pose, input_speed, hasBaton, start_x, justRan, 1, 1, s, justPassed, doneRunning);
	    Simulator sim = new Simulator();
	    RunnerController rc = new RunnerController(sim, current_v, next_runner, prev_runner, comp_runner);
	    		
    }

    @org.junit.Test(expected=IllegalArgumentException.class)
	public void addNullGVTest() {
    	 double input_speed = 0;
    	 double[] pose = { 0, 0, 0 };
    	    int start_x = 50;
    	    int teamID = 1;
    	    int legID = 1;
    	    double s = 0;
    	    boolean hasBaton = false;
    	    boolean justRan = false;
    	    boolean justPassed = false;
    	    boolean doneRunning = false;
    	    boolean hasWon = false;
    	    Runner current_v = new Runner(pose, input_speed, hasBaton, start_x, justRan, 0, 1, s, justPassed, doneRunning);
    	    Runner next_runner = new Runner(pose, input_speed, hasBaton, start_x, justRan, 0, 2, s, justPassed, doneRunning);
    	    Runner prev_runner = new Runner(pose, input_speed, hasBaton, start_x, justRan, 0, 0, s, justPassed, doneRunning);
    	    Runner comp_runner = new Runner(pose, input_speed, hasBaton, start_x, justRan, 1, 1, s, justPassed, doneRunning);
    	    Simulator sim = new Simulator();
    	    RunnerController rc = new RunnerController(sim, current_v, next_runner, prev_runner, comp_runner);
    	    		
    }
    public static void main(String[] args){
	org.junit.runner.JUnitCore.main(TestRunnerController.class.getName());
    }
}
