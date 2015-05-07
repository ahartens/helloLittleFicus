package allen2hpo.clustering.kmeans.algorithms.kmeans_parallel;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.initclust.*;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.calck.GapStatParallel;
public class KmeansParallelTest{




    static Matrix m = null;

    static Matrix bigMatrix=null;

    @BeforeClass public static void setUpClass() {
    	double[][] b = {{13,90,70,150},{8,7,4,6},{6,4,0,3}};
    	m= new Matrix(b);
    	double[][] d = {{41.000000,45.000000},{39.000000,44.000000},{42.000000,43.000000},{44.000000,43.000000},{10.000000,42.000000},{38.000000,42.000000},{8.000000,41.000000},{41.000000,41.000000},{13.000000,40.000000},{45.000000,40.000000},{7.000000,39.000000},{38.000000,39.000000},{42.000000,39.000000},{9.000000,38.000000},{12.000000,38.000000},{19.000000,38.000000},{25.000000,38.000000},{6.000000,37.000000},{13.000000,35.000000},{9.000000,34.000000},{12.000000,34.000000},{32.000000,27.000000},{26.000000,25.000000},{39.000000,24.000000},{34.000000,23.000000},{37.000000,23.000000},{22.000000,22.000000},{38.000000,21.000000},{35.000000,20.000000},{31.000000,18.000000},{26.000000,16.000000},{38.000000,13.000000},{29.000000,11.000000},{34.000000,11.000000},{37.000000,10.000000},{40.000000,9.000000},{42.000000,9.000000}};
    	bigMatrix=new Matrix(d);


    }


    /**
    *   Check clustering of big matrix using gap stat
    */
    @Test
    public void testClustering(){

        DistComputable dc = new DistEuclidean();
        InitClusterable ic = new InitClustMostDistant();

        
        //   Do gap stat to find k. should return 3
         /*
        *   Do gap stat to find k. should return 3
        */
        int repeats = 10;
        int sum = 0;
        for (int i = 0; i<repeats; i++){
            GapStatParallel gap = new GapStatParallel(bigMatrix,dc,ic,2);
            sum += gap.getK();
        }
        int finalGap = (int)Math.floor(sum/repeats);
        System.out.println("FLOOR WAS : "+finalGap);
        Assert.assertEquals(3,finalGap);

        
        //   Do kmeans clustering with k found by gap stat and get resulting clusters
        
        KmeansParallel km = new KmeansParallel(bigMatrix,finalGap,dc,ic);
        km.doClustering();
        double[][] clusterPrototypes = km.getClusterPrototypes();


        
                
        //   Best cluster centroids
        
        double[][] actualClusters = {{41.1,41.7},{11.9,37.8},{33.7,17.6}};

        
        //   Check that actual and calculated clusters equal
        //   Kmeans clusters may be in different order than 'actualClusters' listed order
        //  So check if first value is equal then compare row, otherwise skip row
        for(int i = 0;i<clusterPrototypes.length; i++)
        {
            for(int j =0; j<actualClusters.length;j++)
            {
                if (Math.abs(clusterPrototypes[i][0]-actualClusters[j][0])<=.2) {
                    for(int x =0; x<clusterPrototypes[0].length;x++)
                    {
                        Assert.assertEquals(clusterPrototypes[i][x],actualClusters[j][x],.2);
                    }
                }
            }
        }
    }

  
}
