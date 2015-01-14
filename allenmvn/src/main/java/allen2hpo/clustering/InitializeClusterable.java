package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

interface InitializeClusterable {
	
	double[][] initClusters(Matrix m, int k);

}