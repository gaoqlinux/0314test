package com.data4truth.mos;


import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import lombok.val;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;

import static feign.form.ContentType.MULTIPART;
import static java.util.Collections.singletonMap;

/**
 * @auther zhangcq
 * @date 2020-06-29 17:17:51
 * @desc
 */

public class FeignSpringFormEncoder extends FormEncoder {

    /**
     * Constructor with the default Feign's encoder as a delegate.
     */
    public FeignSpringFormEncoder() {
        this(new Default());
    }


    /**
     * Constructor with specified delegate encoder.
     *
     * @param delegate delegate encoder, if this encoder couldn't encode object.
     */
    public FeignSpringFormEncoder(Encoder delegate) {
        super(delegate);

        val processor = (MultipartFormContentProcessor) getContentProcessor(MULTIPART);
        processor.addWriter(new SpringSingleMultipartFileWriter());
        processor.addWriter(new SpringManyMultipartFilesWriter());
    }


    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (bodyType.equals(MultipartFile.class)) {
            val file = (MultipartFile) object;
            val data = singletonMap(file.getName(), object);
            super.encode(data, MAP_STRING_WILDCARD, template);
            return;
        } else if (bodyType.equals(MultipartFile[].class)) {
            val file = (MultipartFile[]) object;
            if(file != null) {
                val data = singletonMap(file.length == 0 ? "" : file[0].getName(), object);
                super.encode(data, MAP_STRING_WILDCARD, template);
                return;
            }
        }
        super.encode(object, bodyType, template);
    }
}