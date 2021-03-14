package com.data4truth.mos.service;

import com.aliyun.oss.OSSClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;
import com.data4truth.mos.config.ObsConfig;
import com.data4truth.mos.util.BASE64DecodedMultipartFile;
import com.data4truth.mos.util.FileUtils;
import com.zjdex.framework.exception.CodeException;
import com.zjdex.framework.util.ResultCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ossSts服务类
 * @author wushan
 * @version $Id: OssFileAccessService.java, v 0.1 2019年12月9日 上午10:22:54 wushan Exp $
 */
public class OssStsFileAccessService implements UploadService{

    private final Logger       logger             = LogManager.getLogger();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            //设置链接超时
            .connectTimeout(10, TimeUnit.SECONDS)
            // 设置写数据超时
            .writeTimeout(10, TimeUnit.SECONDS)
            // 设置读数据超时
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private ObsConfig obsConfig;

    private MyObsUtil myObsUtil;

    public OssStsFileAccessService(ObsConfig obsConfig){
        this.obsConfig = obsConfig;
        this.myObsUtil = new MyObsUtil(obsConfig);
    }


    // 目前只有"cn-hangzhou"这个region可用, 不要使用填写其他region的值,后续优化成可动态配置的
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";

    private OSSClient getOSSClient() {
        final Credentials credentials = assumeRole().getCredentials();
        return new OSSClient(obsConfig.getEndPoint(), credentials.getAccessKeyId(), credentials.getAccessKeySecret(), credentials.getSecurityToken());
    }

    private AssumeRoleResponse assumeRole() {

        // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
        final IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU, obsConfig.getAccessKey(), obsConfig.getSecretKey());

        final DefaultAcsClient client = new DefaultAcsClient(profile);

        // 创建一个 AssumeRoleRequest 并设置请求参数
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(obsConfig.getRoleArn());
        request.setRoleSessionName("sl-");
        // 发起请求，并得到response
        try {
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void deleteObject(String key) {
        final OSSClient client = getOSSClient();
        try {
            client.deleteObject(obsConfig.getBucketName(), key);
        } finally {
            client.shutdown();
        }
    }

    public byte[] getBytes(String key) throws IOException {
        final OSSClient client = getOSSClient();
        try {
            Map<String, String> requestHeaders = new HashMap<String, String>();
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            URL signedUrl = client.generatePresignedUrl(obsConfig.getBucketName(), key, expiration);
            return IOUtils.toByteArray(client.getObject(signedUrl, requestHeaders).getObjectContent());
        } finally {
            client.shutdown();
        }
    }

    public void putObject(String key, InputStream input) {
        final OSSClient client = getOSSClient();
        try {
            client.putObject(obsConfig.getBucketName(), key, input);
        } finally {
            client.shutdown();
        }
    }

    public void putObject(Map<String, InputStream> files) {
        final OSSClient client = getOSSClient();
        try {
            for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                client.putObject(obsConfig.getBucketName(), entry.getKey(), entry.getValue());
                IOUtils.closeQuietly(entry.getValue());
            }
        } finally {
            client.shutdown();
        }
    }

    public boolean doesObjectExist(String key) {

        final OSSClient client = getOSSClient();

        boolean result = false;

        try {
            result = client.doesObjectExist(obsConfig.getBucketName(), key);
        } finally {
            client.shutdown();
        }
        return result;
    }


    /** 文件最大值 ,单位1024*1024 */
    private static Integer                 MAX_SIZE          = 20;

    /**
     * 文件上传处理
     */
    public String uploadFile(MultipartFile file) {
        final FileDto result = new FileDto();
        String fileName = file.getOriginalFilename();
        String fileSuffix = FileUtils.resolveSuffix(fileName);
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
            if (bytes.length > MAX_SIZE * 1024 * 1024) {
                throw new RuntimeException("upload file too large."+ new Integer[] { 5 });
            }
            final String fileKey = UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY) + "." + fileSuffix;
            final InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
            this.putObject(fileKey, inputStream);
            result.setFileId(fileKey);
            result.setFileName(fileName);
            result.setFileSuffix(fileSuffix);
            result.setFileSize(bytes.length);
            return this.getPreviewUrl(result.getFileId());
        } catch (IOException e) {
            throw new RuntimeException("upload file exception."+e.getMessage());
        }
    }

    public void download(FileDto file, HttpServletResponse response) throws IOException {
        final byte[] bytes = this.getBytes(file.getFileId());
        final OutputStream out = response.getOutputStream();
        //下面三行是关键代码，处理乱码问题
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getFileName().getBytes("utf-8"), "iso8859-1"));
        out.write(bytes);
        out.flush();
    }

    public void delete(String fileId) {
        this.deleteObject(fileId);
    }

    /**
     * 文件上传处理
     */
    public List<String> uploadFiles(List<MultipartFile> files) {
        final List<FileDto> results = new LinkedList<FileDto>();
        try {
            final Map<String, InputStream> fileMap = new HashMap<String, InputStream>();
            for (final MultipartFile file : files) {
                final FileDto result = new FileDto();
                String fileName = file.getOriginalFilename();
                String fileSuffix = FileUtils.resolveSuffix(fileName);
                final byte[] bytes = file.getBytes();
                if (bytes.length > MAX_SIZE * 1024 * 1024) {
                    throw new RuntimeException("upload file too large."+ new Integer[] { 5 });
                }
                String fileKey = RandomStringUtils.random(32, true, true) + "." + fileSuffix;
                InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
                fileMap.put(fileKey, inputStream);
                result.setFileId(fileKey);
                result.setFileName(fileName);
                result.setFileSuffix(fileSuffix);
                result.setFileSize(bytes.length);
                results.add(result);
            }
            this.putObject(fileMap);
            List<String> urls = new ArrayList<>();
            for (FileDto fileDto :results){
                urls.add(this.getPreviewUrl(fileDto.getFileId()));
            }
            return urls;
        } catch (IOException e) {
            throw new RuntimeException("upload file exception."+e.getMessage());
        }
    }


    public String getPreviewUrl(String fileId) {
        String previewPrefix = obsConfig.getAccessUrl();
        //String previewPrefix = "https://" + defaultBucket + "." + this.endpoint + "/";
        return fileId.startsWith(previewPrefix) ? fileId : previewPrefix + fileId;
    }

    /**
     * base64文件上传
     * @param base64s
     * @return
     */
    public List<String> uploadByBase64s(List<String> base64s) {
        if (CollectionUtils.isEmpty(base64s)){
            throw new CodeException(ResultCode.Codes.PARRAMS_ERROR, "参数异常");
        }
        List<String> imgUrls = new ArrayList<>();
        for (String base64 : base64s) {
            MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipart(base64);
            String imgUrl;
            try {
                imgUrl = uploadFile(file);
            } catch (Exception e) {
                throw new CodeException(ResultCode.Codes.PARRAMS_ERROR, "上传失败");
            }
            imgUrls.add(imgUrl);
        }
        return imgUrls;
    }

    @Override
    public List<String> uploadFileToPicture(String fileUrl) {
        List<String> urls = new ArrayList<>(8);
        for (InputStream inputStream : getStreamData(fileUrl)) {
            String pictureName = String.format("%s.png", UUID.randomUUID().toString());
            String url = myObsUtil.put2Obs(pictureName, inputStream);
            urls.add(url);
        }
        return urls;
    }

    /**
     * 根据url地址获取流
     *
     * @param url
     * @return
     */
    private List<InputStream> getStreamData(String url) {
        PDDocument pdDocument;
        List<InputStream> inputStreams = new ArrayList<>();
        try {
            Response response = doGet(url);
            pdDocument = PDDocument.load(response.body().byteStream());
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", outputStream);
                byte[] data = outputStream.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(data);
                inputStreams.add(inputStream);
            }
            return inputStreams;
        } catch (Exception e) {
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR, e.getMessage());
        } finally {
            for (InputStream inputStream : inputStreams) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 普通get请求
     *
     * @param url 请求链接
     * @return OK HTTP请求的结果
     * @throws Exception 异常
     */
    private static Response doGet(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        return okHttpClient.newCall(request).execute();
    }
}