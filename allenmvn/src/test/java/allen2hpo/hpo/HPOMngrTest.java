package allen2hpo.hpo;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import java.util.ArrayList;

import allen2hpo.matrix.Matrix;
import allen2hpo.allen.AllenDataMngr;
public class HPOMngrTest{





    /**
    *   Check clustering of big matrix using gap stat
    */
    @Test
    public void testMngr(){
        HPOMngr mngr = new HPOMngr();
        AllenDataMngr allenMngr = new AllenDataMngr();
        //  make 'hpo terms' (3 in total)
        ArrayList<String> hpoOrganizedNames = new ArrayList<String>();
        hpoOrganizedNames.add("hpoTerm1");
        hpoOrganizedNames.add("hpoTerm2");
        hpoOrganizedNames.add("hpoTerm3");
        mngr.setPtg_PhenotypeList(hpoOrganizedNames);

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
        mngr.setPtg_GeneIndicesForPL(hpoOrganizedGeneIndices);


        //  Make 'expression' matrix
        ArrayList<double[]> data = new ArrayList<double[]>();
        for (int i =0; i<10; i++){
            double[] array = {i,i,i,i,i};
            data.add(array);
        }
        Matrix m = new Matrix(data);
        allenMngr.setExpression(m);

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
        allenMngr.setGeneAnnotations(names);
        mngr.setGtp_GeneList(names);
        mngr.setHpoAnnotatedGeneExpressionMngr(allenMngr);

        //  make 'not existent in allen data' gene names
        ArrayList<String> notEqual = new ArrayList<String>();
        mngr.setGenesInHpoButNotInAllenNames(notEqual);
        notEqual.add("ten");

        //  organize expression by hpo terms
        mngr.ptg_organizeHPOAnnotatedExpressionDataByHpo();

        // get t values
        Matrix tval = mngr.getTvalMatrix();

        double[] correctTVal = {1.73,6.93,12.12};

        for (int i =0; i<tval.getRowSize(); i++){
            Assert.assertEquals(tval.getValueAtIndex(i,0),correctTVal[i],.1);

        }
    
    }

}