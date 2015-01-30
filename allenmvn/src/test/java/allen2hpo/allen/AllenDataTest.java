package allen2hpo.allen;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


import allen2hpo.matrix.*;
import allen2hpo.clustering.*;
import allen2hpo.allen.*;

public class AllenDataTest{

	@Test
	public void getGeneNamesTest(){

		///READ ALL DATA
		AllenData mngr = new AllenData("/Users/ahartens/Desktop/AllenTest",100);


		///INITIALIZE KMEANS OBJECT AND CLUSTER DATA
		GapStat gap = new GapStat(mngr.getData());
		Kmeans kmeans = new Kmeans(mngr.getData(),gap.getK());
		kmeans.beginClustering(10);


		///GET GENE NAMES FOR CLUSTERS
		String[][] clusters = mngr.getGeneClusters(kmeans.getClusterIndices());


		///PRINT CLUSTERS IN TERMINAL
		for(int i =0;i<clusters.length;i++){
			System.out.printf("Cluster %d",i);
			for(int j=0; j<clusters[i].length-1; j++){
				System.out.printf(" %s,",clusters[i][j]);
			}
			System.out.printf(" %s,",clusters[i][clusters[i].length-1]);
			System.out.printf("\ncount of : %d\n\n",clusters[i].length);
		}


		///WRITE FILE
		/*FileWriter writer = new FileWriter();
		writer.createFileWithName("/Users/ahartens/Desktop/clusters.csv");
		for(int i =0;i<clusters.length;i++){
			for(int j=0; j<clusters[i].length-1; j++){
				writer.writeString(clusters[i][j]);
				writer.writeDelimit();
			}
			writer.writeString(clusters[i][clusters[i].length-1]);
			writer.writeNextLine();
		}
		writer.closeFile();*/


		///PRINT CLUSTER PROTOTYPES
		/*double [][] protos = kmeans.getClusterPrototypes();
		System.out.println("CLUSTER PROTOTYPES:");
		for(int i=0; i<protos.length; i++){
			System.out.printf("%d HAS THE PROTOTYPE : \n",i);
			for(int j=0; j<protos[0].length; j++){
				System.out.printf("(%d) : %.0f\t",j,protos[i][j]);

			}
			System.out.printf("\n\n\n\n\n");
		}*/
	}

}
