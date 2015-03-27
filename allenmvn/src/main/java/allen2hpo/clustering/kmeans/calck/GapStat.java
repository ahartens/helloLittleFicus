package allen2hpo.clustering.kmeans.calck;

import allen2hpo.matrix.*;
import allen2hpo.clustering.kmeans.Kmeans;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.initclust.*;

import org.apache.log4j.Logger;

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

*
*	NOTE TO DO : LEGACY VERSION CALCULATED GAPSTAT CAP_K TIMES AND THEN COMPARED
*	GAP VALUES
*	12.3.2015 : CHECKS GAP TO PREVIOUS GAP AFTER EACH TRIAL OF K : WHICH IS 
*	BETTER? OBVIOUSLY MORE EFFICIENT AS DOESN'T HAVE TO DO USELESS KMEANS
*
*	@author Alex Hartenstein
*/

public class GapStat implements GetKable{

	//__________________________________________________________________________
    //
    //  Class Variables                                   
    //__________________________________________________________________________


	/**	K value with lowest Gap between log of expected dispersion and log of 
	*	actual dispersion */
	int kfinal = 0;

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

    /**	Enum used by calcDispersion to specify if received matrix is real or 
    *	randomly generated */
    private enum Data {REAL,RANDOM};

    /** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(GapStat.class.getName());



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


		/*	Number of iterations, thus testing K values 2-k+2 */
		int k = 40;
		
		/*	Number of uniform random distributions created for each k for 
		*	which dispersion is calculated */
		int b = 2;

		//Init kmeans object that will perform kmeans on actual data
		this.kmeans = new Kmeans(m,dc,cp);




		boolean optimalKFound = newStepOneTwo(k,b,m);

		/*
		OLD IMPLEMENTATION 12.5.15
		Calculated gap for every k < cap_k then compared them
		while(this.kfinal == 0){
			System.out.println("First round of gap stat");
			double[] gap_k = stepOneTwo(k,b,m);
			double[] s_k = stepThree(k,b);
			this.kfinal = stepFour(gap_k,s_k,b);
		}*/
	}



	//__________________________________________________________________________
    //
    //  Getters                                   
    //__________________________________________________________________________

	/**	
	*	Returns k value for which the gap statistic is the greatest. Required
	*	for GetK interface implementation.
	*/
	public int getK(){
		return this.kfinal;
	}



	//__________________________________________________________________________
    //
    //  Gap stat lifecycle implementation                                   
    //__________________________________________________________________________

	/**
	*	Performs kmeans on 
	*	<ol>
	*	<li>Actual data</li>
	*	<li>capB generated uniform random distributions</li>
	*	</ol>
	*	And uses the resulting clusters to calculate the gap statistic
	*	<br>After first iteration, the current gap calculated is compared to the
	*	gap of the previous iteration
	*	If the gap (after subtracting s) is greater for the previous iteration,
	*	the optimal k value has been found, returns true
	*	@param int capital K, the number of k values to be tested
	*	@param int capital B, the number of random uniform 
	*	@return boolean true when/if optimal k value is found (previous gap is
	*	greater than current gap)
	*	distributions to be created
	*/
	private boolean newStepOneTwo(int cap_k, int cap_b, Matrix m)
	{

		/* Init random expression data generator with actual data */
		UniformRandomMatrixGenerator generator = 
			new UniformRandomMatrixGenerator(m);

		/*	Init array that will hold calculated gap value for given k value 
		*	(capital K long) */
		double[] gap = new double[cap_k];

		/*	Calculated dispersion for actual Data */
		this.wk = new double[cap_k];

		/*	Calculated dispersion for randomly generated data */
		this.wkb_star = new double[cap_k][cap_b];

		int firstK = 2;

		/*	For each k value perform kmeans and calculate gap */
		for (int k = 0; k<cap_k; k++)
		{

			/*	Calculate actual log(Wk) for given k value.
			*	Start with k = 2 */
			wk[k] = calcMeanDispersion(k+firstK, null, Data.REAL);

			/*	Reset sum of gaps */
			double gapSum_k = 0;

			/*	
			*	Calculate log(Wkb_star) for given k for B uniform random 
			*	matrices. 
			*/
			for (int b = 0; b<cap_b; b++)
			{
				/*	Create random uniform matrix and calculate dispersion
				*	Start with k = 2 */

				wkb_star[k][b] = 
					calcMeanDispersion(k+firstK,generator.generateUniformRand(),
						Data.RANDOM);

				/*	Calculate gap and add to sum */
				gapSum_k += wkb_star[k][b] - wk[k];
			}
			
			/*	
			*	Calculate actual gap by dividing by capital B (number of random 
			*	distributions created) 
			*/
			gap[k] = gapSum_k/cap_b;

			/*
			*	If not first value k checked, calculate gap between current 
			*	clustering and previous clustering
			*/
			if (k>0) 
			{
				double s_k = newStepThree(cap_b,wkb_star[k]);

				boolean optimalKFound = newStepFour(gap[k-1],gap[k],s_k);

				log.info(String.format("k : %d gk: %f  >=  %f   gk+1: %f    s:%f\n",
					k+firstK-1,gap[k-1],gap[k] - s_k,gap[k],s_k));

				if (optimalKFound) 
				{
					/*
					*	Started clustering with with firstk. singleStepThree 
					*	calculates s for previous k
					*/
					this.kfinal = k+firstK-1;

					return true;
				}
			}
		}

		return false;
	}
	
	private double newStepThree( int cap_b, double[] wkb_star1)
	{
		/*
		*	PART ONE
		*	Calculate l_bar for each value k
		*/
		double lbar_k = 0;
		for (int b = 0; b < wkb_star1.length; b++){
			lbar_k += wkb_star1[b];
		}
		lbar_k /= cap_b;

		/*
		*	PART TWO
		*	Calculate standard deviation for each value k, using l_bar
		*/
		double sd_k = 0;
		double s_k = 0;

		for (int b =0; b<cap_b; b++){
			sd_k = Math.pow(wkb_star1[b] - lbar_k,2);
		}

		sd_k = Math.sqrt(sd_k/cap_b);
		s_k = Math.sqrt(1+(1/cap_b))*sd_k;

		return s_k;
	}

	private boolean newStepFour(double previousGap, double currentGap, double s)
	{

		if (previousGap >= currentGap - s) 
		{
			return true;
		}
		return false;
	}



	//__________________________________________________________________________
    //
    //	Calculate Dispersion                                   
    //__________________________________________________________________________

	/**
	*	Calculates dispersion of given data m separated into k clusters r times
	*	(runs k means r times) and returns average dispersion. 
	*	<br>Called by Step One.
	*	@param int k value for which dispersion should be calculated
	*	@param Matrix m data matrix (either real or randomly generated)
	*	@param Data enum specifying if data is real or randomly generated
	*/
	private double calcMeanDispersion(int k, Matrix m, Data realOrRandom){

		this.repeat = 1;
		double sumW = 0;

		Kmeans kmo = null;
		Matrix[] clusters = null;
		
		if (realOrRandom == Data.REAL){
			kmo = this.kmeans;
			kmo.setK(k);
		}
		else{
			kmo = new Kmeans(m,k,this.distCalc,this.cpInit);
		}

		for (int j=0; j<this.repeat ; j++){
			kmo.doClustering();
			sumW += calcDispersionFromClusteredValues(kmo.getClusters(),k);		
		}
		return sumW/repeat;
	}

	/**
	*
	*/
	double calcDispersionFromClusteredValues(Matrix[] clusters, int k){
		double wk = 0;

		/*
		*	For each cluster calculate the sum of pairwise distance of 
		*	expression values
		*/
		for(int x=0; x<k; x++)
		{	
			float sumOfPairwiseDistanceForClst = 0;

			/*
			*	Calculate distance of every row to every other row and add to
			*	sum of pairwise distance.
			*/
			for(int i=0; i<clusters[x].getRowSize(); i++)
			{
				for(int j=0; j<clusters[x].getRowSize(); j++)
				{
					sumOfPairwiseDistanceForClst += 
					distCalc.calculateProximity(clusters[x].getRowAtIndex(i),
						clusters[x].getRowAtIndex(j));
				}
			}

			wk += (1.0/(clusters[x].getRowSize()))*sumOfPairwiseDistanceForClst;
		}
		return Math.log(wk);
	}




	//__________________________________________________________________________
    //
    //  OLD 12.5.15 GapStat Lifecycle Implementation                                 
    //__________________________________________________________________________


	/**
	*	Step one of gap stat, returns an array capital K, with a gap value 
	*	corresponding index + 1
	*	@param first int is capital K, the number of k values to be tested
	*	@param second int is capital B, the number of random uniform 
	*	distributions to be created
	*/
	private double[] stepOneTwo(int cap_k, int cap_b, Matrix m)
	{

		/* Init random expression data generator with actual data */
		UniformRandomMatrixGenerator generator = 
			new UniformRandomMatrixGenerator(m);

		/*	Init array that will hold calculated gap value for given k value 
		*	(capital K long) */
		double[] gap = new double[cap_k];

		/*	Calculated dispersion for actual Data */
		this.wk = new double[cap_k];

		/*	Calculated dispersion for randomly generated data */
		this.wkb_star = new double[cap_k][cap_b];


		/*	For each k value perform kmeans and calculate gap */
		for (int k = 0; k<cap_k; k++)
		{

			/*	Calculate actual log(Wk) for given k value.
			*	Start with k = 2 */
			wk[k] = calcMeanDispersion(k+2, null, Data.REAL);

			/*	Reset sum of gaps */
			double gapSum_k = 0;

			/*	
			*	Calculate log(Wkb_star) for given k for B uniform random 
			*	matrices. 
			*/
			for (int b = 0; b<cap_b; b++)
			{
				/*	Create random uniform matrix and calculate dispersion
				*	Start with k = 2 */

				wkb_star[k][b] = 
					calcMeanDispersion(k+2,generator.generateUniformRand(),
						Data.RANDOM);

				/*	Calculate gap and add to sum */
				gapSum_k += wkb_star[k][b] - wk[k];
			}
			
			/*	
			*	Calculate actual gap by dividing by capital B (number of random 
			*	distributions created) 
			*/
			gap[k] = gapSum_k/cap_b;

		}

		return gap;
	}

	/**
	*
	*/
	private double[] stepThree( int cap_k, int cap_b)
	{
		/*
		*	PART ONE
		*	Calculate l_bar for each value k
		*/
		double[] lbar_k = new double[cap_k];

		for (int k = 0; k<cap_k; k++)
		{
			for (int b = 0; b<cap_b; b++)
			{
				lbar_k[k] += this.wkb_star[k][b];
			}
			lbar_k[k] /= cap_b;
		}

		/*
		*	PART TWO
		*	Calculate standard deviation for each value k, using l_bar
		*/
		double[] sd_k = new double[cap_k];
		double[] s_k = new double[cap_k];

		for (int k = 0; k<cap_k; k++)
		{
			for (int b = 0; b<cap_b; b++)
			{
				sd_k[k] += Math.pow(this.wkb_star[k][b] - lbar_k[k],2);
			}
			
			sd_k[k] = Math.sqrt(sd_k[k]/cap_b);
			s_k[k] = Math.sqrt(1+(1/cap_b))*sd_k[k];
		}
		return s_k;
	}

	/**
	*	11.3.2015 changed this to not look at k = 1 (first element in gap array)
	*	because was often greater. is this reasonable???
	*/
	private int stepFour(double[] gap, double[] s, int cap_k){
		System.out.println("step four");
		for(int k = 0; k< cap_k - 1; k++){
			/*System.out.printf("gk: %f  >=  %f   gk+1: %f    s:%f\n",
			gap[k],gap[k+1] - s[k+1],gap[k+1],s[k+1]);*/

			if ( gap[k] >= gap[k+1] - s[k+1] ){
				
				/*	started with k = 2 so at position zero are values 
				*	corresponding to a k = 2 */
				return k+2;
			}
		}
		return 0;
	}

}