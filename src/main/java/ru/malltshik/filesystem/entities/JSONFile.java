package ru.malltshik.filesystem.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JSONFile {
    private String name;
    private String path;
    private String parentName;
    private String parentPath;
    private boolean directory;
    private String data;

    public JSONFile(File d) {
        this.name = d.getName();
        this.path = d.getPath();
        this.directory = d.isDirectory();
        this.data = d.getData();
        this.parentName = d.getParent() != null ? d.getParent().getName() : null;
        this.parentPath = d.getParent() != null ? d.getParent().getPath() : null;
    }
}