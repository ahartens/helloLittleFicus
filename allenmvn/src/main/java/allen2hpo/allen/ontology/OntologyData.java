package allen2hpo.allen;

import java.util.*;
public class OntologyData{

    Structure[] structures = null;
    int length = 0;

    public OntologyData(String dir, int dim){

        ReadOntology ontology = new ReadOntology(dir,dim);
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
    



}
