package allen2hpo.clustering.kmeans.algorithms.kmediod_parallel;

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

import allen2hpo.clustering.kmeans.algorithms.kmeans_parallel.KmeansStepOneReturnObj;
import allen2hpo.clustering.kmeans.algorithms.kmediod_parallel.KmediodStepOne;
import allen2hpo.clustering.kmeans.algorithms.kmediod_parallel.KmediodStepTwo;

import allen2hpo.matrix.Matrix;
import allen2hpo.matrix.DistanceMatrix;

import org.apache.log4j.Logger;


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

public class KmediodParallel implements Clusterable{

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

    /** prototype index. centroid of each cluster is a point in the matrix.
    index is stored here (corresponding datpoint stored in cp) */
    private int[] cpi = null;

    /**	cluster index. Stores the index of the cluster (0-(k-1)) to which 
    *   each data point in expression matrix belongs */
    private int[] ci = null;

    /** cluster index organized. for each cluster is an arraylist that stores 
    *   indices ind ata matrix that belong to cluster at list index */
    private ArrayList<ArrayList<Integer>> ciorg = null;

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

    /** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(KmediodParallel.class.getName());

    private Matrix distanceMatrix = null;


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
    public KmediodParallel(Matrix mat, DistComputable dc, InitClusterable ic){
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
    public KmediodParallel(Matrix mat, int kval, DistComputable dc,InitClusterable cp){
        if (mat == null)
            throw new IllegalArgumentException("Data not initialized");
        if (dc == null)
            throw new 
        IllegalArgumentException("proximity measure object not initialized");
        if (cp == null)
            throw new 
        IllegalArgumentException("cluster init object not initialized");

 
        //   Set data matrix private variable 
        this.m = mat;

        //   Set K value 
        setK(kval);
 
        //   Set distance calculation object 
        this.distCalc = dc;
 
        //   Initialize cluster prototype start values using InitClusterable 
        //   object
        this.cpInit = cp;
 
        //   Initialize cluster index array. While iterative clustering, the 
        //   cluster to
        //   which a data point belongs to will be stored here. 
        this.ci = new int[mat.getRowSize()];

        //   Init distance matrix that will store distance of each point to every other.
        this.distanceMatrix = new DistanceMatrix(this.m,this.distCalc);

        if (this.m.getRowSize() > 3000) {
            this.numberThreads = 260;
   
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
         
        //   Set K value 
        this.k = kval;
         
        //   Init cluster size array. Stores number of points assigned to each 
        //   cluster in step 1. 
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

    public int getThreadCount(){
        return this.numberThreads;
    }

    public int getRowsPerThread(){
        return this.linesPerThread;
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

        //   Initialize array with length k (one matrix per cluster)
        Matrix [] clusters = new Matrix[this.k];

        //   Initialize each matrix object in clusters array with correct size
        for (int i = 0;i<this.k;i++)
        {
            ArrayList<Integer> ca = this.ciorg.get(i);
            ArrayList<double[]> clusterExpression = new ArrayList<double[]>();

            for (int j = 0; j<ca.size(); j++) {
                clusterExpression.add(this.m.getRowAtIndex(ca.get(j)));
                
            }
            Matrix cm = new Matrix(clusterExpression);
            clusters[i] = cm;
        }

        return clusters;
    }

    /**
    *   Clusterable interface method
    */
    public ArrayList<ArrayList<Integer>> getClusterIndices(){
        return this.ciorg;
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
        this.cpi = this.cpInit.getClusterPrototypeIndices();

     
        
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
        log.info("finished max iterations");
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
        this.ciorg = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i<this.k; i++){
            ArrayList<Integer> empty = new ArrayList<Integer>();
            this.ciorg.add(empty);
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
            Callable<KmeansStepOneReturnObj> worker = new KmediodStepOne(start,
                                    end,
                                    this.k,
                                    this.perThreadClusterAssignments.get(i),
                                    this.distanceMatrix,
                                    this.cpi);
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
                        this.ciorg.get(i);
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
    public void calcClusterMean(){

        ExecutorService executor = Executors.newFixedThreadPool(numberThreads);
        List<Future<Integer>> list = 
            new ArrayList<Future<Integer>>();

        for (int i=0; i<this.k; i++){
            ArrayList<Integer> clusterIndices = this.ciorg.get(i);
            if (clusterIndices.size() > 1) {
                    //  Init thread with data
                Callable<Integer> worker = new KmediodStepTwo(
                                        this.distanceMatrix, 
                                        this.ciorg.get(i));
                //  Start thread
                Future<Integer> submit = executor.submit(worker);

                //  Save thread to list
                list.add(submit);
            }
            
        }
    

        //  Retrieve results
        for (Future<Integer> future : list)
        {
            try{
                Integer prototype = future.get();
                
                this.cpi[list.indexOf(future)] = prototype;
                this.cp[list.indexOf(future)] = this.m.getRowAtIndex(prototype);
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

                int indexOfNewCP = findIndexOfMostDistantPointInCluster(idxMaxSSE);
                /*
                *   Set empty cluster cluster prototype to that point
                */
                for (int j=0; j<this.cp[0].length; j++)
                {
                    this.cp[i][j] = this.m.getValueAtIndex(indexOfNewCP,j);
                }

                return true;
            }
        }
        return false;
    }

    /**
    *   Given the index of a cluster, finds point furthest away from the cluster mean and 
    *   returns its index (in all expression data)
    *   Called by checkForEmptyClusterAndReassign.
    */
    int findIndexOfMostDistantPointInCluster(int indexOfClusterWithLargestSSE){
        //  Get array of all points (an array of indices pointing to )
        ArrayList<Integer> pointsInCluster = this.ciorg.get(indexOfClusterWithLargestSSE);

        //  Calculate mean of all points in the cluster
        double sum[] = new double[this.m.getColumnSize()];
        for(int i = 0; i<pointsInCluster.size(); i++){
            for(int j=0; j<this.m.getColumnSize(); j++){
                sum[j] += this.m.getValueAtIndex(pointsInCluster.get(i),j);

            }
        }
        for(int i=0; i<this.m.getColumnSize(); i++){
            sum[i] /= pointsInCluster.size();
        }

        //  Find point furthest away from the mean
        double maxDist = 0;
        double currentDist = 0;
        int indexMaxDistFromCurrent = 0;
        
        for(int i = 0; i<pointsInCluster.size(); i++){        
            //  Calculate distance from mean of previous 
            currentDist = this.distCalc.calculateProximity
                (sum,m.getRowAtIndex(pointsInCluster.get(i)));
            
            //  Check if furthest away and set current max if it is
            if (currentDist > maxDist) 
            {
                indexMaxDistFromCurrent = pointsInCluster.get(i);
                maxDist = currentDist;
            }
            
       }

       return indexMaxDistFromCurrent;
    }
}
