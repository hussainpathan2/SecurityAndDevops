package com.example.demo.controllers;

import com.example.demo.model.requests.CreateUserRequest;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JacksonTester<CreateUserRequest> jacksonTesterCUR;
    private String token = "Token eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.o4xDDTeTY7WBGdKnouJpb2wqG7cJcpRULDjYz3dvEC7_4QGoWkDRPwCUu8jVdSlULRNMr6oPOZh0fXpUWYRKiw";
    @Before
    public void begin(){
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }
    //find user by id
    @Test
    public void findUserById() throws Exception{
        CreateUserRequest createUserRequest=new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCUR.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Integer userId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        String uriPath = "/api/user/id/" + userId;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(new URI(uriPath))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        Assert.assertNotNull(result.getResponse().getContentAsString());
    }
    //find user by name
    @Test
    public void findUserByName() throws Exception{

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username1");
        createUserRequest.setPassword("password1");
        createUserRequest.setConfirmPassword("password1");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCUR.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/user/username1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        Assert.assertNotNull(mvcResult.getResponse().getContentAsString());
    }
    //create user account
    @Test
    public void createUserAccount() throws Exception{
        CreateUserRequest createUserRequest=new CreateUserRequest();
        createUserRequest.setUsername("username2");
        createUserRequest.setPassword("password2");
        createUserRequest.setConfirmPassword("password2");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCUR.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    //error when password and confirm password does not match
    @Test
    public void checkConfirmPasswordMatch() throws Exception{
        CreateUserRequest createUserRequest=new CreateUserRequest();
        createUserRequest.setUsername("username3");
        createUserRequest.setPassword("password3");
        createUserRequest.setConfirmPassword("wrong");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCUR.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    //error when password is not met specification
    @Test
    public void passwordIsSmall() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("superuser");
        createUserRequest.setPassword("abcde");
        createUserRequest.setConfirmPassword("abcde");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCUR.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    //error when user does not found
    @Test
    public void checkUsername() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/user/wrong"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
