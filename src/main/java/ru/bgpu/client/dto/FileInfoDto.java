package ru.bgpu.client.dto;

import java.io.File;

public class FileInfoDto {
    private String name;
    private double size;

    public FileInfoDto(File file) {
        this.name = file.getName();
        this.size = file.length()/(1024*1024);
    }

    public String getName() {
        return name;
    }

    public double getSize() {
        return size;
    }

    @Override
    public String toString() {
        return name;
    }
}
