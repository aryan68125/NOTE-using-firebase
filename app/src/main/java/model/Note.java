package model;

public class Note {
    //the Strings should match the document field in our firebase database
    private String title;
    private String content;

    //empty constructor
    public Note(){

    }
    //constructor that will take the title and content as a input String parameter
    public Note(String title,String content){
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
