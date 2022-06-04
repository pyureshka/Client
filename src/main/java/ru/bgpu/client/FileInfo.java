package ru.bgpu.client;

public class FileInfo {
    public String[] names;

    public FileInfo(String[] names){
        this.names = names;
    };
    public FileInfo(){};

    public String[] getNames() {
        return names;
    }
}
