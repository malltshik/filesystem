package ru.malltshik.filesystem.entities;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@AllArgsConstructor
public class File {

    private List<File> files;
    private File parent;
    private String name;
    private Date created;
    private Date updated;
    private boolean directory;
    private String data;

    @Setter(AccessLevel.PRIVATE)
    private String path;

    public File(){
        this("/");
    }



    public File(String name){
        this.name = name;
        this.created = new Date();
        this.updated = created;
        this.files = new ArrayList<>();
    }

    public File(boolean isDir) {
        this("/", isDir);
    }

    public File(String name, boolean isDir){
        this(name);
        this.directory = isDir;
    }

    public File(File file) {
        this.name = file.name;
        this.created = new Date();
        this.updated = created;
        this.data = file.data;
        this.directory = file.directory;
        this.files = file.files != null ?
                file.files.stream().map(File::new).collect(Collectors.toList()) : new ArrayList<>();
    }

    public String getPath() {
        if(parent != null) {
            if (parent.getName().equals("/")) return "/" + name;
            else return parent.getPath() + "/" + name;
        } else return name;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof File && ((File) other).getPath()
                .equals(this.getPath()));
    }

    @Override
    public int hashCode() {
        return this.getPath().hashCode() * 31;
    }


    @JsonProperty("files")
    public List<JSONFile> getChildFilesJSON(){
        return this.files != null ? this.files.stream().map(JSONFile::new)
                .collect(Collectors.toList()) : null;
    }

    @JsonProperty("parent")
    public JSONFile getParentFileJSON() {
        return this.parent != null ? new JSONFile(this.parent) : null;
    }

}
