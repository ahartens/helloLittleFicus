package allen2hpo.allen;


public class OntologyData{

    Structure[] structures = null;

    public OntologyData(String dir, int dim){

        ReadOntology ontology = new ReadOntology(dir,dim);
        this.structures = ontology.getData();

    }

    public void printStructureAtIndex(int i){
        System.out.println("GOT THIS FAR TO ONTOLOGY DATA");
    	this.structures[i].printLeveled();
    }

}
