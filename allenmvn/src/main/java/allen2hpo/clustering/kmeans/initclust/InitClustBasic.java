package allen2hpo.clustering.kmeans.initclust;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.kmeans.distance.*;


import java.util.Random;
import java.util.ArrayList;


/**
*	Given a data set, randomly selects k data points as cluster seeds
*	Most basic implementation for initializaing cluster prototypes
*	Implements InitClusterable interface to return clusters
*	@author Alex Hartenstein
*/

public class InitClustBasic implements InitClusterable{

	//__________________________________________________________________________
    //
    // Variables                                   
    //__________________________________________________________________________
	
	/**	Cluster prototypes that are initialized here */
	private double[][] cp = null;



	//__________________________________________________________________________
    //
    //	Getters                                   
    //__________________________________________________________________________
	
	/**
	*	Getter for cluster prototypes intialized here. In interface declaration.
	*/
	public double[][] getClusterPrototypes()
	{
		return this.cp;
	} 



	//__________________________________________________________________________
    //
    //	Methods                                   
    //__________________________________________________________________________

	public void initClusters(int k, Matrix m, DistComputable distCalc)
	{
		/*
		*	Init cluster prototypes array;
		*/
		this.cp = new double[k][m.getColumnSize()];

		/*
		*	Init random number generating object
		*/
		Random rand = new Random();

		/*
		*	Init array to which indices of rows already used as prototypes will
		*	be added
		*/
        ArrayList<Integer> indicesAlreadySelected = new ArrayList<Integer>();

	    /*
	    *	Init k cluster seeds selected at random
	    */
	    int ksAdded = 0;

	    /*
	    *	Select random non repeating points from matrix until k points found
	    */
	    while(ksAdded<k)
	    {
	    	/*	Select a random index */
	       	int randVal = (int)(m.getRowSize() * rand.nextDouble());

	       	/*	If index not already selected 
	       	*	1) add corresponding point to cluster prototypes
	       	*	2) add index to index selected array
			*/
	       	if (!indicesAlreadySelected.contains(randVal)) 
	       	{
	       		this.cp[ksAdded] = m.getRowAtIndex(ksAdded);
	       		indicesAlreadySelected.add(ksAdded);
	       		ksAdded ++;
	       	}
	    }
	}
}
