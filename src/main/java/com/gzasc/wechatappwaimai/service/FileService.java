package com.gzasc.wechatappwaimai.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传用户头像（自动清理旧头像）
     * @param file 图片文件
     * @param userId 用户ID
     * @return 可访问的完整URL
     */
    String uploadUserAvatar(MultipartFile file, Long userId);

    /**
     * 上传商品图片（允许多张，不清理旧图）
     * @param file 图片文件
     * @param productId 商品ID
     * @param userId 上传者用户ID
     * @return 可访问的完整URL
     */
    String uploadProductImage(MultipartFile file, Long productId, Long userId);

    /**
     * 上传店铺Logo（自动清理旧Logo）
     * @param file 图片文件
     * @param shopId 店铺ID
     * @param userId 上传者用户ID
     * @return 可访问的完整URL
     */
    String uploadShopLogo(MultipartFile file, Long shopId, Long userId);

    /**
     * 上传未登录用户的临时头像（按日期分目录，不写 upload_file 表）
     * @param file 图片文件
     * @return 可访问的完整URL
     */
    String uploadTempAvatar(MultipartFile file);
}
