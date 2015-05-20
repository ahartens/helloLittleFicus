package allen2hpo.hpo;

import java.io.PrintWriter;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

import allen2hpo.allen.parsing.ReadExpression;
import allen2hpo.matrix.Matrix;
import org.apache.log4j.Logger;

public class PvalueTableAnalyzer{

	/** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(PvalueTableAnalyzer.class.getName());

	private ArrayList<String> hpoTermNames = null;
	private ArrayList<String> tissueNames = null;
	private ArrayList<String> timepoints = null;
	
	private Matrix m = null;
	
	public PvalueTableAnalyzer(ArrayList<String> tissueNames){
		this.tissueNames = tissueNames;
	}


	public PvalueTableAnalyzer(ArrayList<String> tissueNames,ArrayList<String> timepoints){
		this.tissueNames = tissueNames;
		this.timepoints = timepoints;
	}

	public void readPvalueTable(){
		
		//	Reads pvalue table row names, setting hpoTermNames array
		readAnnotationFile();

		//	Read pvalues and store in matrix
		ReadExpression expression = 
			new ReadExpression("/Users/ahartens/Dropbox/Hartenstein/Developmental_Brain/allPvaluesPTG_developmental_12.5.csv",
				this.hpoTermNames.size(),this.tissueNames.size(),false);

		this.m = expression.getData();
        log.info("Finished parsing expression matrix : " 
        		+ this.m.getRowSize() + " x " +this.m.getColumnSize());
	}

	public void findSignificantPvaluesAndOutput(double cutoff){
		PrintWriter writer = null;
		try{
			writer = new PrintWriter("/Users/ahartens/Desktop/pvalueAnalysis.csv", "UTF-8");
		}
		catch(Exception e){
		}
		double currentValue = 0;
		double min = 1;
		ArrayList<String> termsPresent = new ArrayList<String>();
		ArrayList<Integer> termCount = new ArrayList<Integer>();
		int count = 0;
		int indexLast;
		for (int i = 0; i<this.m.getRowSize(); i++){
			for(int j=0; j<this.m.getColumnSize(); j++){
				currentValue = this.m.getValueAtIndex(i,j);
				if (currentValue<=cutoff&&currentValue != 0) {
					if (currentValue<min) {
						min = currentValue;
					}
					if (!termsPresent.contains(this.hpoTermNames.get(i))) {
						if (termsPresent.size() != 0) {
							termCount.add(count);
						}

						termsPresent.add(this.hpoTermNames.get(i));
						indexLast = i;
						count = 1;
					}
					else{
						count ++;
					}
					if (this.timepoints == null) {
						writer.printf("%.40f\t%s\t%s\n",this.m.getValueAtIndex(i,j),this.hpoTermNames.get(i),this.tissueNames.get(j));
					}
					else{
						writer.printf("%.40f\t%s\t%s\t%s\n",this.m.getValueAtIndex(i,j),this.hpoTermNames.get(i),this.timepoints.get(j),this.tissueNames.get(j));

					}
				}
			}
		}
		termCount.add(count);
		System.out.println("MIN VALUE : "+min);
		writer.close();

		writer = null;
		try{
			writer = new PrintWriter("/Users/ahartens/Desktop/pvalueAnalysisTermList.csv", "UTF-8");
		}
		catch(Exception e){
		}

		for (int i =0; i<termsPresent.size(); i++){
			writer.printf("%s\t%d\n",termsPresent.get(i),termCount.get(i));

		}
		writer.close();
	}

	public void findSignificantDevelopmentalPvaluesAndOutput(double cutoff){
		/*PrintWriter writer = null;
		try{
			writer = new PrintWriter("/Users/ahartens/Desktop/pvalueAnalysisDevelopmental.csv", "UTF-8");
		}
		catch(Exception e){
		}
		double currentValue = 0;
		double min = 1;
		ArrayList<String> termsPresent = new ArrayList<String>();
		ArrayList<Integer> termCount = new ArrayList<Integer>();
		ArrayList<String> timepointsPresent = new ArrayList<String>();
		ArrayList<Integer> timepointsCount = new ArrayList<Integer>();

		int count = 0;
		int indexLast;
		for (int i = 0; i<this.m.getRowSize(); i++){
			for(int j=0; j<this.m.getColumnSize(); j++){
				currentValue = this.m.getValueAtIndex(i,j);
				if (currentValue<=cutoff&&currentValue != 0) {
					if (currentValue<min) {
						min = currentValue;
					}
					if (!termsPresent.contains(this.hpoTermNames.get(i))) {
						if (termsPresent.size() != 0) {
							termCount.add(count);
						}

						termsPresent.add(this.hpoTermNames.get(i));
						indexLast = i;
						count = 1;
					}
					else{
						count ++;
					}

					if (!timepointsPresent.contains(this.timepoints.get(j))){
						this.timepointsPresent.add(this.timepoints.get(j));
						this.timepointsCount.add(1);
					}
					else{
						int index = this.timepointsPresent.indexOf(this.timepoints.get(j));
						this.timepointsCount.get(index)++;
					}
					
					//writer.printf("%.40f\t%s\t%s\t%s\n",this.m.getValueAtIndex(i,j),this.hpoTermNames.get(i),this.timepoints.get(j),this.tissueNames.get(j));
					
					}
				}
			}
		}
		termCount.add(count);
		System.out.println("MIN VALUE : "+min);
		writer.close();

		writer = null;
		try{
			writer = new PrintWriter("/Users/ahartens/Desktop/pvalueDevelopmentalAnalysisTermList.csv", "UTF-8");
		}
		catch(Exception e){
		}

		for (int i =0; i<termsPresent.size(); i++){
			writer.printf("%s\t%d\n",termsPresent.get(i),termCount.get(i));

		}
		writer.close();


		writer = null;
		try{
			writer = new PrintWriter("/Users/ahartens/Desktop/pvalueDevelopmentalAnalysisCounts.csv", "UTF-8");
		}
		catch(Exception e){
		}

		for (int i =0; i<timepointsPresent.size(); i++){
			writer.printf("%s\t%d\n",timepointsPresent.get(i),timepointsCount.get(i));

		}
		writer.close();*/
	}


	private void readAnnotationFile(){
		Scanner scanner = null;
		try{
			scanner = new Scanner(new File("/Users/ahartens/Dropbox/Hartenstein/Developmental_Brain/allPvaluesAnnotsDevelopmental_PTG_12.5.txt"));
		}
		catch (Exception e){
		}
	
		this.hpoTermNames = new ArrayList<String>();
		
		//	Parse each following line
		//	Handling of line is specified by subclasses of readAnnots
	    while (scanner.hasNext()) {
	    	String line = scanner.next();
			this.hpoTermNames.add(line.replace(".csv",""));
	    }
	}
}
