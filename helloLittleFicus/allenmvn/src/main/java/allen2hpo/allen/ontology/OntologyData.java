package allen2hpo.allen;


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

    public void getStructureWithId(int id){
        for (int i=0; i<)
    }

}
