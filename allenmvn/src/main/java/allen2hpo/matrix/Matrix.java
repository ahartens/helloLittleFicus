package allen2hpo.matrix;



/**
*   Shell for 2d array
*   @author Alex Hartenstein
*/
public class Matrix{

    /** 2d array containing all data*/
    private double[][] dat=null;

    /** arrays containing column means and max*/
    private double[] colMeans = null;
    private double[] colMax = null;
    private double[] colMin = null;

    private double[] rowMeans = null;
    private double[] rowMax = null;
    private double[] rowMin = null;

    double meanAll = 0;


    public Matrix(){
    }

    /**
    *@param requires initialized 2d data array of non zero column and row size
    */
    public Matrix(double[][] data) {
	   setMatrix(data);
    }

    /**
    *   Sets data of matrix object. Can be called by subclasses using 'super.setMatrix'
    */
    public void setMatrix(double[][] data){

        this.dat = data;

        if (this.dat==null)
           throw new IllegalArgumentException("Data not initialized");
        if (this.dat.length == 0){
           System.out.println("data matrix is empty");
            this.dat = new double[1][1];
        }
        if (this.dat[0].length == 0){
           System.out.println("data matrix has 0 columns");
           this.dat = new double[1][1];
        }
    }

    /**
     *
     */
    public void setMatrixSize(int row, int col){
        this.dat = new double[row][col];
    }

    /**
    *   Set value
    */
    public void setValueAtIndex(int r, int c, double val){
        this.dat[r][c] = val;
    }

    /**
     *  @return int number of rows of the data matrix
     */
    public int getRowSize() {
	   return this.dat.length;
    }

    /**
     *  @return int number of columns of the data matrix
     */
    public int getColumnSize() {
       return this.dat[0].length;
    }

    /**
    *   @param int, int : index [row,column], from 0<= {r,c} < dim
    *   @return double : value of data array and indx
    */
    public double getValueAtIndex(int row, int col){
        if (row >= getRowSize() || col >= getColumnSize())
            throw new IllegalArgumentException("Requested value is out of bounds of matrix");
        return this.dat[row][col];
    }

    /**
    *   @param takes index of row. Start from 0
    *   @return returns array of doubles. COPY of matrix row
    */
    public double[] getRowAtIndex(int idx){
        if (idx >= getRowSize() || idx < 0)
            throw new IllegalArgumentException("Index is out of bounds of matrix");
        double row[] = new double[getColumnSize()];
        System.arraycopy(this.dat[idx],0,row,0,getColumnSize());
        return row;
    }

    /**
    *   @param takes index of column. Start from 0
    *   @return returns array of doubles. COPY of matrix row
    */
    public double[] getColumnAtIndex(int idx){
        if (idx >= getColumnSize() || idx < 0)
            throw new IllegalArgumentException("Index is out of bounds of matrix");
        double col[] = new double[getRowSize()];
        for (int i=0;i<getRowSize();i++){
            col[i] = this.dat[i][idx];
        }
        return col;
    }

    /*public double getMeanOfRow(int idx){
        double sum = 0;
        int n = getColumnSize();
        for (int i=0;i<n;i++){
            sum += getValueAtIndex(idx,i);
        }
        return sum/n;
    }

    public double getMeanOfColumn(int idx){
        double sum = 0;
        int n = getRowSize();
        for (int i=0;i<n;i++){
            sum += getValueAtIndex(i,idx);
        }
        return sum/n;
    }*/


    /**
    *
    *   @return Array or arrays. 1st row means, 2nd array = column means, 3rd array = single value, mean of entire matrix
    *   26.1.2015 : ADDED GETSUMMARY WHICH CALCULATES MEANS FO ROWS AND SUMS. THSI IS REDUNDANT
    */
    public double[][] getAllMeans(){
        double [] rowMeans = new double[getRowSize()];
        double allSum = 0;
        double [] columnSums = new double[getColumnSize()];

        for (int i = 0;i<getRowSize();i++){
            double rowSum = 0;
            for(int j = 0;j<getColumnSize();j++){
                rowSum += getValueAtIndex(i,j);
                columnSums[j] += getValueAtIndex(i,j);
                allSum += getValueAtIndex(i,j);
            }
            rowMeans[i] = rowSum/getColumnSize();
        }


        for (int i=0;i<getColumnSize();i++){
            columnSums[i] = columnSums[i]/getRowSize();
        }

        double allMean = allSum/(getColumnSize()*getRowSize());
        double[][] allMeanInfo = {rowMeans,columnSums,{allMean}};
        return allMeanInfo;
    }



    /**
    *   Adds a 2d array to matrix.
    *   @param requires initialized 2d array with same dimensions as matrix object
    */
    public void add(double[][] b){
        if (b==null)
            throw new IllegalArgumentException("Matrix to be added is not initialized");
        if (b.length != getRowSize())
            throw new IllegalArgumentException("Dimensions don't agree : # rows differ");
        if (b[0].length != getColumnSize())
            throw new IllegalArgumentException("Dimensions don't agree : # columns differ");

        for (int i=0;i<getRowSize();i++){
            for(int j=0;j<getColumnSize();j++){
                this.dat[i][j] += b[i][j];
            }
        }
    }



    /**
    *   Matrix matrix multiplication. Performs dotproduct of rows and columns
    *   @param initialized array of which number of rows are equal to number of columns of matrix object
    */
    public void multiply(double[][] b){

        ///NUMBER OF COLUMNS IN THIS.DAT MUST BE EQUAL TO NUMBER OF ROWS IN B;
        if(b == null)
            throw new IllegalArgumentException("matrix is null");
        if(getColumnSize() != b.length)
            throw new IllegalArgumentException("dimensions do not agree");


        ///RESULTING MATRIX OF (NxM)*(MxP) = (NxP)
        double[][] c = new double[getRowSize()][b[0].length];

        ///FILL IN EACH COLUMN OF DOT PRODUCT MATRIX ONE BY ONE
        for (int z=0;z<c[0].length;z++){
            ///FOR EACH ROW IN THIS.DAT
            for (int i=0;i<getRowSize();i++){
                ///SUM IS INITIALIZED AS ZERO
                double rowSum = 0;
                ///ITERATE ACROSS THE ROW OF THIS.DAT AND DOWN THE COLUMN OF B
                for(int j=0;j<b.length;j++){
                    rowSum += getValueAtIndex(i,j)*b[j][z];
                }
                ///ASSIGN VALUE TO DOTPRODUCT MATRIX
                c[i][z]=rowSum;
            }
        }
        ///REASSIGN THIS.DAT
        this.dat = c;
    }



    /**
    *   Matrix Scalar multiplication
    *   @param double
    *   @return scalar multiplication of matrix by given value
    */
    public void multiply(double b){
        for(int i=0;i<getRowSize();i++){
            for(int j=0;j<getColumnSize();j++){
                this.dat[i][j] *= b;
            }
        }
    }



    /**
    *   Transpose matrix
    */
    public void transpose(){
        double[][] t = new double[getColumnSize()][getRowSize()];
        for (int i = 0;i<getRowSize();i++){
            for(int j=0;j<getColumnSize();j++){
                t[j][i] = this.dat[i][j];
            }
        }

        this.dat = t;
    }


    /**
    *   Finds max, mind and mean of every column and row
    */
    public void calcSummary(){

        this.colMeans = new double[getColumnSize()];
        this.colMax = new double[getColumnSize()];
        this.colMin = new double[getColumnSize()];

        this.rowMeans = new double[getRowSize()];
        this.rowMax = new double[getRowSize()];
        this.rowMin = new double[getRowSize()];

        double []columnSums = new double[this.getColumnSize()];

        //Initialize columne max/mins with values in first row
        for(int j = 0; j<getColumnSize(); j++){
            columnSums[j] = this.dat[0][j];
            this.colMax[j] = this.dat[0][j];
            this.colMin[j] = this.dat[0][j];
        }

        ///Initialize row max/mins with value in first cell
        double rowSum = this.dat[0][0];
        this.rowMin[0] = this.dat[0][0];
        this.rowMax[0] = this.dat[0][0];

        ///Handle first row in totality IS THIS NECESSARY!??! WILL LOOK AT LATER SICK OF HTIS
        for(int j = 1;j<getColumnSize();j++){
            ///Handle row sum/max/min
            rowSum += this.dat[0][j];
            if(this.dat[0][j]<this.rowMin[0]){
                this.rowMin[0] = this.dat[0][j];
            }
            if(this.dat[0][j]>this.rowMax[0]){
                this.rowMax[0] = this.dat[0][j];
            }
        }
        this.rowMeans[0] = rowSum/getColumnSize();
        this.meanAll += rowSum;




        //from second row onwards, iterate through each row
        for(int i = 1;i<getRowSize();i++){
            ///Initialize row max/mins with value in first cell
            this.rowMin[i] = this.dat[i][0];
            this.rowMax[i] = this.dat[i][0];

            ///Reset row sum to zero
            rowSum = 0;


            ///From second column onwards, Iterate through each column
            for(int j = 0;j<getColumnSize();j++){

                ///Handle row sum/max/min
                rowSum += this.dat[i][j];
                if(this.dat[i][j]<this.rowMin[i]){
                    this.rowMin[i] = this.dat[i][j];
                }
                if(this.dat[i][j]>this.rowMax[i]){
                    this.rowMax[i] = this.dat[i][j];
                }

                ///Handle column sum/max/min
                columnSums[j] += this.dat[i][j];
                if(this.dat[i][j]<this.colMin[j]){
                    this.colMin[j] = this.dat[i][j];
                }
                if(this.dat[i][j]>this.colMax[j]){
                    this.colMax[j] = this.dat[i][j];
                }
            }
            this.rowMeans[i] = rowSum/getColumnSize();
            this.meanAll += rowSum;
        }


        ///Calculate Means for each column
        for(int j = 0; j<getColumnSize(); j++){
            this.colMeans[j] = columnSums[j]/getRowSize();
        }

        this.meanAll /= (getRowSize()*getColumnSize());
    }


    public double getColumnMin(int idx){
        return this.colMin[idx];
    }

    public double getColumnMax(int idx){
        return this.colMax[idx];
    }

    public double getRowMin(int idx){
        return this.rowMin[idx];
    }

    public double getRowMax(int idx){
        return this.rowMax[idx];
    }

    public double getRowMean(int idx){
        return this.rowMeans[idx];
    }

    public double getColumnMean(int idx){
        return this.colMeans[idx];
    }


    public void meanNormalize(){

        for (int i = 0;i<getRowSize();i++){
            ///FIRST FIND MAX VALUE
            double max = getValueAtIndex(i,0);
            double sum = getValueAtIndex(i,0);
            for(int j = 1;j<getColumnSize();j++){
                double val = getValueAtIndex(i,j);
                sum += val;
                if (val > max)
                max = val;
            }

            double mean = sum/getColumnSize();


            for(int j=0;j<getColumnSize();j++){
                double meanNorm = (getValueAtIndex(i,j) - mean)/max;
                setValueAtIndex(i,j,meanNorm);
            }
        }
    }

    public void meanNormalizeAcrossGenesAndSamples(){
        ///Calculate mean of every row, column, and entire data set
        calcSummary();

        for(int i=0; i<getRowSize(); i++){
            for(int j=0; j<getColumnSize(); j++){
                double val = getValueAtIndex(i,j);

                double norm = val - this.colMeans[j] - this.rowMeans[i] + this.meanAll;
            }
        }
    }

    public void featureScale(){

        for (int i = 0;i<getRowSize();i++){
            ///FIRST FIND MAX VALUE
            double max = getValueAtIndex(i,0);
            for(int j = 1;j<getColumnSize();j++){
                double val = getValueAtIndex(i,j);
                if (val > max)
                max = val;
            }


            for(int j=0;j<getColumnSize();j++){
                double featScale = getValueAtIndex(i,j)/max;
                setValueAtIndex(i,j,featScale);
            }
        }
    }

    /**
    *   Prints data matrix in csv format
    *   @param String of file name to which data should be printed
    */
    public void printToFile(String fp){
        //Initialize filewriter with given name
        FileWriter fw = new FileWriter();
        fw.createFileWithName(fp);

        int j;
        //Iterate through all rows
        for (int i = 0; i<getRowSize(); i++){
            //Iterate through all cells in row except the last
            for ( j = 0; j<getColumnSize()-1; j++){
                fw.writeDouble(this.dat[i][j]);
                fw.writeDelimit();
            }
            //Last cell in row shouldn't have a delimiter. write next line
            fw.writeDouble(this.dat[i][j]);
            fw.writeNextLine();
        }

        //Close the file
        fw.closeFile();
    }

    /**
    *   Print matrix to console
    */
    public void print(){
        for(int i = 0;i<getRowSize();i++){
            for(int j = 0;j<getColumnSize();j++){
                System.out.printf("%.5f\t",this.dat[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n\n\n");
    }



}
