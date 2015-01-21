package allen2hpo.clustering;

public class DistEuclidean implements DistComputable{

	public double calculateProximity(double[] p1, double[] p2){
		double dist = 0;
		
		for(int i=0;i<p1.length;i++){
	                ///DISTANCE OF EACH DIMENSION TO CORRESPONDING DIMENSION OF CLUSTER MEAN.
	               dist +=  Math.pow(p1[i] - p2[i],2.0);
        }
        ///CALCULATE SSE AND SAVE IN ARRAY
        return Math.sqrt(dist);
	}

}