package com.data4truth.mos.util;

import com.data4truth.mos.config.ObsConfig;
import com.obs.services.IObsClient;
import com.obs.services.ObsClient;
import com.obs.services.model.*;
import com.zjdex.framework.exception.CodeException;
import com.zjdex.framework.util.ResultCode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 功能：
 * 路径： ObsUtil
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/21 15:01
 */
public class ObsUtil {

    /**
     * 上传到obs
     *
     * @param bucketName
     * @param key
     * @param inputStream
     * @return
     */
    public static PutObjectResult put2Obs(ObsConfig obsConfig, String bucketName, String key, InputStream inputStream) {
        if (StringUtils.isBlank(bucketName) || StringUtils.isBlank(key) || inputStream == null) {
            throw new CodeException(ResultCode.Codes.UNKNOW);
        }
        PutObjectRequest request = new PutObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(key);
        request.setInput(inputStream);
        request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        return put2Obs(obsConfig, request);
    }

    public static PutObjectResult put2Obs(ObsConfig obsConfig, PutObjectRequest request) {
        if (StringUtils.isBlank(request.getBucketName()) ||
                StringUtils.isBlank(request.getObjectKey()) ||
                (request.getInput() == null && request.getFile() == null)) {
            throw new CodeException(ResultCode.Codes.UNKNOW);
        }
        IObsClient client = null;
        try {
            client = getClient(obsConfig);
            return client.putObject(request);
        } catch (Exception e) {
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR.getCode(), e.getMessage());
        } finally {
            returnClient(client);
        }
    }

    /**
     * 检测键是否已存在
     *
     * @param bucketName
     * @param key
     * @return
     */
    public static boolean keyExisted(ObsConfig obsConfig, String bucketName, String key) {
        if (StringUtils.isBlank(bucketName) || StringUtils.isBlank(key)) {
            throw new CodeException(ResultCode.Codes.UNKNOW);
        }
        IObsClient client = null;
        try {
            client = getClient(obsConfig);
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix(key);
            ObjectListing objectListing = client.listObjects(listObjectsRequest);
            List<ObsObject> matchObjs = objectListing.getObjects();
            if (matchObjs == null || matchObjs.size() == 0) {
                return false;
            } else {
                return matchObjs.stream().anyMatch(obj -> key.equals(obj.getObjectKey()));
            }
        } catch (Exception e) {
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR.getCode(), e.getMessage());
        } finally {
            returnClient(client);
        }
    }

    public static IObsClient getClient(ObsConfig obsConfig) {
        return new ObsClient(obsConfig.getAccessKey(), obsConfig.getSecretKey(), obsConfig.getObsConfiguration());
    }

    public static void returnClient(IObsClient obsClient) {
        if (obsClient != null) {
            try {
                obsClient.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 文件名生成
     *
     * @param fileName
     * @return
     */
    public static String fileKeyGenerate(String fileName) {
        String symbol = ".";
        String suffix = "";
        if (fileName.contains(symbol)) {
            int index = fileName.lastIndexOf(symbol);
            suffix = fileName.substring(index);
        }
        String prefix = UUID.randomUUID().toString();
        String fileKey = prefix + suffix;
        return fileKey;
    }
}