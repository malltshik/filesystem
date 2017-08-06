package ru.malltshik.filesystem.services.implementations;

import org.springframework.stereotype.Component;
import ru.malltshik.filesystem.entities.Directory;
import ru.malltshik.filesystem.exceptions.BadRequestException;
import ru.malltshik.filesystem.exceptions.ConflictException;
import ru.malltshik.filesystem.exceptions.NotFoundException;
import ru.malltshik.filesystem.services.FileSystem;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemFileSystem implements FileSystem {

    private static final Directory root = new Directory();

//    Наполнение данными для отладки
    static {
        Directory home = new Directory("home");
        home.setParent(root);
        Directory user = new Directory("user");
        user.setParent(home);
        home.getDirectories().add(user);
        root.getDirectories().add(home);
    }

    @Override
    public Directory getDirectory() throws Exception {
        return getDirectory(root.getPath());
    }

    @Override
    public Directory getDirectory(String path) throws Exception {
        Directory target = root;
        for(String dir : processedPath(path)) {
            target = target.getDirectories().stream().filter(d -> d.getName().equals(dir))
                    .findFirst().orElseThrow(() -> new NotFoundException(
                            "No such directory '" + dir + "'."));
        }
        return target;
    }

    @Override
    public Directory createDirectory(String path, Directory directory) throws Exception {
        Directory parent = getDirectory(path);
        validateDirectory(parent, directory);
        directory.setParent(parent);
        if (parent.getDirectories() == null) parent.setDirectories(
                Collections.singletonList(directory));
        else parent.getDirectories().add(directory);
        return directory;
    }

    @Override
    public Directory updateDirectory(String originalPath, Directory directory) throws Exception {

        Directory origin = getDirectory(originalPath);

        validateDirectory(origin.getParent(), directory);

        origin.setName(directory.getName());
        origin.setUpdated(new Date());

        return origin;
    }

    @Override
    public void deleteDirectory(String path) throws Exception {
        Directory target = getDirectory(path);
        target.getParent().getDirectories().removeIf(d -> d.equals(target));
    }

    private List<String> processedPath(String path) {
        return (path == null) ? new ArrayList<>() : Arrays.stream(
                path.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private void validateDirectory(Directory parent, Directory target) throws Exception {
        if(target.getName() == null || target.getName().isEmpty())
            throw new BadRequestException("Directory name is required.");
        if(target.getName().contains("/"))
            throw new BadRequestException("Directory name can't contain '/'");
        if(parent.getDirectories().stream().anyMatch(d -> d.getName().equals(target.getName())))
            throw new ConflictException("Directory already exist.");
    }

}
