package allen2hpo.clustering;


/**
*	<p>
*	Interface that clustering algorithms must implement in order to be 
*	compatible with {@link allen2hpo.clustering ClusteringMngr}.
*	<br>Allows for retrieval of clustering results after clustering is complete.
*	</p>
*	@author Alex Hartenstein
*/

public interface Clusterable{
	
	/**
	*	Method to commence clustering algorithm.
	*/
	public void doClustering();

	/**
	*	Two dimensional array is returned containing (row) indices of data 
	*	points organized by cluster assignment.
	*	<br>Imagined as a table :
	*	<ol>
	*	<li>There are as many rows as there are clusters. Each row of table is 
	*	an array. (eg. in kmeans there are k rows)</li>
	*	<li>Each column of table is a (row) index that points to the location of
	*	 a data point in microarray expression data.</li>
	*	</ol>
	*	@return int[][]
	*/
	public int[][] getClusterIndices();

	/**
	*	Returns the centroids of the clusters produced.
	*	<br>Imagined as a table:
	*	<ol>
	*	<li>There are as many rows as there are clusters.</li>
	*	<li>There are as many columns as there are columns in original 
	*	expression data. (equal dimensionality)</li>
	*	</ol>
	*	@return double[][] 
	*/
	public double[][] getClusterPrototypes();
}