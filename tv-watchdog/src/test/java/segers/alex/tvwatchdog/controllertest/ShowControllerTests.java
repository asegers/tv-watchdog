package segers.alex.tvwatchdog.controllertest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import segers.alex.tvwatchdog.application.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ShowControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void paramSlugsShouldReturnShows() throws Exception {

    	String[] testSlugs = {"breaking-bad", "game-of-thrones", "westworld"};
    	
        this.mockMvc.perform(get("/getShows").param("shows", testSlugs)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.shows.[0].title").value("Breaking Bad"))
                .andExpect(jsonPath("$.shows.[0].detail").value(" (2008-2013)"))
                .andExpect(jsonPath("$.shows.[1].title").value("Game of Thrones"))
                .andExpect(jsonPath("$.shows.[2].title").value("Westworld"));
    }

    @Test
    public void noParamsShouldReturnDefaultShows() throws Exception {

    	this.mockMvc.perform(get("/getShows")).andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.shows.[0].title").value("Game of Thrones"))
        .andExpect(jsonPath("$.shows.[1].title").value("Breaking Bad"))
        .andExpect(jsonPath("$.shows.[1].detail").value(" (2008-2013)"))
        .andExpect(jsonPath("$.shows.length()").value(2));
    }

}
