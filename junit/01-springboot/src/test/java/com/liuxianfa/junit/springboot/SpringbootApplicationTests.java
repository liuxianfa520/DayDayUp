package com.liuxianfa.junit.springboot;

import com.liuxianfa.junit.springboot.service.HelloService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class SpringbootApplicationTests {


    @Autowired
    HelloService helloService;
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象
    }


    @Test
    public void testService() {
        helloService.hello("axl");
    }

    @Test
    public void testController() throws Exception {
        MockHttpServletRequestBuilder request = get("/hello")
                .content("name=axl")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String contentAsString = mockMvc.perform(request)
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString();

        System.out.println(contentAsString);
    }
}
