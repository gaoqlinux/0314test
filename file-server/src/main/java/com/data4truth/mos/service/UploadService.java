package com.data4truth.mos.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @auther zhangcq
 * @date 2020-07-01 16:27:29
 * @desc  文件上传公共方法
 */

public interface UploadService {

    String uploadFile(MultipartFile file) throws IOException;

    List<String> uploadFiles(List<MultipartFile> files) throws IOException;

    List<String> uploadByBase64s(List<String> base64s);

    List<String> uploadFileToPicture(String fileUrl);


}
