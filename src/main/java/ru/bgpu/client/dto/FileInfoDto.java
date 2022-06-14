package ru.bgpu.client.dto;

import java.io.File;

public class FileInfoDto {
    private String name;
    private long size;

    public FileInfoDto(File file) {
        this.name = file.getName();
        this.size = file.length();
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
