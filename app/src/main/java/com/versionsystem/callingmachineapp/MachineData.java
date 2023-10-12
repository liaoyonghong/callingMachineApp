package com.versionsystem.callingmachineapp;

public class MachineData {
    private String htmlContent;
    private String playList;

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getPlayList() {
        return playList;
    }

    public void setPlayList(String playList) {
        this.playList = playList;
    }

    @Override
    public String toString() {
        return "MachineData{" +
                "htmlContent='" + htmlContent + '\'' +
                ", playList='" + playList + '\'' +
                '}';
    }
}