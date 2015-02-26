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
*   <b>Clusters genes with similar expression patterns across multiple tissues</b><br>
*   <dl>
*       <dt>Receives single (or directory of) Allen Brain microarray analysis directories<dt>
*       <dd>Each Allen Brain directory must contain a MicroarrayExpression.csv, SampleAnnot.csv and Probes.csv file</dd>
*   </dl>
*   </p>
*   <p>
*   Each Allen Brain Directory is consecutively analyzed.
*   <ol>
*   <li>Expression data + annotations are parsed and packaged in an AllenDataMngr object</li>
*   <li>Expression data is clustered</li>
*   <li>Resulting clusters are sent to AllenDataMngr, which returns gene names corresponding to expression data</li>
*   <li>Each cluster is printed to a separate file, one gene per line</li>
*   </ol>
*   </p>
*   @author Alex Harenstein
*/

public class Allen2HPO {

    /** 
    *   Required input parsed from command line.<br>
    *   Directory containing following OR  subdirectories containing following :
    *   <ol>
    *   <li> Probes.csv</li>, <li>MicroarrayExpression.csv</li>, <li>SampleAnnot.csv</li>
    *   </ol>
    */
    private String dataPath = null;

    /** Optional input parsed from command line. If not specified will print into dataPath given in Clusters_OUTPUT.csv*/
    private String outputPath = null;

    /** @return Data path specified by user as parsed from command line */
    public String getDataPath(){
        return this.dataPath;
    }


    public static void main(String[] args) {

        Allen2HPO allen2hpo = new Allen2HPO();

        /**
        *   Parse the command line for directory path
        */
        allen2hpo.parseCommandLine(args);



        /**
        *   Read in the ontology file
        *   Can be used by cluster analysis to modify proximity measure calculation
        *   Currently not being used
        */
        //OntologyDataMngr ontology = new OntologyDataMngr(allen2hpo.getDataPath());



        /**
        *   Go through each brain donor directory. For each directory :
        *   1) Extract microarray expression and corresponding annotations (probe,sample)
        *   2) Perform cluster analysis
        *   3) Print cluster files
        */
        
        File parentDir = new File(allen2hpo.getDataPath());
        int donorCount = 0;

        /** Data path provided points to a single brain donor */

        if (allen2hpo.checkAllAllenBrainFilesPresent(parentDir)) 
        {
            /** Do the cluster analysis on single brain donor */
            allen2hpo.performSingleDonorAnalysis(parentDir);
            donorCount ++;
        }


        /** Data path provided points to a directory of brain donors */
        else
        {
            /** Get name of all files in parent directory */
            String[] files = parentDir.list();
            /** Check if file is a directory and if it is allenbrain compatible */
            for(String fileName : files)
            {
                File child = new File(allen2hpo.getDataPath() + parentDir.separator + fileName);
                if (child.isDirectory())
                {
                    if (allen2hpo.checkAllAllenBrainFilesPresent(child)) {
                        /** Do the cluster analysis on single brain donor */
                        allen2hpo.performSingleDonorAnalysis(child);
                        donorCount++;
                    }
                }
            }
        }

        /** Data path doesn't point to a compatible file */
        if (donorCount == 0)
        {
            System.out.println("No compatible Allen Brain directories were found");
        }



    }





    /**
    *   <p>Checks for presence of all necessary files for AllenDataMngr</p>
    *   <p>Returns true if following are present in directory :</p>
    *   <ol>
    *   <li>MicroarrayExpression.csv</li>
    *   <li>Probes.csv</li>
    *   <li>SampleAnnot.csv</li>
    *   </ol>
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

    /**
    *
    */
    private void performSingleDonorAnalysis(File dir){
        

        /**
        *   Init AllenDataMngr object to serve as wrapper of directory corresponding to a single Allen Brain donor
        */
        AllenDataMngr brainDataMngr = new AllenDataMngr(dir.getPath());

        /**
        *   Parse directory 
        */
        brainDataMngr.parseExpressionAndAnnotations();

        /**
        *   Average expression of probes with same gene name to create unique gene-expression pairs
        */
        brainDataMngr.collapseRepeatProbesToUniqueGenes();

        /**
        *   Normalize the data
        */
        brainDataMngr.meanNormalizeData();




        /**
        *   Cluster Data
        */
        ClusteringMngr clusteringMngr = new ClusteringMngr(brainDataMngr);

        clusteringMngr.doKmeansClusteringWithGapStat();
        clusteringMngr.writeOutputToFile(this.dataPath);
    }


    /**
     * Parse the command line using apache's CLI.
     */
    private void parseCommandLine (String[] args){
        try{
            Options options = new Options();

            options.addOption(new Option("D","data",true,"Path to data"));
            options.addOption(new Option("S","size",true,"NumberOf"));

            //options.addOption(new Option("O","output",false,"Path to write clusters to"));
        //    options.addOption(new Option("A","allOutput",false,"Print cluster prototypes to output file"));

            /* options.addOption(new Option("W","vcf2",true,"Path to downsampled VCF file"));
            options.addOption(new Option("D","ucsc",true,"Path to serialized UCSC file"));
            options.addOption(new Option("B","bed",true,"Path to bed file"));
            options.addOption(new Option("F","fname",true,"Output file name"));
            options.addOption(new Option(null,"cov",true,"coverage of original BAM"));
            options.addOption(new Option(null,"covdown",true,"coverage of downsampled BAM"));
            options.addOption(new Option(null,"bam",true,"name of original BAM"));*/

            Parser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("D")) {
                this.dataPath = cmd.getOptionValue("D");
                this.outputPath = this.dataPath+"/Clusters_OUTPUT.csv";

            } else {
                usage();
            }


        }
        catch (ParseException pe) {
            System.err.println("Error parsing command line options");
            System.err.println(pe.getMessage());
            System.exit(1);
        }

    }



    public static void usage() {
        System.err.println("[INFO] Usage: java -jar Allen2HPO.jar -D ????");
        System.err.println("[INFO] where ARGS comprises:");
        System.err.println("[INFO]");
        System.err.println("[INFO] -D: data directory with Allen Brain microarray files");
    System.exit(1);
    }

}
