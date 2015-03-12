package allen2hpo.clustering.kmeans.initclust;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;

import java.util.ArrayList;

/**
*	Initializes cluster prototypes with k data points that are the farthest from
*	Implements InitClusterable interface to return clusters
*	@author Alex Hartenstein
*/

public class InitClustMostDistant implements InitClusterable{

 	//__________________________________________________________________________
    //
    // Variables                                   
    //__________________________________________________________________________
	
	/**	Cluster prototypes initialized here */
	private double[][] cp;



	//__________________________________________________________________________
    //
    //	Methods                                   
    //__________________________________________________________________________
	
	public void initClusters(int k, Matrix m, DistComputable distCalc)
	{
		/*	Init cluster prototypes array */
		this.cp = new double[k][m.getColumnSize()];

		/*	Store indices of rows already used as prototypes */
        ArrayList<Integer> indicesAlreadySelected = new ArrayList<Integer>();

        /*	Select first point in data matrix as initial cluster */
        this.cp[0] = m.getRowAtIndex(0);

        /*	Mark that first point/index 0 has already been added to cp */
        indicesAlreadySelected.add(0);
		
        
        /*
        *	Maximum distance will be calculated from mean of previous points
        *	Initialize with first row (first cluster prototype) here
        */
        double[] meanOfPreviousPoints = new double[m.getColumnSize()];
        meanOfPreviousPoints = m.getRowAtIndex(0);

        /*
        *	Init variables for iteration
        */
		double maxDist;
		double currentDist;
		int indexMaxDistFromCurrent;
		int ksAdded = 0;
		/*
		*	Until k 
		*/
		for(int j=0; j<k; j++)
		{

			/*	Set all distance variables to 0 */
			maxDist = 0;
			currentDist = 0;
			indexMaxDistFromCurrent = 0;

			/*	
			*	For each row in expression data calculate distance to the mean 
			*	of previously selected cluster prototypes
			*	The point furthest away from mean is the next point selected
			*/
			for(int i=0; i<m.getRowSize(); i++)
			{
				/*	If row not already selected */
				if( !indicesAlreadySelected.contains(i))
				{
					/*	Calculate distance from mean of previous */
					currentDist = distCalc.calculateProximity
						(meanOfPreviousPoints,m.getRowAtIndex(i));

					/*	Check if furthest away and set current max if it is */
					if (currentDist > maxDist) 
					{
						indexMaxDistFromCurrent = j;
						maxDist = currentDist;
					}
				}
			}

			/*	Add point furthest away to cluster prototypes array (+ index) */
			indicesAlreadySelected.add(indexMaxDistFromCurrent);
			this.cp[j] = m.getRowAtIndex(indexMaxDistFromCurrent);

			/*	Mean all cluster points selected till now */
			meanOfPreviousPoints = calculateMeanOfPreviousPoints(j+1);
		}
	}

	private double[] calculateMeanOfPreviousPoints(int fillCount){
		/*
		*	Init array to hold sum of all dimensions, and later the mean
		*/
		double[] sum = new double[this.cp[0].length];

		for(int i = 0; i<fillCount; i++)
		{
			for(int j = 0; j<this.cp[0].length; j++)
			{
				sum[j] += this.cp[i][j];
			}
		}

		for(int i = 0; i<fillCount; i++)
		{
			for(int j = 0; j<this.cp[0].length; j++)
			{
				sum[j] /= fillCount;
			}
		}
		return sum;
	}

	
	
	//__________________________________________________________________________
    //
    //	Getters                                   
    //__________________________________________________________________________
	
	/**
	*	Getter for cluster prototypes intialized here. In interface declaration
	*/
	public double[][] getClusterPrototypes(){
		return this.cp;
	}

}