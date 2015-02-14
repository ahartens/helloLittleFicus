package allen2hpo.clustering.kmeans.calck;


/**
*	GetKable interface declaration.
*	Use to create classes that are able to find optimal k values for given data
*	eg. gap stat, or elbow method could implement getkable interface
*	@author Alex Hartenstein
*/

interface GetKable{
	int getK();
}
