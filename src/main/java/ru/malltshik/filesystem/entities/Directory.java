package ru.malltshik.filesystem.entities;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@AllArgsConstructor
public class Directory {

    private List<Directory> directories;
    private Directory parent;
    private String name;
    private Date created;
    private Date updated;

    @Setter(AccessLevel.PRIVATE)
    private String path;

    public Directory(){
        this("/");
    }

    public Directory(String name){
        this.name = name;
        this.created = new Date();
        this.updated = created;
        this.directories = new ArrayList<>();
        this.parent = null;
    }

    public Directory(Directory other, Directory parent) {
        this.name = other.name;
        this.created = other.created;
        this.updated = other.updated;
        this.directories = other.directories;
        this.parent = parent;
    }

    public String getPath() {
        if(parent != null) {
            if (parent.getName().equals("/")) return "/" + name;
            else return parent.getPath() + "/" + name;
        } else return name;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Directory && ((Directory) other).getPath()
                .equals(this.getPath()));
    }

    @Override
    public int hashCode() {
        return this.getPath().hashCode() * 31;
    }


    @JsonProperty("directories")
    public List<JSONDirectory> getChildDirectoriesJSON(){
        return this.directories.stream().map(JSONDirectory::new)
                .collect(Collectors.toList());
    }

    @JsonProperty("parent")
    public JSONDirectory getParentDirectoryJSON() {
        return this.parent != null ? new JSONDirectory(this.parent) : null;
    }

    @Data
    private class JSONDirectory {
        private String name;
        private String path;
        private String parentName;
        private String parentPath;

        JSONDirectory(Directory d) {
            this.name = d.name;
            this.path = d.getPath();
            this.parentName = d.parent != null ? d.parent.name : null;
            this.parentPath = d.parent != null ? d.parent.getPath() : null;
        }
    }

}
