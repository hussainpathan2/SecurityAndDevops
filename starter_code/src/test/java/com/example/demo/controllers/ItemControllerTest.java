package com.example.demo.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private String token = "Token eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.o4xDDTeTY7WBGdKnouJpb2wqG7cJcpRULDjYz3dvEC7_4QGoWkDRPwCUu8jVdSlULRNMr6oPOZh0fXpUWYRKiw";
    @Before
    public void begin() throws Exception{}
    //retriving all the items
    @Test
    public void retriveAllItems() throws Exception{
        MvcResult mvcResult= mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andReturn();
        List<Integer> items = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$..id");
        Assert.assertEquals(2, items.size());
    }
    //find item by id
    @Test
    public void FindItemById() throws Exception{
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andReturn();
        List<Integer> items = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$..id");
        Assert.assertEquals(1, items.size());
    }
    //find item by name
    @Test
    public void findItemByName() throws Exception{
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item/name/Square%20Widget"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andReturn();
        List<Integer> items = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$..id");
        Assert.assertEquals(1, items.size());
    }
    //return error when item is not found
    @Test
    public void returnErrorWhenItemIsNotFound() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item/5"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
