package allen2hpo.allen;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


import allen2hpo.matrix.*;
import allen2hpo.clustering.*;
import allen2hpo.allen.*;

public class CollapseRowsTest{

    @Test
    public void CollapseRowsTest(){
        double[][] data = {{1,2,3,4,5,6},{1,2,3,4,5,6,},{1,2,3,4,5,6},{1,2,3,4,5,6},{7,8,9,10,11,12},{7,8,9,10,11,12},{13,14,15,16,17,18}};
        Matrix m = new Matrix(data);

        int[][] indices = {{0,1,2,3},{4,5},{6}};

        CollapseRows collapse = new CollapseRows(m,indices);
        Matrix condensed = collapse.getData();
        condensed.print();

        System.out.printf("Condensed is this big : %d %d\n\n",condensed.getRowSize(),condensed.getColumnSize());
    }

    @Test
    public void CollapseRowsTestTwo(){
        double[][] data = {{1,2,3,4,5,6},{1,2,3,4,5,6,},{1,2,3,4,5,6},{1,2,3,4,5,6},{7,8,9,10,11,12},{7,8,9,10,11,12},{13,14,15,16,17,18}};
        Matrix m = new Matrix(data);

        int[] indices = {12,12,12,12,11,11,33};

        CollapseRows collapse = new CollapseRows(m,indices);
        Matrix condensed = collapse.getData();
        condensed.print();

        System.out.printf("Condensed two is this big : %d %d\n\n",condensed.getRowSize(),condensed.getColumnSize());
    }
}
