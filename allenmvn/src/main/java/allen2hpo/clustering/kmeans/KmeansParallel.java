package allen2hpo.clustering.kmeans;

import allen2hpo.matrix.Matrix;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import allen2hpo.clustering.Clusterable;
import allen2hpo.clustering.kmeans.calck.*;
import allen2hpo.clustering.kmeans.distance.*;
import allen2hpo.clustering.kmeans.initclust.*;
import allen2hpo.clustering.kmeans.KmeansStepOneReturnObj;

import allen2hpo.clustering.kmeans.KmeansStepOne;
import allen2hpo.clustering.kmeans.KmeansStepTwo;

import allen2hpo.matrix.Matrix;

//import org.apache.log4j.Logger;


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

public class KmeansParallel implements Clusterable{

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

    /** Logger object to output info/warnings */
    //static Logger logger = Logger.getLogger(Kmeans.class);

    private ArrayList<int[]> perThreadClusterAssignments = null;

    private ArrayList<ArrayList<Integer>> clusterAssignments = null;

    private int numberThreads;

    private int linesPerThread;

    //__________________________________________________________________________
    //
    //  Constructors                                   
    //__________________________________________________________________________    

    /**
    *   Simplest constructor method requires no k value.
    *   @param Matrix expression data to be clustered.
    *   @param DistComputable distance calculation implementing DistComputable 
    *   interface to customize distance calculation.
    *   @param InitClusterable object specifying method used to initialize 
    *   cluster prototypes 
    */
    public KmeansParallel(Matrix mat, DistComputable dc, InitClusterable ic){
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
    public KmeansParallel(Matrix mat, int kval, DistComputable dc,InitClusterable cp){
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


        if (this.m.getRowSize() > 3000) {
            this.numberThreads = 2000;
   
        }
        else{
            this.numberThreads = 10;
        }
        //  Set number of threads to be made
        //  Calculate how many rows each thread should handle
        this.linesPerThread = (int)Math.ceil(this.m.getRowSize()/(double)this.numberThreads);

        this.perThreadClusterAssignments = new ArrayList<int[]>();
        //  Init cluster assignment array for each thread. During each iteration
        //  Cluster assignment is check with previous assignment to see if it moved
        for (int i=0; i<numberThreads; i++){
            int[] clusterAssignmentsForThread = new int[this.linesPerThread];

            for (int j = 0; j<this.linesPerThread; j++){
                clusterAssignmentsForThread[j] = 0;
            }
            this.perThreadClusterAssignments.add(clusterAssignmentsForThread);
        }


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
            ArrayList<Integer> ca = this.clusterAssignments.get(i);
            ArrayList<double[]> clusterExpression = new ArrayList<double[]>();

            for (int j = 0; j<ca.size(); j++) {
                clusterExpression.add(this.m.getRowAtIndex(ca.get(j)));
                
            }
            Matrix cm = new Matrix(clusterExpression);
            clusters[i] = cm;
        }

        return clusters;
    }


    public Matrix[] getClustersold(){

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
    *   Clusterable interface method
    */
    public ArrayList<ArrayList<Integer>> getClusterIndices(){
        return this.clusterAssignments;
    }

    /**
    *   Clusterable interface method
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
    */
    public void doClustering(){

        
        //   Initialize the cluster prototypes using specified or default 
        //   intialization method.
        this.cpInit.initClusters(this.k,this.m,this.distCalc);
        this.cp = this.cpInit.getClusterPrototypes();
        
     
        
        int maxRep = 100;
        int finishClustering = 0;
        int i = 0;

        //   Iterate cluster assignments until less than 1% of points move during
        //   cluster assignment, or maximum repetition number is reached
        while(finishClustering == 0 && i< maxRep)
        {
            //   Step 1 : assign data points to nearest cluster
            finishClustering = assignPointsToCluster();

            //   Step 2 : recalculate cluster center/prototype
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
    private synchronized int assignPointsToCluster(){

        //  Init all values that are calculated during cluster assignment step :
        
        //  Cluster assignments array : one array per cluster, filled with 
        //  all indices corresponding to it
        this.clusterAssignments = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i<this.k; i++){
            ArrayList<Integer> empty = new ArrayList<Integer>();
            this.clusterAssignments.add(empty);
        }
        this.cs = new int[this.k];
        this.sses = new double[this.k];
        int countUnmoved = 0;
        

        //  Start threads

        //  Init threads
        ExecutorService executor = Executors.newFixedThreadPool(numberThreads);
        List<Future<KmeansStepOneReturnObj>> list = 
            new ArrayList<Future<KmeansStepOneReturnObj>>();

        for (int i=0; i<this.numberThreads; i++){
            //  Calculate start and end indices for thread
            int start = i*this.linesPerThread;
            int end = Math.min(this.m.getRowSize(),start+this.linesPerThread);

            //  Init thread with data
            Callable<KmeansStepOneReturnObj> worker = new KmeansStepOne(start,
                                    end,
                                    this.k,
                                    this.m, 
                                    this.cp, 
                                    this.distCalc,
                                    this.perThreadClusterAssignments.get(i));
            //  Start thread
            Future<KmeansStepOneReturnObj> submit = executor.submit(worker);

            //  Save thread to list
            list.add(submit);
        }
    

        //  Retrieve results

        for (Future<KmeansStepOneReturnObj> future : list)
        {
            try{
                KmeansStepOneReturnObj results = future.get();
                
                //  Save this round of cluster assignments at proper index 
                //  for next iteration
                int[] resultClusterAssignments = 
                    results.getClusterAssignments();
                this.perThreadClusterAssignments.set(list.indexOf(future),
                    resultClusterAssignments);

                
                //  increment count unmoved
                countUnmoved += results.getCountUnmoved();
                
                //  Merge results from threads
                int[] resultCS = results.getClusterSizeArray();
                double[] resultSSE = results.getSumOfSquareErrorsArray();
                ArrayList<ArrayList<Integer>> resultClusterAssignment = 
                    results.getThreadClusterAssignments();
                
                for(int i = 0; i<this.k; i++)
                {
                    this.cs[i] += resultCS[i];
                    this.sses[i] += resultSSE[i];
                    ArrayList thisClusterAssignments = 
                        this.clusterAssignments.get(i);
                    thisClusterAssignments.addAll(resultClusterAssignment.get(i));
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //  Shut down concurrency 
        executor.shutdown();
        
        //   Check for empty cluster. If found then assign empty cluster a data
        //   point and continue clustering
    
        boolean emptyClusterFound = checkForEmptyClusterAndReassign();
        if (emptyClusterFound) {
            return 0;
        }

        //   If less than 1% of data points change cluster assignment, end 
        //   clustering
        if (countUnmoved > .99*this.m.getRowSize()){
            return 1;
        }
        return 0;
    }

    /**
    *	<b>Step 2</b> of kmeans iterative process.
    *   <br>Calculates new mean of cluster after a reassignment step and saves 
    *   result in this.cp array (means are new cluster prototypes).
    */
    public synchronized void calcClusterMeanOld(){
        
        //   Init array where sum of each cluster is stored
        double[][] clusterSums = new double[this.k][this.m.getColumnSize()];

        //   Iterate through each row of expression values (genes)
        //   Add (each dimension of) expression value to cluster sum (at index of
        //   cluster to which it belongs)
        for (int i = 0; i<this.clusterAssignments.size(); i++) 
        {
            //  
            ArrayList<Integer> cluster = this.clusterAssignments.get(i);
            
            //  For each data point assigned to cluster i
            for (int z = 0; z<cluster.size(); z++)
            {
                //  Go through each column and calculate average
                for (int j = 0; j<this.m.getColumnSize();j++)
                {

                    clusterSums[i][j] += this.m.getValueAtIndex(cluster.get(z),j);
                }
            }
        }

        //   Find mean of every dimension
        for (int i=0; i<this.k; i++) 
        {
            for (int j=0; j<this.m.getColumnSize(); j++) 
            {
                this.cp[i][j] = clusterSums[i][j]/this.cs[i];
            }
        }
    }

    public void calcClusterMean(){

        ExecutorService executor = Executors.newFixedThreadPool(numberThreads);
        List<Future<double[]>> list = 
            new ArrayList<Future<double[]>>();

        for (int i=0; i<this.k; i++){
        
            //  Init thread with data
            Callable<double[]> worker = new KmeansStepTwo(
                                    this.m, 
                                    this.clusterAssignments.get(i));
            //  Start thread
            Future<double[]> submit = executor.submit(worker);

            //  Save thread to list
            list.add(submit);
        }
    

        //  Retrieve results
        for (Future<double[]> future : list)
        {
            try{
                double[] prototype = future.get();
                
                this.cp[list.indexOf(future)] = prototype;
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        //  Shut down concurrency 
        executor.shutdown();
    }
    /**
    *   Checks for a cluster size of zero. If found, finds the cluster with the
    *   largest sse (calculated during cluster assignment) and assigns a random
    *   point from largest sse cluster to empty cluster prototype
    */
    private boolean checkForEmptyClusterAndReassign(){
    
        //   Iterate through each cluster size integer
        for (int i=0; i<this.clusterAssignments.size(); i++)
        {
            ArrayList<Integer> currentCluster = this.clusterAssignments.get(i);
            //   If a cluster is empty, reinitialize the cluster prototype with a
            //   point belonging to the cluster witht he highest SSE (the cluster
            //   with the largest scatter)
            if (currentCluster.size() == 0 )
            {
                //   Find cluster with the largest sse
                int idxMaxSSE = 0;
                double maxSSE = this.sses[0];

                for (int j=1; j<this.sses.length; j++)
                {
                    if (this.sses[j] > maxSSE){
                        maxSSE = this.sses[j];
                        idxMaxSSE = j;
                    }
                }

                //   Pick a random row from cluster with largest SSE
                Random rand = new Random();
                int idxRand = (int)(this.cs[idxMaxSSE] * rand.nextDouble());
                //   Set empty cluster cluster prototype to that point
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
