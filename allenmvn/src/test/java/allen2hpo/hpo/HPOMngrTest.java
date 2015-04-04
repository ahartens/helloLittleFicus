package allen2hpo.hpo;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import java.util.ArrayList;

import allen2hpo.matrix.Matrix;

public class HPOMngrTest{




   /* static Matrix m = null;

    static Matrix bigMatrix=null;

    @BeforeClass public static void setUpClass() {
    	double[][] b = {{13,90,70,150},{8,7,4,6},{6,4,0,3}};
    	m= new Matrix(b);
    	double[][] d = {{41.000000,45.000000},{39.000000,44.000000},{42.000000,43.000000},{44.000000,43.000000},{10.000000,42.000000},{38.000000,42.000000},{8.000000,41.000000},{41.000000,41.000000},{13.000000,40.000000},{45.000000,40.000000},{7.000000,39.000000},{38.000000,39.000000},{42.000000,39.000000},{9.000000,38.000000},{12.000000,38.000000},{19.000000,38.000000},{25.000000,38.000000},{6.000000,37.000000},{13.000000,35.000000},{9.000000,34.000000},{12.000000,34.000000},{32.000000,27.000000},{26.000000,25.000000},{39.000000,24.000000},{34.000000,23.000000},{37.000000,23.000000},{22.000000,22.000000},{38.000000,21.000000},{35.000000,20.000000},{31.000000,18.000000},{26.000000,16.000000},{38.000000,13.000000},{29.000000,11.000000},{34.000000,11.000000},{37.000000,10.000000},{40.000000,9.000000},{42.000000,9.000000}};
    	bigMatrix=new Matrix(d);


    }*/


    /**
    *   Check clustering of big matrix using gap stat
    */
    @Test
    public void testMngr(){
        HPOMngr mngr = new HPOMngr();

        //  make 'hpo terms' (3 in total)
        ArrayList<String> hpoOrganizedNames = new ArrayList<String>();
        hpoOrganizedNames.add("hpoTerm1");
        hpoOrganizedNames.add("hpoTerm2");
        hpoOrganizedNames.add("hpoTerm3");
        mngr.setHpoOrganizedNames(hpoOrganizedNames);

        //  make gene indices assigned to hpo term
        ArrayList<ArrayList<Integer>> hpoOrganizedGeneIndices = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> firstTerm = new ArrayList<Integer>();
        firstTerm.add(0);
        firstTerm.add(1);
        firstTerm.add(2);
        hpoOrganizedGeneIndices.add(firstTerm);

        ArrayList<Integer> secondTerm = new ArrayList<Integer>();
        secondTerm.add(3);
        secondTerm.add(4);
        secondTerm.add(5);
        hpoOrganizedGeneIndices.add(secondTerm);

        ArrayList<Integer> thirdTerm = new ArrayList<Integer>();
        thirdTerm.add(6);
        thirdTerm.add(7);
        thirdTerm.add(8);
        hpoOrganizedGeneIndices.add(thirdTerm);
        mngr.setHpoOrganizedGeneIndices(hpoOrganizedGeneIndices);


        //  Make 'expression' matrix
        ArrayList<double[]> data = new ArrayList<double[]>();
        for (int i =0; i<10; i++){
            double[] array = {i,i,i,i,i};
            data.add(array);
        }
        Matrix m = new Matrix(data);

        //  make corresponding 'gene' names
        ArrayList<String> names = new ArrayList<String>();
        names.add("one");
        names.add("two");
        names.add("three");
        names.add("four");
        names.add("five");
        names.add("six");
        names.add("seven");
        names.add("eight");
        names.add("nine");
        names.add("ten");
        mngr.setGeneOrganizedNames(names);

        //  make 'not existent in allen data' gene names
        ArrayList<String> notEqual = new ArrayList<String>();
        notEqual.add("ten");

        //  organize expression by hpo terms
        mngr.organizeExpressionDataByHpo(m,names,notEqual);

        // get t values
        Matrix tval = mngr.getTvalMatrix();

        double[] correctTVal = {1.73,6.93,12.12};

        for (int i =0; i<tval.getRowSize(); i++){
            Assert.assertEquals(tval.getValueAtIndex(i,0),correctTVal[i],.1);

        }

    }

}