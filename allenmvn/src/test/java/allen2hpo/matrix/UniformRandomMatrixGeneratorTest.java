package allen2hpo.matrix;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;




public class UniformRandomMatrixGeneratorTest {


	///CONSTRUCTOR TESTING
	@Test
	public void UniformRandomGeneratorTest(){
		double [][] d = {{1,2,3,4,5,6},{7,8,9,10,11,12},{18,17,16,15,14,13},{12,11,10,9,8,1}};
		Matrix m = new Matrix(d);
		UniformRandomMatrixGenerator g = new UniformRandomMatrixGenerator(m);


		double[] colMin = {1,2,3,4,5,1};
		double[] colMax = {18,17,16,15,14,13};

		//double[] rowMin = {1,7,13,1};
		//double[] rowMax = {6,12,18,12};

		int iter = 1;
		
		for(int x = 0; x<iter; x++){
			Matrix r = g.generateUniformRand();

			for (int i=0; i<d.length; i++){
				for (int j=0; j<d[0].length; j++){
					Assert.assertTrue(r.getValueAtIndex(i,j)>colMin[j]&&r.getValueAtIndex(i,j)<colMax[j]);
				}
			}
		}
	}
}