package allen2hpo.allen;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import allen2hpo.matrix.Matrix;
import allen2hpo.clustering.*;
import allen2hpo.allen.*;
public class ReadExpressionTest {
	@Test
	public void TestReadFile(){
		ReadExpression reader = new ReadExpression("/Users/ahartens/Desktop/AllenTest");
		ReadProbeAnnots probes = new ReadProbeAnnots("/Users/ahartens/Desktop/AllenTest",100);
		KmeansBasic kmeans = new KmeansBasic(reader.getData());
	}
}

