package com.gzasc.wechatappwaimai.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzasc.wechatappwaimai.config.WeChatProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WxApiUtil {

    private final WeChatProperties weChatProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * 通过 code 换取 openid、unionid 和 session_key
     * @param code 小程序临时登录凭证
     * @return 包含 openid、unionid、session_key 的 Map
     */
    public Map<String, Object> jscode2session(String code) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", weChatProperties.getAppid())
                .queryParam("secret", weChatProperties.getSecret())
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        String raw = restTemplate.getForObject(url, String.class);
        log.info("微信 jscode2session 原始响应: {}", raw);

        try {
            Map<String, Object> result = objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {});
            Object errcode = result.get("errcode");
            if (errcode != null && (!(errcode instanceof Integer) || ((Integer) errcode) != 0)) {
                throw new RuntimeException("微信登录失败: " + result.get("errmsg"));
            }
            return result;
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("微信登录响应解析失败: " + e.getMessage());
        }
    }
}