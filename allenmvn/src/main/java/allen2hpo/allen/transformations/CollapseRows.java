package allen2hpo.allen.transformations;

import allen2hpo.matrix.*;

import java.util.ArrayList;
import java.util.List;

/**
*	<p>
*   Responsible for finding the mean expression across all rows that refer to the same gene
*   <br>In allen2hpo this means that if multiple probes are used to test the expression of a single gene,
*   the average value of all the probes is used to cluster the gene<br>
*   This results in a single gene - single expression value data matrix.<br>
*   To use, call 'doCollapseRowsGivenGeneIds'
*   </p>
*/

public class CollapseRows{

        /**	Matrix object containing all expression data meaned over condensed indices*/
        private Matrix condData = null;

        /** Array list of unique gene names */
        private ArrayList<String> condNames = null;

        /** Get data for unique gene names (averaged over probes) */
        public Matrix getData(){
            return this.condData;
        }

        /**
        *   @return String[] of unique gene names
        */
        public ArrayList<String> getGeneNames(){
            return this.condNames;
        }

        public CollapseRows(){

        }

        /**
        *   Main method to be called in order to mean across rows
        *   <br>Pass gene id array, expression data and gene names
        *   <br>Gene ids are compared to find repeats and corresponding expression values are averaged
        *   <br>
        *   @param Matrix m data matrix of expression values
        *   @param int[] all gene Ids (with repeating gene names. each row corresponds to row of matrix)
        *   @param String[] all gene names. Condensed in parallel to data matrix
        */
        public void doCollapseRowsGivenGeneIds(Matrix m, ArrayList<Integer> geneIds, ArrayList<String> geneNames){
            /** 
            *   Get indices of rows that correspond to one gene using 
            *   Form an array of arrays.
            *   Each gene is a unique array of ints, 
            *   Each int is the index of expression value in original data matrix
            */
            //List<List<Number>> indices = createCollapsedIndicesArray(geneIds, geneNames);
            
            List<List<Number>> indices = searchForRepeatGenesAndCreateCollapsedIndicesArray(geneIds, geneNames);

            /**
            *   Average across rows that correspond to one gene
            */
            collapseRowsWithIndices(m,indices);
        }

        /**
        *   Given a two dimensional array of indices, is able to mean across rows that refer to same gene.
        *   @param  Matrix m, the data which should be averaged/compressed
        *   @param  int[][] array of arrays. length of array is length final expression matrix should be.
        *   each subarray points to the indices of rows which should be averaged
        */
        public void doCollapseRowsGivenIndices(Matrix m, int[][] condensedIndices){
                  double[][] condensedData = new double[condensedIndices.length][m.getColumnSize()];
            for (int i=0; i<condensedIndices.length; i++){

                for(int j=0; j<condensedIndices[i].length; j++){
                    for(int x=0; x<m.getColumnSize(); x++){
                        condensedData[i][x] += m.getValueAtIndex(condensedIndices[i][j],x);
                    }
                }

                if (condensedIndices[i].length>1){

                    for(int x=0; x<m.getColumnSize(); x++){
                        condensedData[i][x] /= condensedIndices[i].length ;
                    }
                }
            }

            this.condData = new Matrix(condensedData);
        }

        /**
        *   Iterates through probes and compares gene ids, placing indices of probes
        *   with identical gene ids into array at index of unique gene id
        *   @param  int[] array of gene names with repeated gene ids
        *   @return List<List<Number>> 2d array where each row is a unique gene and each column is the index of expressionvalue that corresponds to gene
        */
        private List<List<Number>> createCollapsedIndicesArray(ArrayList<Integer> geneIds, ArrayList<String> geneNames){

            /**
            *   Init 2d array list that will contain one array per gene
            *   each array containg indices of expression values in original 
            *   expression matrix (referring to that gene)
            */  
            List<List<Number>> condensedIndices = new ArrayList<List<Number>>();

            /**
            *   Init condensed names array list
            */
            this.condNames = new ArrayList<String>();

            int previousGeneId = 0;
            int currentCount = 0;

            /** 
            *   Iterate through each probe id 
            */
            for(int i=0; i<geneIds.size(); i++)
            {
                /** 
                *   If the gene id is the same as the one before it, add the 
                *   index of the probe to the unique gene it corresponds to
                */
                if (geneIds.get(i) == previousGeneId)
                {
                    condensedIndices.get(currentCount-1).add(i);
                }
                
                /** 
                *   If the gene id is unique/the first of repeats, create a new
                *   numberlist to hold values of unique gene and 
                *   add new array to condensed indices, and name to condensed names
                */
                else
                {

                    this.condNames.add(geneNames.get(i));
                    List<Number> newGene = new ArrayList<Number>();
                    newGene.add(i);
                    condensedIndices.add(newGene);

                    /**
                    *   Previous gene Id is first of unique genes
                    *   Compare next gene in list (during next iteration) to see if repeat
                    */
                    previousGeneId = geneIds.get(i);

                    /**
                    *   Increment count of unique genes
                    */
                    currentCount ++;
                }
            }
            return condensedIndices;
        }

        /**
        *   Averages rows that correspond to same gene
        *   @param  Matrix m the expression data of all probes
        *   @param  List<List<Number>> a 2d arraylist where each row corresponds 
        *   to a unique gene and each column corresponds to an index or row in m corresponding to (a probe) it
        */
        private void collapseRowsWithIndices(Matrix m, List<List<Number>> condensedIndices){

            /**
            *   Init 2d array to hold condensed data
            */
            double[][] condensedData = new double[condensedIndices.size()][m.getColumnSize()];

            /**
            *   Iterate through the condensed indices array (each row corresponds to a unique gene)
            */
            for (int i=0; i<condensedIndices.size(); i++){

                /**
                *   Get the ith array of condensed indices (indices pointing to all probes testing gene i)
                */
                List<Number> indices = condensedIndices.get(i);

                /**
                *   Find the average of all expression values measured by probes of gene i
                */
                
                /**
                *   Iterate through indices
                */
                for(int j=0; j<indices.size(); j++)
                {
                    /**
                    *   Iterate through each dimension/column of expression data and sum value
                    */
                    for(int x=0; x<m.getColumnSize(); x++)
                    {
                        condensedData[i][x] += m.getValueAtIndex(indices.get(j).intValue(),x);
                    }
                }

                /**
                *   Divide by number of probes referring to gene (if greater than 1)
                */
                if (indices.size()>1)
                {
                    /**
                    *   Average each column value
                    */
                    for(int x=0; x<m.getColumnSize(); x++)
                    {
                        condensedData[i][x] /= indices.size() ;
                    }
                }
            }

            /**
            *   Create matrix object wrapper for condensed data
            */
            this.condData = new Matrix(condensedData);

        }

        public void searchForRepeatGenes(ArrayList<String> geneNames){

            for (int i=0 ; i<geneNames.size(); i++){
                for(int j=0; j<geneNames.size(); j++){
                    if (geneNames.get(i).equals(geneNames.get(j))) {
                        System.out.println(geneNames.get(i) + " is equal to "+geneNames.get(j));
                    }
                }
            }
        }


        /**
        *   Iterates through probes and compares gene ids, placing indices of probes
        *   with identical gene ids into array at index of unique gene id
        *   @param  int[] array of gene names with repeated gene ids
        *   @return List<List<Number>> 2d array where each row is a unique gene and each column is the index of expressionvalue that corresponds to gene
        */
        private List<List<Number>> searchForRepeatGenesAndCreateCollapsedIndicesArray(ArrayList<Integer> geneIds, ArrayList<String> geneNames){

            
            //   Init 2d array list that will contain one array per gene
            //   each array containg indices of expression values in original 
            //   expression matrix (referring to that gene)  
            List<List<Number>> condensedIndices = new ArrayList<List<Number>>();

            
            //   Init condensed names array list
            this.condNames = new ArrayList<String>();

            
            for (int i=0 ; i<geneNames.size(); i++)
            {
                //  Haven't come across this gene yet

                if (!this.condNames.contains(geneNames.get(i))) 
                {
                    //  Add current gene name and index of current expression
                    this.condNames.add(geneNames.get(i));
                    List<Number> indicesForCurrent = new ArrayList<Number>();
                    indicesForCurrent.add(i);

                    //  Search through list distal to this point for repeats of gene

                    for(int j=i; j<geneNames.size(); j++)
                    {

                        if (geneNames.get(i).equals(geneNames.get(j))) 
                        {
                            indicesForCurrent.add(j);
                            System.out.println(geneNames.get(i) + " is equal to "+geneNames.get(j));
                        }
                    }

                    condensedIndices.add(indicesForCurrent);
                }
                
            }
            return condensedIndices;
        }

}
