package ru.malltshik.filesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.malltshik.filesystem.entities.Directory;
import ru.malltshik.filesystem.services.FileSystem;

@RestController
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired private FileSystem fs;

    @RequestMapping(method = RequestMethod.GET)
    public Directory getDirectory(@RequestParam(name = "p", required = false) String path)
            throws Exception {
        return fs.getDirectory(path);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Directory createDirectory(@RequestParam(name = "p", required = false) String path,
                                     @RequestBody Directory directory) throws Exception {
        return fs.createDirectory(path, directory);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Directory updateDirectory(@RequestParam(name = "p", required = false) String path,
                                     @RequestBody Directory directory) throws Exception {
        return fs.updateDirectory(path, directory);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Directory> deleteDirectory(
            @RequestParam(name = "p", required = false) String path) throws Exception {
        fs.deleteDirectory(path);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
