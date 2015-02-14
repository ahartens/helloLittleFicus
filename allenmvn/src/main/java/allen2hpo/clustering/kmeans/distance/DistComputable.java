package allen2hpo.clustering.kmeans.distance;


/**
*	Interface declaration of Distance calculation that can be used
*	in Kmeans algorithm to determine distance between two points
*	Classes that implement this interface must perform a proximity calculation
*	between two points and return the distance in calculate proximity
*	@author Alex Hartenstein
*/

public interface DistComputable{
	double calculateProximity(double[] p1, double[] p2);
}
