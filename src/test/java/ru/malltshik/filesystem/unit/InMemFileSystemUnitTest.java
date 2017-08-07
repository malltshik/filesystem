package ru.malltshik.filesystem.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import ru.malltshik.filesystem.entities.File;
import ru.malltshik.filesystem.exceptions.BadRequestException;
import ru.malltshik.filesystem.exceptions.ConflictException;
import ru.malltshik.filesystem.exceptions.NotFoundException;
import ru.malltshik.filesystem.services.FileSystem;
import ru.malltshik.filesystem.services.implementations.InMemFileSystem;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InMemFileSystemUnitTest {

    @InjectMocks private FileSystem fs = new InMemFileSystem();

    private File root;

    @Before
    public void before() throws Exception {
        root = fs.getFile();
        root.setFiles(new ArrayList<>());
    }

    @Test
    public void getRoot() throws Exception {
        assertThat(root.getName(), equalTo("/"));
        assertThat(root.getPath(), equalTo("/"));
        assertThat(root.getFiles(), hasSize(0));
    }

    @Test(expected = NotFoundException.class)
    public void getWrongPathFile() throws Exception {
        fs.getFile("/invalid");
    }

    @Test
    public void createFile() throws Exception {
        fs.createFile(root.getPath(), new File("home"));
        assertThat(root.getFiles(), hasSize(1));
        File home = root.getFiles().get(0);
        assertThat(home.getName(), equalTo("home"));
        assertThat(home.getPath(), equalTo("/home"));
        assertThat(home.getParent(), equalTo(root));

        fs.createFile(home.getPath(), new File("user"));
        assertThat(root.getFiles(), hasSize(1));
        File user = home.getFiles().get(0);
        assertThat(user.getName(), equalTo("user"));
        assertThat(user.getPath(), equalTo("/home/user"));
        assertThat(user.getParent(), equalTo(home));
    }

    @Test(expected = BadRequestException.class)
    public void createFileWithNullName() throws Exception {
        fs.createFile(root.getPath(), new File((String) null));
    }

    @Test(expected = BadRequestException.class)
    public void createFileWithEmptyName() throws Exception {
        fs.createFile(root.getPath(), new File(""));
    }

    @Test(expected = BadRequestException.class)
    public void createFileWithSlashName() throws Exception {
        fs.createFile(root.getPath(), new File("/"));
    }

    @Test(expected = ConflictException.class)
    public void createFileAlreadyExist() throws Exception {
        fs.createFile(root.getPath(), new File("home"));
        fs.createFile(root.getPath(), new File("home"));
    }

    @Test
    public void updateFile() throws Exception{
        File file = new File("name");
        fs.createFile(root.getPath(), file);
        Thread.sleep(100L);
        fs.updateFile("/name", new File("name2"));
        File updated = fs.getFile("/name2");
        assertThat(updated.getName(), equalTo("name2"));
        assertThat(updated.getCreated(), equalTo(file.getCreated()));
        assertThat(updated.getUpdated().getTime() > updated.getCreated().getTime(), is(true));
    }

    @Test(expected = NotFoundException.class)
    public void deleteFile() throws Exception {
        fs.createFile(root.getPath(), new File("deleteMe"));
        fs.deleteFile("/deleteMe");
        assertThat(root.getFiles(), hasSize(0));
        fs.getFile("/deleteMe");

    }

}