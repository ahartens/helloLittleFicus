package allen2hpo.matrix;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

public class DistanceMatrixTest{

	@Test
	public void testDistanceMatrix(){
		double[][] d = { {1d,1d},{2d,2d},{4d,1d}};
		Matrix m = new Matrix(d);


		double[][] correct = {{0,1.4142135,3},{1.4142135,0,2.236067},{3.0,2.236067,0}};

		DistanceMatrix sm = new DistanceMatrix(m);

		//Get sum of all distances
		double sumPairwise = 0;
		for (int i=0;i<sm.getRowSize();i++){
			for(int j=0;j<=i;j++){
				//Sum Pairwise distance
				sumPairwise += correct[i][j];

				//check that distance matrix value correct
				Assert.assertEquals(sm.getValueAtIndex(i,j),correct[i][j],.01);
			}
		}

		//Check that pairwise distance is correct
		Assert.assertEquals(sumPairwise,sm.getSumOfPairwiseDistances(),.001);
	}
}
