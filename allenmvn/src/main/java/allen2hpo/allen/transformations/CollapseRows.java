package allen2hpo.allen.transformations;

import allen2hpo.matrix.*;

import java.util.*;

/**
*	Responsible for condensing number of rows in a data matrix
*   In allen2hpo. averages the multiple probles of expression values corresponding to one gene
*   Takes a data array and returns new data array with averaged indices
*/

public class CollapseRows{

        /**	Matrix object containing all expression data meaned over condensed indices*/
        private Matrix condData = null;

        /** Array list of unique gene names*/
        private List<String> condNames = null;

        /** Get data for unique gene names (averaged over probes) */
        public Matrix getData(){
            return this.condData;
        }

        /**
        *   @return String[] of unique gene names
        */
        public String[] getGeneNames(){
            String[] names = new String[condNames.size()];
            for(int i=0; i<this.condNames.size(); i++){
                names[i] = this.condNames.get(i);
            }
            return names;
        }

        /**
        *   Pass gene id array. Gene ids are compared and corresponding average values of same gene value are averaged
        *   @param Matrix m data matrix of expression values
        *   @int[] all gene Ids (with repeating gene names. each row corresponds to row of matrix)
        */
        public CollapseRows(Matrix m, int[] geneIds, String[] geneNames){
            ///Get indices of rows that correspond to one gene
            List<List<Number>> indices = createCollapsedIndicesArray(geneIds, geneNames);
            ///Average across rows that correspond to one gene
            collapseRowsWithIndices(m,indices);
        }

        /**
        *   @param Matrix m, the data which should be averaged/compressed
        *   @param int[][] array of arrays. length of array is length final expression matrix should be.
        *   each subarray points to the indices of rows which should be averaged
        */
        public CollapseRows(Matrix m, int[][] condensedIndices){
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
        *   Creates 2d array. each row is a unique gene, each column is the index of expressionvalue that corresponds to gene
        *   @param int[] array of gene names with repeated gene ids
        */
        private List<List<Number>> createCollapsedIndicesArray(int[] geneIds, String[] geneNames){

            //Init 2d array that will contain indices of unique genes
            List<List<Number>> condensedIndices = new ArrayList<List<Number>>();
            this.condNames = new ArrayList<String>();
            int previousGeneId = 0;
            int currentCount = 0;

            //For each probe gene id
            for(int i=0; i<geneIds.length; i++){
                //If the gene id is the same as the one before it, add the index of the probe to the unique gene it corresponds to
                if(geneIds[i] == previousGeneId){
                    condensedIndices.get(currentCount-1).add(i);
                }
                //If the gene id is unique/the first of repeats, create a new numberlist to hold values of unique gene
                else{
                    this.condNames.add(geneNames[i]);
                    List<Number> newGene = new ArrayList<Number>();
                    newGene.add(i);
                    condensedIndices.add(newGene);

                    previousGeneId = geneIds[i];
                    currentCount ++;
                }
            }
            return condensedIndices;
        }

        /**
        *   Averages rows that correspond to same gene
        *   @param Matrix m the expression data of all probes
        *   @param List<List<Number>> a 2d arraylist
        */
        private void collapseRowsWithIndices(Matrix m, List<List<Number>> condensedIndices){
            double[][] condensedData = new double[condensedIndices.size()][m.getColumnSize()];
            for (int i=0; i<condensedIndices.size(); i++){
                List<Number> indices = condensedIndices.get(i);
                for(int j=0; j<indices.size(); j++){
                    for(int x=0; x<m.getColumnSize(); x++){
                        condensedData[i][x] += m.getValueAtIndex(indices.get(j).intValue(),x);
                    }
                }

                if (indices.size()>1){

                    for(int x=0; x<m.getColumnSize(); x++){
                        condensedData[i][x] /= indices.size() ;
                    }
                }
            }

            this.condData = new Matrix(condensedData);
        }
}
