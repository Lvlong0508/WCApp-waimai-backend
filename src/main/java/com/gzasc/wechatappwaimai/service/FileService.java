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
     * 上传临时店铺Logo（注册前使用，需登录，按日期分目录）
     * @param file 图片文件
     * @param userId 上传者用户ID
     * @return 可访问的完整URL
     */
    String uploadTempShopLogo(MultipartFile file, Long userId);

    /**
     * 注册后将临时店铺Logo迁移为正式Logo（移动到 shoplogo/ 目录并更新shop表）
     * @param tempUrl 临时Logo的完整访问URL
     * @param shopId 店铺ID
     * @param userId 上传者用户ID
     * @return 迁移后的永久URL
     */
    String moveTempShopLogoToPermanent(String tempUrl, Long shopId, Long userId);

    /**
     * 上传未登录用户的临时头像（按日期分目录，不写 upload_file 表）
     * @param file 图片文件
     * @return 可访问的完整URL
     */
    String uploadTempAvatar(MultipartFile file);

    /**
     * 登录后将临时头像迁移为正式用户头像（移动到 avatar/ 目录并更新URL）
     * @param tempUrl 临时头像的完整访问URL
     * @param openid 微信openid（目录名和文件名）
     * @return 迁移后的永久URL
     */
    String moveTempAvatarToPermanent(String tempUrl, String openid);

    /**
     * 删除临时头像文件（老用户登录时清理用）
     * @param tempUrl 临时头像的完整访问URL
     */
    void deleteTempAvatarByUrl(String tempUrl);
}
