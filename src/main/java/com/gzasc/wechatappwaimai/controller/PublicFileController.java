package com.gzasc.wechatappwaimai.controller;

import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicFileController {

    private final FileService fileService;

    @PostMapping("/upload/temp-avatar")
    public WcwmResponse<Map<String, String>> uploadTempAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadTempAvatar(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return WcwmResponse.success(data);
    }
}
