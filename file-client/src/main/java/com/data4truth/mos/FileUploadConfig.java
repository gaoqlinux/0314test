package com.data4truth.mos;

import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther zhangcq
 * @date 2020-06-29 16:52:56
 * @desc
 */
@Configuration
public class FileUploadConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;



    @Bean
    public Encoder feignFormEncoder() {
        return new FeignSpringFormEncoder(new SpringEncoder(messageConverters));

    }

}
