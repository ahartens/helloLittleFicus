package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;
import java.util.Random;

public class InitClustBasic implements InitClusterable{


	public double[][] initClusters(Matrix m, int k){
		double[][] cp = new double[k][m.getColumnSize()];

		Random rand = new Random();

	    ///INIT K CLUSTER SEEDS
	    for (int i = 0; i<k; i++) {
	        ///FOR NOW JUST TAKE FIRST 3 POINTS OF DATA TABLE
	       	int randVal = (int)(m.getRowSize() * rand.nextDouble());
	        for (int j = 0; j< m.getColumnSize(); j++) {
	            cp[i][j] = m.getValueAtIndex(randVal,j);
				System.out.printf("rand falue is %d : %.5f\n",randVal, cp[i][j]);

	        }
			System.out.printf("\n\n");
	    }

		for (int i = 0; i<k; i++) {
			///FOR NOW JUST TAKE FIRST 3 POINTS OF DATA TABLE

			for (int j = 0; j< m.getColumnSize(); j++) {
				System.out.printf("%.0f \t",cp[i][j]);
			}
			System.out.printf("\n");

		}


		return cp;
	}

}
