package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;
import java.util.Random;


/**
* <p>Performs kmeans cluster analysis:</p>
* <ol>
* <li>Class must include a {@link allen2hpo.clustering.Kmeans Kmeans} as a field
* <li>Class must implement the Kmeansable interface
* <li>In Kmeansable methods, calculate corresponding kmeans values (k value, seed clusters, distance equation)
* <li>Set calculated value in KmeansObject
* </ol>
*   Variables that affect speed of clustering :
*   cap_B, this.repeat
*	@author Alex Hartenstein
*/

public class Kmeans{

    /** The number of clusters */
    private int k = 0;

    /** An m x n matrix of data to be clustered */
    private Matrix m = null;

    /** cluster prototypes. The centroids of the clusters being calculated. A k x n two dimensional array */
    private double[][] cp = null;

    /**	cluster index. Stores the index of the cluster (0-(k-1)) to which each data point in matrix belongs */
    private int[] ci = null;

    /** Stores size of clusters are they are created */
    private int[] cs = null;

    /** Contains sum of squared errors for each cluster. As assign data points add sum of squared error, after assignment divide by cluster size cs */
    private double[] sses = null;

    /** Object implementing DistComputable interface which performs distance calculation. Default (Euclidean distance) is used if none provided in constructor method*/
    private DistComputable distCalc = null;


    ///CONSTRUCTOR METHODS
    /**
    *   Simplest constructor method. k,distance calculation,etc can be set using setter variables
    *   @param Data matrix to be clustered
    */
    public Kmeans(Matrix mat){
        this(mat,1);
    }

    /**
    *	@param Takes a matrix object of the data that is to be clustered
    *   @param int k is number of clusters data matrix should be partitioned into
    */
    public Kmeans(Matrix mat, int kval){
        this(mat,kval,new DistEuclidean());
    }

    /**
    *   @param matrix object is data to be clustered
    *   @param int k is number of clusters data matrix should be partitioned into
    *   @param distComputable implementing object to customize distance calculation
    */
    public Kmeans(Matrix mat, int kval, DistComputable d){
        setData(mat,kval);
        this.distCalc = d;
    }

    /**
    *   Initializes data matrix, kval and cluster index array
    *   Necessary because of various constructor methods
    */
    private void setData(Matrix mat, int kval){

        ///SET MATRIX FIELD
        this.m = mat;

        ///Initialize cluster index array. While iterative clustering, the cluster to which a data point belongs to will be stored here.
        this.ci = new int[mat.getRowSize()];

        setK(kval);
    }

    /**
    *   @param int k is number of clusters to data matrix should be partitioned into.
    */
    public void setK(int kval){
        this.k = kval;
        this.cs = new int[kval];

    }

    /**
    *   @param double[][] init cluster prototypes with k rows and number of columns corresponding to datamatrix column size
    */
    public void setInitClusters(double[][] clusterSeeds){
        this.cp = clusterSeeds;
    }

    /**
    *   @param distComputable object is able to calculate distance calculation between two points
    */
    public void setDistCalc(DistComputable d){
        this.distCalc = d;
    }

    /**
    *   Checks that all necessary components of kmeans algorithm are provided
    *   If parts are not provided initializes missing components with default values
    */
    private void preclusterCheck(){
        //Check that distance calcuation is specified. If not, use default of euclidean distance
        if (this.distCalc==null){
            setDistCalcBasic();
        }
        //Check that cluster prototypes array exists. If not initialize using random initialization
        if (this.cp == null){
            setInitClustersBasic();
        }
    }

    ///DEFAULT IMPLEMENTATION
    /**
    *	Default implementation : Most basic implementation of Kmeans cluster initialzation, just takes first 3 values of matrix.
    *   Called if no cluster prototypes are provided in constructor method (or set with setInitClusters)
    */
    public void setInitClustersBasic(){
        InitClustBasic init = new InitClustBasic();
        setInitClusters(init.initClusters(this.m,this.k));
    }

    /**
    *   Default implementation : most basic distance calculation (Euclidean distance) between two points.
    *   Called if no custom distComputable object is provided in constructor method (or set with setDistCalc)
    */
    public void setDistCalcBasic(){
        DistEuclidean dist = new DistEuclidean();
        setDistCalc(dist);
    }

    ///GETTER METHODS

    /** @return int k, number of clusters being formed */
    public int getK(){
        return this.k;
    }

    /** @return matrix object being clustered */
    public Matrix getData(){
        return this.m;
    }

    /** @return double[][] with k rows and dimensionality of original data. The means of the clustered data */
    public double[][] getClusterPrototypes(){
        return this.cp;
    }

    /**	@return int [] containing the index of the cluster prototype (from 0-(k-1)) that each data point is assigned too (in order of original data matrix) */
    public int[] getClusterAssignments(){
        return this.ci;
    }

    /**
    *	@return array of Matrix objects corresponding to the data matrix split into k clusters.
    */
    public Matrix[] getClusters(){

        //Initialize array of matrices of length k (one matrix per cluster)
        Matrix [] clusters = new Matrix[this.k];

        //Initialize matrix object for each cluster and place in array of matrices

        for (int i = 0;i<this.k;i++){
            //Initialize array of proper size from value stored in this.clusterSize (cs)
            double[][] ca = new double[this.cs[i]][this.m.getColumnSize()];
            Matrix cm = new Matrix(ca);
            clusters[i] = cm;
        }

        //Initialize array holding counter for each cluster (incremented as add row to matrix)
        int [] inClustCount = new int[this.k];


        //For each value in data matrix, check what cluster it belongs to and place in corresponding matrix
        for (int i=0;i<this.m.getRowSize();i++){
            for(int j=0; j<this.m.getColumnSize(); j++){
                clusters[this.ci[i]].setValueAtIndex(inClustCount[this.ci[i]],j,this.m.getValueAtIndex(i,j));
            }
            inClustCount[this.ci[i]]++;
        }

        return clusters;
    }

    /**
    *	Returns an array k long of arrays containing indices of data points
    */
    public int[][] getClusterIndices(){
        ///INITIALIZE ARRAY OF MATRICES OF LENGTH K (ONE MATRIX PER CLUSTER)
        int [][] clusterIndices = new int[this.k][];

        ///INITALIZE MATRIX OBJECT FOR EACH CLUSTER AND PLACE IN ARRAY OF MATRICES
        for (int i = 0;i<this.k;i++){
            ///INITALIZE ARRAY OF PROPER SIZE FROM VALUE HELD IN THIS. CLUSTER SIZE
            int[] ci = new int[this.cs[i]];
            clusterIndices[i] = ci;
        }

        ///INITIALIZE ARRAY TO HOLD COUNTERS SO KNOW WHERE CURRENTLY INCREMENTING
        int [] cc = new int[this.k];
        ///FOR EACH
        for (int i=0; i<this.ci.length; i++){
            clusterIndices[this.ci[i]][cc[this.ci[i]]] = i;
            cc[this.ci[i]] ++;

        }
        return clusterIndices;
    }


    ///KMEANS CLUSTERING
    /**
    *	Goes through each data point, assigning data points to nearest cluster.
    *   Then recalculates cluster means
    *	@param takes an int for number of iterations to be run
    */
    public void beginClustering(){
        if (this.m == null)
        throw new IllegalArgumentException("Data not initialized");
        if (this.k == 0)
        throw new IllegalArgumentException("k not initialized");

        ///Ensure that all components of kmeans algorithm have been set
        preclusterCheck();

        int maxRep = 100;
        int finishClustering = 0;
        int i = 0;

        //Iterate cluster assignments until less than 1% of points move during
        //cluster assignment, or maximum repetition number is reached
        while(finishClustering ==0 && i< maxRep){
            finishClustering = assignPointsToCluster();
            calcClusterMean();
            i++;
        }
    }

    /**
    *	STEP 1 of kmeans iterative process. Goes through each data point and
    *   assigns it to the nearest cluster.
    *   After all points are assigned, checks for empty clusters and if found,
    *   reinitializes empty cluster prototype with with points from most dispersed
    *   cluster.
    *   If empty cluster found, returns 1 (empty = true) meaning that clustering
    *   must continue (unless maxium iteration count has been reached)
    */
    private int assignPointsToCluster(){

        //Init array where all the calculated distances will be stored
        double[] allDists = new double[this.k];

        //Reinit arrays to store size of clusters (cs) sum of squared error(sse)
        this.cs = new int[this.k];
        this.sses = new double[this.k];

        //Declare distance variable. Summation of distance between each dimension
        double dist;

        //Init counter of points which do not change cluster assignment
        int countUnmoved = 0;

        ///GO THROUGH EACH DATA POINT, ASSIGNING THEM TO CLUSTERS
        for (int i=0; i<this.m.getRowSize(); i++) {

            ///FOR EACH DATA POINT CALCULATE THE DISTANCE FROM ALL CLUSTER CENTERS(SEED POINTS)
            for (int j=0; j<this.k; j++) {
                ///CALCULATE DISTANCE OF EACH DIMENSION FROM SELECTED MEAN
                dist = 0;

                double[] p1 = m.getRowAtIndex(i);///DATA POINT TO BE CLUSTERED
                double[] p2 = this.cp[j];///CLUSTER PROTOTYPE

                allDists[j] = this.distCalc.calculateProximity(p1,p2);///CALCULATE 'DISTANCE' FROM CLUSTER PROTOTYPE
            }


            ///FIND OUT WHICH MEAN IS THE CLOSEST
            ///INIT minDist AS THE FIRST VALUE
            double minDist = allDists[0];
            int indexOfMinDist = 0;


            ///ENUMERATE THROUGH REMAINING DISTANCES FIND THE MINIMUM

            for (int j = 1; j<this.k; j++) {
                if (allDists[j] < minDist) {
                    minDist = allDists[j];
                    indexOfMinDist = j;
                }
            }

            ///SAVE INDEX OF JUST ASSIGNED CLUSTER TO DATA POINT
            if(this.ci[i] == indexOfMinDist){
                countUnmoved ++;
            }
            else{
                this.ci[i] = indexOfMinDist;
            }

            ///INCREMENT SIZE OF CLUSTER
            this.cs[indexOfMinDist]+=1;

            ///ADD SQUARED ERROR (THE DISTANCE SQUARED) TO SUM OF SQUARED ERROR
            this.sses[indexOfMinDist] += Math.pow(minDist,2);
        }


        ///CHECK IF ANY OF THE CLUSTERS ARE EMPTY
        for (int i=0; i<this.cs.length; i++){
            ///IF A CLUSTER IS EMPTY, REINITIALIZE CLUSTER PROTOTYPE WITH A POINT BELONGING TO CLUSTER WITH HIGHEST SSE (BIGGEST SCATTER)
            if (this.cs[i] == 0 ){
                ///FIRST FIND CLUSTER WITH MAX SSE
                double maxSSE = this.sses[0];
                int idxMaxSSE = 0;
                for (int j=1; j<this.sses.length; j++){
                    if (this.sses[j] > maxSSE){
                        maxSSE = this.sses[j];
                        idxMaxSSE = j;
                    }
                }

                ///PICK A RANDOM DATA POINT FROM CLUSTER WITH LARGEST SSE
                Random rand = new Random();
                int idxRand = (int)(this.cs[idxMaxSSE] * rand.nextDouble());

                ///ASSIGN CLUSTER PROTOTYPE TO THAT POINT
                for (int j=0; j<this.cp[0].length; j++){
                    this.cp[i][j] = this.cp[idxMaxSSE][j];
                }
                return 0;
            }
        }

        ///Less than 1% of data points are reassigned to a new cluster
        if (countUnmoved > .99*this.ci.length){
            return 1;

        }
        return 0;

    }

    /**
    *	STEP 2 of kmeans iterative process. Calculates new mean of cluster after
    *   a reassignment and saves result in this.cp array (means are new cluster
    *   prototypes).
    */
    private void calcClusterMean(){
        ///INIT ARRAYS WHERE DATA IS STORED
        double[][] clusterSums = new double[this.k][this.m.getColumnSize()];


        ///ENUMERATE THROUGHD DATA POINTS
        ///SUM UP ALL DATA AND COUNT VALUES
        for (int i = 0; i<this.m.getRowSize(); i++) {
            for (int j = 0;j<this.m.getColumnSize();j++){
                clusterSums[this.ci[i]][j] += this.m.getValueAtIndex(i,j);
            }
        }

        ///CALCULATE AVERAGE OF EVERY DIMENSION
        for (int i=0; i<this.k; i++) {
            for (int j=0; j<this.m.getColumnSize(); j++) {

                this.cp[i][j] = clusterSums[i][j]/this.cs[i];
            }
        }
    }

    private void print(){
        for(int i = 0;i<this.cp.length;i++){
            for(int j = 0;j<this.cp[0].length;j++){
                System.out.printf("%.3f\t",this.cp[i][j]);
            }
            System.out.printf("\n");
        }
    }

    /**
    *
    */
    private void checkForEmptyCluster(){

    }
}
