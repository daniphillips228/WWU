class Node {
   String name;
   int numMale;
   int numFemale;
   int total;
   Node leftChild;
   Node rightChild;
   
   Node(String name, int numMale, int numFemale) {
      this.name = name;
      this.numMale = numMale;
      this.numFemale = numFemale;
      this.total = numMale + numFemale;
   }
   
   Node(int addMale, int addFemale){
      this.numMale += addMale;
      this.numFemale += addFemale;
      this.total += addMale + addFemale;
   }
   
   public String toString() {
      return name + " has the keys: M: " + numMale + " F: " + numFemale;
   }
}