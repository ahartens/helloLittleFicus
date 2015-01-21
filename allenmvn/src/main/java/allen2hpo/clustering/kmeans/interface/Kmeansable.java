package allen2hpo.clustering;



/**
*	Kmeansable can be used in any class which wishes to perform Kmeans clustering.
*	To make a class that performs K means :
*	1) Class must include a KmeansObject as a field
*	2) Class must implement the Kmeansable interface
*	3) In Kmeansable methods, calculate corresponding kmeans values (k value, seed clusters, distance equation)
*	4) Set calculated value in KmeansObject
*	5) Call beginClustering()
*/
interface Kmeansable{
	void setK();			
	void setInitClusters(); 					
	void beginClustering(int i);
	void setDistCalc(DistComputable d);

}