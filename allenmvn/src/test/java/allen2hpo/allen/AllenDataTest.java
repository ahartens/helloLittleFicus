package allen2hpo.allen;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.*;
import allen2hpo.allen.*;
public class AllenDataTest{

	@Test
	public void getGeneNamesTest(){


		AllenData mngr = new AllenData("/Users/ahartens/Desktop/AllenTest",63000);
		

		KmeansBasic kmeans = new KmeansBasic(mngr.getData());

		String[][] clusters = mngr.getGeneClusters(kmeans.getClusterIndices());

		for(int i =0;i<clusters.length;i++){
			System.out.printf("Cluster %d",i);
			for(int j=0; j<clusters[i].length; j++){
				System.out.printf(" %s,",clusters[i][j]);
			}
			System.out.printf("\ncount of : %d\n\n",clusters[i].length);
		}

	}

}
	