package allen2hpo.clustering;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.Kmeans;


public class KmeansTest{




    static Matrix m = null; 

    static Matrix bigMatrix=null;

    @BeforeClass public static void setUpClass() {
	double[][] b = {{13,90,70,150},{8,7,4,6},{6,4,0,3}};
	m= new Matrix(b);
	double[][] d = {{41.000000,45.000000},{39.000000,44.000000},{42.000000,43.000000},{44.000000,43.000000},{10.000000,42.000000},{38.000000,42.000000},{8.000000,41.000000},{41.000000,41.000000},{13.000000,40.000000},{45.000000,40.000000},{7.000000,39.000000},{38.000000,39.000000},{42.000000,39.000000},{9.000000,38.000000},{12.000000,38.000000},{19.000000,38.000000},{25.000000,38.000000},{6.000000,37.000000},{13.000000,35.000000},{9.000000,34.000000},{12.000000,34.000000},{32.000000,27.000000},{26.000000,25.000000},{39.000000,24.000000},{34.000000,23.000000},{37.000000,23.000000},{22.000000,22.000000},{38.000000,21.000000},{35.000000,20.000000},{31.000000,18.000000},{26.000000,16.000000},{38.000000,13.000000},{29.000000,11.000000},{34.000000,11.000000},{37.000000,10.000000},{40.000000,9.000000},{42.000000,9.000000}};
	bigMatrix=new Matrix(d);

    }


    @Test
    public void testInitClusters(){
	Kmeans km = new Kmeans(m);
	int k = km.getK();
	Assert.assertEquals(1,k);
    }

    @Test public void testInitClusters2(){
	Kmeans km = new Kmeans(m,42);
	int k = km.getK();
	Assert.assertEquals(42,k);
    }

    @Test public void testSimpleClustering() {
	Kmeans km = new Kmeans(m,2);
	km.beginClustering();
	int[] idx = km.getClusterAssignments();
	int cluster1 = idx[0];
	int cluster2 = idx[1];
	int cluster3 = idx[2];
	Assert.assertEquals(cluster2,cluster3);
	Assert.assertNotEquals(cluster1,cluster3);
	
	
    }

      /* 
       

        GapStat gap = new GapStat(m);

        Kmeans km = new Kmeans(m,gap.getK());
        km.beginClustering();
        double [][] protos = km.getClusterPrototypes();

        for(int i=0; i<protos.length; i++){
            for(int j=0; j<protos[0].length; j++){
                System.out.printf("%.0f\t",protos[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.println("THIS WAS THE THEW PROTOTYPE : "+ gap.getK());


*/

   


}
