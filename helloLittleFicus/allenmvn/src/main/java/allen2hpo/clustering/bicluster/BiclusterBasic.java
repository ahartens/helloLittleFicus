package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class BiclusterBasic{
	private Matrix dat  = null;
	public BiclusterBasic(Matrix m){
		this.dat = m;
	}

	public double calcMeanSquaredResidue(Matrix m){
		///Returns array with length 3
		///First array contains row means, 2nd = column means, 3rd = whole matrix mean
		double [][] allMeans = m.getAllMeans();

		double [] a_iJ = allMeans[0];
		double [] a_Ij = allMeans[1];
		double a_IJ = allMeans[2][0];


		double h_sum = 0;

		for(int i=0; i<m.getRowSize(); i++){
			for(int j=0; j<m.getColumnSize(); j++){
				double meanResidue = m.getValueAtIndex(i,j) - a_iJ[i] - a_Ij[j] + a_IJ;
				h_sum += Math.pow(meanResidue,2);
			}
		}
		double h = h_sum/(m.getColumnSize()*m.getRowSize());
		return h;
	}
}