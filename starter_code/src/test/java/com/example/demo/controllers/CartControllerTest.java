package com.example.demo.controllers;

import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.jayway.jsonpath.JsonPath;
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

import static org.junit.Assert.assertEquals;

import java.net.URI;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<ModifyCartRequest> jacksonTesterMCR;
    @Autowired
    private JacksonTester<CreateUserRequest> jacksonTesterCR;
    private final String token="Token eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.o4xDDTeTY7WBGdKnouJpb2wqG7cJcpRULDjYz3dvEC7_4QGoWkDRPwCUu8jVdSlULRNMr6oPOZh0fXpUWYRKiw";
    private void begin(String username) throws Exception{
        CreateUserRequest createUserRequest=new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                        .content(jacksonTesterCR.write(createUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //token missing error
    @Test
    public void tokenMissing() throws Exception{
        begin("user");
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setUsername("user");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    // Test for Adding item to cart
    @Test
    public void addItemToCart() throws Exception{
        begin("username");
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        String nameOfTheProduct= JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.items[0].name");
        assertEquals("Round Widget",nameOfTheProduct);
    }
    //removing item from the cart
    @Test
    public void removeItemFromCart() throws Exception{
        final double DELTA=1e-15;
        begin("username");
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/removeFromCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        Double totalResult= JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.total");
        Assert.assertEquals(0.00,totalResult,DELTA);
    }
    //user does not exist error
    @Test
    public void userDoesNotExistError() throws Exception{
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setUsername("invalid");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                        .content(jacksonTesterMCR.write(modifyCartRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
