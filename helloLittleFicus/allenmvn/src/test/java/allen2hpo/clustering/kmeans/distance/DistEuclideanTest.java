package allen2hpo.clustering;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


public class DistEuclideanTest{
	
	@Test
	public void testDist(){
		DistEuclidean dist = new DistEuclidean();
		double [] d1 = {1d,12d,3d};
		double [] d2 = {10d,4d,100d};
		double distance = dist.calculateProximity(d1,d2);
		Assert.assertEquals(distance,Math.sqrt(9554),.01);	
	}


}