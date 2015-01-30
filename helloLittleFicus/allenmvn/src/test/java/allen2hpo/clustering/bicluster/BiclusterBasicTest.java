package allen2hpo.clustering;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;

public class BiclusterBasicTest {
	@Test
	public void TestHCalculation(){
		double [][] d = {{1,2,3,4,5,6},{23,100,10,3,4,3},{1,3,4,5,6,1},{1,45,5,22,1,14}};
		Matrix m = new Matrix(d);

		BiclusterBasic biclust = new BiclusterBasic(m);
		//System.out.println("FINAL VALUE FOR H :"+biclust.calcMeanSquaredResidue(m));

	}
}

