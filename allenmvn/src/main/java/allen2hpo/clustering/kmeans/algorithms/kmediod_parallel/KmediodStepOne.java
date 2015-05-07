package allen2hpo.clustering.kmeans.algorithms.kmediod_parallel;

import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

import allen2hpo.clustering.kmeans.algorithms.kmeans_parallel.KmeansStepOneReturnObj;
import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;

public class KmediodStepOne implements Callable<KmeansStepOneReturnObj>{
	private final int start;
	private final int end;
	private final int k;
	private final Matrix distanceMatrix;

    /** Cluster prototype index. row index where cluster prototypes can be found in distance matrix */
    private final int[] cpi;
	private final int[] threadPreviousClusterAssignments;
	
	KmediodStepOne(int start, int end, int k, int[] previousClusterAssignments, Matrix distanceMatrix, int[] cpi){
		this.start = start;
		this.end = end;
		this.k = k;
        this.distanceMatrix = distanceMatrix;
        this.cpi = cpi;
        this.threadPreviousClusterAssignments = previousClusterAssignments;
	}

	@Override
 	public KmeansStepOneReturnObj call() throws Exception {
    	
    	//   Init array where the calculated distances of one row to every 
        //   cluster will be stored.
        double[] allDists = new double[this.k];

        ArrayList<ArrayList<Integer>> threadClusterAssignments = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i<this.k; i++){
        	ArrayList<Integer> clusterAssignments = new ArrayList<Integer>();
        	threadClusterAssignments.add(clusterAssignments);
        }
        
        //   Reinit arrays to store size of clusters (cs) sum of squared error
        //  (sse)
        int[] cs = new int[this.k];
        double[] sses = new double[this.k];
        int[] ci = new int[end-start];
        int[] ca = new int[this.threadPreviousClusterAssignments.length];
        //   Init counter of points which do not change cluster assignment
        int countUnmoved = 0;

        int count = 0;
        //   Place each row(gene) in the nearest cluster
        for (int i=this.start; i<this.end; i++) 
        {

            //   For each cluster prototype, calculate distance to current row 
            for (int j=0; j<this.k; j++) 
            {
                allDists[j] = this.distanceMatrix.getValueAtIndex(i,this.cpi[j]);

            }

            //   Find index corresponding to minimum distance (this is the 
            //   cluster assignment) and store in cluster indices array (ci) at 
            //   index i (current row in expression data)
            int indexOfMinDist = 0;
            double minDist = allDists[0];

            for (int j = 1; j<this.k; j++) 
            {
                if (allDists[j] < minDist) 
                {
                    minDist = allDists[j];
                    indexOfMinDist = j;
                }
            }

            //   Check if just found cluster assignment has changed from previous
            //   iteration through all data points. I
            if(this.threadPreviousClusterAssignments[count] == indexOfMinDist)
            {
                countUnmoved ++;
            }
            else
            {
                ca[count] = indexOfMinDist;
            }
            ArrayList<Integer> clusterAssignmentList = threadClusterAssignments.get(indexOfMinDist);
            clusterAssignmentList.add(i);
            //   Increment cluster size variable for found cluster
            cs[indexOfMinDist]+=1;

            //   Add squared error (distance squared) to sse for found cluster 
            sses[indexOfMinDist] += Math.pow(minDist,2);
            count ++;
        }

      
        KmeansStepOneReturnObj results = new KmeansStepOneReturnObj();
        results.setClusterSizeArray(cs);
        results.setSumOfSquareErrorsArray(sses);
        results.setCountUnmoved(countUnmoved);
        results.setClusterAssignments(ca);
        results.setThreadClusterAssignments(threadClusterAssignments);

    	return results;

  	}

}