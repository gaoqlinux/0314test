package com.data4truth.mos.req;

import lombok.Data;

import java.util.List;

/**
 * UploadFilesReq
 *
 * @author songbin
 * @date 2020/06/04 11:39
 * @description
 */
@Data
public class UploadFilesReq {

    /**
     * 图片base64
     */
    private List<String> base64s;
}
