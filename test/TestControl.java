
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class TestControl {	
  @Test(expected=IllegalArgumentException.class)
    public void makeLowS(){
    //Put an invalid low s value in. Should throw an exception
      new Control(-1,0);
  }
	
  @Test(expected=IllegalArgumentException.class)
    public void makeHighS(){
    //Put an invalid low s value in. Should throw an exception
      new Control(2.5, 50);
  }

  
  @Test(expected=IllegalArgumentException.class)
  public void makeT(){
  //Put an invalid t value in. Should throw an exception
    new Control(1, 50);
}
  
  public static void main(String[] args){
    JUnitCore.main(TestControl.class.getName());
  }
}
