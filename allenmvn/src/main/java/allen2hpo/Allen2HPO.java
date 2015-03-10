package allen2hpo;

import allen2hpo.allen.*;
import allen2hpo.allen.ontology.*;
import allen2hpo.clustering.ClusteringMngr;
import allen2hpo.matrix.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;

/** Command line parser from apache */
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

/**
*   <p>
*   Responsible for managing lifecycle of program.
*   <br>Given a single (or directory of) Allen Brain microarray analysis 
*   directories, finds clusters of genes with similar expression patterns across
*    multiple tissues.
*   <br>Each Allen Brain Directory is consecutively analyzed :
*   <ol>
*   <li>Expression data and annotations are parsed and packaged in an 
*   {@link allen2hpo.allen AllenDataMngr} object</li>
*   <li>Expression data is clustered using selected clustering method.</li>
*   <li>{@link allen2hpo.clustering ClusteringMngr} prints clusters, one cluster
*   per file, one gene per line using {@link allen2hpo.allen AllenDataMngr} to 
*   retrieve gene names.</li>
*   </ol>
*   </p>
*   @author Alex Hartenstein
*/

public class Allen2HPO {

    //__________________________________________________________________________
    //
    //  Main                              
    //__________________________________________________________________________

    /**
    *   <p>Lifecycle of program.
    *   <ol>
    *   <li>Parse the command line, getting path to directory of data to be 
    *   analyzed.</li>
    *   <li>For each directory found containing necessary files for analysis, 
    *   perform analysis by calling 'doSingleDonorAnalysis'</li>
    *   </ol>
    *   </p>
    */
    public static void main(String[] args) {

        Allen2HPO allen2hpo = new Allen2HPO();

        
        /*  Parse the command line for directory path */
        allen2hpo.parseCommandLine(args);


        /*
        *   Read in the ontology file
        *   Can be used by cluster analysis to modify proximity measure 
        *   calculation
        *   Currently not being used
        */
        
        //OntologyDataMngr ontology = 
            //new OntologyDataMngr(allen2hpo.getDataPath());


        /*
        *   Go through each brain donor directory. For each directory :
        *   1) Extract microarray expression and corresponding annotations
        *   (probe,sample)
        *   2) Perform cluster analysis
        *   3) Print cluster files
        */
        
        File parentDir = new File(allen2hpo.getDataPath());
        int donorCount = 0;

        /*  
        *   Data path provided points to a single brain donor.  Do cluster 
        *   analysis on single brain donor.
        */
        if (allen2hpo.checkAllAllenBrainFilesPresent(parentDir)) 
        {
            allen2hpo.doSingleDonorAnalysis(parentDir);
            donorCount ++;
        }


        /* 
        *   Data path provided points to a directory of brain donors 
        *   (ie contains subdirectories).
        *   Find Allen Brain subdirectories and analyze them consecutively
        */
        else
        {
            /*  Get name of all files in parent directory, check if allenBrain
            *   compatible */
            String[] files = parentDir.list();
            for(String fileName : files)
            {
                File child = new File(allen2hpo.getDataPath() 
                    + parentDir.separator + fileName);
                if (child.isDirectory())
                {
                    /*  
                    *   Child is a single brain donor. Do cluster analysis. 
                    */
                    if (allen2hpo.checkAllAllenBrainFilesPresent(child)) 
                    {
                        allen2hpo.doSingleDonorAnalysis(child);
                        donorCount++;
                    }
                }
            }
        }

        /** 
        *   Data path doesn't point to a compatible file 
        */
        if (donorCount == 0)
        {
            System.out.println("No compatible Allen Brain directories found");
        }

    }



    //__________________________________________________________________________
    //
    //  Class Variables                                   
    //__________________________________________________________________________

    /** Required input parsed from command line. Directory containing expression
    *   data and sample/probe annotations (OR subdirectories containing said) */
    private String dataPath = null;

    /** Optional input parsed from command line. If not specified will print 
    *   into dataPath given in Clusters_OUTPUT.csv*/
    private String outputPath = null;

    /** @return Data path specified by user as parsed from command line */
    public String getDataPath(){
        return this.dataPath;
    }



    //__________________________________________________________________________
    //
    //  Allen2HPO Methods                                   
    //__________________________________________________________________________

    /**
    *   <p>Do cluster analysis on one brain directory and print out the results
    *   <ol>
    *   <li>Parse all data contained in directory using {@link allen2hpo.allen 
    *   AllenDataMngr}</li>
    *   <li>Collapse multiple probes to unique gene-expression value pairs</li>
    *   <li>Mean normalize the data</li>
    *   <li>Cluster the data using kmeans with gap statistic</li>
    *   <li>Print cluster files</li>
    *   </ol>
    *   </p>
    */
    private void doSingleDonorAnalysis(File dir){
        

        /*
        *   Init AllenDataMngr object to serve as wrapper of directory 
        *   corresponding to a single Allen Brain donor
        */
        AllenDataMngr brainDataMngr = new AllenDataMngr(dir.getPath());

        /*
        *   Parse directory 
        */
        brainDataMngr.parseExpressionAndAnnotations();

        /*
        *   Average expression of probes with same gene name to create unique 
        *   gene-expression pairs
        */
        brainDataMngr.collapseRepeatProbesToUniqueGenes();

        /*
        *   Normalize the data
        */
        brainDataMngr.meanNormalizeData();

        /*
        *   Cluster Data
        */
        ClusteringMngr clusteringMngr = new ClusteringMngr(brainDataMngr);

        /*
        *   Perform Kmeans clustering using the gap statistic to calculate k
        */
        clusteringMngr.doKmeansClusteringWithGapStat();

        /*
        *   Print output to terminal
        */
        clusteringMngr.printClusterGenesInTerminal();

        /*
        *   Write population file (all genes clustered one gene per line)
        */
        clusteringMngr.writePopulationGenesToFile(this.dataPath);

        /*
        *   Write one cluster per file, one gene per line
        */
        clusteringMngr.writeClusterGenesOneClusterPerFile(this.dataPath);
    }

    /**
    *   <p>Checks for presence of all necessary files for AllenDataMngr.<br>
    *   Returns true if following are present in directory :
    *   <ol>
    *   <li>MicroarrayExpression.csv</li>
    *   <li>Probes.csv</li>
    *   <li>SampleAnnot.csv</li>
    *   </ol>
    *   </p>
    *   @param File directory to be checked
    *   @return boolean true if all present, false if any missing
    */
    private boolean checkAllAllenBrainFilesPresent(File dir){
        boolean expression = false;
        boolean probes = false;
        boolean samples = false;

        /** Check for presence of all necessary files */
        for(String fileName : dir.list())
        {
            if (fileName.equals("MicroarrayExpression.csv") )
                expression = true;
            if (fileName.equals("Probes.csv") )
                probes = true;
            if (fileName.equals("SampleAnnot.csv") )
                samples = true;
        }

        /** If all necessary files present, return true */
        if (expression == true && probes == true && samples == true)
            return true;

        return false;
    }



    //__________________________________________________________________________
    //
    //  Parse Command Line Methods                                   
    //__________________________________________________________________________

    /**
     * Parse the command line using apache's CLI.
     */
    private void parseCommandLine (String[] args)
    {
        try
        {
            Options options = new Options();

            options.addOption(new Option("D","data",true,"Path to data"));
            options.addOption(new Option("S","size",true,"NumberOf"));
     
            Parser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("D")) {
                this.dataPath = cmd.getOptionValue("D");
                this.outputPath = this.dataPath+"/Clusters_OUTPUT.csv";

            } else 
            {
                usage();
            }


        }
        catch (ParseException pe) 
        {
            System.err.println("Error parsing command line options");
            System.err.println(pe.getMessage());
            System.exit(1);
        }

    }

    public static void usage() 
    {
        System.err.println("[INFO] Usage: java -jar Allen2HPO.jar -D ???");
        System.err.println("[INFO] where ARGS comprises:");
        System.err.println("[INFO]");
        System.err.println
        ("[INFO] -D: data directory with Allen Brain microarray files");
        System.exit(1);
    }

}
