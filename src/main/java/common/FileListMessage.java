package common;



import java.util.ArrayList;

public class FileListMessage extends AbstractMessage {
    public ArrayList<String> getArr() {
        return arr;
    }
    private ArrayList<String> arr;

    public FileListMessage(ArrayList arr){
       this.arr = arr;
    }

    }

