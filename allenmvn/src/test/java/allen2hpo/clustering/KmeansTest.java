package allen2hpo.clustering;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;

public class KmeansTest{
	
	@Test
	public void testInitClusters(){
		double[][] d = { {1d,1d},{2d,2d},{4d,1d}};
		Matrix m = new Matrix(d);

		Kmeans km = new Kmeans(m,3);
	
	}


}