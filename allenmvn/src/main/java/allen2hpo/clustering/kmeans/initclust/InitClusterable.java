package allen2hpo.clustering.kmeans.initclust;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;

/**
*	Interface to be used by Kmeans clustering
*	Given a dataset, classes implementing this class must return
*	k data points to use as cluster prototypes
*	@author Alex Hartenstein
*/

public interface InitClusterable {

	public void initClusters(int k, Matrix m, DistComputable distCalc);

	public double[][] getClusterPrototypes();

}
