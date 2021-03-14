package com.data4truth.mos.service;


import com.data4truth.mos.config.ObsConfig;
import com.data4truth.mos.util.BASE64DecodedMultipartFile;
import com.data4truth.mos.util.ObsUtil;
import com.zjdex.framework.exception.CodeException;
import com.zjdex.framework.util.ResultCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 * 路径： com.data4truth.mos.service.ObsService
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/21 10:23
 */


public class ObsService implements UploadService {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            //设置链接超时
            .connectTimeout(10, TimeUnit.SECONDS)
            // 设置写数据超时
            .writeTimeout(10, TimeUnit.SECONDS)
            // 设置读数据超时
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private MyObsUtil myObsUtil;

    public ObsService(ObsConfig obsConfig){
        this.myObsUtil = new MyObsUtil(obsConfig);
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

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileKey = ObsUtil.fileKeyGenerate(originalFilename);
        String resultUrl = myObsUtil.put2Obs(fileKey, file.getInputStream());
        return resultUrl;
    }

    /**
     * 多文件上传
     *
     * @param files
     * @return
     */
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> urls = myObsUtil.multiFileUpload(files);
        return urls;
    }

    /**
     * base64文件上传
     *
     * @param base64s
     * @return
     */
    public List<String> uploadByBase64s(List<String> base64s) {
        if (CollectionUtils.isEmpty(base64s)) {
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

    /**
     * pdf转为图片后上传
     *
     * @param fileUrl
     * @return
     */
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
}
