package ru.malltshik.filesystem.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.malltshik.filesystem.Application;
import ru.malltshik.filesystem.entities.File;
import ru.malltshik.filesystem.services.FileSystem;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class InMemFileSystemIntegrationTest {

    @Autowired private WebApplicationContext wac;
    @Resource private FileSystem fs;

    private MockMvc mvc;

    @Before
    public void before() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
    }

}