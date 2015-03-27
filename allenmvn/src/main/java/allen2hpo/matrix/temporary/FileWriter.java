package allen2hpo.matrix;

import java.util.*;
import java.io.*;

public class FileWriter{
	private Formatter file;
	PrintWriter writer;

	public FileWriter(){
	}
	public FileWriter(String name){
		createFileWithName(name);
	}


	public void createFileWithName(String name){
		try{
			writer = new PrintWriter(name, "UTF-8");
		}
		catch(Exception e){
		}
	}

	public void writeDouble(double d){
		writer.printf("%.8f",d);
	}

	public void writeInt(int d){
		writer.printf("%d",d);
	}

	public void writeDelimit(){
		writer.printf(",");
	}

	public void writeIndex(int y, int x){
		writer.printf("%d : %d,",y,x);
	}

	public void writeNextLine(){
		writer.printf("\n");
	}

	public void writeString(String s){
		writer.printf("%s",s);
	}

	public void closeFile(){
		writer.close();
	}
}
