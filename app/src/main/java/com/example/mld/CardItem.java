package com.example.mld;

public class CardItem {
    private String title;
    private String contents;

    public CardItem(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String content) {
        this.contents=content;

    }

}