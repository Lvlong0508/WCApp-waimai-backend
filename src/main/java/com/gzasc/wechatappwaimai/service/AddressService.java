package com.gzasc.wechatappwaimai.service;

import com.gzasc.wechatappwaimai.dto.AddressRequest;
import com.gzasc.wechatappwaimai.vo.AddressVO;

import java.util.List;

public interface AddressService {

    AddressVO getDefaultAddress(Long userId);

    List<AddressVO> listAddresses(Long userId);

    AddressVO getAddress(Long id, Long userId);

    void addAddress(AddressRequest request, Long userId);

    void updateAddress(Long id, AddressRequest request, Long userId);

    void deleteAddress(Long id, Long userId);
}
