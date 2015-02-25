package allen2hpo;

import allen2hpo.allen.*;
import allen2hpo.allen.ontology.*;

import allen2hpo.clustering.kmeans.Kmeans;
import allen2hpo.clustering.kmeans.calck.GapStat;

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
*   Parse expression data from multiple brains into data matrices
*   Cluster data matrices
*   Print one cluster per file with single gene name per line
*   @author Alex Harenstein
*/

public class Allen2HPO {

    /** Required input parsed from command line. Directory that contains Probes.csv, MicroarrayExpression.csv, SampleAnnot.csv*/
    private String dataPath = null;

    /** Optional input parsed from command line. If not specified will print into dataPath given in Clusters_OUTPUT.csv*/
    private String outputPath = null;

    /** Getter for dataPath */
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
        *   Go through each brain donor. For Each brain :
        *   1) Extract microarray expression and corresponding annotations (probe,sample)
        *   2) Perform cluster analysis
        *   3) Export cluster files
        */
        File parentDir = new File(allen2hpo.getDataPath());
        int donorCount = 0;
        /** Data path provided points to a single brain donor */
        if (allen2hpo.checkIfDirAllenDataMngrCompatible(parentDir)) {
            allen2hpo.performSingleDonorAnalysis(parentDir);
            donorCount ++;
        }
        /** Check if data path provided points to a directory of brain donors */
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
                    if (allen2hpo.checkIfDirAllenDataMngrCompatible(child)) {
                        allen2hpo.performSingleDonorAnalysis(child);
                        donorCount++;
                    }
                }
            }
        }
        if (donorCount == 0)
        {
            System.out.println("No compatible Allen Brain directories were found");
            break;
        }





        /**
        *   Read in the ontology file
        *   In future can be used by cluster analysis to modify proximity measure calculation
        */
    //    OntologyDataMngr ontology = new OntologyDataMngr(allen2hpo.getDataPath());

    }

    /**
    *   Checks for presence of all necessary files for AllenDataMngr
    *   @param File object representing a directory to be checked
    */
    private boolean checkIfDirAllenDataMngrCompatible(File dir){
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
        ///Must be set in order to perfrom analysis. Should correspond to number of rows in microarray analysis file
        int numberOfProbes = 63000;

        AllenDataMngr brainData = new AllenDataMngr(this.dataPath,numberOfProbes);

        Cluster clust = new Cluster(brainData);

    }

    /**
    *   Cluster nested class is responsible for
    *   1) performing cluster analysis on a single brain
    *   2) printing output to cl/file
    */
    class Cluster{
        private Matrix prototypes;
        /**
        *   Constructor method performs cluster method and prints
        *   @param AllenData object (contains fully parsed brain microarray expression/annotations)
        */
        public Cluster(AllenDataMngr mngr){
            ///Initialize kmeans object and cluster data
            GapStat gap = new GapStat(mngr.getExpression());
            Kmeans kmeans = new Kmeans(mngr.getExpression(),gap.getK());

            kmeans.performClustering();
            ///Print clusters
            writeOutputToFile(mngr, kmeans);

            this.prototypes = new Matrix (kmeans.getClusterPrototypes());
        }

        public Matrix getPrototypes(){
            return this.prototypes;
        }


        /**
        *   Prints names of genes in a cluster into a csv file
        */
        private void writeOutputToFile(AllenDataMngr mngr, Kmeans kmeans){

            ///Get gene names corresponding to indices in the clustering
            String[][] clusters = mngr.getGeneClusters(kmeans.getClusterIndices());

            ///Init 2d array k elements long containing cluster prototypes
            double [][] protos = kmeans.getClusterPrototypes();


            ///Write gene names to file
            FileWriter writer = new FileWriter();
            writer.createFileWithName(outputPath);
            writeAllClustersGenesToOneFile(writer,clusters);
            writeClusterPrototypesToFile(writer,protos);
            writer.closeFile();


            writeClusterGenesToFile(dataPath,clusters);
            writePopulationGenesToFile(dataPath,mngr.getAllGenes());

            ///Print clusters in terminal
            printClusterPrototypesInTerminal(protos);
            printClusterGenesInTerminal(clusters);

        }

        private void printClusterGenesInTerminal(String[][] clusters){
            for(int i =0;i<clusters.length;i++){
                System.out.printf("Cluster %d",i);
                for(int j=0; j<clusters[i].length-1; j++){
                    System.out.printf(" %s,",clusters[i][j]);
                }
                System.out.printf(" %s,",clusters[i][clusters[i].length-1]);
                System.out.printf("\ncount of : %d\n\n",clusters[i].length);
            }
        }

        private void writeClusterPrototypesToFile(FileWriter writer, double[][] protos){

            ///Write cluster prototypes to file
            for(int i =0;i<protos.length;i++){
                for(int j=0; j<protos[i].length-1; j++){
                    writer.writeDouble(protos[i][j]);
                    writer.writeDelimit();
                }
                writer.writeDouble(protos[i][protos[i].length-1]);
                writer.writeNextLine();
            }
        }

        private void writeClusterGenesToFile(String dir, String[][] clusters){
            for(int i =0;i<clusters.length;i++){
                FileWriter newFile = new FileWriter();
                newFile.createFileWithName(dir+"/cluster_"+String.valueOf(i)+".txt");

                for(int j=0; j<clusters[i].length; j++){
                    newFile.writeString(clusters[i][j]);
                    newFile.writeNextLine();
                }
                newFile.closeFile();
            }
        }

        private void writePopulationGenesToFile(String dir, String[] array){

            FileWriter newFile = new FileWriter();
            newFile.createFileWithName(dir+"/population.txt");

            for(int i=0; i<array.length; i++){
                newFile.writeString(array[i]);
                newFile.writeNextLine();
            }
            newFile.closeFile();
        }

        private void writeAllClustersGenesToOneFile(FileWriter writer, String[][] clusters){
            for(int i =0; i<clusters.length; i++){
                for(int j=0; j<clusters[i].length-1; j++){
                    writer.writeString(clusters[i][j]);
                    writer.writeDelimit();
                }
                writer.writeString(clusters[i][clusters[i].length-1]);
                writer.writeNextLine();
            }
        }

        private void printClusterPrototypesInTerminal(double [][]protos){
            ///Print cluster prototypes in command line
            System.out.println("CLUSTER PROTOTYPES:");
            for(int i=0; i<protos.length; i++){
                System.out.printf("%d HAS THE PROTOTYPE : \n",i);
                for(int j=0; j<protos[0].length; j++){
                    System.out.printf("(%d) : %.3f\t",j,protos[i][j]);
                }
                System.out.printf("\n\n\n\n\n");
            }
        }

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
