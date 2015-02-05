package allen2hpo.allen;


public class OntologyData{

    Structure[] structures = null;

    public OntologyData(String dir, int dim){

        ReadOntology ontology = new ReadOntology(dir,dim);
        this.structures = ontology.getData();

    }

    public void printStructureAtIndex(int i){
    	this.structures[i].printLeveled();
    }

}
