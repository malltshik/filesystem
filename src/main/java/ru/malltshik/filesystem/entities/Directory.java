package ru.malltshik.filesystem.entities;

import com.fasterxml.jackson.annotation.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class Directory {

    @JsonManagedReference
    private List<Directory> directories;
    @JsonBackReference
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
    }

    public Directory(String name, Directory parent) {
        this(name);
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

}
