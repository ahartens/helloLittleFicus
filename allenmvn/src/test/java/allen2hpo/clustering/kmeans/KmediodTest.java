package allen2hpo.clustering.kmeans;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.initclust.*;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.calck.GapStat;

import java.util.ArrayList;
public class KmediodTest{




    static Matrix m = null;

    static Matrix bigMatrix=null;
    static Matrix evenMatrix = null;
    @BeforeClass public static void setUpClass() {
    	double[][] b = {{13,90,70,150},{8,7,4,6},{6,4,0,3}};
    	m= new Matrix(b);
    	double[][] d = {{41.000000,45.000000},{39.000000,44.000000},{42.000000,43.000000},{44.000000,43.000000},{10.000000,42.000000},{38.000000,42.000000},{8.000000,41.000000},{41.000000,41.000000},{13.000000,40.000000},{45.000000,40.000000},{7.000000,39.000000},{38.000000,39.000000},{42.000000,39.000000},{9.000000,38.000000},{12.000000,38.000000},{19.000000,38.000000},{25.000000,38.000000},{6.000000,37.000000},{13.000000,35.000000},{9.000000,34.000000},{12.000000,34.000000},{32.000000,27.000000},{26.000000,25.000000},{39.000000,24.000000},{34.000000,23.000000},{37.000000,23.000000},{22.000000,22.000000},{38.000000,21.000000},{35.000000,20.000000},{31.000000,18.000000},{26.000000,16.000000},{38.000000,13.000000},{29.000000,11.000000},{34.000000,11.000000},{37.000000,10.000000},{40.000000,9.000000},{42.000000,9.000000}};
    	bigMatrix=new Matrix(d);


        double[][] s = {{10000,10},{10000,10},{10000,10},{10000,10},{10000,10},{10000,10},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{40,800},{40,800},{40,800},{40,800},{40,800},{40,800},{40,800},{40,800},{40,800}};
        evenMatrix = new Matrix(s);
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
       // GapStat gap = new GapStat(bigMatrix,dc,ic);

        /*
        *   Do kmeans clustering with k found by gap stat and get resulting clusters
        */
     //   Kmediod km = new Kmediod(bigMatrix,gap.getK(),dc,ic);
        Kmediod km = new Kmediod(evenMatrix,3,dc,ic);

        km.doClustering();
        double[][] clusterPrototypes = km.getClusterPrototypes();


        
        //   Best cluster centroids
        double[][] actualClusters = {{10000,10},{30,0},{40,800}};
                
        //  Check that actual and calculated clusters equal
        //  Kmeans clusters may be in different order than 'actualClusters' listed order
        //  So check if first value is equal then compare row, otherwise skip row
        for(int i = 0;i<clusterPrototypes.length; i++)
        {
            for(int j =0; j<actualClusters.length;j++)
            {
                if (Math.abs(clusterPrototypes[i][0]-actualClusters[j][0])<=.2) {
                    System.out.printf("{");

                    for(int x =0; x<clusterPrototypes[0].length;x++)
                    {
                        System.out.printf("%f,",clusterPrototypes[i][x]);

                        Assert.assertEquals(clusterPrototypes[i][x],actualClusters[j][x],.2);
                    }
                    System.out.printf("},");

                }
            }
        }
        
        ArrayList<ArrayList<Integer>> clusterIndices = km.getClusterIndices();
        for (int i =0; i<clusterIndices.size(); i++){
            System.out.printf("[%d : %d]   ",i,clusterIndices.get(i).size());
        }
        System.out.printf("\n");
        for(int i = 0;i<clusterIndices.size(); i++)
        {
            ArrayList<Integer> rowList = clusterIndices.get(i);
            System.out.printf("///////CLUSTER %d///////",i);

            for(int j =0; j<rowList.size();j++)
            {
                System.out.printf("[ ");
                for(int z = 0; z<evenMatrix.getColumnSize(); z++){
                    System.out.printf("%.0f ",evenMatrix.getValueAtIndex(rowList.get(j),z));
                }
                System.out.printf("]  ");
            }
            System.out.printf("\n");
        }
    }


    /**
    *   Check clustering of big matrix using gap stat
    */
    @Test
    public void testClustering2(){

        DistComputable dc = new DistEuclidean();
        InitClusterable ic = new InitClustMostDistant();

        /*
        *   Do gap stat to find k. should return 3
        */
       // GapStat gap = new GapStat(bigMatrix,dc,ic);

        /*
        *   Do kmeans clustering with k found by gap stat and get resulting clusters
        */
     //   Kmediod km = new Kmediod(bigMatrix,gap.getK(),dc,ic);
               Kmediod km = new Kmediod(bigMatrix,3,dc,ic);

        km.doClustering();
        double[][] clusterPrototypes = km.getClusterPrototypes();


        
        //  Best cluster centroids
        

        double[][] actualClusters = {{12.000000,38.000000},{35.000000,20.000000},{41.000000,41.000000}};
        //   Check that actual and calculated clusters equal
        
       for(int i = 0;i<clusterPrototypes.length; i++)
        {
            for(int j =0; j<actualClusters.length;j++)
            {
                if (Math.abs(clusterPrototypes[i][0]-actualClusters[j][0])<=.2) {
                    System.out.printf("{");

                    for(int x =0; x<clusterPrototypes[0].length;x++)
                    {
                        System.out.printf("%f,",clusterPrototypes[i][x]);

                        Assert.assertEquals(clusterPrototypes[i][x],actualClusters[j][x],.2);
                    }
                    System.out.printf("},");

                }
            }
        }

        ArrayList<ArrayList<Integer>> clusterIndices = km.getClusterIndices();
        for (int i =0; i<clusterIndices.size(); i++){
            System.out.printf("[%d : %d]   ",i,clusterIndices.get(i).size());
        }
        System.out.printf("\n");
        for(int i = 0;i<clusterIndices.size(); i++)
        {
            ArrayList<Integer> rowList = clusterIndices.get(i);
            System.out.printf("///////CLUSTER %d///////",i);

            for(int j =0; j<rowList.size();j++)
            {
                System.out.printf("[ ");
                for(int z = 0; z<bigMatrix.getColumnSize(); z++){
                    System.out.printf("%.0f ",bigMatrix.getValueAtIndex(rowList.get(j),z));
                }
                System.out.printf("]  ");
            }
            System.out.printf("\n");
        }
    }
   

}
