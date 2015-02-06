package allen2hpo.allen;

import allen2hpo.matrix.*;


/**
*   Prints various fileformats for visualization of data
*/
public class DataPrinterForGraphs{


    /**
    *   Prints a json file var object (2d array) for visualization with protovis/d3
    */
    public void print2dMatrixAsJson(Matrix m){
        ///Can change variable name
        String varName = "qr";

        FileWriter writer = new FileWriter("/Users/ahartens/Desktop/ProtovisTutorial/TryMatrix/matbig.js");

        ///Print variable name and opening braces
        writer.writeString("var "+varName +"= [\n");

        ///Print out all data rows except the last one
        for (int i=0; i<m.getRowSize(); i++){
            writer.writeString("[");
            for(int j=0; j<m.getColumnSize(); j++){
                writer.writeDouble(m.getValueAtIndex(i,j));
                writer.writeDelimit();
            }
            writer.writeDouble(m.getValueAtIndex(i,m.getColumnSize()-1));
            writer.writeString("],");
            writer.writeNextLine();
        }

        ///Print last row without the last delimiter (no "," on last value)
        writer.writeString("[");
        for(int j=0; j<m.getColumnSize(); j++){
            writer.writeDouble(m.getValueAtIndex(m.getRowSize()-1,j));
            writer.writeDelimit();
        }
        writer.writeDouble(m.getValueAtIndex(m.getRowSize()-1,m.getColumnSize()-1));
        writer.writeNextLine();
        writer.writeString("]\n]");

        ///close file
        writer.closeFile();
    }


    /**
    *   For eventual network visualization
    */
    public void print2dMatrixAndColumnHeadersCovariance(Matrix m, String[] names){
        FileWriter writer = new FileWriter("/Users/ahartens/Desktop/json.json");
        writer.writeString("var mat = {\nnodes:[\n");


        for (int i=0; i<names.length-1; i++){
            writer.writeString("{nodeName:\""+names[i]+"\"},\n");
        }
        writer.writeString("{nodeName:\""+names[names.length-1]+"\"}\n");
        writer.writeString("],\nlinks:[\n");
        for (int i=0; i<m.getRowSize()-1; i++){
            for(int j=0; j<m.getColumnSize(); j++){
                writer.writeString("{value:" + m.getValueAtIndex(i,j)  + "},\n");
            }
        }


        for(int j=0; j<m.getColumnSize()-1; j++){
            writer.writeString("{value:" + m.getValueAtIndex(m.getRowSize()-1,j)  + "},\n");
        }
        writer.writeString("{value:" + m.getValueAtIndex(m.getRowSize()-1,m.getColumnSize()-1)   + "}\n");

        writer.writeString("]\n}");
        writer.closeFile();
    }

}
