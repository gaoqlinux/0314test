package com.data4truth.mos;

import com.zjdex.framework.bean.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author peisong.chen
 * @date 2020/6/19
 * @description
 */
public class FileClientFallBack implements FileClient {

    @Override
    public BaseResponse<List<String>> uploadFileToPicture(String fileUrl) {
        return null;
    }

    @Override
    public BaseResponse<String> uploadOneFile(MultipartFile file) {
        return null;
    }

    @Override
    public BaseResponse<List<String>> uploadManyFiles(MultipartFile[] files) {
        return null;
    }

    @Override
    public BaseResponse<List<String>> uploadsBase64Str(UploadFilesReq req) {
        return null;
    }
}