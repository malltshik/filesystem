package ru.malltshik.filesystem.services.implementations;

import org.springframework.stereotype.Component;
import ru.malltshik.filesystem.entities.File;
import ru.malltshik.filesystem.entities.JSONFile;
import ru.malltshik.filesystem.exceptions.BadRequestException;
import ru.malltshik.filesystem.exceptions.ConflictException;
import ru.malltshik.filesystem.exceptions.NotFoundException;
import ru.malltshik.filesystem.services.FileSystem;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFileSystem implements FileSystem {

    private static final File root = new File(true);

    @Override
    public File getFile() throws Exception {
        return root;
    }

    @Override
    public File getFile(String path) throws Exception {
        File target = root;
        for(String dir : processedPath(path)) {
            target = target.getFiles().stream().filter(d -> d.getName().equals(dir))
                    .findFirst().orElseThrow(() -> new NotFoundException(
                            "No such directory '" + dir + "'."));
        }
        return target;
    }

    @Override
    public File createFile(String path, File file) throws Exception {
        File parent = getFile(path);
        validate(parent, file);
        file.setParent(parent);
        if (parent.getFiles() == null) parent.setFiles(Collections.singletonList(file));
        else parent.getFiles().add(file);
        if(file.isDirectory()) file.setFiles(new ArrayList<>());
        else file.setData("");
        return file;
    }

    @Override
    public File updateFile(String path, File file) throws Exception {
        File origin = getFile(path);
        if(!origin.getName().equals(file.getName()))
            validate(origin.getParent(), file);
        origin.setName(file.getName());
        origin.setData(file.getData());
        origin.setUpdated(new Date());
        return origin;
    }

    @Override
    public void deleteFile(String path) throws Exception {
        File target = getFile(path);
        File parent = target.getParent();
        if (parent == null) throw new ConflictException("Can't remove root directory");
        else parent.getFiles().removeIf(d -> d.equals(target));
    }

    @Override
    public List<File> moveFiles(String path, List<JSONFile> files) throws Exception {
        File parent = getFile(path);
        if (!parent.isDirectory())
            throw new BadRequestException("Can't move to file. Only to directory");
        List<File> result = new ArrayList<>();
        for(JSONFile file: files) {
            File origin = getFile(file.getPath());
            validate(parent, origin);
            // Can't move directory into itself
            if(parent.equals(origin)) continue;
            parent.getFiles().add(origin);
            origin.getParent().getFiles().removeIf(f -> f.getName().equals(origin.getName()));
            origin.setParent(parent);
            result.add(origin);
        }
        return result;
    }

    @Override
    public List<File> copyFiles(String path, List<JSONFile> files) throws Exception {
        File parent = getFile(path);
        if (!parent.isDirectory())
            throw new BadRequestException("Can't move to file. Only to directory");
        List<File> result = new ArrayList<>();
        for(JSONFile file: files) {
            File origin = new File(getFile(file.getPath()));
            validate(parent, origin);
            // Can't move directory into itself
            if(parent.equals(origin)) continue;
            parent.getFiles().add(origin);
            origin.setParent(parent);
            result.add(origin);
        }
        return result;
    }

    private List<String> processedPath(String path) {
        return (path == null) ? new ArrayList<>() : Arrays.stream(
                path.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private void validate(File parent, File target) throws Exception {
        if(target.getName() == null || target.getName().isEmpty())
            throw new BadRequestException("File name is required.");
        if(target.getName().contains("/"))
            throw new BadRequestException("File name can't contain '/'");
        if(parent.getFiles().stream().anyMatch(f -> f.getName().equals(target.getName())))
            throw new ConflictException("File already exist.");
    }

}
