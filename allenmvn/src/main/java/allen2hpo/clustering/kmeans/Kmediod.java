package allen2hpo.clustering.kmeans;

import allen2hpo.matrix.Matrix;
import allen2hpo.matrix.DistanceMatrix;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import allen2hpo.clustering.Clusterable;
import allen2hpo.clustering.kmeans.calck.*;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.initclust.*;


/**
*   <p>Performs kmeans cluster analysis.
*   <br>Various constructor methods allow for customization of various aspects 
*   of clustering implementation. Can specify method of :
*   <ol>
*   <li>Distance calculation using object implementing DistComputable</li>
*   <li>Cluster initializaiton using object implementing InitClusterable</li>
*   </li>
*   If not specified, default implementations are used.
*   Variables that affect speed of clustering :</p>
*	@author Alex Hartenstein
*/

public class Kmediod implements Clusterable{

    //__________________________________________________________________________
    //
    //  Class Variables                                   
    //__________________________________________________________________________

    /** The number of clusters */
    private int k = 0;

    /** An m x n matrix of data to be clustered */
    private Matrix m = null;

    /** cluster prototypes. The centroids of the clusters being calculated.
    *    A k x n two dimensional array */
    private double[][] cp = null;

    /**	cluster index. Stores the index of the cluster (0-(k-1)) to which 
    *   each data point in expression matrix belongs */
    private int[] ci = null;

    /** Stores size of clusters as they are created */
    private int[] cs = null;

    /** cluster index organized. for each cluster is an arraylist that stores 
    *   indices ind ata matrix that belong to cluster at list index */
    private ArrayList<ArrayList<Integer>> ciorg = null;


    /** Stores Sum of squared errors of clusters as they are created. As assign 
    *   data points add sum of squared error, after assignment divide by cluster
    *    size cs */
    private double[] sses = null;

    /** Object implementing DistComputable interface which performs distance 
    *   calculation. Default (Euclidean distance) is used if none provided in 
    *   constructor method.*/
    private DistComputable distCalc = null;

    /** Object implementing InitClusterable interface which returns k data 
    *   points to use for first iteration of kmeans. Default (k random points) 
    *   is used if none provided in constructor method.*/
    private InitClusterable cpInit = null;


    private Matrix distanceMatrix = null;

    //__________________________________________________________________________
    //
    //  Constructors                                   
    //__________________________________________________________________________    

    /**
    *   Simplest constructor method initializes only data to be clustered.
    *   @param Matrix expression data to be clustered.
    */
    public Kmediod(Matrix mat, DistComputable dc, InitClusterable ic){
        this(mat,1,dc,ic);
    }

    /**
    *   Exhaustive constructor method that, in addition to setting expression 
    *   data and k value, allows for customization of distance calculation used 
    *   in clustering and method with which cluster prototypes are initialized.
    *   @param Matrix object is data to be clustered.
    *   @param int k is number of clusters data matrix should be partitioned 
    *   into
    *   @param DistComputable distance calculation implementing DistComputable 
    *   interface to customize distance calculation.
    *   @param InitClusterable object specifying method used to initialize 
    *   cluster prototypes 
    */
    public Kmediod(Matrix mat, int kval, DistComputable dc,InitClusterable cp){

        if (mat == null)
            throw new IllegalArgumentException("Data not initialized");
        if (dc == null)
            throw new 
        IllegalArgumentException("proximity measure object not initialized");
        if (cp == null)
            throw new 
        IllegalArgumentException("cluster init object not initialized");


        /* 
        *   Set data matrix private variable 
        */
        this.m = mat;

        /*
        *   Set K value 
        */
        setK(kval);

        /* 
        *   Set distance calculation object 
        */
        this.distCalc = dc;

        /* 
        *   Initialize cluster prototype start values using InitClusterable 
        *   object
        */
        this.cpInit = cp;

        /* 
        *   Initialize cluster index array. While iterative clustering, the 
        *   cluster to
        *   which a data point belongs to will be stored here. 
        */
        this.ci = new int[mat.getRowSize()];

        /*
        *   Init distance matrix that will store distance of each point to every other.
        */
        this.distanceMatrix = new DistanceMatrix(this.m,this.distCalc);

    }




    //__________________________________________________________________________
    //
    //  Setters                                   
    //__________________________________________________________________________


    /**
    *   Called by constructor method or can be called externally if wish to 
    *   change k.
    *   <br> Sets k value to be used as well as initializes arrays necessary 
    *   for k of that size
    *   @param int k is number of clusters to data matrix should be partitioned 
    *   into.
    */
    public void setK(int kval){
        
        /* 
        *   Set K value 
        */
        this.k = kval;
        
        /* 
        *   Init cluster size array. Stores number of points assigned to each 
        *   cluster in step 1. 
        */
        this.cs = new int[kval];

    }



    //__________________________________________________________________________
    //
    //  Getters                                   
    //__________________________________________________________________________

    /** 
    *   Return number of clusters being formed.
    *   @return int k
    */
    public int getK(){
        return this.k;
    }

    /** 
    *   Return data that is being clustered. 
    *   @return Matrix object being clustered 
    */
    public Matrix getData(){
        return this.m;
    }

    /**	
    *   Return index of cluster each row is assigned to. 
    *   @return int [] containing the index of the cluster prototype 
    *   (from 0-(k-1)) that each data point is assigned too (in order of 
    *   original data matrix) 
    */
    public int[] getClusterAssignments(){
        return this.ci;
    }

    /**
    *   Get all expression values of original data organized by cluster assign-
    *   ment.
    *	@return Matrix array of Matrix objects corresponding to the data matrix 
    *   split into k clusters.
    */
    public Matrix[] getClusters(){

        /*
        *   Initialize array with length k (one matrix per cluster)
        */
        Matrix [] clusters = new Matrix[this.k];

        /*
        *   Initialize each matrix object in clusters array with correct size
        */
        for (int i = 0;i<this.k;i++)
        {
            double[][] ca = new double[this.cs[i]][this.m.getColumnSize()];
            Matrix cm = new Matrix(ca);
            clusters[i] = cm;
        }

        /*
        *   Initialize array holding counter for each cluster (incremented as 
        *   add row to matrix)
        */
        int [] inClustCount = new int[this.k];


        /*
        *   Iterate through each row in the expression data matrix.
        *   Place the row into the cluster it belongs to
        */
        for (int i=0;i<this.m.getRowSize();i++)
        {
            for(int j=0; j<this.m.getColumnSize(); j++)
            {
                /*
                *   in ci is stored index of cluster to which current row 
                *   (of expression values/single gene) belongs to
                *   that index corresponds to :
                *       1. index in clusters Matrix
                *       2. index in inClustCount (counter for iteration)
                *   set value of each 'column' in row in Matrix
                */
                clusters[this.ci[i]].setValueAtIndex(inClustCount[this.ci[i]],j,
                    this.m.getValueAtIndex(i,j));
            }

            inClustCount[this.ci[i]]++;
        }

        return clusters;
    }

    /**
    *   Two dimensional array is returned containing (row) indices of data 
    *   points organized by cluster assignment.
    *   <br>Imagined as a table :
    *   <ol>
    *   <li>There are as many rows as there are clusters. Each row of table is 
    *   an array. (eg. in kmeans there are k rows)</li>
    *   <li>Each column of table is a (row) index that points to the location of
    *    a data point in microarray expression data.</li>
    *   </ol>
    *   @return int[][]
    */
    public int[][] getClusterIndices(){
        
        /*
        *   Initialize a two dimensional array with k subarrays/rows
        */
        int [][] clusterIndices = new int[this.k][];

        /*
        *   Initialize the subarrays/columns with cluster sizes (which was 
        *   counted during cluster assignment)
        */
        for (int i = 0;i<this.k;i++)
        {
            int[] ci = new int[this.cs[i]];
            clusterIndices[i] = ci;
        }

        /*
        *   Initialize array to hold counters so know where you are currently 
        *   incrementing (current cluster count)
        */
        int [] cc = new int[this.k];

        /*
        *   Iterate through cluster indices (holds index of cluster to which 
        *   each row of expression matrix is assigned to)
        *   Row index determined by cluster index (ith value in clusterIndices
        *   (ci) array)
        *   Column index determined by counter, want next 'empty'/unassigned 
        *   field, namely the count of values which have been added until this 
        *   point
        *   Value is i, the index of the data point to which the current cluster
        *   assignment corresponds to
        */
        for (int i=0; i<this.ci.length; i++)
        {
            clusterIndices[this.ci[i]][cc[this.ci[i]]] = i;
            cc[this.ci[i]] ++;
        }

        return clusterIndices;
    }

    /**
    *   Returns the centroids of the clusters produced.
    *   <br>Imagined as a table:
    *   <ol>
    *   <li>There are as many rows as there are clusters.</li>
    *   <li>There are as many columns as there are columns in original 
    *   expression data. (equal dimensionality)</li>
    *   </ol>
    *   @return double[][] 
    */
    public double[][] getClusterPrototypes(){
        return this.cp;
    }





    //__________________________________________________________________________
    //
    //      Kmeans Algorithm                                   
    //__________________________________________________________________________

    /**
    *   Call to perform K means clustering (repeat step 1 and step 2).
    *   <ol>
    *   <li>Initializes k cluster prototypes using specified initialization 
    *   method.</li>
    *   <br>Repeat :
    *	<li>Goes through each data point, assigning data points to nearest 
    *   cluster.</li>
    *   <li>Recalculates cluster means</li>
    *   <br>Until : 
    *   <li>reach maxRep (100) or until less than 1% move during cluster 
    *   assignment</li>
    *   </ol>
    *	@param takes an int for number of iterations to be run
    */
    public void doClustering(){

        /*
        *   Initialize the cluster prototypes using specified or default 
        *   intialization method.
        */

        this.cpInit.initClusters(this.k,this.m,this.distCalc);
        this.cp = this.cpInit.getClusterPrototypes();
    
        int maxRep = 100;
        int finishClustering = 0;
        int i = 0;

        /*
        *   Iterate cluster assignments until less than 1% of points move during
        *   cluster assignment, or maximum repetition number is reached
        */
        while(finishClustering == 0 && i< maxRep)
        {
            /*
            *   Step 1 : assign data points to nearest cluster
            */
            finishClustering = assignPointsToCluster();

            /*
            *   Step 2 : recalculate cluster center/prototype
            */
            calcClusterMean();

            i++;
        }
    }

    /**
    *	<b>Step 1</b> of kmeans iterative process. 
    *   <br>Goes through each row of expression data and assigns it to the 
    *   nearest cluster.
    *   <br>After all points are assigned, checks for empty clusters and, if 
    *   found, sets empty cluster prototype with random point from most 
    *   dispersed cluster.
    *   <br>Specifies if clustering should end or continue by returning 1 if 
    *   clustering can end.
    *   @return int 0 if clustering should countinue, 1 if clustering should end
    *   (less than 1% of data points change their cluster assignment) 
    */
    private int assignPointsToCluster(){

        /*
        *   Init array where the calculated distances of one row to every 
        *   cluster will be stored.
        */
        double[] allDists = new double[this.k];

        /*
        *   Reinit arrays to store size of clusters (cs) sum of squared error
        *   (sse)
        */
        this.cs = new int[this.k];
        this.sses = new double[this.k];
        this.ciorg = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i<this.k; i++){
            this.ciorg.add(new ArrayList<Integer>());
        }
        /*
        *   Init counter of points which do not change cluster assignment
        */
        int countUnmoved = 0;

        /*
        *   assign each row(gene) to the nearest cluster
        */
        for (int i=0; i<this.m.getRowSize(); i++) 
        {

            /*
            *   For each cluster prototype, calculate distance to current row 
            */
            for (int j=0; j<this.k; j++) 
            {
                /*
                *   Calculate distance and store in allDistances array 
                */
                allDists[j] = this.distanceMatrix.getValueAtIndex(i,j);
            }

            /*
            *   Find index corresponding to minimum distance (this is the 
            *   cluster assignment) and store in cluster indices array (ci) at 
            *   index i (current row in expression data)
            */
            int clusterIndexOfMinDist = 0;
            double minDist = allDists[0];

            for (int j = 1; j<this.k; j++) 
            {
                if (allDists[j] < minDist) 
                {
                    minDist = allDists[j];
                    clusterIndexOfMinDist = j;
                }
            }

            /*
            *   Check if just found cluster assignment has changed from previous
            *   iteration through all data points. I
            */
            if(this.ci[i] == clusterIndexOfMinDist)
            {
                countUnmoved ++;
            }
            else
            {
                this.ci[i] = clusterIndexOfMinDist;
                /*
                *   Store index of data point (i) currently viewing in 
                *   array list corresponding to cluster assignment
                */
                List<Integer> currentCluster = this.ciorg.get(clusterIndexOfMinDist);
                currentCluster.add(i);
            }

            /*
            *   Increment cluster size variable for found cluster
            */
            this.cs[clusterIndexOfMinDist]+=1;

            /*
            *   Add squared error (distance squared) to sse for found cluster 
            */
            this.sses[clusterIndexOfMinDist] += Math.pow(minDist,2);
        }

        /*
        *   Check for empty cluster. If found then assign empty cluster a data
        *   point and continue clustering
        */
        boolean emptyClusterFound = checkForEmptyClusterAndReassign();
        if (emptyClusterFound) 
        {
            return 0;
        }

        /*
        *   If less than 1% of data points change cluster assignment, end 
        *   clustering
        */
        if (countUnmoved > .99*this.ci.length)
        {
            return 1;
        }
        return 0;
    }

    /**
    *	<b>Step 2</b> of kmeans iterative process.
    *   <br>Calculates new mean of cluster after a reassignment step and saves 
    *   result in this.cp array (means are new cluster prototypes).
    */
    private void calcClusterMean(){

        /*
        *   For each cluster
        */
        for(int i = 0; i<this.ciorg.size(); i++)
        {
            /*
            *   For each datapoint (identified by index in expression values) assigned to cluster
            *   find distance of every other data point in cluster
            *   store sum of distances of point i to every other point in array
            */
            List<Integer> currentList = this.ciorg.get(i);
            if (currentList.size() > 0) {
                
                
                double[] sumD = new double[currentList.size()];
                /*
                *   Sum distances of every other data point in cluster to 
                */
                for(int j = 0; j<currentList.size(); j++)
                {
                    sumD[j] = 0;

                    for(int y = 0; y<currentList.size(); y++)
                    {
                        sumD[j] += this.distanceMatrix.getValueAtIndex(currentList.get(j),currentList.get(y));
                    }
                }

                /*
                *   Find minimum distance. Assign cluster centroid to that point
                */
                double minD = sumD[0];
                int indexMinD = 0;
                for(int j = 1; j<sumD.length; j++){
                    if(sumD[j]<minD){
                        indexMinD = j;
                        minD = sumD[j];
                    }
                }

                this.cp[i] = this.m.getRowAtIndex(currentList.get(indexMinD));
            }
        }
    }

    /**
    *   Checks for a cluster size of zero. If found, finds the cluster with the
    *   largest sse (calculated during cluster assignment) and assigns a random
    *   point from largest sse cluster to empty cluster prototype
    */
    private boolean checkForEmptyClusterAndReassign(){
        /*
        *   Iterate through each cluster size integer
        */
        for (int i=0; i<this.cs.length; i++)
        {
            /*
            *   If a cluster is empty, reinitialize the cluster prototype with a
            *   point belonging to the cluster witht he highest SSE (the cluster
            *   with the largest scatter)
            */
            if (this.cs[i] == 0 )
            {
                /*
                *   Find cluster with the largest sse
                */
                int idxMaxSSE = 0;
                double maxSSE = this.sses[0];


                for (int j=1; j<this.sses.length; j++)
                {
                    if (this.sses[j] > maxSSE){
                        maxSSE = this.sses[j];
                        idxMaxSSE = j;
                    }
                }

                /*
                *   Pick a random row from cluster with largest SSE
                */
                Random rand = new Random();
                int idxRand = (int)(this.cs[idxMaxSSE] * rand.nextDouble());

                /*
                *   Set empty cluster cluster prototype to that point
                */
                for (int j=0; j<this.cp[0].length; j++)
                {
                    this.cp[i][j] = this.cp[idxMaxSSE][j];
                }

                return true;
            }
        }
        return false;
    }
}
