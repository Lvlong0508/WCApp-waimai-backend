package com.gzasc.wechatappwaimai.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserFileController {

    private final FileService fileService;

    @PostMapping("/upload/user")
    public WcwmResponse<Map<String, String>> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadUserAvatar(file, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        log.info("用户上传头像：" + url);
        return WcwmResponse.success(data);
    }

    @PostMapping("/upload/product/{productId}")
    public WcwmResponse<Map<String, String>> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long productId) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadProductImage(file, productId, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        log.info("用户上传商品图片 productId={}, url={}", productId, url);
        return WcwmResponse.success(data);
    }

    @PostMapping("/upload/temp-shoplogo")
    public WcwmResponse<Map<String, String>> uploadTempShopLogo(@RequestParam("file") MultipartFile file) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadTempShopLogo(file, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        log.info("用户上传临时店铺Logo userId={}, url={}", userId, url);
        return WcwmResponse.success(data);
    }

    @PostMapping("/upload/shoplogo/{shopId}")
    public WcwmResponse<Map<String, String>> uploadShopLogo(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long shopId) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadShopLogo(file, shopId, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        log.info("用户上传店铺Logo shopId={}, url={}", shopId, url);
        return WcwmResponse.success(data);
    }
}
