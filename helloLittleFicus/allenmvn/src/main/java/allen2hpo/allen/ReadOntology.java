package allen2hpo.allen;

import java.util.*;
import java.io.*;


import allen2hpo.allen.*;


/**
*	Reads /Probes.csv file line by line, storing whatever info required in arrays
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/
public class ReadOntology extends ReadAnnots{
    Structure[] structures = null;
    Structure[] currentParents = null;
    int previousPathLength = 0;
    int count = 0;
    int maxQuotes = 0;
    ///max number of items in ontology is 8
    public ReadOntology(String dir, int dim){
        String file = dir+"/Ontology.csv";
        this.structures = new Structure[1840];
        this.currentParents = new Structure[40];
        super.StartReading(file,dim);



        /*for (int i=0; i<count; i++){
            this.structures[i].printLeveled();
        }*/

        System.out.println("This is number 100 and its children : ");
        this.structures[100].printLeveled();
    }


    @Override public void handleRow(String line, int ri){
        Scanner quoteSc = new Scanner(line);
        quoteSc.useDelimiter("\"");
        String[] lineSplitByQuotes = new String[3];
        int quoteCount = 0;
        while(quoteSc.hasNext()){
            lineSplitByQuotes[quoteCount] = quoteSc.next();
            quoteCount ++;
        }

        this.structures[this.count] = new Structure(this.count);

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





        this.count ++;
     }

     /**
     *  Assigns current brain structure as child of all preceding brain structures in the hierarchy
     */
     private void handleChildAssignment(Structure s, int pathLength){
         ///Path length of this s is shorter than the previous path length
         ///This means that this cluster is a new parent structure/ is a node higher than preceding structure
         if (pathLength < this.previousPathLength){
             System.out.println("less than");
         }
         else if (pathLength > this.previousPathLength){
             System.out.println("greater than");

             this.currentParents[pathLength] = s;
         }
         else{
             System.out.println("equal to ");

         }
         ///Structure is a child of preceding node
         ///Add structure
         for(int i=1; i<pathLength; i++){
             if(this.previousPathLength>=1){

             }
             this.currentParents[i].addChild(s);
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




     class Structure{

         int id = 0;
         int parentid = 0;
         String name;
         String acronym;
         int[] path = null;
         int index = 0;

         int level = 0;
         Structure[] children = null;
         int childCount = 0;

         public Structure(int i){
             this.children = new Structure[2000];
             this.index = i;
         }

         public void setAcronym(String s){
             this.acronym = s;
         }

         public void setName(String s){
             this.name = s;
         }

         public void setIndex(int i){
             this.index = i;
         }

         public void setID(int i){
             this.id = i;
         }

         public void setParentID(int i){
             this.parentid = i;
         }

         public void setPath(int[] p){
             this.path = p;
         }

         public void setLevel(int l){
             this.level = l;
         }

         public void addChild(Structure child){
             this.children[childCount] = child;
             childCount++;
         }



         public int getPathLength(){
             return this.path.length;
         }

         public int getChildCount(){
             return childCount;
         }

         public String getName(){
             return this.name;
         }

         public void printLeveled(){
             for (int j=0; j<this.level; j++){
                 System.out.printf(":    ");
             }
             System.out.printf("%s\n%d children\n",getName(),this.childCount);

             for (int i = 0; i<this.childCount; i++){
                 for (int j=0; j<this.children[i].level; j++){
                     System.out.printf(":    ");
                 }
                 System.out.printf("%s\n",this.children[i].getName());
             }
             System.out.printf("\n");

         }


     }

}
