package allen2hpo.matrix;


import java.util.Random;
public class UniformRandomMatrixGenerator{
	private Random rand = null;
	private int n;
	private Matrix m;

	public UniformRandomMatrixGenerator(Matrix m){

	    this.rand = new Random();
	    this.m = m;

	    //Calculates max and min of every column/row (columns are necessary here)
	    this.m.calcSummary();
	}

	public Matrix generateUniformRand(){
		double[][] newRandMat = new double[this.m.getRowSize()][this.m.getColumnSize()];
		Matrix urm = new Matrix(newRandMat);
		for (int i=0; i<this.m.getRowSize(); i++){
			for (int j=0; j<this.m.getColumnSize(); j++){
				urm.setValueAtIndex(i,j,getRandExpression(this.m.getColumnMin(j), this.m.getColumnMax(j)));
			}
		}
		return urm;
	}


	private double getRandExpression(double min, double max){
		double mag = max - min;
		double randVal = (mag * rand.nextDouble()) + min;

		return randVal;
	}
}