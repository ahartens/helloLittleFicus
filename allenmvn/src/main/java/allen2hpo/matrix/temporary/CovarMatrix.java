package allen2hpo.matrix;


public class CovarMatrix{
	
	private double covarMatrix[][] = null;
	private int dim = 0;
	

	public void covarCalcMatrix(Matrix m, int print){

		dim = m.getColumnSize();
		///INIT THE EMPTY MATRIX
		this.covarMatrix = new double[dim][dim];
		m.calcSummary();

		///FILL IN FROM LEFT TO RIGHT, THEN TOP TO BOTTOM
		for (int i = 0; i<dim; i++){
			for (int j = 0; j<i+1; j++){
				calcCovarAtRowIdx(i,j,m.getColumnMean(i),m.getColumnMean(j),m);
			}
		}

		for (int i = 1; i<dim; i++){
			for (int j = 0; j<i; j++){
				convertCovarToCorrelationCoefficient(i,j,this.covarMatrix);
			}
		}

		if(print == 1){
			//printToFile();
			printToFileLabeled(m);
		}
	}

	private void calcCovarAtRowIdx(int x, int y, double x_bar, double y_bar, Matrix m){
		double sum = 0;
		for (int z = 0; z<m.getRowSize(); z++){
			sum += (m.getValueAtIndex(z,x) - x_bar)*(m.getValueAtIndex(z,y) - y_bar);
		}
		this.covarMatrix[y][x] = sum/m.getRowSize();
	}

	private void convertCovarToCorrelationCoefficient(int x, int y, double[][] cm){
		cm[y][x] = cm[y][x]/(Math.sqrt(cm[x][x])*Math.sqrt(cm[y][y]));
	}

	private void printCovarMtrx(){
		for (int i = 0; i<dim; i++){
			//for (int j = i;j<m.getDimColumns;j++){
			for (int j = 0; j<dim; j++){
				System.out.printf("%.20f\t",covarMatrix[i][j]);
			}
			System.out.printf("\n");
		}	
	}

	private void printToFile(){
		FileWriter fw = new FileWriter();

		fw.createFileWithName("/Users/ahartens/Dropbox/labProgramming/Java/Data/Export/covarMatrix.csv");
		for (int i = 0; i<dim; i++){
			for (int j = 0; j<dim; j++){
				fw.writeDouble(covarMatrix[i][j]);
			}
			fw.writeNextLine();
		}	
		fw.closeFile();
	}

	private void printHeaderLineToFile(FileWriter fw, int numHdrs, Matrix m, int wtp){
		int i = 0;
		for (i=0;i<numHdrs;i++){
			fw.writeDelimit();
		}
		for (i=0;i<dim;i++){
			/*Tissue tis = (Tissue)m.getColHeaderAtIdx(i);
			if (wtp==0){
				fw.writeString(tis.getTissueName());
			}
			else if(wtp==1){
				fw.writeString(tis.getTissueLoc());
			}
			else if(wtp ==2){
				fw.writeString(tis.getTissueHemis());
			}*/
		}
	}
	
	private void printHeaderRowToFile(FileWriter fw,int i, Matrix m,int wtp){
		/*Tissue tis = (Tissue)m.getColHeaderAtIdx(i);
		if (wtp==0){
			fw.writeString(tis.getTissueName());
		}
		else if(wtp==1){
			fw.writeString(tis.getTissueLoc());
		}
		else if(wtp ==2){
			fw.writeString(tis.getTissueHemis());
		}*/
	}

	private void printToFileLabeled(Matrix m){
		FileWriter fw = new FileWriter();

		fw.createFileWithName("/Users/ahartens/Desktop/corCoeffic.csv");
		/*int numHdrs = 3;

		for (int i = 0;i<numHdrs;i++){
			printHeaderLineToFile(fw,numHdrs,m,i);
			fw.writeNextLine();
		}
		*/

		for (int i = 0; i<dim; i++){
			/*for (int j = 0;j<numHdrs;j++){
				printHeaderRowToFile(fw,i,m,j);
			}*/

			//for (int j = i;j<m.getDimColumns;j++){
			for (int j = 0; j<dim; j++){
				if (j<i) {
					fw.writeDelimit();
				}
				else if(j==i){
					fw.writeDouble(1.0);
				}
				else{
					fw.writeDouble(covarMatrix[i][j]);
				}
			}
			fw.writeNextLine();
		}	
		fw.closeFile();
	}
}