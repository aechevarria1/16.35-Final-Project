import java.util.List;

public class DeadlockTester {

    /**
     * Checks that the current thread that is trying to access Runner
     * 'gv' method does not hold a different lock except the built-in lock of
     * 'gv'
     * 
     * @param gv
     *            Runner that is calling this method
     * @param s
     *            simulator in which this Runner is added.
     * @return true if it doesn't hold any lock (different from built-in lock of
     *         'gv')
     * @throws DeadLockTesterException
     */

    public static boolean testLock(Runner gv, Simulator s) throws DeadlockTesterException {
	List<Runner> gvList = s.RunnerList;
	for (int i = 0; i < gvList.size(); i++) {
	    if (Thread.holdsLock(gvList.get(i))) {
		System.out.printf("Holds Lock to %d\n", i);
		if (!gvList.get(i).equals(gv)) {
		    throw new DeadlockTesterException();
		}
	    }
	}
	return true;
    }



    // Alternatively, we could use the methods below to ensure that we acquire
    // locks in a specific order (and release in reverse order).

    /**
     * Each vehicle has a distinct Id. The order to get the locks is
     * from lowest to highest. If this ground vehicle has a lower id
     * than the leader, it gets its own lock first and then the
     * leaders lock. Else the order to get the locks will be reversed.
     */

    // public static void lockgvLocks(List<Runner> gvList, int ID) {
    // 	List<Runner> orderedList = sortVehiclesById(gvList);
    // 	for (int i = 0; i < orderedList.size(); i++) {	    
    // 	    orderedList.get(i).getVehicleLock().lock();
    // 	}
    // }

    // /**
    //  * @return an arrayList<Runner> with the same Runners of
    //  *         gvList but ordered by the 'Id' variable of Runner. (From
    //  *         lowest to highest).
    //  */
    // public static List<Runner> sortVehiclesById(List<Runner> gvList) {
    // 	Runner key;
    // 	int i, j;
    // 	for (j = 0 + 1; j <= gvList.size() - 1; j++) {
    // 	    key = gvList.get(j);
    // 	    for (i = j - 1; i >= 0 && key.compareId(gvList.get(i)) < 0; i--) {
    // 		gvList.set(i + 1, gvList.get(i));
    // 	    }
    // 	    gvList.set(i + 1, key);
    // 	}
    // 	return gvList;
    // }

    // /**
    //  * @return an arrayList<Runner> with the same Runners of
    //  *         gvList but ordered by the 'Id' variable of Runner. (From
    //  *         lowest to highest).
    //  */
    // public static List<Runner> reverseSortVehiclesById(List<Runner> gvList) {
    // 	Runner key;
    // 	int i, j;
    // 	for (j = 0 + 1; j <= gvList.size() - 1; j++) {
    // 	    key = gvList.get(j);
    // 	    for (i = j - 1; i >= 0 && key.reverseCompareId(gvList.get(i)) < 0; i--) {
    // 		gvList.set(i + 1, gvList.get(i));
    // 	    }
    // 	    gvList.set(i + 1, key);
    // 	}
    // 	return gvList;
    // }

    // /**
    //  * Unlocks all the 'mygvLocks' lock of each Runner object in the
    //  * gvList.
    //  */
    // public static void unlockgvLocks(List<Runner> gvList, int ID) {
    // 	List<Runner> orderedList = reverseSortVehiclesById(gvList);
    // 	for (int i = 0; i < orderedList.size(); i++) {
    // 	    orderedList.get(i).getVehicleLock().unlock();
    // 	}
    // }

}
