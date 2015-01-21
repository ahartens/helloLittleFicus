package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class InitClustBasic implements InitClusterable{
	public double[][] initClusters(Matrix m, int k){
		double[][] cp = new double[k][m.getColumnSize()];
	    ///INIT K CLUSTER SEEDS
	    for (int i = 0; i<k; i++) {
	        ///FOR NOW JUST TAKE FIRST 3 POINTS OF DATA TABLE
	        for (int j = 0; j< m.getColumnSize(); j++) {
	            cp[i][j] = m.getValueAtIndex(i,j);
	        }
	    }

	    return cp;
	}
}