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


		int i = 10;							///NUMBER OF ITERATIONS, SO TESTING K 1 - 10
		double[] gap = new double[i];		///EMPTY ARRAY TO STORE GAP VALUES

		kmeans = new KmeansObject(m);


		///CALCULATE THE GAP STATISTIC
		for (int k = 0;k<i;k++){
			double expected = calcExpectedDispersion(n,p,k+1); 
			double actual = calcDispersion(k+1, m);
			gap[k] = Math.abs(expected) - Math.abs(actual);
			System.out.printf("Gap = %f - %f = %f\n\n",expected,actual, gap[k]);
		}

		///DETERMINE MAXIMUM GAP
		double max = gap[0];
		this.kfinal = 0;
		for (int j = 1; j<gap.length; j++) {
			
			if (gap[j]>max) {
				max = gap[j];
				this.kfinal = j;
			}
		}
	}


	/**
	*	First variable of gap statistic calculation : Calculates expected log of dispersion given n uniform data points in p dimensions with k centers
	*/
	private double calcExpectedDispersion(int n, int p, int k){
		double val = 0;
		return Math.log(p*n/12)-(2/p)*(Math.log(k))+ val;
	}


	/**
	*	Second variable of gap statistic calculation : Actual log of dispersion.
	*/
	private double calcDispersion(int k, Matrix m){
		///THIS WILL HAVE TO PERFORM ENTIRE KMEANS AND CALCULATE LOG WK
		//Wk = sum from r = 1 to K of (1/(2*n in cluster r) * The sum of pairwise values between all points in cluster r/
		
		this.kcurrent = k;
		setK();
		setInitClusters();
		setDistCalc();
		beginClustering();
		
		Matrix[] clusters = kmeans.getClusters();

		double wk = 0;

		for(int i=0;i<k;i++){
			SimilarityMatrix sim = new SimilarityMatrix(clusters[i]);
			wk += (1.0/(clusters[i].getRowSize()))*sim.getSumOfPairwiseDistances();
		}
		return Math.log(wk);
	}


	///KMEANS_ABLE INTERFACE METHODS

	public void setK(){
		kmeans.setK(this.kcurrent);
	}	

	public void setInitClusters(){
		kmeans.setInitClustersBasic();
	}	

	public void setDistCalc(){
		kmeans.setDistCalcBasic();
	}

	public void beginClustering(){
		kmeans.beginClustering(20);
	}

}