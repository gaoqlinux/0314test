package com.data4truth.mos.config;

import com.obs.services.ObsConfiguration;

/**
 * 功能：
 * 路径： com.data4truth.pi.config.ObsConfig
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/21 14:59
 */

public class ObsConfig {

    private String accessKey;

    private String secretKey;

    private String endPoint;

    private Integer socketTimeout;

    private Integer connectTimeout;

    private String bucketName;

    // 访问url
    private String accessUrl;

    private String roleArn;

    public ObsConfiguration getObsConfiguration() {
        ObsConfiguration configuration = new ObsConfiguration();
        if (socketTimeout != null) {
            configuration.setSocketTimeout(socketTimeout);
        }
        if (connectTimeout != null) {
            configuration.setConnectionTimeout(connectTimeout);
        }
        configuration.setEndPoint(endPoint);
        return configuration;
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

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }
}