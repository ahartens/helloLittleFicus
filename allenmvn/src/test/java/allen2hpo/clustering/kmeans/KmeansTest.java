package allen2hpo.clustering.kmeans;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.initclust.*;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.calck.GapStat;
public class KmeansTest{




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
   /* @Test
    public void testClustering(){

        DistComputable dc = new DistEuclidean();
        InitClusterable ic = new InitClustMostDistant();

        //   Do gap stat to find k. should return 3
        GapStat gap = new GapStat(bigMatrix,dc,ic);
        Assert.assertEquals(3,gap.getK());

        //   Do kmeans clustering with k found by gap stat and get resulting clusters
        Kmeans km = new Kmeans(bigMatrix,gap.getK(),dc,ic);
        km.doClustering();
        double[][] clusterPrototypes = km.getClusterPrototypes();


        //   Best cluster centroids
        double[][] actualClusters = {{41.1,41.7},{11.9,37.8},{33.7,17.6}};

        //   Check that actual and calculated clusters equal
        for(int i = 0;i<clusterPrototypes.length; i++)
        {
            for(int j =0; j<clusterPrototypes[0].length;j++)
            {
                Assert.assertEquals(clusterPrototypes[i][j],actualClusters[i][j],.2);
            }
        }
    }

    /*
    *   Check kmeans clustering of small matrix.
    */
   /* @Test 
    public void testSimpleClustering() {
        Kmeans km = new Kmeans(m,2,new DistEuclidean(),new InitClustBasic());
        km.doClustering();
        int[] idx = km.getClusterAssignments();
        int cluster1 = idx[0];
        int cluster2 = idx[1];
        int cluster3 = idx[2];
        Assert.assertEquals(cluster2,cluster3);
        Assert.assertNotEquals(cluster1,cluster3);
    }

    @Test 
    public void testSimpleClusteringWithInitMostDistant() {
        Kmeans km = new Kmeans(m,2,new DistEuclidean(),new InitClustMostDistant());
        km.doClustering();
        int[] idx = km.getClusterAssignments();
        int cluster1 = idx[0];
        int cluster2 = idx[1];
        int cluster3 = idx[2];
        Assert.assertEquals(cluster2,cluster3);
        Assert.assertNotEquals(cluster1,cluster3);
    }

    /*
    *   Check that cluster initialization works
    */
  /*  @Test
    public void testInitClusters(){
        Kmeans km = new Kmeans(m,1,new DistEuclidean(),new InitClustBasic());
        int k = km.getK();
        Assert.assertEquals(1,k);
    }

    @Test 
    public void testInitClusters2(){
        Kmeans km = new Kmeans(m,42,new DistEuclidean(),new InitClustBasic());
        int k = km.getK();
        Assert.assertEquals(42,k);
    }*/
}
