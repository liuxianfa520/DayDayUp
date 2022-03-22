package com.anxiaole.example.api;

import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.interceptor.ApiInterceptor;
import com.gitee.easyopen.support.ApiController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author LiuXianfa
 * 
 * @date 11/25 15:37
 */
@Controller
@RequestMapping("/api")
public class IndexController extends ApiController {

    @Override
    protected void initApiConfig(ApiConfig apiConfig) {
        apiConfig.setTimeoutSeconds(600000);
        apiConfig.setShowDoc(true);
        apiConfig.openAppMode();
        apiConfig.setIgnoreValidate(true);

        apiConfig.setInterceptors(new ApiInterceptor[]{new MyExampleInterceptor()});
    }
}
