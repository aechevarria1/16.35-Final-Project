import static org.junit.Assert.*;

public class TestSimulator {

    @org.junit.Test
	public void constructor() {
	Simulator sim = new Simulator();
	assertTrue(sim.getCurrentSec() == 0);
    }

    /**
     * Constructs a Simulator Object and tests if the value returned by the
     * function "getCurrentUSec()" in the Simulator class is 0 before method
     * "run()" is called.
     */





    
    public static void main(String[] args){
	org.junit.runner.JUnitCore.main(TestSimulator.class.getName());
    }

    
}
