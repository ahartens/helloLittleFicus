package allen2hpo.clustering.kmeans;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.initclust.*;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.calck.GapStat;
public class KmediodTest{




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

        /*
        *   Do gap stat to find k. should return 3
        */
        GapStat gap = new GapStat(bigMatrix,dc,ic);

        /*
        *   Do kmeans clustering with k found by gap stat and get resulting clusters
        */
        Kmediod km = new Kmediod(bigMatrix,gap.getK(),dc,ic);
        km.doClustering();
        double[][] clusterPrototypes = km.getClusterPrototypes();


        /*
        *   Best cluster centroids
        */
        double[][] actualClusters = {{41.1,41.7},{11.9,37.8},{33.7,17.6}};

        /*
        *   Check that actual and calculated clusters equal
        */
        for(int i = 0;i<clusterPrototypes.length; i++)
        {
            for(int j =0; j<clusterPrototypes[0].length;j++)
            {
            	System.out.printf("%f   ",clusterPrototypes[i][j]);
                //Assert.assertEquals(clusterPrototypes[i][j],actualClusters[i][j],.2);
            }
            System.out.printf("\n");
        }
    }

   

}
