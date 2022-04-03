package com.example.demo.controllers;

import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.*;
import java.awt.image.DirectColorModel;
import java.net.URI;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<ModifyCartRequest> jacksonTesterMCR;
    @Autowired
    private JacksonTester<CreateUserRequest> jacksonTesterCRU;
    private String token = "Token eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.o4xDDTeTY7WBGdKnouJpb2wqG7cJcpRULDjYz3dvEC7_4QGoWkDRPwCUu8jVdSlULRNMr6oPOZh0fXpUWYRKiw";
    //submit order
    @Test
    public void submitOrder() throws Exception{
        createUserAccount("username");
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/order/submit/username"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
    //view submitted orders
    @Test
    public void viewOrders() throws Exception{
        createUserAccount("username");
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/order/submit/username"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
        MvcResult allOrders = mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/order/history/username"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        Assert.assertNotNull(allOrders.getResponse().getContentAsString());
    }
    private void createUserAccount(String username) throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCRU.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
