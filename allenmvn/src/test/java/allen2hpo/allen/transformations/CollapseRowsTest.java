package allen2hpo.allen;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


import allen2hpo.matrix.*;
import allen2hpo.clustering.*;
import allen2hpo.allen.*;
import allen2hpo.allen.transformations.*;


public class CollapseRowsTest{

    @Test
    public void CollapseRowsTest(){
        /*  Create data matrix */
        double[][] data = {{1,2,3,4,5,6},{1,2,3,4,5,6,},{1,2,3,4,5,6},{1,2,3,4,5,6},
                            {7,8,9,10,11,12},{7,8,9,10,11,12},
                            {13,14,15,16,17,18}};
        Matrix m = new Matrix(data);

        /*  Designate which rows should be collapsed/meaned across */
        int[][] indices = {{0,1,2,3},{4,5},{6}};

        /*  Correct values : */
        double[][] expected = {{1,2,3,4,5,6},{7,8,9,10,11,12},{13,14,15,16,17,18}};

        /*  Collapse rows and get values */
        CollapseRows collapse = new CollapseRows();
        collapse.doCollapseRowsGivenIndices(m,indices);
        Matrix condensed = collapse.getData();

        /*  Check that received and correct values agree */
        for(int i =0; i<condensed.getRowSize(); i++){
            for (int j=0; j<condensed.getColumnSize(); j++){
                Assert.assertEquals(condensed.getValueAtIndex(i,j),expected[i][j],.01);
            }
        }
    }

    /*
    *   Check that collapsing of names works correctly
    */
    @Test
    public void CollapseRowsTestTwo(){
        /**
         *  7x6 matrix of data
         */
        double[][] data = {{1,2,3,4,5,6},{1,2,3,4,5,6,},{1,2,3,4,5,6},{1,2,3,4,5,6},{7,8,9,10,11,12},{7,8,9,10,11,12},{13,14,15,16,17,18}};
        Matrix m = new Matrix(data);

        /**
         *  
         */
        int[] indices = {12,12,12,12,11,11,33};
        String[] names = {"one","one","one","one","two","two","three"};

        CollapseRows collapse = new CollapseRows();
        collapse.doCollapseRowsGivenGeneIds(m,indices,names);

        Matrix condensed = collapse.getData();
        double[][] correct = {{1,2,3,4,5,6},{7,8,9,10,11,12},{13,14,15,16,17,18}};
        for(int i=0; i<condensed.getRowSize(); i++){
            for(int j=0; j<condensed.getColumnSize(); j++){
                Assert.assertEquals(correct[i][j],condensed.getValueAtIndex(i,j),.001);
            }
        }


        String[] namesCondensed = collapse.getGeneNames();
        String[] correctNames = {"one","two","three"};

        for(int i=0; i<namesCondensed.length; i++){
            Assert.assertEquals(namesCondensed[i], correctNames[i]);
        }
    }
}
