package com.gzasc.wechatappwaimai.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.AddressRequest;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.AddressService;
import com.gzasc.wechatappwaimai.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Slf4j
public class UserAddressController {

    private final AddressService addressService;

    @GetMapping("/default")
    public WcwmResponse<AddressVO> getDefaultAddress() {
        Long userId = getCurrentUserId();
        AddressVO data = addressService.getDefaultAddress(userId);
        log.info("用户查询默认地址 userId={}", userId);
        return WcwmResponse.success(data);
    }

    @GetMapping("/list")
    public WcwmResponse<List<AddressVO>> listAddresses() {
        Long userId = getCurrentUserId();
        List<AddressVO> data = addressService.listAddresses(userId);
        log.info("用户查询地址列表 userId={}", userId);
        return WcwmResponse.success(data);
    }

    @GetMapping("/{id}")
    public WcwmResponse<AddressVO> getAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        AddressVO data = addressService.getAddress(id, userId);
        log.info("用户查询地址详情 id={}", id);
        return WcwmResponse.success(data);
    }

    @PostMapping("/add")
    public WcwmResponse<Void> addAddress(@RequestBody AddressRequest request) {
        Long userId = getCurrentUserId();
        addressService.addAddress(request, userId);
        log.info("用户新增地址 userId={}", userId);
        return WcwmResponse.success(null);
    }

    @PutMapping("/update/{id}")
    public WcwmResponse<Void> updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        Long userId = getCurrentUserId();
        addressService.updateAddress(id, request, userId);
        log.info("用户更新地址 id={}", id);
        return WcwmResponse.success(null);
    }

    @DeleteMapping("/delete/{id}")
    public WcwmResponse<Void> deleteAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        addressService.deleteAddress(id, userId);
        log.info("用户删除地址 id={}", id);
        return WcwmResponse.success(null);
    }

    private Long getCurrentUserId() {
        StpUtil.checkLogin();
        return StpUtil.getLoginIdAsLong();
    }
}
