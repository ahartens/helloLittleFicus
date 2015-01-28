package allen2hpo.clustering;

import allen2hpo.matrix.*;


/**
*	GapStat is a method with which to find the optimal K value
*	It is therefore called by a kmeansable setk() method. 
*	Confusingly, GapStat itself performs Kmeans, in an iterative fashion.
*	For each k value for which it performs kmeans, it calculates
*		1) the expected dispersion given a random distribution with n samples each with p dimensions
*		2) the actual dispersion W by a given k and data set, Matrix m
*	
*	TO USE : 
*	1) Call constructor method using the data set to be optimized as the argument.
*	2) call getK(); getter method
*/
public class GapStat implements GetKable,Kmeansable{
	
	/**
	*	K value with lowest Gap between log of expected dispersion and log of actual dispersion
	*/
	int kfinal;
	int kcurrent;
	KmeansObject kmeans;

	/**
	*	Array number of tested K long, containing calculated dispersion for given matrix for index i clusters. 
	*/
	double [] wk = null;

	/**
	*	Array of arrays containing calculated dispersion of a uniform random matrix B
	*	Array #tested K long by B (# of uniform random matrices created)
	*/
	double [][] wkb_star = null;

	/**
	*	Returns k value for which the gap statistic is the greatest
	*/
	public int getK(){
		return this.kfinal;
	}


	/**	
	*	@param Matrix m of data for which number of clusters k should be optimized
	*/
	public GapStat(Matrix m){
		
		int n = m.getRowSize();
		int p = m.getColumnSize();


		int k = 10;							///NUMBER OF ITERATIONS, SO TESTING K 1 - 10
		int b = 10;							///Number of uniform random distributions created for each k for which dispersion is calculated

		double[] gap_k = stepOneTwo(k,b,m);
		double[] s_k = stepThree(k,b);

		this.kfinal = stepFour(gap_k,s_k,b);
	}



	/**
	*	Step one of gap stat, returns an array capital K, with a a gap value corresponding index + 1
	*	@param first int is capital K, the number of k values to be tested
	*	@param second int is capital B, the number of random uniform distributions to be created
	*/
	private double[] stepOneTwo(int cap_k, int cap_b, Matrix m){
		
		this.kmeans = new KmeansObject(m);

		UniformRandomMatrixGenerator generator = new UniformRandomMatrixGenerator(m);

		double [] gap = new double[cap_k];

		this.wk = new double[cap_k];
		this.wkb_star = new double[cap_k][cap_b];

		for (int k = 0; k<cap_k; k++){
			
			///Calculate actual log(Wk) for given k value 
			wk[k] = calcDispersion(k+1, m);

			///Reset sum of gaps
			double gapSum_k = 0;

			
			///Calculate log(Wkb_star) for given k for B uniform random matrices. 
			for (int b = 0; b<cap_b; b++){
				///Create random uniform matrix
				wkb_star[k][b] = calcDispersionForRandomUniform(k+1,generator.generateUniformRand());

				///Calculate gap and add to sum
				gapSum_k += wkb_star[k][b] - wk[k];
				//System.out.printf("k = %d, b + %d : %f - %f =  %f \n",k, b,wkb_star[k][b], wk[k],gapSum_k);

			}

			///Calculate actual gap by dividing by capital B (number of random distributions created)
			gap[k] = gapSum_k/cap_b;
		}

		return gap;
	}



	/**
	*
	*/
	private double[] stepThree( int cap_k, int cap_b){

		///PART ONE : Calculate l_bar for each value k
		double[] lbar_k = new double[cap_k];
		
		for (int k = 0; k<cap_k; k++){
			for (int b = 0; b<cap_b; b++){
				lbar_k[k] += this.wkb_star[k][b];
			}
			lbar_k[k] /= cap_b;
		}

		///PART TWO : Calculate standard deviation for each value k, using l_bar
		double[] sd_k = new double[cap_k];
		double[] s_k = new double[cap_k];

		for (int k = 0; k<cap_k; k++){
			for (int b = 0; b<cap_b; b++){
				sd_k[k] += Math.pow(this.wkb_star[k][b] - lbar_k[k],2);
			}
			sd_k[k] = Math.sqrt(sd_k[k]/cap_b);
			s_k[k] = Math.sqrt(1+(1/cap_b))*sd_k[k];
		}
		return s_k;
	}



	/**
	*
	*/
	private int stepFour(double[] gap, double[] s, int cap_k){
		
		for(int k = 0; k< cap_k - 1; k++){

			if (gap[k] > 0 && gap[k] >= gap[k+1] - s[k+1] ){
				return k+1;
			}
		}
		return 0;
	}



	/**
	* 	@param takes data matrix for which optimal number of clusters should be found and current iteration of k
	*	Performs kmeans clustering
	*	Calculates D, the sum of pairwise distances between points in one cluster, for each cluster
	*	Calculates W, the pooled within-cluster sum of squares around the cluster means (sum from 1 to k of ((1/2n)*D))
	*	Returns log of W.
	*/
	private double calcDispersion(int k, Matrix m){
		///THIS WILL HAVE TO PERFORM ENTIRE KMEANS AND CALCULATE LOG WK
		//Wk = sum from r = 1 to K of (1/(2*n in cluster r) * The sum of pairwise values between all points in cluster r/

		this.kcurrent = k;
		setK(this.kmeans);
		setInitClusters(this.kmeans);
		setDistCalc(this.kmeans);
		beginClustering(this.kmeans);
		
		Matrix[] clusters = kmeans.getClusters();

		double wk = 0;

		for(int i=0;i<k;i++){
			SimilarityMatrix sim = new SimilarityMatrix(clusters[i]);
			wk += (1.0/(clusters[i].getRowSize()))*sim.getSumOfPairwiseDistances();
		}
		return Math.log(wk);
	}



	/**
	* 	@param takes random uniform matrix should be found and current iteration of k
	*	Performs The same calculation as calcDispersion, except for a random uniform distribution
	*/
	private double calcDispersionForRandomUniform(int k, Matrix m){
		///THIS WILL HAVE TO PERFORM ENTIRE KMEANS AND CALCULATE LOG WK
		//Wk = sum from r = 1 to K of (1/(2*n in cluster r) * The sum of pairwise values between all points in cluster r/
		KmeansObject kmo = new KmeansObject(m);

		setK(kmo);
		setInitClusters(kmo);
		setDistCalc(kmo);
		beginClustering(kmo);
		
		Matrix[] clusters = kmo.getClusters();

		double wk = 0;

		for(int i=0;i<k;i++){
			SimilarityMatrix sim = new SimilarityMatrix(clusters[i]);
			wk += (1.0/(clusters[i].getRowSize()))*sim.getSumOfPairwiseDistances();
		}
		return Math.log(wk);
	}



	///KMEANS_ABLE INTERFACE METHODS

	public void setK(KmeansObject kmo){
		kmo.setK(this.kcurrent);
	}	

	public void setInitClusters(KmeansObject kmo){
		kmo.setInitClustersBasic();
	}	

	public void setDistCalc(KmeansObject kmo){
		kmo.setDistCalcBasic();
	}

	public void beginClustering(KmeansObject kmo){
		kmo.beginClustering(20);
	}

	public int[][] getClusterIndices(){
		return kmeans.getClusterIndices();
	}

}