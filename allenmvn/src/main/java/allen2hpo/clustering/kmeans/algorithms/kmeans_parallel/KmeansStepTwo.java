package allen2hpo.clustering.kmeans.algorithms.kmeans_parallel;

import java.util.concurrent.Callable;
import java.util.ArrayList;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;

public class KmeansStepTwo implements Callable<double[]>{
	
	private final Matrix m;

	private final ArrayList<Integer> indices;
	
	KmeansStepTwo(Matrix data, ArrayList<Integer>indices){
		this.m = data;
		this.indices = indices;
	}
	
	@Override
 	public double[] call() throws Exception {
 		//   Iterate through each row of expression values (genes)
        //   Add (each dimension of) expression value to cluster sum (at index of
        //   cluster to which it belongs)
        
        double [] clusterSums = new double[this.m.getColumnSize()];
        //  For each data point assigned to cluster i
        for (int z = 0; z<this.indices.size(); z++)
        {
            //  Go through each column and calculate average
            for (int j = 0; j<this.m.getColumnSize();j++)
            {

                clusterSums[j] += this.m.getValueAtIndex(this.indices.get(z),j);
            }
        }

        double[] prototype = new double[this.m.getColumnSize()];
     	//   Find mean of every dimension

        for (int j=0; j<this.m.getColumnSize(); j++) 
        {
            prototype[j] = clusterSums[j]/this.indices.size();
        }
        return prototype;
 	}
 }