import java.lang.IllegalArgumentException;

public class Control
{
  private double s;
  private double theta;
  
  public Control (double s, double theta){
    if (s < 5 || s > 10) //Check to make sure s is in range.
      throw new IllegalArgumentException("S out of range");
   /* if (theta < -Math.PI || theta >= Math.PI) //Check to make sure theta is in range.
      throw new IllegalArgumentException("Omega out of range");*/
    
    this.s = s;
    this.theta = theta;    
  }
  
  public double getSpeed() {
    return s;
  }

  public double getAngle() {
    return theta;
  }  
}
