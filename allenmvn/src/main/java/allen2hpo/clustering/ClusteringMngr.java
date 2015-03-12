package allen2hpo.clustering;

import allen2hpo.allen.AllenDataMngr;
import allen2hpo.matrix.Matrix;

import allen2hpo.matrix.FileWriter;

import allen2hpo.clustering.kmeans.Kmeans;
import allen2hpo.clustering.kmeans.calck.GapStat;
import allen2hpo.clustering.kmeans.initclust.*;
import allen2hpo.clustering.kmeans.distance.*;


/**
*	<p>
*	Responsible for interfacing between clustering output and 
*   {@link allen2hpo.allen AllenDataMngr}
*   <br>Given an {@link allen2hpo.allen AllenDataMngr}, various clustering 
*   methods can be performed on expression data
*	<br>Once clustering has been performed, uses results of clustering to print 
*   corresponding annotations to file<br>
*	</p>
*	@author Alex Hartenstein
*   5.3.2015
*/

public class ClusteringMngr{

    //__________________________________________________________________________
    //
    // Variables                                   
    //__________________________________________________________________________

    /** Wrapper for Microarray Expression data and corresponding annotations */
    private AllenDataMngr allenData;

    /** Object implementing Clusterable interface capabable of clustering data 
    *   in a Matrix object matrix */
    private Clusterable clusteringObject;

    /** Two dimensional array containg averages of clusters formed by clustering 
    *   Object */
    private double[][] prototypes;

    /** Each array corresponds to a cluster, each int in array corresponds to 
    *   (row) index of data point in original expression matrix */
    private int[][] clusterIndices;

    /** Gene names organized into clusters. Parallel to cluster Indices, 
    *   AllenDataMngr takes indices and returns corresponding names */
    private String [][] clusteredGeneNames;



    //__________________________________________________________________________
    //
    //  Constructors                                   
    //__________________________________________________________________________

    /**
    *   Constructor Method sets private variable AllenDataMngr 
    *   @param AllenData object (wrapper for expression data and corresponding 
    *   annotations)
    */
    public ClusteringMngr(AllenDataMngr allenDataMngr)
    {
    	this.allenData = allenDataMngr;
    }



    //__________________________________________________________________________
    //
    //  Clustering Methods                                   
    //__________________________________________________________________________

    /**
    *	Perform Kmeans cluster analysis using GapStat to calculate the value 
    *   of K
    */
    public void doKmeansClusteringWithGapStat()
    {

        DistComputable distCalc = new DistEuclidean();
        InitClusterable initClust = new InitClustMostDistant();
    	/*
    	*	Do gapstat analysis on expression data 
    	*/
        GapStat gap = 
            new GapStat(this.allenData.getExpression(),distCalc,initClust);
        
        System.out.println("GapStat Optimal number of clusters : "+gap.getK());

        /* 
        *	Init kmeans clustering object
		*	Use k value calculated by gap statistic
		*	Do not specify proximity measure (Uses default proximity measure, 
        *   euclidean distance)
        *   Do not specify cluster initialization method (Uses default, random 
        *   initialization)
		*/
        this.clusteringObject = 
            new Kmeans(this.allenData.getExpression(),gap.getK(),distCalc,
                initClust);


        /*
        *	Do the clustering
        */
        clusteringObject.doClustering();


        /*
        *	Retrieve results of clustering and store
        */
        this.prototypes = clusteringObject.getClusterPrototypes();
        this.clusterIndices = clusteringObject.getClusterIndices();

        /*
        *   Use AllenBrainMngr to get gene names organized into clusters
        */
        this.clusteredGeneNames = this.allenData.getGeneClusters
            (clusteringObject.getClusterIndices());
    }



    //__________________________________________________________________________
    //
    //  Write to file methods                                   
    //__________________________________________________________________________

    /**
    *   Write cluster prototypes to a file.
    */
    public void writeClusterPrototypesToFile(String outputPath)
    {
        FileWriter writer = new FileWriter();
        writer.createFileWithName(outputPath+"clusterPrototypes.txt");
                System.out.println(outputPath+"clusterPrototypes.txt");

        ///Write cluster prototypes to file
        for(int i =0;i<this.prototypes.length;i++){
            for(int j=0; j<this.prototypes[i].length-1; j++){
                writer.writeDouble(this.prototypes[i][j]);
                writer.writeDelimit();
            }
            writer.writeDouble(this.prototypes[i][this.prototypes[i].length-1]);
            writer.writeNextLine();
        }
        writer.closeFile();
    }

    /**
    *   Writes cluster_x.txt file.
    *   <br>One file per cluster, one gene per line in output directory
    */
    public void writeClusterGenesOneClusterPerFile(String outputPath)
    {

        for(int i =0;i<this.clusteredGeneNames.length;i++){
            FileWriter writer = new FileWriter();
            writer.createFileWithName
                (outputPath+"cluster_"+String.valueOf(i)+".txt");
                System.out.println(outputPath+"cluster_"+String.valueOf(i)+".txt");

            for(int j=0; j<this.clusteredGeneNames[i].length; j++){
                writer.writeString(this.clusteredGeneNames[i][j]);
                writer.writeNextLine();
            }
            writer.closeFile();
        }
    }

    /**
    *   Prints population.txt file.
    *   <br>All genes clustered, one gene per line.
    */
    public void writePopulationGenesToFile(String outputPath)
    {
        System.out.println(outputPath+"population.txt");
        String[] population = this.allenData.getAllGenes();

        FileWriter writer = new FileWriter();
        writer.createFileWithName(outputPath+"population.txt");

        for(int i=0; i<population.length; i++)
        {
            writer.writeString(population[i]);
            writer.writeNextLine();
        }
        writer.closeFile();
    }

    /**
    *   Prints cluster_OUTPUT.txt.
    *   All clusters one cluster per line, one gene per column.
    */
    public void writeAllClustersGenesToOneFile(String outputPath)
    {
        FileWriter writer = new FileWriter();
        writer.createFileWithName(outputPath+"clusters_OUTPUT.txt");

        for(int i =0; i<this.clusteredGeneNames.length; i++)
        {
            for(int j=0; j<this.clusteredGeneNames[i].length-1; j++){
                writer.writeString(this.clusteredGeneNames[i][j]);
                writer.writeDelimit();
            }
            writer.writeString
            (this.clusteredGeneNames[i][this.clusteredGeneNames[i].length-1]);
            
            writer.writeNextLine();
        }

        writer.closeFile();
    }



    //__________________________________________________________________________
    //
    //  Print to command line methods                                  
    //__________________________________________________________________________

    /**
    *   Print gene names organized into cluster in terminal.
    */
    public void printClusterGenesInTerminal()
    {
        for(int i =0;i<this.clusteredGeneNames.length;i++)
        {
            System.out.printf("Cluster %d",i);
            for(int j=0; j<this.clusteredGeneNames[i].length; j++)
            {
                System.out.printf(" %s,",this.clusteredGeneNames[i][j]);
            }
            System.out.printf
                ("\ncount of : %d\n\n",this.clusteredGeneNames[i].length);
        }
    }

    /**
    *   Print cluster prototypes in command line.
    */  
    public void printClusterPrototypesInTerminal()
    {
        System.out.println("CLUSTER PROTOTYPES:");
        for(int i=0; i<this.prototypes.length; i++){
            System.out.printf("%d HAS THE PROTOTYPE : \n",i);
            for(int j=0; j<this.prototypes[0].length; j++){
                System.out.printf("(%d) : %.3f\t",j,this.prototypes[i][j]);
            }
            System.out.printf("\n\n\n\n\n");
        }
    }
}