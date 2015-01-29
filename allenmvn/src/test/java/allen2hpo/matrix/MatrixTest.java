package allen2hpo.matrix;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


public class MatrixTest {


	///CONSTRUCTOR TESTING
	@Test(expected = IllegalArgumentException.class)
    public void testMatrixArgNull(){
    	double[][] d = null;
    	Matrix m = new Matrix(d);
    }

   /*THESE CONDITIONS WERE REMOVED ON 27.1.2015 BC OF EMPTY CLUSTER ISSUE.. MAYBE PUT BACK 
	@Test(expected = IllegalArgumentException.class)
    public void testMatrixArgZeroHeight(){
    	double[][] d = {};
    	Matrix m = new Matrix(d);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testMatrixArgZeroWidth(){
    	double[][] d = {{},{}};
    	Matrix m = new Matrix(d);
    }
*/


    ///GETTER TESTING
    @Test
    public void testMatrixRowSize(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		int rowsize = m.getRowSize();
		Assert.assertEquals(2,rowsize);
    }

    @Test
    public void testMatrixColumnSize(){
    	double[][] d = {{1d,2d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	int colsize = m.getColumnSize();
    	Assert.assertEquals(2,colsize);
    }



    ///CHECK GET COLUMN
    @Test
    public void testGetColumn(){
		double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	double val[] = m.getColumnAtIndex(1);


    	for (int i = 0;i<m.getRowSize();i++){
    		Assert.assertEquals(d[i][1],val[i],0);
    	}
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnOutOfBoundsEqualSize(){
    	double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	double val[] = m.getColumnAtIndex(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnOutOfBounds(){
    	double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	double val[] = m.getColumnAtIndex(100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnOutOfBoundsEqualNegative(){
    	double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	double val[] = m.getColumnAtIndex(-10);
    }



    ///CHECK GET ROW
    @Test
    public void testGetRow(){
		double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);

    	int idx = 0;
    	double val[] = m.getRowAtIndex(idx);


    	for (int i = 0;i<m.getColumnSize();i++){
    		Assert.assertEquals(d[idx][i],val[i],0);
    	}
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRowOutOfBoundsEqualSize(){
    	double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	int idx = 7;
    	double val[] = m.getRowAtIndex(idx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRowOutOfBounds(){
    	double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	int idx = 100;
    	double val[] = m.getRowAtIndex(idx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRowOutOfBoundsEqualNegative(){
    	double[][] d = {{1d,2d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d},{3d,4d}};
    	Matrix m = new Matrix(d);
    	int idx = -10;
    	double val[] = m.getRowAtIndex(idx);
    }



    ///ADDITION : TEST MATRIX TO BE ADDED
    @Test(expected = IllegalArgumentException.class)
    public void testAddNullMatrix(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		double[][] b = null;
		m.add(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMismatchRows(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		double[][] b = {{1d,2d},{3d,4d},{5d,6d}};
		m.add(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMismatchColumns(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		double[][] b = {{1d,2d,3d},{4d,5d,6d}};
		m.add(b);
	}

	///ADDITION : GET VALUE AT INDEX OUT OF BOUNDS
	@Test(expected = IllegalArgumentException.class)
	public void testGetValueAtIndexRowEqual(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		m.getValueAtIndex(2,0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetValueAtIndexRowBigger(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		m.getValueAtIndex(5,0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetValueAtIndexColEqual(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		m.getValueAtIndex(0,2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetValueAtIndexColBigger(){
		double[][] d = { {1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		m.getValueAtIndex(0,5);
	}


	///ADDITION : RESULT
	@Test
	public void testAdd(){

		double[][] d = {{1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		Matrix a = new Matrix(d);

		double[][] b = {{10d,20d},{30d,40d}};

		a.add(b);

		for (int i=0;i<d.length;i++){
            for(int j=0;j<d[0].length;j++){
                Assert.assertEquals(a.getValueAtIndex(i,j),m.getValueAtIndex(i,j),.00000001);
            }
        }
	}
	///DOTPRODUCT
	@Test(expected = IllegalArgumentException.class)
	public void testMultiplyDimAgreeLess(){
		double[][] a = {{1d,2d},{3d,4d}};
		Matrix m = new Matrix(a);
		double[][] b = {{1d}};
		m.multiply(b);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testMultiplyDimAgreeGreater(){
		double[][] a = {{1d,2d},{3d,4d}};
		Matrix m = new Matrix(a);
		double[][] b = {{1d},{2d},{3d}};
		m.multiply(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMultiplyNullPassed(){
		double[][] a = {{1d,2d},{3d,4d}};
		Matrix m = new Matrix(a);
		double[][] b = null;
		m.multiply(b);
	}

	@Test
	public void testMultiply(){
		/*double[][] d = {{1,2,3},{4,5,6}};
		Matrix m = new Matrix(d);
		double[][] b = {{7,8},{9,10},{11,12}};*/

		double[][] d = {{3,4,2}};
		Matrix m = new Matrix(d);
		double[][] b = {{13,9,7,15},{8,7,4,6},{6,4,0,3}};


		m.multiply(b);

	}


	///SCALAR MULTIPLICATION
	@Test
	public void testMultiplyScalar(){
		double[][] a = {{1d,2d},{3d,4d}};
		Matrix m = new Matrix(a);
		double b = 30;

		m.multiply(b);
	}

	///TRANSPOSITION
	@Test
	public void testTranspose(){
		double[][] d = {{1d,2d},{3d,4d}};
		Matrix m = new Matrix(d);
		m.transpose();
	}


	///GET ALL MEAN DATA
	@Test
	public void testGetMatrixMeanData(){
		double [][] d = {{1,2,3,4,5,6},{1,2,3,4,5,6},{1,2,3,4,5,6},{1,2,3,4,5,6}};
		Matrix m = new Matrix(d);
		double [][] allMeans = m.getAllMeans();

		double [][] answer = {{3.5,3.5,3.5,3.5,3.5,3.5},{1d,2d,3d,4d,5d,6d},{3.5}};

		for(int i = 0;i<allMeans.length;i++){
			for (int j= 0;j<allMeans[i].length;j++){
				Assert.assertEquals(allMeans[i][j],answer[i][j],.01);

			}
		}
	}

	@Test
	public void testCalcSummary(){
		double [][] d = {{1,2,3,4,5,6},{1,2,3,4,5,6},{1,2,3,4,5,6},{1,2,3,4,5,6},{1,2,3,4,5,6},{1,2,3,4,5,6}};
		Matrix m = new Matrix(d);
		m.calcSummary();

		double [][] answer = {{3.5,3.5,3.5,3.5,3.5,3.5},{1d,2d,3d,4d,5d,6d}};

		for(int i = 0;i<d[0].length;i++){
			Assert.assertEquals(answer[1][i],m.getColumnMean(i),.001);
		}

		for(int i = 0;i<d.length;i++){
			Assert.assertEquals(answer[0][i],m.getRowMean(i),.001);
		}
	}

	@Test
	public void testCalcSummaryMaxMin(){
		double [][] d = {{1,2,3,4,5,6},{7,8,9,10,11,12},{18,17,16,15,14,13},{12,11,10,9,8,1}};
		Matrix m = new Matrix(d);
		m.calcSummary();

		double[] colMin = {1,2,3,4,5,1};
		double[] colMax = {18,17,16,15,14,13};

		double[] rowMin = {1,7,13,1};
		double[] rowMax = {6,12,18,12};

		for(int i = 0;i<d.length;i++){
			Assert.assertEquals(m.getRowMin(i),rowMin[i],.0001);
			Assert.assertEquals(m.getRowMax(i),rowMax[i],.0001);
		}
		for (int j= 0;j<d[0].length;j++){
			Assert.assertEquals(m.getColumnMin(j),colMin[j],.0001);
			Assert.assertEquals(m.getColumnMax(j),colMax[j],.0001);
		}
	}




}
