
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class TestControl {	
  @Test(expected=IllegalArgumentException.class)
    public void makeLowS(){
    //Put an invalid low s value in. Should throw an exception
      new Control(.3,0);
  }
	
  @Test(expected=IllegalArgumentException.class)
    public void makeHighS(){
    //Put an invalid low s value in. Should throw an exception
      new Control(.3, 50);
  }
  
  public static void main(String[] args){
    JUnitCore.main(TestControl.class.getName());
  }
}
