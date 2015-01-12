package allen2hpo.matrix;

public class SimilarityMatrix extends Matrix{
	
	/**

	*@param Takes an mxn matrix and creates an mxm similarity matrix, comparing 

	*/
	public SimilarityMatrix(Matrix m){
		
		///INITIALIZE SIMILARITY MATRIX WITH SIZE CORRESPONDING TO DATA MATRIX
		super.setMatrix(new double[m.getRowSize()][m.getRowSize()]);
		System.out.println("CALCULATING SIMILARITY");
		computeSimilarity(m);
		super.print();


		m.meanNormalize();
		computeSimilarity(m);
		super.print();


		/*m.featureScale();
		computeSimilarity(m);
		super.print();*/
	}

	private void computeSimilarity(Matrix m){
	///FOR EACH ROW OF SIMILARITY MATRIX
			for (int i=0;i<m.getRowSize();i++){
				///FOR EACH COLUMN OF SIMILARITY MATRIX
				for(int j=0;j<m.getRowSize();j++){

					double sum = 0;

					///FOR EACH COLUMN OF DATA COLUMN (CORRESPONDING TO DIMENSION OF VECTOR)
					for (int z =0;z<m.getColumnSize();z++){
						///CALCULATE DISTANCE OF DIMENSION
						sum += Math.pow(m.getValueAtIndex(i,z) - m.getValueAtIndex(j,z),2);
						//System.out.println(m.getValueAtIndex(i,z)+" ^2 - "+m.getValueAtIndex(j,z)+" ^2   =   "+sum);
					}
					///CALCULATE EUCLIDEAN DISTANCE
					double dist = Math.sqrt(sum);
					///SET VALUE IN SIMILARITY MATRIX
					setValueAtIndex(i,j,dist);
				}
			}
	}
}