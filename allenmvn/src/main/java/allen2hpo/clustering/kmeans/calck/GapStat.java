package allen2hpo.clustering.kmeans.calck;

import allen2hpo.matrix.*;
import allen2hpo.clustering.kmeans.Kmeans;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.initclust.*;

/**
*	<p>Finds optimal number of clusters using kmeans.
*	<br>GapStat itself performs Kmeans, in an iterative fashion.
*	For each k value for which it performs kmeans, it calculates:
*	<ol>
*	<li>The expected dispersion given a random distribution with n samples 
*	each with p dimensions</li>
*	<li>The actual dispersion W by a given k and data set, Matrix m</li>
*	</ol>
*	TO USE :
*	<ol>
*	<li>Create instance setting data to be clustered and optional distance 
*	calculation object (if none specified defaults to Euclidean distance).</li>
*	<li>Call getK() to get optimal k value value.</li>
*	</ol>
*	@author Alex Hartenstein
*/

public class GapStat implements GetKable{


	//__________________________________________________________________________
    //
    //  Class Variables                                   
    //__________________________________________________________________________


	/**	K value with lowest Gap between log of expected dispersion and log of 
	*	actual dispersion */
	int kfinal;

	/**	object performing kmeans or actual data. random distributions init their
	*	own kmeans object */
	Kmeans kmeans;

	/**	Array number of tested K long, containing calculated dispersion for 
	*	given matrix for index i clusters. */
	double [] wk = null;

	/** contains calculated dispersion of a uniform random matrix B. Array 
	*	#tested K long by B (# of uniform random matrices created) */
	double [][] wkb_star = null;

	/** number of times that kmeans should be performed at each iteration */
	int repeat = 1;

	/** Object implementing DistComputable interface which performs distance 
    *   calculation. Default (Euclidean distance) is used if none provided in 
    *   constructor method.*/
    private DistComputable distCalc = null;

    /** Object implementing InitClusterable interface which returns k data 
    *   points to use for first iteration of kmeans. Default (k random points) 
    *   is used if none provided in constructor method.*/
    private InitClusterable cpInit = null;




    //__________________________________________________________________________
    //
    //  Constructors                                   
    //__________________________________________________________________________    

	/**
	*	Constructor method implementing lifecycle of gap stat. 
	*	@param Matrix data for which optimal k value should be found
	*	@param DistComputable distance calculation
	*	@param InitClusterable cluster initialization object
	*/
	public GapStat(Matrix m, DistComputable dc, InitClusterable cp)
	{

		/*
		*	Set distance calculation object
		*/
		this.distCalc = dc;

		this.cpInit = cp;


		/*	Number of iterations, thus testing K values 1-20 */
		int k = 20;
		
		/*	Number of uniform random distributions created for each k for 
		*	which dispersion is calculated */
		int b = 10;

		//Init kmeans object that will perform kmeans on actual data
		this.kmeans = new Kmeans(m,dc,cp);



		while(this.kfinal == 0){
			double[] gap_k = stepOneTwo(k,b,m);
			double[] s_k = stepThree(k,b);
			this.kfinal = stepFour(gap_k,s_k,b);
		}
	}



	//__________________________________________________________________________
    //
    //  Getters                                   
    //__________________________________________________________________________

	/**	Returns k value for which the gap statistic is the greatest */
	public int getK(){
		return this.kfinal;
	}



	//__________________________________________________________________________
    //
    //  GapStat Implementation                                 
    //__________________________________________________________________________


	/**
	*	Step one of gap stat, returns an array capital K, with a gap value 
	*	corresponding index + 1
	*	@param first int is capital K, the number of k values to be tested
	*	@param second int is capital B, the number of random uniform 
	*	distributions to be created
	*/
	private double[] stepOneTwo(int cap_k, int cap_b, Matrix m){

		//Init random expression data generator with actual data
		UniformRandomMatrixGenerator generator = new UniformRandomMatrixGenerator(m);

		//Init array that will hold calculated gap value for given k value (capital K long)
		double[] gap = new double[cap_k];

		//Calculated dispersion for actual data
		this.wk = new double[cap_k];

		//Calculated dispersion for randomly generated data
		this.wkb_star = new double[cap_k][cap_b];


		//For each k value perform kmeans and calculate gap
		for (int k = 0; k<cap_k; k++){

			///Calculate actual log(Wk) for given k value
			wk[k] = calcMeanDispersion(k+1, m,0);

			///Reset sum of gaps
			double gapSum_k = 0;

			///Calculate log(Wkb_star) for given k for B uniform random matrices.
			for (int b = 0; b<cap_b; b++){
				///Create random uniform matrix
				wkb_star[k][b] = calcMeanDispersion(k+1,generator.generateUniformRand(),1);

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
	*	@param k value, data matrix xto be clustered
	*	@param realOrRandom, third arguement : if 0 cluster real data, else 
	*	cluster random uniform data
	*/
	private double calcMeanDispersion(int k, Matrix m, int realOrRandom){

		this.repeat = 1;
		double sumW = 0;

		for (int j=0; j<this.repeat ; j++){
			if (realOrRandom == 0){
				sumW += calcDispersion(k,m);
			}
			else{
				sumW += calcDispersionForRandomUniform(k,m);
			}
		}
		return sumW/repeat;
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

		this.kmeans.setK(k);
		this.kmeans.doClustering();

		Matrix[] clusters = this.kmeans.getClusters();

		double wk = 0;

		/*
		*	For each cluster calculate the sum of pairwise distance of expression values
		*/
		for(int x=0; x<k; x++){
			
			float sumOfPairwiseDistanceForCluster = 0;


			for(int i=0; i<clusters[x].getRowSize(); i++)
			{
				for(int j=0; j<clusters[x].getRowSize(); j++)
				{
					sumOfPairwiseDistanceForCluster += distCalc.calculateProximity(clusters[x].getRowAtIndex(i),clusters[x].getRowAtIndex(j));
				}
			}

			wk += (1.0/(clusters[x].getRowSize()))*sumOfPairwiseDistanceForCluster;
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
		Kmeans kmo = new Kmeans(m,k,this.distCalc,this.cpInit);
		kmo.doClustering();

		Matrix[] clusters = kmo.getClusters();

		double wk = 0;

		for(int i=0;i<k;i++){
			DistanceMatrix sim = new DistanceMatrix(clusters[i]);
			wk += (1.0/(clusters[i].getRowSize()))*sim.getSumOfPairwiseDistances();
		}
		return Math.log(wk);
	}
}
