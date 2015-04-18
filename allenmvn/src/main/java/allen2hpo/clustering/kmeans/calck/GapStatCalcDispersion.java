package allen2hpo.clustering.kmeans.calck;

import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;

public class GapStatCalcDispersion implements Callable<Double>{

	private final Matrix m;
	private final DistComputable distCalc;
	
	GapStatCalcDispersion(Matrix data, DistComputable distCalc){
		this.m = data;
		this.distCalc = distCalc;
	}
	
	@Override
 	public Double call() throws Exception {

		float sumOfPairwiseDistanceForClst = 0;
		
		//	Calculate distance of every row to every other row and add to
		//	sum of pairwise distance.
	
		for(int i=0; i<this.m.getRowSize(); i++)
		{
			for(int j=0; j<this.m.getRowSize(); j++)
			{
				sumOfPairwiseDistanceForClst += 
				distCalc.calculateProximity(this.m.getRowAtIndex(i),
					this.m.getRowAtIndex(j));
			}
		}
		return (1.0/(this.m.getRowSize()))*sumOfPairwiseDistanceForClst;
    }
}