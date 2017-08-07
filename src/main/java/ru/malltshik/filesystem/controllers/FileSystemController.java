package ru.malltshik.filesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.malltshik.filesystem.entities.File;
import ru.malltshik.filesystem.entities.JSONFile;
import ru.malltshik.filesystem.services.FileSystem;

import java.util.List;

@RestController
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired
    private FileSystem fs;

    @RequestMapping(method = RequestMethod.GET)
    public File getFile(@RequestParam(name = "p", required = false) String path)
            throws Exception {
        return fs.getFile(path);
    }

    @RequestMapping(method = RequestMethod.POST)
    public File createFile(@RequestParam(name = "p", required = false) String path,
                                @RequestBody File file) throws Exception {
        return fs.createFile(path, file);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public File updateFile(@RequestParam(name = "p") String path,
                           @RequestBody File file) throws Exception {
        return fs.updateFile(path, file);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<File> deleteFile(
            @RequestParam(name = "p", required = false) String path) throws Exception {
        fs.deleteFile(path);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public List<File> moveFiles(
            @RequestParam(name = "p", required = false) String path,
            @RequestBody List<JSONFile> files) throws Exception {
        return fs.moveFiles(path, files);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public List<File> copyFiles(
            @RequestParam(name = "p", required = false) String path,
            @RequestBody List<JSONFile> files) throws Exception {
        return fs.copyFiles(path, files);
    }

}
