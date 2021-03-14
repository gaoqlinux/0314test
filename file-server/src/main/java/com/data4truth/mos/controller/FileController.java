package com.data4truth.mos.controller;

import com.alibaba.fastjson.JSON;
import com.data4truth.mos.req.UploadFilesReq;
import com.data4truth.mos.service.ObsService;
import com.data4truth.mos.service.UploadService;
import com.zjdex.framework.bean.BaseResponse;
import com.zjdex.framework.exception.CodeException;
import com.zjdex.framework.util.ResponseUtil;
import com.zjdex.framework.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 功能：
 * 路径： FileController
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/21 10:19
 */

@RestController
@RequestMapping("/api/resources/files")
@Slf4j
public class FileController {

    @Autowired
    private UploadService uploadService;


    @PostMapping("/uploads")
    public Object uploads(List<MultipartFile> files) {
        try {
            log.info("上传文件数量：{}",files.size());
            if (files == null || files.size() == 0) {
                return new ArrayList<String>();
            }
            List<String> urls = uploadService.uploadFiles(files);
            log.info("返回结果是:{}", JSON.toJSONString(urls));
            return ResponseUtil.success(urls);
        } catch (IOException e) {
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR, "上传失败");
        }
    }

    @PostMapping("/upload")
    public Object uploadFile(@RequestPart("file") MultipartFile file) {
        if (StringUtils.isBlank(file.getOriginalFilename())) {
            throw new CodeException(ResultCode.Codes.FILE_NOT_FIND);
        }
        try {
            String url = uploadService.uploadFile(file);
            log.info("返回结果是:{}", url);
            return ResponseUtil.success(url);
        } catch (IOException e) {
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR, "上传失败");
        }
    }

    @PostMapping("/uploadManyFiles")
    public Object uploadManyFiles(@RequestPart("files") MultipartFile[] files) {
        try {
            List<MultipartFile> fileList = new ArrayList<>(Arrays.asList(files));
            log.info("上传文件数量：{}",fileList.size());
            if (fileList == null || fileList.size() == 0) {
                return new ArrayList<String>();
            }
            List<String> urls = uploadService.uploadFiles(fileList);
            log.info("返回结果是:{}", JSON.toJSONString(urls));
            return ResponseUtil.success(urls);
        } catch (IOException e) {
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR, "上传失败");
        }
    }

    @PostMapping("/uploads/base64")
    public Object uploadsBase64(@RequestBody UploadFilesReq req) {
        log.info("上传文件数量：{}",req.getBase64s().size());
        List<String> stringList = uploadService.uploadByBase64s(req.getBase64s());
        log.info("返回结果是:{}", JSON.toJSONString(stringList));
        return ResponseUtil.success(stringList);
    }

    /**
     * 上传一个pdf文件的地址，转成流之后，上传到Obs服务器，返回图片地址
     *
     * @param fileUrl
     * @return
     */
    @GetMapping("/uploadFileToPicture")
    public BaseResponse<List<String>> uploadFileToPicture(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return ResponseUtil.success(Collections.EMPTY_LIST);
        }
        try {
            List<String> urls = uploadService.uploadFileToPicture(fileUrl);
            return ResponseUtil.success(urls);
        } catch (Exception e) {
            log.error("uploadFileToPicture error: {}", e.getMessage());
            throw new CodeException(ResultCode.Codes.BUSINESS_ERROR, "上传失败");
        }
    }
}
