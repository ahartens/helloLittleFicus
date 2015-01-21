package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

interface InitClusterable {
	
	double[][] initClusters(Matrix m, int k);

}