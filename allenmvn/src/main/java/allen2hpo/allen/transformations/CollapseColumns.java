package allen2hpo.allen.transformations;

import allen2hpo.matrix.*;
import allen2hpo.allen.ontology.*;


/**
*	This class decreases the number of columns in data by calculating mean of
*   all children to specified parent structure. 
*   <br>Given a parent brain structure, collapseColumns finds all corresponding 
*   children structures contained in microarray data and means across those 
*   structures.
*   <br>What results is a single meaned value for a parent structure (one 
*   column)
*   <br>Eg given frontal lobe, parietal lobe and temporal lobe and a data matrix
*   with 63,000 rows and 1000 tissue simples(columns), collapse columns will 
*   return a matrix with 63000 rows and 3 columns
*	@author Alex Hartenstein.
*/

public class CollapseColumns{


    //__________________________________________________________________________
    //
    //  Class Variables                              
    //__________________________________________________________________________

    /** Ordered array of all sample (ie tissue) ids derived from 
    *   SampleAnnot.csv. Order == columns in microarrayexpression.csv */
    private int[] tissueIds = null;

    /**	Matrix object containing all expression data. each row is a gene, 
    *   each column a tissue sample */
    private Matrix data = null;

    /** Matrx object containing collapsed expression data. */
    private Matrix collapsedData= null;

    /** OntologyData class responsible for all queries/handling of 
    *   Ontology.csv */
    private OntologyDataMngr ontology = null;




    //__________________________________________________________________________
    //
    //  Constructor                              
    //__________________________________________________________________________


    /**
    *
    */
    public CollapseColumns(Matrix m, int[] tisIds, OntologyDataMngr o){
        this.tissueIds = tisIds;
        this.data = m;
        this.ontology = o;
    }



    //__________________________________________________________________________
    //
    //  Getter                              
    //__________________________________________________________________________


    public Matrix getDataCollapsed(){
        return this.collapsedData;
    }




    //__________________________________________________________________________
    //
    //  Methods                              
    //__________________________________________________________________________

    /**
    *	An example of how data can be collapsed for given parent structures 
    *   (eg. lobes, developmental origin)
    *   <br> Collapses expression data into 
    */
    public void collapseExample(){
        /*
        *   Collapse into lobi : 
        *   frontal, insula, limbic lob, parietal,temporal, occipital
        */
       // int[] brain = {4009,4268,4219,4084,4132, 4180};
        
        /*
        *   Collapse into developmental origin :
        *   Tel,di,mes,met,myl
        */
        int[] brain = {4007,4391,9001,4833,9512};
        
        /*
        *   Collapse
        */
        collapseSamples(brain);
    }

    /**
    *	Compresses a wide data array by calculating mean expression of all 
    *   children structures of given parent structures
    *   Eg. pass (2) ids of frontal lobe, and parietal lobe and will receive a 
    *   2d matrix with 2 columns, with each column containing mean of all child 
    *   structures of parietal + temporal
    *	@param int[] an array of ontological parent structure ids
    */
    public void collapseSamples(int[] samples){
        
        /*
        *   Init 2d array to store meaned values
        */
        double[][] collapsed = 
            new double[this.data.getRowSize()][samples.length];

        /*
        *   Organize expression data columns by parent structure : 
        *   One array for each parent structure
        *   Each parent structure array contains indices of tissue samples (of
        *   expression data) that belong to it
        */
        int[][] allChildrenIndices = new int[samples.length][];
        for(int i=0; i<samples.length; i++)
        {
            allChildrenIndices[i] = 
                this.ontology.getIndicesOfChildrenOfStructureWithId(samples[i],
                    this.tissueIds);
        }

        /*
        *   For each parent tissue, mean across corresponding expression value
        */
        for(int i=0; i<this.data.getRowSize(); i++)
        {
            for(int j=0; j<allChildrenIndices.length; j++)
            {
                double sum = 0;
                for(int x=0; x<allChildrenIndices[j].length; x++)
                {
                    sum += this.data.getValueAtIndex(i,allChildrenIndices[j][x]);
                }
                collapsed[i][j] = sum/allChildrenIndices[j].length;
            }
        }

        /*
        *   Matrix wrapper for collapsed data.
        */
        this.collapsedData = new Matrix(collapsed);
    }

    /**
    *	Given a parent brain structure, find all children sampled in given data 
    *   set
    *   Eg. pass frontal lobe id and will average expression of every child
    *   of frontal lobe, for as many rows as provided
    *	@param int the id of an ontological parent structure
    *   @return double[] a vector with as many rows as this.data
    */
    public double[] collapseParent(int parent){

        double[] collapsed = new double[this.data.getRowSize()];

        /*
        *   Array of array. One array for each parent structure, containing 
        *   indices of tissue samples that belong to it
        */
        int[] allChildrenIndices = 
            this.ontology.getIndicesOfChildrenOfStructureWithId(parent,
                this.tissueIds);


        /*
        *   Mean all columns corresponding to parent
        */
        if (allChildrenIndices.length>0)
        {
            for(int i=0; i<this.data.getRowSize(); i++)
            {
                double sum = 0;

                for(int j=0; j<allChildrenIndices.length; j++)
                {
                    sum += this.data.getValueAtIndex(i,allChildrenIndices[j]);
                }
                collapsed[i] = sum/allChildrenIndices.length;
            }
        }
        return collapsed;
    }

    public void collapseAll(){
        collapseSamples(this.tissueIds);
    }
}
