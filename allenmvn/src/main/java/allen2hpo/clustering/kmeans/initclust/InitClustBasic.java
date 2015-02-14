package allen2hpo.clustering.kmeans.initclust;

import allen2hpo.matrix.Matrix;
import java.util.Random;


/**
*	Given a data set, randomly selects k data points as cluster seeds
*	Most basic implementation for initializaing cluster prototypes
*	Implements InitClusterable interface to return clusters
*	@author Alex Hartenstein
*/

public class InitClustBasic implements InitClusterable{


	public double[][] initClusters(Matrix m, int k){
		double[][] cp = new double[k][m.getColumnSize()];

		Random rand = new Random();

	    ///INIT K CLUSTER SEEDS
	    for (int i = 0; i<k; i++) {
	        ///RANDOMLY SELECT A ROW FROM DATA TABLE AND SET THAT AS A CLUSTER POINT
	       	int randVal = (int)(m.getRowSize() * rand.nextDouble());
	        for (int j = 0; j< m.getColumnSize(); j++) {
	            cp[i][j] = m.getValueAtIndex(randVal,j);
	        }
	    }

		return cp;
	}

}
