package com.gzasc.wechatappwaimai.service.impl;

import cn.hutool.core.io.FileUtil;
import com.gzasc.wechatappwaimai.entity.Product;
import com.gzasc.wechatappwaimai.entity.Shop;
import com.gzasc.wechatappwaimai.entity.UploadFile;
import com.gzasc.wechatappwaimai.mapper.FileMapper;
import com.gzasc.wechatappwaimai.mapper.ProductMapper;
import com.gzasc.wechatappwaimai.mapper.ShopMapper;
import com.gzasc.wechatappwaimai.mapper.UserMapper;
import com.gzasc.wechatappwaimai.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final UserMapper userMapper;
    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.url}")
    private String accessUrl;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/png", "image/jpeg", "image/jpg");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private String saveFile(MultipartFile file, String entityType, Long entityId, Long userId, boolean deleteOldFiles) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!Arrays.asList("png", "jpg", "jpeg").contains(extension)) {
            throw new IllegalArgumentException("只支持 png/jpg 格式的图片");
        }

        String relativeDir = entityType + "/" + entityId;
        File dir = new File(uploadPath, relativeDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (deleteOldFiles) {
            deleteOldFilesByEntity(entityType, entityId);
        }

        String fileName = entityId + "_" + randomLetters() + "." + extension;

        File destFile = new File(dir, fileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        }

        String savePath = relativeDir + "/" + fileName;
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUserId(userId);
        uploadFile.setOriginalName(originalFilename);
        uploadFile.setSavePath(savePath);
        uploadFile.setSize(file.getSize());
        uploadFile.setCreateTime(LocalDateTime.now());
        uploadFile.setEntityType(entityType);
        uploadFile.setEntityId(entityId);
        fileMapper.insert(uploadFile);

        return accessUrl + "/image/" + savePath;
    }

    private String randomLetters() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
        }
        return builder.toString();
    }

    private void deleteOldFilesByEntity(String entityType, Long entityId) {
        List<UploadFile> oldFiles = fileMapper.selectByEntity(entityType, entityId);
        for (UploadFile oldFile : oldFiles) {
            File diskFile = new File(uploadPath, oldFile.getSavePath());
            if (diskFile.exists()) {
                diskFile.delete();
            }
        }
        if (!oldFiles.isEmpty()) {
            fileMapper.deleteByEntity(entityType, entityId);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("只支持 png/jpg 格式的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }
    }

    @Override
    public String uploadUserAvatar(MultipartFile file, Long userId) {
        String url = saveFile(file, "user", userId, userId, true);
        userMapper.updateAvatarUrl(userId, url);
        return url;
    }

    @Override
    public String uploadProductImage(MultipartFile file, Long productId, Long userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        Shop shop = shopMapper.selectById(product.getShopId());
        if (shop == null || !userId.equals(shop.getOwnerUserId())) {
            throw new IllegalArgumentException("无权操作该商品");
        }
        String url = saveFile(file, "product", productId, userId, false);
        productMapper.updateImage(productId, url);
        return url;
    }

    @Override
    public String uploadShopLogo(MultipartFile file, Long shopId, Long userId) {
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("店铺不存在");
        }
        if (!userId.equals(shop.getOwnerUserId())) {
            throw new IllegalArgumentException("无权操作该店铺");
        }
        String url = saveFile(file, "shoplogo", shopId, userId, true);
        shopMapper.updateLogo(shopId, url);
        return url;
    }

    @Override
    public String uploadTempAvatar(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!Arrays.asList("png", "jpg", "jpeg").contains(extension)) {
            throw new IllegalArgumentException("只支持 png/jpg 格式的图片");
        }

        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativeDir = "temp_avatar/" + datePart;
        File dir = new File(uploadPath, relativeDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + randomLetters() + "." + extension;
        File destFile = new File(dir, fileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        }

        return accessUrl + "/image/" + relativeDir + "/" + fileName;
    }
}
