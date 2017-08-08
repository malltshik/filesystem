package ru.malltshik.filesystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.malltshik.filesystem.Application;
import ru.malltshik.filesystem.entities.File;
import ru.malltshik.filesystem.entities.JSONFile;
import ru.malltshik.filesystem.services.FileSystem;

import javax.annotation.Resource;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class InMemoryFileSystemIntegrationTest {

    @Autowired private WebApplicationContext wac;
    @Autowired private FileSystem fs;
    private ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @Before
    public void before() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        fs.getFile().setFiles(new ArrayList<>());
    }

    @Test
    public void getRoot() throws Exception {
        mvc.perform(get("/fs")).andExpect(status().isOk());
    }

    @Test
    public void getFile() throws Exception {
        String rootPath = fs.getFile().getPath();
        fs.createFile(rootPath, new File("home"));
        mvc.perform(get("/fs?p=/home")).andExpect(status().isOk());
        mvc.perform(get("/fs?p=/home/empty")).andExpect(status().isNotFound());
    }

    @Test
    public void createFile() throws Exception {
        mvc.perform(post("/fs")
                .content(mapper.writeValueAsString(new JSONFile(new File("home", true))))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());
    }

    @Test
    public void updateFile() throws Exception {
        fs.createFile(fs.getFile().getPath(), new File("update me!"));
        File newFile = new File("YEAH!");
        newFile.setData("I'm updated successful");
        mvc.perform(put("/fs?p=/update me!")
            .content(mapper.writeValueAsString(new JSONFile(newFile)))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk());
        File file = fs.getFile("/YEAH!");
        assertThat(file.getName(), equalTo("YEAH!"));
        assertThat(file.getData(), equalTo("I'm updated successful"));
    }

    @Test
    public void deleteFile() throws Exception {
        fs.createFile(fs.getFile().getPath(), new File("delete me!"));
        mvc.perform(delete("/fs?p=/delete me!")).andExpect(status().isNoContent());
        assertThat(fs.getFile().getFiles(), hasSize(0));
    }

    @Test
    public void deleteRootFile() throws Exception {
        fs.createFile(fs.getFile().getPath(), new File("delete me!"));
        mvc.perform(delete("/fs"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("message", is("Can't remove root directory")));
    }

    @Test
    public void moveFile() throws Exception {
        File file = new File("move me!");
        File dir = new File("dir", true);
        fs.createFile(fs.getFile().getPath(), file);
        fs.createFile(fs.getFile().getPath(), dir);
        mvc.perform(post("/fs/move?p=/dir")
            .content(mapper.writeValueAsString(new JSONFile[]{new JSONFile(file)}))
            .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
        assertThat(fs.getFile().getFiles(), hasSize(1));
        assertThat(fs.getFile("/dir").getFiles(), hasSize(1));
    }

    @Test
    public void copyFile() throws Exception {
        File file = new File("copy me!");
        File dir = new File("dir", true);
        fs.createFile(fs.getFile().getPath(), file);
        fs.createFile(fs.getFile().getPath(), dir);
        mvc.perform(post("/fs/copy?p=/dir")
                .content(mapper.writeValueAsString(new JSONFile[]{new JSONFile(file)}))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        assertThat(fs.getFile().getFiles(), hasSize(2));
        assertThat(fs.getFile("/dir").getFiles(), hasSize(1));
    }

}