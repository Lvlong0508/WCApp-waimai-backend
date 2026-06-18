package com.gzasc.wechatappwaimai.utils;

import com.gzasc.wechatappwaimai.config.WeChatProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WxApiUtil {

    private final WeChatProperties weChatProperties;
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

        Map<String, Object> result = restTemplate.getForObject(url, Map.class);
        Object errcode = result == null ? null : result.get("errcode");
        if (result == null || errcode instanceof Number && ((Number) errcode).intValue() != 0) {
            throw new RuntimeException("微信登录失败: " + (result != null ? result.get("errmsg") : "未知错误"));
        }
        return result;
    }
}