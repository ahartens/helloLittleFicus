package allen2hpo.matrix;



public class Matrix{


    private double[][] dat=null;
    

    public Matrix(){
    }
    

 
    /**
    
    *
    
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
        if (this.dat.length == 0)
           throw new IllegalArgumentException("data matrix is empty");
        if (this.dat[0].length == 0)
           throw new IllegalArgumentException("data matrix has 0 columns");

    }


    /**

    *   Set value

    */
    public void setValueAtIndex(int r, int c, double val){
        this.dat[r][c] = val;
    }

    

    /**
    
     * @return number of rows of the data matrix
    
     */
    public int getRowSize() {
	   return this.dat.length;
    }

    

    /**
    
     *  @return number of columns of the data matrix
    
     */
    public int getColumnSize() {
       return this.dat[0].length;
    }

   

    /**
    
    *   @param requires index [row,column], from 0<= {r,c} < dim
    
    *   @return double value of data array and indx
    
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

    *   Print matrix 

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