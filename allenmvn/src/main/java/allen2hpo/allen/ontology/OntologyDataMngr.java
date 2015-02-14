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
            while(level < previousLevel){
                for(int j=0; j<previousLevel-1; j++){
                    writer.writeString("  ");
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

            previousLevel = level;
        }


        while(previousLevel >1){
            for(int j=0; j<previousLevel-1; j++){
                writer.writeString("  ");
            }
            writer.writeString("},\n");
            previousLevel --;
        }
        writer.writeString("};\n");

        writer.closeFile();
    }

    public void printAllStructuresHierarchyClusterExpressionValues(int[] sampleIds, Matrix m){
        FileWriter writer = new FileWriter("/Users/ahartens/Desktop/PrototypeValues.js");
        writer.writeString("var values = [\n");

        CollapseColumns collapser = new CollapseColumns(m, sampleIds, this);
        for(int i=0; i<this.length; i++){
            double[]values;
            if(this.structures[i].hasChildren()){
                values = collapser.collapseParent(this.structures[i].getId());
            }
            else{
                values = new double[m.getRowSize()];

                ///check if samples contains structure
                for(int j=0; j<sampleIds.length; j++){
                    //if it does, copy prototype expression values (all rows of structure's column)
                    if(sampleIds[j] == this.structures[i].getId()){
                        for(int x=0; x<m.getRowSize(); x++){
                            values[x] = m.getValueAtIndex(x,j);
                        }
                    }
                }
                //for(int j=0; j<this)
            }
            writer.writeString("[");

            for(int j=0; j<values.length; j++){
                writer.writeDouble(values[j]);
                writer.writeDelimit();
            }
            writer.writeString("],\n");
        }

        writer.writeString("];");
        writer.closeFile();

    }

}
