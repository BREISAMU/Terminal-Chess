// Q1Gen.java
// Generic queue implementation using a linked list of nodes (see NGen.java)

public class Q1Gen<T> implements QGen <T> {

    // constructor

    public Q1Gen() {}

    // selectors

    public void add(T o) {

        if (size == 0) {
          front = new NGen <T> (o, null);
          rear = front;
        }
        else {
          rear.setNext(new NGen <T> (o, null));
          rear = rear.getNext();
        }
        size++;
    }

    public T remove() {

        T answer;

        if (size == 0)
          throw new RuntimeException("Removing from empty queue"); 
        
        answer = front.getData();
        front = front.getNext();
        size--;
        if (size == 0)
          rear = null;
        return answer;
    }

    public int length() {
        return size;
    }

    public String toString(){
        NGen ptr = front;
        String ret = "";

        while(ptr != null){
            try{
                int[] arr = (int[]) ptr.getData();
                ret += arr[0] + ", " + arr[1];
            } catch (Exception e){
            }
            ret += "\n";
            ptr = ptr.getNext();
        }

        return ret;
    }

    private int size;
    private NGen <T> front;
    private NGen <T> rear;

}  // Q1Gen class

