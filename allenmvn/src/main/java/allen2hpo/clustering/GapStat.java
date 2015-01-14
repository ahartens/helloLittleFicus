package allen2hpo.clustering;

import allen2hpo.matrix.*;

public class GapStat implements GetKable,Kmeansable{
	
	/**
	*	K value with lowest Gap between log of expected dispersion and log of actual dispersion
	*/
	int kfinal;
	int kcurrent;
	KmeansObject kmeans;
	

	/**	
	*	@param n : sample size
	*/
	public GapStat(Matrix m){
		
		int n = m.getRowSize();
		int p = m.getColumnSize();


		int i = 10;							///NUMBER OF ITERATIONS, SO TESTING K 1- 10
		double[] gap = new double[i];		///EMPTY ARRAY TO STORE GAP VALUES

		kmeans = new KmeansObject(m);
		
		///CALCULATE THE GAP STATISTIC
		for (int k = 0;k<i;k++){
			gap[k] = calcExpectedDispersion(n,p,k) - calcDispersion(k, m);
		}

		///DETERMINE MINIMUM GAP
		double min = gap[0];
		this.kfinal = 0;
		for (int j = 1; j<gap.length; j++) {
			
			if (gap[j]<min) {
				min = gap[j];
				this.kfinal = j;
			}
		}
	}


	public int getK(){
		return this.kfinal;
	}


	private double calcExpectedDispersion(int n, int p, int k){
		double val = 0;
		return Math.log(p*n/12)-(2/p)*(Math.log(k))+ val;
	}

	private double calcDispersion(int k, Matrix m){
		///THIS WILL HAVE TO PERFORM ENTIRE KMEANS AND CALCULATE LOG WK
		//Wk = sum from r = 1 to K of (1/(2*n in cluster r) * The sum of pairwise values between all points in cluster r/
		this.kcurrent = k;
		setK();
		setInitClusters();
		beginClustering(10);
		
		double[][][] clusters = kmeans.getClusters();

		double wk = 0;

		for(int i=0;i<k;i++){
			SimilarityMatrix sim = new SimilarityMatrix(clusters[i]);
			wk += (1/(2*clusters[i].length))*sim.getSumOfPairwiseDistances();
		}
		return wk;
	}


	///KMEANS_ABLE INTERFACE METHODS

	public void setK(){
		kmeans.setK(this.kcurrent);
	}	


	public void setInitClusters(){
		kmeans.setInitClustersBasic();
	}	


	public void beginClustering(int i){
		kmeans.beginClustering(i);
	}

}