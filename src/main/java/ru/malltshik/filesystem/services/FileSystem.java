package ru.malltshik.filesystem.services;

import org.springframework.stereotype.Service;
import ru.malltshik.filesystem.entities.Directory;

@Service
public interface FileSystem {

    Directory getDirectory() throws Exception;

    Directory getDirectory(String path) throws Exception;

    Directory createDirectory(String path, Directory directory) throws Exception;

    Directory updateDirectory(String path, Directory directory) throws Exception;

    void deleteDirectory(String path) throws Exception;

}
