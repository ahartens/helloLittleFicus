package allen2hpo;

import allen2hpo.allen.*;
import allen2hpo.allen.ontology.*;
import allen2hpo.clustering.ClusteringMngr;
import allen2hpo.matrix.*;
import allen2hpo.hpo.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;


/** Command line parser from apache */
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import java.io.InputStream;



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

        
        // Parse the command line for directory path 
        allen2hpo.parseCommandLine(args);

        //   Read in the ontology file
        //   Can be used by cluster analysis to modify proximity measure 
        //   calculation
        //   Currently not being used
        
        //OntologyDataMngr ontology = 
            //new OntologyDataMngr(allen2hpo.getDataPath());


        //   Go through each brain donor directory. For each directory :
        //   1) Extract microarray expression and corresponding annotations
        //   (probe,sample)
        //   2) Perform cluster analysis
        //   3) Print cluster files
        
        File parentDir = new File(allen2hpo.getDataPath());
        int donorCount = 0;

        //   Data path provided points to a single brain donor.  Do cluster 
        //   analysis on single brain donor.

        int status = allen2hpo.checkAllAllenBrainFilesPresent(parentDir);
        if (status != 2) 
        {
            allen2hpo.doSingleDonorAnalysis(parentDir);
            donorCount ++;
        }


        //   Data path provided points to a directory of brain donors 
        //   (ie contains subdirectories).
        //   Find Allen Brain subdirectories and analyze them consecutively

        else
        {
            //  Get name of all files in parent directory, check if allenBrain
            //  compatible
            String[] files = parentDir.list();
            for(String fileName : files)
            {
                File child = new File(allen2hpo.getDataPath() 
                    + parentDir.separator + fileName);
                if (child.isDirectory())
                {
                    //   Child is a single brain donor. Do analysis. 
        
                    status = 
                        allen2hpo.checkAllAllenBrainFilesPresent(parentDir);

                    if (status != 2) 
                    {
                        allen2hpo.doSingleDonorAnalysis(child);
                        donorCount++;
                    }
                }
            }
        }
 
        //   Data path doesn't point to a compatible file 
        if (donorCount == 0)
        {
            System.out.println("No compatible Allen Brain directories found");
        }

        /*String developmentalDataPath = allen2hpo.getDevelopmentalDataPath();
        if (developmentalDataPath != null) {
            allen2hpo.doDevelopmentalAnalysis(new File(developmentalDataPath));
        }*/
    }



    //__________________________________________________________________________
    //
    //  Class Variables                                   
    //__________________________________________________________________________

    /** Required input parsed from command line. Directory containing expression
    *   data and sample/probe annotations (OR subdirectories containing said) */
    private String dataPath = null;

    private String developmentalDataPath = null;


    /** Optional input parsed from command line. If not specified will print 
    *   into dataPath given in Clusters_OUTPUT.csv*/
    private String outputPath = null;

    /** Path to hpo annotations file */
    private String hpoGeneToPhenotypePath = null;
    private String hpoPhenotypeToGenePath = null;

    private enum DirectoryType {DEVELOPMENTAL,SINGLE_DONOR};


    /** Returns path to allen brain data as parsed from command line */
    public String getDataPath(){
        return this.dataPath;
    }

    /** Returns path to allen brain data as parsed from command line */
    public String getDevelopmentalDataPath(){
        return this.developmentalDataPath;
    }

    /** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(Allen2HPO.class.getName());



    //__________________________________________________________________________
    //
    //  Constructor                                   
    //__________________________________________________________________________

    public Allen2HPO(){
        //  Set up logger
        PropertyConfigurator.configure("src/test/resources/log4j.properties");

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
        
        //  Get data either by deserializing or parsing        
        AllenDataMngr brainDataMngr = getAllenDataFromDirectory(dir,DirectoryType.SINGLE_DONOR);

        
        //  Print normalized and collapsed expression data
        Matrix data = brainDataMngr.getExpression();
        data.printToFile("/Users/ahartens/Desktop/NormalizedBrainExpression2.csv");
        data = null;

        //  Print row annotations to nromalized and collapsed expressiond ata 
        ArrayList <String> names = brainDataMngr.getAllGenes();
        PrintWriter writer = null;
        try{
            writer = new PrintWriter("/Users/ahartens/Desktop/NormalizedBrainExpressionRowAnnots2.txt", "UTF-8");
        }
        catch(Exception e){
        }
        for (int i = 0; i<names.size(); i++){
            writer.printf("%s\n",names.get(i));
        }
        writer.close();
        writer = null; 
        

        
        //  Analyze P values outputed from R
        /*PvalueTableAnalyzer analyzer = new PvalueTableAnalyzer(brainDataMngr.getTissueNames());
        analyzer.readPvalueTable();
        analyzer.findSignificantPvaluesAndOutput(.000000001);

        /*
        //  Cluster data and print out
        clusterAllenBrainData(brainDataMngr,dir);*/

        
        //  Parse hpo data and cluster 
        //doHpoTermAnalysis(brainDataMngr,dir);
        
        //brainDataMngr.calculateDistanceMatrixForTissueLocations();

        /*
        //  Parse brain tissue ontology data
        OntologyDataMngr ontology = new OntologyDataMngr(dir.getPath());
        brainDataMngr.collapseTissuesToSelectedParents(ontology);*/

        
    }

    private void doDevelopmentalAnalysis(File dir){
        
        //  Get data either by deserializing or parsing        
        AllenDataMngr brainDataMngr = getAllenDataFromDirectory(dir,DirectoryType.DEVELOPMENTAL);
        


        //  Analyze P values outputed from R
        PvalueTableAnalyzer analyzer = new PvalueTableAnalyzer(brainDataMngr.getTissueNames(),brainDataMngr.getTimepoints());
        analyzer.readPvalueTable();
        analyzer.findSignificantPvaluesAndOutput(.000000001);
        
        /*
        //  Cluster data and print out
        clusterAllenBrainData(brainDataMngr,dir);*/

        /*
        //  Parse hpo data and cluster 
        doHpoTermAnalysis(brainDataMngr,dir);*/

        /*//  Parse brain tissue ontology data
        OntologyDataMngr ontology = new OntologyDataMngr(dir.getPath());
        brainDataMngr.collapseTissuesToSelectedParents(ontology);*/

        
    }

    /**  
    *   Encapsulates expression and metadata in allenDataMngr object, either
    *   by parsing files in directory provided or deserializing already created
    *   object
    *   @param File containing necessary files
    *   @param DirectoryType specification if developmental data or single donor, 
    *   which changes how files are parsed 
    */
    private AllenDataMngr getAllenDataFromDirectory(File dir, 
                                                    DirectoryType type){
        
        AllenDataMngr brainDataMngr = null;
        
        String serializedDataPath = dir.getAbsolutePath()
        +dir.separator+"SerializedAllenDataMngr.bin";

        boolean deserializeSuccessful = false;

        for(String fileName : dir.list()){
            if (fileName.equals("SerializedAllenDataMngr.bin")){
                
                brainDataMngr = deserializeData(serializedDataPath);
                
                if(brainDataMngr != null){
                    deserializeSuccessful = true;
                    log.info("Serialized data found and could be successfully read");
                }
                else
                {
                    log.info("Found serialized data but could not be read. Reparsing");
                }

                break;
            }
        }


        //  No serialized data exists, parse files and serialize data to 
        //  directory
        if (deserializeSuccessful == false) 
        {
            log.info("No serialized data found begin parsing");

            //  Init AllenDataMngr object to serve as wrapper of directory 
            //  corresponding to a single Allen Brain donor

            brainDataMngr = new AllenDataMngr(dir);

            if (type == DirectoryType.DEVELOPMENTAL){
                //  Parse directory 
                brainDataMngr.parseDevelopmentalExpressionAndAnnotations();
            }
            else{
                //  Parse directory 
                brainDataMngr.parseExpressionAndAnnotations();
                
                //  Remove probes where gene name and probe name are identical
                brainDataMngr.removeUnknownProbeData();

                //  Average expression of probes with same gene name to create 
                //  unique gene-expression pairs
                brainDataMngr.collapseRepeatProbesToUniqueGenes();
            }
           
            //  Normalize the data
            brainDataMngr.meanNormalizeData();

            //  Serialize data and save to directory
            serializeDataToFile(brainDataMngr,serializedDataPath);
        }
        
        // brainDataMngr.calculateDistanceMatrixForTissueLocations();

        //OntologyDataMngr ontology = new OntologyDataMngr(dir.getPath());
       
        //brainDataMngr.collapseTissuesToSelectedParents(ontology);

        //  Cluster Data
        return brainDataMngr;
    }

    /**
    *   Given expression data packaged in an allenDataMngr object, performs
    *   cluster analysis and prints out results to file
    */
    private void clusterAllenBrainData(AllenDataMngr brainDataMngr, File dir){
        
        //   Cluster Data
        ClusteringMngr clusteringMngr = new ClusteringMngr(brainDataMngr);

        //  Perform Kmeans clustering using the gap statistic to calculate k
        boolean success = clusteringMngr.doParallelKmeansClustering();

        if(success)
        {
            //  Print output to terminal
            //clusteringMngr.printClusterGenesInTerminal();


            //  Create a directory called clustering for output
            File outputDirectory = createOutputDirectory(dir);
            if (outputDirectory != null)
            {
                //  Create string for outputDirectory path 
                String outputDirString = outputDirectory.getAbsolutePath()
                    +dir.separator;
                
                //  Write population file (all genes clustered one gene per line)
                clusteringMngr.writePopulationGenesToFile(outputDirString);

                //  Write one cluster per file, one gene per line
                clusteringMngr.writeClusterGenesOneClusterPerFile(outputDirString);

                //  Write Cluster Prototypes to file
                clusteringMngr.writeClusterPrototypesToFile(outputDirString);
            }
        }
        else{
            log.info("Clustering was unsuccessful");
        }
    }

    /**
    *
    *
    */
    public void doHpoTermAnalysis(AllenDataMngr brainDataMngr, File dir){
        HPOMngr hpoMngr = new HPOMngr(this.hpoGeneToPhenotypePath,this.hpoPhenotypeToGenePath);
        hpoMngr.parseHPO();
        hpoMngr.getExpressionDataForHpoAnnotatedGenes(brainDataMngr);
        hpoMngr.ptg_organizeHPOAnnotatedExpressionDataByHpo();
        hpoMngr.gtp_organizeHPOAnnotatedExpressionDataByHpo();

        //hpoMngr.getExpressionDataForHpoAnnotatedGenes(brainDataMngr);

       // AllenDataMngr hpoAnnotedExpressionMngr = hpoMngr.getHpoAnnotedGeneExpression();
       // clusterAllenBrainData(hpoAnnotedExpressionMngr,dir);
    }


    private File createOutputDirectory(File parentDir){
        File outputDirectory = 
            
            new File(parentDir.getAbsolutePath()+parentDir.separator
                +"clustering");

        //  If the directory does not exist, create it
        if (!outputDirectory.exists()) {
            System.out.println(parentDir.getAbsolutePath()+parentDir.separator
                +"clustering");
            boolean result = false;

            try
            {
                outputDirectory.mkdir();
                result = true;
            } 
            catch(SecurityException se)
            {
                //  handle it
            }        
            if(result) 
            {    
                System.out.println("DIR created");  
                return outputDirectory;
            }
            return null;

        }
        return outputDirectory;
    }

    /**
    *   <p>Checks for presence of all necessary files for AllenDataMngr.<br>
    *   If finds seralized data returns
    *   Returns true if following are present in directory :
    *   <ol>
    *   <li>MicroarrayExpression.csv</li>
    *   <li>Probes.csv</li>
    *   <li>SampleAnnot.csv</li>
    *   </ol>
    *   </p>
    *   @param File directory to be checked
    *   @return int 0 if all files present
    *   @return int 1 if serialized data is present
    *   @return int 2 if insufficient files present
    */
    private int checkAllAllenBrainFilesPresent(File dir){
        boolean expression = false;
        boolean probes = false;
        boolean samples = false;
        boolean serializedData = false;

        //  Check for presence of all necessary files
        
        for(String fileName : dir.list())
        {
            if (fileName.equals("MicroarrayExpression.csv") )
                expression = true;
            if (fileName.equals("Probes.csv") )
                probes = true;
            if (fileName.equals("SampleAnnot.csv") )
                samples = true;
            if (fileName.equals("SerializedAllenDataMngr.bin"))
                serializedData = true;
        }

        if (serializedData == true) 
            return 1;

        //  If all necessary files present, return true
        
        if (expression == true && probes == true && samples == true)
            return 0;

        //  Not all files are present
        
        return 2;
    }

    /**
    *
    *
    */
    public void serializeDataToFile(Serializable object, String fileName){
        try{
            ObjectOutputStream os = 
                new ObjectOutputStream(new FileOutputStream(fileName));
            os.writeObject(object);
            os.close();
            log.info("AllenDataMngr serialized to : "+fileName);

        }
        catch (FileNotFoundException e)
        {

        }
        catch (IOException e)
        {

        }
    }

    /**
    *
    *
    */
    private AllenDataMngr deserializeData(String fileName){
        AllenDataMngr mngr = null;
        try
        {
            ObjectInputStream is = 
                new ObjectInputStream(new FileInputStream(fileName));
            mngr = (AllenDataMngr)is.readObject();
            is.close();

        }
        catch (FileNotFoundException e)
        {
            log.warn("No file to deserialize found");
        }
        catch (IOException e)
        {
            log.warn("Deserialize IOException");

        }
        catch (ClassNotFoundException e)
        {
            log.warn("Deserialized data is not correct class");
        }
        return mngr;
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
            options.addOption(new Option("T","timepoints",true,"Path to developmental data"));

            options.addOption(new Option("G","hpo",true,"genes to phenotype Hpo annotation file path"));
            options.addOption(new Option("P","hpo",true,"phenotype to genes Hpo annotation file path"));

            Parser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("D")) {
                this.dataPath = cmd.getOptionValue("D");
                this.outputPath = this.dataPath+"/Clusters_OUTPUT.csv";

            } 
            else 
            {
                usage();
            }
            if (cmd.hasOption("G")) {
                this.hpoGeneToPhenotypePath = cmd.getOptionValue("G");
            } 
            if (cmd.hasOption("P")) {
                this.hpoPhenotypeToGenePath = cmd.getOptionValue("P");
            } 
            if (cmd.hasOption("T")) {
                this.developmentalDataPath = cmd.getOptionValue("T");
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
