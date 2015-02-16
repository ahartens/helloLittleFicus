package allen2hpo.matrix;

import allen2hpo.clustering.kmeans.distance.*;


/**
*	<p>Given an mxn matrix, computes a similarity matrix that is an mxm matrix</p>
* 	<ol>
* 	<li>Can customize distance measure to be used in calculation by passing a distComputable object in constructor method</li>
*	<li>Default behavior is to use euclidean distance as distance between any two points.</li>
*	@author Alex Hartenstein
*/

public class SimilarityMatrix extends Matrix{

	/** Sum of pairwise distances */
	private int spw = 0;

	/** Distance calculation object */
	private DistComputable distCalc;

	/**
	*	Customizable constructor method. Constructs distance matrix calculating distance of each row to every other row
	*	@param Matrix object of data to be used
	*	@param distComputable object from kmeans clustering that is able to compute distance between two points
	*/
	public SimilarityMatrix (Matrix m, DistComputable d){
		computeSimilarityMatrix(m);
		this.distCalc = d;
	}

	/**
	*	Alternate constructor method taking only a Matrix object and using euclidean distance as default.
	*	@param Matrix object m of original data
	*/
	public SimilarityMatrix(Matrix m){
		this(m,new DistEuclidean());
	}

	/**
	*	Alternate constructor method taking 2d array as parameter.
	*	@param double[][] of original data
	*/
	public SimilarityMatrix(double[][] a){
		//Initialize similarity matrix with size corresponding to data matrix
		this(new Matrix(a));
	}

	/**
	*	Called by Constructor methods to make a similarity matrix
	*/
	private void computeSimilarityMatrix(Matrix m){
		///INITIALIZE SIMILARITY MATRIX WITH SIZE CORRESPONDING TO DATA MATRIX

		super.setMatrix(new double[m.getRowSize()][m.getRowSize()]);
		computeSimilarity(m);
	}

	private void computeSimilarity(Matrix m){
	///FOR EACH ROW OF SIMILARITY MATRIX
			for (int i=0;i<m.getRowSize();i++){
				///FOR EACH COLUMN OF SIMILARITY MATRIX
				//for(int j=0;j<=m.getColumnSize();j++){
				for(int j=0;j<=i;j++){


				/*	double sum = 0;


					///FOR EACH COLUMN OF DATA COLUMN (CORRESPONDING TO DIMENSION OF VECTOR)
					for (int z =0;z<m.getColumnSize();z++){
						///CALCULATE DISTANCE OF DIMENSION
						sum += Math.pow(m.getValueAtIndex(i,z) - m.getValueAtIndex(j,z),2);
					}
					///CALCULATE EUCLIDEAN DISTANCE
					double dist = Math.sqrt(sum);*/

					double[] p1 = m.getRowAtIndex(i);
					double[] p2 = m.getRowAtIndex(j);

					double dist = this.distCalc.calculateProximity(p1,p2);


					///SET VALUE IN SIMILARITY MATRIX
					setValueAtIndex(i,j,dist);
					this.spw += dist;
				}
			}
	}

	public double getSumOfPairwiseDistances(){
		return this.spw;
	}

	public void print(){
        for(int i = 0;i<getRowSize();i++){
            for(int j = 0;j<=i;j++){
                System.out.printf("%.5f\t",super.getValueAtIndex(i,j));
            }
            System.out.printf("\n");
        }
        System.out.printf("\n\n\n");

    }
}
