package allen2hpo.clustering.kmeans.distance;


/**
*	Basic implementation of DistComputable interface
*	Takes two points and finds the distance between them in euclidean space
*	@return double distance between two points
*	@author Alex Hartenstein
*/

public class DistEuclidean implements DistComputable{

	public double calculateProximity(double[] p1, double[] p2){

		double dist = 0;

		//Distance for each dimension calculated
		for(int i=0;i<p1.length;i++){
	    	dist +=  Math.pow(p1[i] - p2[i],2.0);
        }
		
        //Calculate final distance
        return Math.sqrt(dist);
	}

}
