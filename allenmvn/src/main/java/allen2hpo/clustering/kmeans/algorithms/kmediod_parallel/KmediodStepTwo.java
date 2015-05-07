package allen2hpo.clustering.kmeans.algorithms.kmediod_parallel;

import java.util.concurrent.Callable;
import java.util.ArrayList;

import allen2hpo.matrix.Matrix;
import allen2hpo.matrix.DistanceMatrix;

import allen2hpo.clustering.kmeans.distance.*;

public class KmediodStepTwo implements Callable<Integer>{
	
	private final Matrix distanceMatrix;

	private final ArrayList<Integer> indices;
	
	KmediodStepTwo(Matrix distanceMatrix, ArrayList<Integer>indices){
		this.distanceMatrix = distanceMatrix;
		this.indices = indices;
	}
	
	@Override
 	public Integer call() throws Exception { 
        double[] sumD = new double[this.indices.size()];

        //   Sum distances of every other data point in cluster to 

        for(int j = 0; j<this.indices.size(); j++)
        {
            sumD[j] = 0;

            for(int y = 0; y<this.indices.size(); y++)
            {
                sumD[j] += this.distanceMatrix.getValueAtIndex(this.indices.get(j),this.indices.get(y));
            }
        }


        //   Find minimum distance. Assign cluster centroid to that point

        double minD = sumD[0];
        int indexMinD = 0;
        for(int j = 1; j<sumD.length; j++){
            if(sumD[j]<minD){
                indexMinD = j;
                minD = sumD[j];
            }
        }
        return this.indices.get(indexMinD);
 	}
 }