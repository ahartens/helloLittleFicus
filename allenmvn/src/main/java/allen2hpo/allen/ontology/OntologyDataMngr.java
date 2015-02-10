package allen2hpo.allen;

import java.util.*;
import allen2hpo.matrix.*;
/**
*   Responsible for parsing/handling queries to Brain Ontology
*   @author Alex Hartenstein
*/

public class OntologyDataMngr{

    /** Array of structure objects containing all brain structures of ontology */
    Structure[] structures = null;

    /** Length of brain structure. could remove and switch the list/collections */
    int length = 0;

    /**
    *   Constructor method parses ontology.csv file and sets global variables.
    */
    public OntologyDataMngr(String dir){

        ReadOntology ontology = new ReadOntology(dir);
        this.structures = ontology.getData();
        this.length = ontology.getDataLength();
    }

    public void printStructureAtIndex(int i){
        this.structures[i].printLeveled();
    }

    public int[] getIndicesOfChildrenOfStructureWithId(int id, int[] samples){
        Structure parent = null;
        for (int i=0; i<this.length; i++){

            if(this.structures[i].getId() == id){

                parent = structures[i];
                System.out.printf("Parent : %d is named : %s\n",id,parent.getName());
                break;
            }
        }
        ///If parent was found
        if (parent !=null){
            ///And if parent has children
            if (parent.getChildCount() > 0){
                ///Initialize children as array and an empty array corresponding to indices
                Structure[] children = parent.getChildren();
                int[] indices = new int[parent.getChildCount()];

                ///Go through children and find index in sample array
                for (int i=0; i<parent.getChildCount(); i++){

                    for (int j=0; j<samples.length; j++){
                        if(children[i].getId() == samples[j]){
                            indices[i] = j;

                        }
                    }

                }
                return indices;
            }
            else{
                return null;
            }

        }
        else{
            System.out.println("Couldn't find parent structure");
            return null;
        }
    }


    /**
    *   Prints a json file that can be used for an indented tree visualization of terms
    */
    public void printAllStructuresHierarchy(){
        FileWriter writer = new FileWriter("/Users/ahartens/Desktop/ProtovisTutorial/indentedTree/hierarchy.js");
        writer.writeString("var flare = {\n");
        int previousLevel = 0;
        int level = 0;
        for(int i=0; i<this.length; i++){
            level = this.structures[i].getLevel();
            System.out.println(this.structures[i].getName()+" is at level : "+level);
            while(level < previousLevel){
                System.out.println("this is level : "+level+ "this is previous"+previousLevel);
                for(int j=0; j<previousLevel-1; j++){
                    writer.writeString("  ");
                    System.out.println("whatup");
                }
                writer.writeString("},\n");
                previousLevel --;
            }

            for(int j=0; j<level; j++){
                writer.writeString("  ");
            }
            writer.writeString(this.structures[i].getName().replaceAll("\\p{P}", "").replace(" ","_")+":");


            if(this.structures[i].hasChildren()){
                writer.writeString("\n");
                for(int j=0; j<level; j++){
                    writer.writeString("  ");
                }
                writer.writeString("{\n");

            }
            else{
                writer.writeString("23 ,\n");
            }

            System.out.printf("\nprevious Level : %d current Level : %d\n",previousLevel,level);

            previousLevel = level;
        }


        while(previousLevel >1){
            for(int j=0; j<previousLevel-1; j++){
                writer.writeString("  ");
            }
            System.out.println("this is level : "+level+ "this is previous"+previousLevel);
            writer.writeString("},\n");
            previousLevel --;
        }
        writer.writeString("};\n");

        writer.closeFile();
    }

    public void printAllStructuresHierarchyClusterExpressionValues(int[] sampleIds, Matrix m){
        FileWriter writer = new FileWriter("/Users/ahartens/Desktop/ProtovisTutorial/indentedTree/values.js");
        writer.writeString("var values = [\n");

        CollapseColumns collapser = new CollapseColumns(m, sampleIds, this);
        for(int i=0; i<this.length; i++){
            double[]values;
            if(this.structures[i].hasChildren()){
                values = collapser.collapseParent(this.structures[i].getId());
            }
            else{
                values = new double[m.getRowSize()];
                //for(int j=0; j<this)
            }
            writer.writeString("[");

            for(int j=0; j<values.length; j++){
                writer.writeDouble(values[j]*100);
                writer.writeDelimit();
            }
            writer.writeString("],\n");
        }

        writer.writeString("];");
        writer.closeFile();

    }

}
