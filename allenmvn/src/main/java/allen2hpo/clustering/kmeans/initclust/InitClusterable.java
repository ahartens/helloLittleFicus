package allen2hpo.clustering.kmeans.initclust;

import allen2hpo.matrix.Matrix;


/**
*	Interface to be used by Kmeans clustering
*	Given a dataset, classes implementing this class must return
*	k data points to use as cluster prototypes
*	@author Alex Hartenstein
*/

public interface InitClusterable {

	double[][] initClusters(Matrix m, int k);

}
