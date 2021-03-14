package com.data4truth.mos.service;


import com.data4truth.mos.config.ObsConfig;
import com.data4truth.mos.util.ObsUtil;
import com.obs.services.IObsClient;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 * 路径： com.data4truth.pi.service.MyObsUtil
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/21 15:07
 */

@Slf4j
public class MyObsUtil {


    public ObsConfig obsConfig;

    public MyObsUtil(ObsConfig obsConfig){
        this.obsConfig = obsConfig;
    }


    /**
     * 单文件上传
     * @param key
     * @param inputStream
     * @return
     */
    public String put2Obs(String key, InputStream inputStream) {
        PutObjectResult putResult = ObsUtil.put2Obs(obsConfig, obsConfig.getBucketName(), key, inputStream);
        log.info("upload key {}, upload result {}", key, putResult);
        return replaceUrl(putResult, obsConfig.getAccessUrl(), key);
    }

    public boolean fileExisted(String key) {
        return ObsUtil.keyExisted(obsConfig, obsConfig.getBucketName(), key);
    }

    /**
     * 多文件上传
     * @param files
     * @return
     * @throws IOException
     */
    public List<String> multiFileUpload(List<MultipartFile> files) throws IOException {
        IObsClient client = ObsUtil.getClient(obsConfig);
        String accessUrl = obsConfig.getAccessUrl();
        try {
            ArrayList<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                PutObjectRequest putRequest = filePutRequest(file);
                PutObjectResult putResult = client.putObject(putRequest);
                log.info("upload req {}, upload result {}", putRequest, putResult);
                String url = replaceUrl(putResult, accessUrl, putRequest.getObjectKey());
                urls.add(url);
            }
            client.close();
            return urls;
        } finally {
            ObsUtil.returnClient(client);
        }

    }

    public PutObjectRequest filePutRequest(MultipartFile file) throws IOException {
        PutObjectRequest request = new PutObjectRequest();
        String fileKey = ObsUtil.fileKeyGenerate(file.getOriginalFilename());
        request.setBucketName(obsConfig.getBucketName());
        request.setObjectKey(fileKey);
        request.setInput(file.getInputStream());
        request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        return request;
    }

    public String replaceUrl(PutObjectResult result, String accessUrl, String key) {
        if (StringUtils.isNotBlank(accessUrl)) {
            // 配置了访问域名的，使用访问域名作为资源访问地址，否则使用obs给出地址作为资源访问地址
            StringBuilder urlBuilder = new StringBuilder(accessUrl);
            if (!accessUrl.endsWith("/")) {
                urlBuilder.append('/');
            }
            urlBuilder.append(key);
            return urlBuilder.toString();
        } else {
            return result.getObjectUrl();
        }
    }
}