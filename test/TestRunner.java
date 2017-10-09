import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class TestRunner {	
  @Test
    public void testConstructor() {
    double [] pose = {1, 2, 3};
    double dx = 5, dy = 0, dt = 0;
    
    
    
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
    

    
    
    
    
    double [] newPose = current_v.getPosition();
    Assert.assertEquals(pose[0], newPose[0], 1e-6);
    Assert.assertEquals(pose[1], newPose[1], 1e-6);
    Assert.assertEquals(pose[2], newPose[2], 1e-6);

    double [] newVel = current_v.getVelocity();
    Assert.assertEquals(0, newVel[0], 1e-6);
    Assert.assertEquals(dy, newVel[1], 1e-6);
    Assert.assertEquals(dt, newVel[2], 1e-6);
  }

  @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsInConstructor() {
    // Too many arguments in pose constructor 
    double [] pose = {0, 0, 0, 0};
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
    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
  
  }

  @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsInConstructor() {
    // Too few arguments in pose constructor 
    double [] pose = {0};
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
    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
  
  }

  @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsSetPosition() {
    // Too many arguments in setPosition 
    double [] pose = {0, 0, 0};
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
    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
  
    double [] newPose = {0, 0, 0, 0};
    gv.setPosition(newPose);
  }

  @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsSetPosition() {
    // Too few arguments in setPosition 
    double [] pose = {0, 0, 0};
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
    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
  
    double [] newPose = {0};
    gv.setPosition(newPose);
  }

  @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsSetVelocity() {
    // Too many arguments in setVelocity 
    double [] pose = {0, 0, 0};
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
    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
  
    double [] newVel = {0, 0, 0, 0};
    gv.setVelocity(newVel);
  }

  @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsSetVelocity() {
    // Too few arguments in setVelocity
    double [] pose = {0, 0, 0};
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
    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
  
    double [] newVel = {0};
    gv.setVelocity(newVel);
  }

  // Test get/set Position/Velocity at all legal position bounds

  //Test speed range
  @Test(expected=IllegalArgumentException.class)
  public void makeLowS(){
  //Put an invalid low s value in. Should throw an exception
	  double [] pose = {0, 0, 0};
	    double input_speed = -1;
	    int start_x = 50;
	    int teamID = 1;
	    int legID = 1;
	    double s = 0;
	    boolean hasBaton = false;
	    boolean justRan = false;
	    boolean justPassed = false;
	    boolean doneRunning = false;
	    boolean hasWon = false;
	    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
}
	
@Test(expected=IllegalArgumentException.class)
  public void makeHighS(){
  //Put an invalid high s value in. Should throw an exception
	  double [] pose = {0, 0, 0};
	    double input_speed = 3;
	    int start_x = 50;
	    int teamID = 1;
	    int legID = 1;
	    double s = 0;
	    boolean hasBaton = false;
	    boolean justRan = false;
	    boolean justPassed = false;
	    boolean doneRunning = false;
	    boolean hasWon = false;
	    Runner gv = new Runner(pose, input_speed, hasBaton, start_x, justRan, teamID, legID, s, justPassed, doneRunning);
}

  

  
  public static void main(String[] args){
    JUnitCore.main(TestRunner.class.getName());
  }
}
