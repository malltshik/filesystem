package ru.malltshik.filesystem.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import ru.malltshik.filesystem.entities.Directory;
import ru.malltshik.filesystem.exceptions.BadRequestException;
import ru.malltshik.filesystem.exceptions.ConflictException;
import ru.malltshik.filesystem.exceptions.NotFoundException;
import ru.malltshik.filesystem.services.FileSystem;
import ru.malltshik.filesystem.services.implementations.InMemFileSystem;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InMemFileSystemUnitTest {

    @InjectMocks private FileSystem fs = new InMemFileSystem();

    private Directory root;

    @Before
    public void before() throws Exception {
        root = fs.getDirectory();
        root.setDirectories(new ArrayList<>());
    }

    @Test
    public void getRootDirectory() throws Exception {
        assertThat(root.getName(), equalTo("/"));
        assertThat(root.getPath(), equalTo("/"));
        assertThat(root.getDirectories(), hasSize(0));
    }

    @Test(expected = NotFoundException.class)
    public void getWrongPathDirectory() throws Exception {
        fs.getDirectory("/invalid");
    }

    @Test
    public void createDirectory() throws Exception {
        fs.createDirectory(root.getPath(), new Directory("home"));
        assertThat(root.getDirectories(), hasSize(1));
        Directory home = root.getDirectories().get(0);
        assertThat(home.getName(), equalTo("home"));
        assertThat(home.getPath(), equalTo("/home"));
        assertThat(home.getParent(), equalTo(root));

        fs.createDirectory(home.getPath(), new Directory("user"));
        assertThat(root.getDirectories(), hasSize(1));
        Directory user = home.getDirectories().get(0);
        assertThat(user.getName(), equalTo("user"));
        assertThat(user.getPath(), equalTo("/home/user"));
        assertThat(user.getParent(), equalTo(home));
    }

    @Test(expected = BadRequestException.class)
    public void createDirectoryWithNullName() throws Exception {
        fs.createDirectory(root.getPath(), new Directory(null));
    }

    @Test(expected = BadRequestException.class)
    public void createDirectoryWithEmptyName() throws Exception {
        fs.createDirectory(root.getPath(), new Directory(""));
    }

    @Test(expected = BadRequestException.class)
    public void createDirectoryWithSlashName() throws Exception {
        fs.createDirectory(root.getPath(), new Directory("/"));
    }

    @Test(expected = ConflictException.class)
    public void createDirectoryAlreadyExist() throws Exception {
        fs.createDirectory(root.getPath(), new Directory("home"));
        fs.createDirectory(root.getPath(), new Directory("home"));
    }

    @Test
    public void getDirectory() throws Exception {
    }

    @Test
    public void updateDirectory() throws Exception {
    }

    @Test
    public void deleteDirectory() throws Exception {
    }

}