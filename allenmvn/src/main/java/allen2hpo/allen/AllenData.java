package allen2hpo.allen;

public class AllenData{
	private String[] geneNames = null;
	private String[] tissueNames = null;


	public AllenData(String[] genes, String[] tissues){
		this.geneNames = genes;
		this.tissueNames = tissues;

	}

	
	/**
	*	returns an array of strings (Gene names) from an arry of indexes
	*/
	public String[] getGenesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.geneNames);
	}


	/**
	*	returns an array of strings (Tissue names) from an arry of indexes
	*/
	public String[] getTissuesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.tissueNames);
	}


	/**
	*	Called by get tissue or get gene. Returns array of Strings corresponding to index
	*/
	private String[] getNamesAtIndexes(int[] indexes, String[] allNames){
		String[] names = new String[indexes.length];


		for(int i = 0; i<indexes.length;i++){
			names[i] = (allNames[indexes[i]]);
		}

		return names;
	}
}