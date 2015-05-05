package allen2hpo.matrix;

import allen2hpo.clustering.kmeans.distance.*;


/**
*	<p>Given an mxn matrix, computes a distance matrix that is an mxm matrix</p>
* 	<ol>
* 	<li>Can customize distance measure to be used in calculation by passing a distComputable object in constructor method</li>
*	<li>Default behavior is to use euclidean distance as distance between any two points.</li>
*	@author Alex Hartenstein
*/

public class DistanceMatrix extends Matrix{

	/** Sum of pairwise distances */
	private double spw = 0;

	/** Distance calculation object */
	private DistComputable distCalc;

	/**
	*	Customizable constructor method. Constructs distance matrix calculating distance of each row to every other row
	*	@param Matrix object of data to be used
	*	@param distComputable object from kmeans clustering that is able to compute distance between two points
	*/
	public DistanceMatrix (Matrix m, DistComputable d){
		//Set distance calculation object (default is set as euclidean)
		this.distCalc = d;

		//Init mxm matrix (from mxn data matrix) for distance matrix
		super.setMatrix(new double[m.getRowSize()][m.getRowSize()]);

		//Compute distance matrix
		computeDistance(m);
	}

	/**
	*	Alternate constructor method taking only a Matrix object and using euclidean distance as default.
	*	@param Matrix object m of original data
	*/
	public DistanceMatrix(Matrix m){
		this(m,new DistEuclidean());
	}

	/**
	*	Alternate constructor method taking 2d array as parameter.
	*	@param double[][] of original data
	*/
	public DistanceMatrix(double[][] a){
		//Initialize similarity matrix with size corresponding to data matrix
		this(new Matrix(a));
	}

	/**
	*	@return sum of pairwise distance, which is calculated as distance matrix is computed
	*/
	public double getSumOfPairwiseDistances(){
		return this.spw;
	}

	/**
	*	Goes through each row and calculates distance to every other row
	*	@param Matrix object m of original data
	*/
	private void computeDistance(Matrix m){
	//For each row of similarity matrix
		for (int i=0;i<m.getRowSize();i++){

			//for(int j=0;j<=m.getColumnSize();j++){

			//For each column of similarity matrix below the diagonal
			for(int j=0;j<=i;j++){

				//Get two rows to be compared
				double[] p1 = m.getRowAtIndex(i);
				double[] p2 = m.getRowAtIndex(j);

				//Get distance between two points
				double dist = this.distCalc.calculateProximity(p1,p2);


				//Set value in similarity matrix
				setValueAtIndex(i,j,dist);
				setValueAtIndex(j,i,dist);
				//Add to sum of pairwise distance
				this.spw += dist;
			}
		}
	}
}
