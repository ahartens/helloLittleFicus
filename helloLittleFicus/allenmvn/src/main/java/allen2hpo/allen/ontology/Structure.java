package allen2hpo.allen;
public class Structure{

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
             System.out.printf("%d : %s\n",i,this.children[i].getName());
         }
         System.out.printf("\n");

     }


 }
