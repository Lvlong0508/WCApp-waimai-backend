package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadFile {
    private Long id;
    private Long userId;           // 上传者
    private String originalName;   // 原始文件名
    private String savePath;       // 存储路径或 URL
    private Long size;
    private LocalDateTime createTime;
    private String entityType;    // 实体类型: user/product/shoplogo
    private Long entityId;        // 实体ID
}