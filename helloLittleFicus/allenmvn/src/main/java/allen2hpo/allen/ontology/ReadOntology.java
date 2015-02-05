package allen2hpo.allen;

import java.util.*;
import java.io.*;


import allen2hpo.allen.*;


/**
*	Reads /Probes.csv file line by line, storing whatever info required in arrays
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/
public class ReadOntology{
    private Scanner scanner = null;

    Structure[] structures = null;
    Structure[] currentParents = null;
    int previousPathLength = 0;
    int count = 0;
    ///max number of items in ontology is 8
    public ReadOntology(String dir, int dim){
        String file = dir+"/Ontology.csv";
        this.structures = new Structure[dim];
        this.currentParents = new Structure[40];
        StartReading(file,dim);
    }

    public Structure[] getData(){
        return this.structures;
    }


    public void StartReading(String filename, int dim){
        openFile(filename);
        readFile();
        scanner.close();
    }

    ///PRIVATE METHODS

    /**
    *	private method. opens file with scanner or fails.
    */
    private void openFile(String filename){
        try{
            scanner = new Scanner(new File(filename));
            System.out.println("Ontology file Opened");
        }
        catch (Exception e){
            System.out.println("File could not be opened");
        }
    }



    /**
    *	Reads file in line by line, passing handling of the line the private method handleRow.
    */
    private void readFile(){
        ///FIRST LINE IS A HEADER : REMOVE IT
        scanner.nextLine();
        ///EACH FOLLOWING ROW IS READ
        this.count = 0;
        while (scanner.hasNext()) {
            handleRow(scanner.nextLine(),this.count);
            this.count++;
        }
    }

    public void handleRow(String line, int ri){

        this.structures[this.count] = new Structure(this.count);

        ///Input is a csv and some cells may contain further commas. These cells have quotes
        ///Thus first split line into components separated by commas. Handle results separately
        Scanner quoteSc = new Scanner(line);
        quoteSc.useDelimiter("\"");
        String[] lineSplitByQuotes = new String[3];
        int quoteCount = 0;
        while(quoteSc.hasNext()){
            lineSplitByQuotes[quoteCount] = quoteSc.next();
            quoteCount ++;
        }


        ///There are no commas within the name. Read each cell sequentially
        if (quoteCount == 1){
            ///Split line into cells
            Scanner lineSc = new Scanner(lineSplitByQuotes[0]);
            lineSc.useDelimiter(",");
            ///Parse cells for necessary data
            int i = 0;

            while (lineSc.hasNext()) {
                if(i==0){
                    this.structures[this.count].setID(lineSc.nextInt());
                }
                else if(i==2){
                    this.structures[this.count].setName(lineSc.next());
                }
                else if (i==6){
                    handlePath(this.structures[this.count],lineSc.next());
                }
                else{
                    lineSc.next();
                }

                i++;
            }
        }

        ///The name contains commas, thus must be handled differently
        else{

            ///First string of 3 strings is the id and acronym
            Scanner lineSc = new Scanner(lineSplitByQuotes[0]);
            lineSc.useDelimiter(",");
            int i = 0;
            while (lineSc.hasNext()) {
                if(i==0){
                    this.structures[this.count].setID(lineSc.nextInt());
                }
                else if(i==1){
                    this.structures[this.count].setAcronym(lineSc.next());
                }
                else{
                    lineSc.next();
                }
                i++;
            }

            ///Second of 3 strings is the name (which contains commas)
            this.structures[this.count].setName(lineSplitByQuotes[1]);

            ///Third of 3 strings contains path
            lineSc = new Scanner(lineSplitByQuotes[2]);
            lineSc.useDelimiter(",");
            i = 0;
            while(lineSc.hasNext()){
                if (i == 3){
                    handlePath(this.structures[this.count],lineSc.next());
                }
                else{
                    lineSc.next();
                }
                i++;
            }

        }
     }

     /**
     *  Assigns current brain structure as child of all preceding brain structures in the hierarchy
     */
     private void handleChildAssignment(Structure s, int pathLength){

         this.currentParents[pathLength-1] = s;

         ///Structure is a child of preceding node
         ///Add structure as child to all its parents
         if (this.previousPathLength != 0){
             for(int i=0; i<pathLength-1; i++){
                 this.currentParents[i].addChild(s);
             }
         }
         this.previousPathLength = pathLength;


     }

     /**
     *  Parses the second to last cell of ontology file
     *  @param takes a structure which is currently being appended to
     *  sets path array, level int, and calls child assignments
     */
     private void handlePath(Structure s, String p){
         int[] path = handlePathString(p);
         s.setPath(path);
         s.setLevel(path.length);
         handleChildAssignment(s,path.length);

     }

     /**
     *  Splits a path string in format /id/id/id/ into components placed into array of ints
     */
     private int[] handlePathString(String path){
         int[] p = new int[10];
         int j = 0;
         int i = 0;

         ///Split path string into components
         Scanner pathSc = new Scanner(path);
         pathSc.useDelimiter("/");
         ///Add each path component to array
         while(pathSc.hasNext()){
             if(i==0){
                 pathSc.next();
             }
             else{
                 p[j] = pathSc.nextInt();
                 j++;
             }
             i++;
         }

         ///path array is shortened to size of members it contains
         if(i<p.length){
             int[] sp = new int[i];
             for(j=0; j<i; j++){
                 sp[j] = p[j];
             }
             return sp;
         }

         return p;
     }
}
