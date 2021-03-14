package com.data4truth.mos;

import com.zjdex.framework.bean.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author peisong.chen
 * @date 2020/6/19
 * @description
 */
@FeignClient(value = "file-server", fallback = FileClientFallBack.class)
public interface FileClient {

    /**
     * 上传一个文件的地址，转成流之后，上传到Obs服务器，返回图片地址
     *
     * @param fileUrl
     * @return
     */
    @GetMapping("/api/resources/files/uploadFileToPicture")
    BaseResponse<List<String>> uploadFileToPicture(@RequestParam String fileUrl);


    /**
     * 上传单个文件
     * @param file
     * @return
     */
    @PostMapping(value = "/api/resources/files/upload" ,consumes = "multipart/form-data")
    BaseResponse<String> uploadOneFile(@RequestPart("file") MultipartFile file);


    /**
     * 上传多个文件
     * @param files
     * @return
     */
    @PostMapping(value = "/api/resources/files/uploadManyFiles",consumes = "multipart/form-data")
    BaseResponse<List<String>> uploadManyFiles(@RequestPart("files") MultipartFile[]  files);


    /**
     * 上传base64字符串文件
     * @param req
     * @return
     */
    @PostMapping(value = "/api/resources/files/uploads/base64",consumes = MediaType.APPLICATION_JSON_VALUE)
    BaseResponse<List<String>> uploadsBase64Str(@RequestBody UploadFilesReq req);

}