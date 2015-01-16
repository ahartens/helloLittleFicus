package allen2hpo.allen;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

public class AllenDataTest{

	@Test
	public void getGeneNamesTest(){
		String[] genes = {"Apple", "Bear", "Cat","Dog","Eagle","Falcon","Grizzly","Hell","Insiduous","Jalapeno","Kantankerous"};
		String[] tissues = {"zoloft", "yersinia", "xylophone","violence","uvula"};

		AllenData mngr = new AllenData(genes, tissues);
		
		int[] indexes = {0,4,8};
		for(String s:mngr.getGenesAtIndexes(indexes)){
			System.out.println(s);
		}
	}

}
	