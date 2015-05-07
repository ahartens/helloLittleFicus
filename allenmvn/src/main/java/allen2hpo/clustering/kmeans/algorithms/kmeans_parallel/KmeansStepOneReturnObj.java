package allen2hpo.clustering.kmeans.algorithms.kmeans_parallel;

import java.util.ArrayList;
import java.util.List;

public class KmeansStepOneReturnObj{
	private int[] cs = null;
    private double[] sses = null;
    private int countUnmoved;
    private int []ci = null;
    private int[]a = null;
    private ArrayList<ArrayList<Integer>> threadClusterAssignments = null;
    public void setClusterSizeArray(int[] cs){
    	this.cs = cs;
    }

    public void setSumOfSquareErrorsArray(double[] sses){
    	this.sses = sses;
    }

    public void setCountUnmoved(int countUnmoved){
    	this.countUnmoved = countUnmoved;
    }

    public void setClusterAssignmentArray(int[] ci){
    	this.ci = ci;
    }

    public void setClusterAssignments(int[]a){
    	this.a = a;
    }

    public void setThreadClusterAssignments(ArrayList<ArrayList<Integer>>tca){
    	this.threadClusterAssignments = tca;
    }

    public int[] getClusterSizeArray(){
    	return this.cs;
    }

    public double[] getSumOfSquareErrorsArray(){
    	return this.sses;
    }

    public int getCountUnmoved(){
    	return this.countUnmoved;
    }

    public int[] getClusterAssignmentArray(){
    	return this.ci;
    }

    public int[] getClusterAssignments(){
    	return this.a;
    }

    public ArrayList<ArrayList<Integer>> getThreadClusterAssignments(){
    	return this.threadClusterAssignments;
    }
}