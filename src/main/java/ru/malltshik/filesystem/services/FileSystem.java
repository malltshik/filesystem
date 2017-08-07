package ru.malltshik.filesystem.services;

import org.springframework.stereotype.Service;
import ru.malltshik.filesystem.entities.File;
import ru.malltshik.filesystem.entities.JSONFile;

import java.util.List;

@Service
public interface FileSystem {

    File getFile() throws Exception;

    File getFile(String path) throws Exception;

    File createFile(String path, File file) throws Exception;

    File updateFile(String path, File file) throws Exception;

    void deleteFile(String path) throws Exception;

    List<File> moveFiles(String path, List<JSONFile> files) throws Exception;

    List<File> copyFiles(String path, List<JSONFile> files) throws Exception;

}
