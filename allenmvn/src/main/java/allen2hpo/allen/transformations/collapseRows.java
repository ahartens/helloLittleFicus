package allen2hpo.allen;

import allen2hpo.matrix.*;


/**
*	This class decreases the number of columns in data
*   Given a parent brain structure, collapseColumns finds all corresponding children
*   structures contained in microarray data and means across those structures
*   What results is a single meaned value for a parent structure (one column)
*   Eg given frontal lobe, parietal lobe and temporal lobe and a data matrix with
*   63,000 rows and 1000 tissue simples(columns), collapse columns will return
*   a matrix with 63000 rows and 3 columns
*	@author Alex Hartenstein.
*/

public class CollapseRows{

        /**	Matrix object containing all expression data. each row is a gene, each column a tissue sample */
        private Matrix origData = null;

        private Matrix condData = null;

        /**
        *
        */
        public CollapseRows(Matrix m, int[] condensedIndices){
            this.data = m;

            double[][] condensedData = new double[condensedIndices.length][m.getColumnSize()];
            for (int i=0; i<condensedIndices.length; i++){

                for(int j=0; j<condensedIndices[i].length; j++){
                    for(int x=0; x<m.getColumnSize(); x++){
                        condensedData[i][x] += m.getValueAtIndex(condensedIndices[j],x);
                    }
                }

                if (condensedIndices[i].length>1){
                    for(int x=0; x<m.getColumnSize(); x++){
                        condensedData[i][x] /= m.getValueAtIndex(condensedIndices[j],x)/condensedIndices[i].length ;
                    }
                }
            }

            this.condData = new Matrix(condensedData);
            this.condData.print();
        }

        /** Get data*/
        public Matrix getData(){
            return this.condData;
        }


}
