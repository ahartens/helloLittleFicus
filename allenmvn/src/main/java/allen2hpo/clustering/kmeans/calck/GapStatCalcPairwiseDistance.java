package allen2hpo.clustering.kmeans.calck;

import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;

/**
*	Future object created by gap stat in order to calculated pairwise distance 
*	of given matrix.
*/
public class GapStatCalcPairwiseDistance implements Callable<Double>{

	private final Matrix m;
	private final DistComputable distCalc;
	private final int start;
	private final int end;

	GapStatCalcPairwiseDistance(Matrix data, 
								DistComputable distCalc,
								int start,
								int end){
		this.m = data;
		this.distCalc = distCalc;
		this.start = start;
		this.end = end;
	}
	
	@Override
 	public Double call() throws Exception {

		double sumOfPairwiseDistanceForClst = 0;
		
		//	Calculate distance of every row to every other row and add to
		//	sum of pairwise distance.
	
		for (int i=this.start; i<this.end; i++) 
        {

			for (int j=this.start; j<this.end; j++) 
        	{

				sumOfPairwiseDistanceForClst += 
				distCalc.calculateProximity(this.m.getRowAtIndex(i),
					this.m.getRowAtIndex(j));
			}
		}
		return sumOfPairwiseDistanceForClst;
    }
}