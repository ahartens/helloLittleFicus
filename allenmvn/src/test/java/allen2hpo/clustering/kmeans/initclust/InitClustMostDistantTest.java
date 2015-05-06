package allen2hpo.clustering.kmeans.initclust;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.matrix.Matrix;

public class InitClustMostDistantTest{
    
    static Matrix evenMatrix = null;
	
	@BeforeClass public static void setUpClass() {
        double[][] s = {{60,10},{60,10},{60,10},{60,10},{60,10},{60,10},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{30,0},{40,40},{40,40},{40,40},{40,40},{40,40},{40,40},{40,40},{40,40},{40,40}};
        evenMatrix = new Matrix(s);
    }

	@Test
	public void testInit(){
		DistComputable distCalc = new DistEuclidean();
		InitClusterable initClusters = new InitClustMostDistant();
		initClusters.initClusters(3,evenMatrix,distCalc);
		double[][] clusterPrototypes = initClusters.getClusterPrototypes();
		double [][] actualClusters = {{60,10},{30,0},{40,40}};
		
   		//  Check that actual and calculated clusters equal
        //  Kmeans clusters may be in different order than 'actualClusters' listed order
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
