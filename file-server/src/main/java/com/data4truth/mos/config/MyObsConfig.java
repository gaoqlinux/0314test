package com.data4truth.mos.config;


import com.data4truth.mos.service.ObsService;
import com.data4truth.mos.service.OssStsFileAccessService;
import com.data4truth.mos.service.UploadService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 功能：
 * 路径： com.data4truth.pi.config.MyObsConfig
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/21 15:11
 */

@Component
@ConfigurationProperties(prefix = "obs")
public class MyObsConfig {

    // ak
    private String accessKey;

    // sk
    private String secretKey;

    // endpoint
    private String endPoint;

    private Integer socketTimeout = 30000;

    private Integer connectTimeout = 10000;

    // bucketname
    private String bucketName;

    // 访问域名
    private String accessUrl;

    private String roleArn;

    private String obsType;//文件服务器类型huawei/ali

    @Bean
    @Qualifier("obsConfig")
    public ObsConfig createObsConfig() {
        ObsConfig obsConfig = new ObsConfig();
        obsConfig.setAccessKey(accessKey);
        obsConfig.setSecretKey(secretKey);
        obsConfig.setEndPoint(endPoint);
        obsConfig.setSocketTimeout(socketTimeout);
        obsConfig.setConnectTimeout(connectTimeout);
        obsConfig.setBucketName(bucketName);
        obsConfig.setAccessUrl(accessUrl);
        obsConfig.setRoleArn(roleArn);
        return obsConfig;
    }

    @Bean
    public UploadService createUploadService(ObsConfig obsConfig){
        if("ali".equals(obsType)){
            return new OssStsFileAccessService(obsConfig);
        }else{
            return new ObsService(obsConfig);
        }
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getObsType() {
        return obsType;
    }

    public void setObsType(String obsType) {
        this.obsType = obsType;
    }

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }
}