package com.gzasc.wechatappwaimai.service.impl;

import com.gzasc.wechatappwaimai.dto.AddressRequest;
import com.gzasc.wechatappwaimai.entity.Address;
import com.gzasc.wechatappwaimai.mapper.AddressMapper;
import com.gzasc.wechatappwaimai.service.AddressService;
import com.gzasc.wechatappwaimai.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public AddressVO getDefaultAddress(Long userId) {
        return toAddressVO(addressMapper.selectDefaultByUserId(userId));
    }

    @Override
    public List<AddressVO> listAddresses(Long userId) {
        return addressMapper.selectByUserId(userId).stream()
                .map(this::toAddressVO)
                .toList();
    }

    @Override
    public AddressVO getAddress(Long id, Long userId) {
        Address address = addressMapper.selectByIdAndUserId(id, userId);
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        return toAddressVO(address);
    }

    @Override
    public void addAddress(AddressRequest request, Long userId) {
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressMapper.clearDefaultByUserId(userId);
        }

        LocalDateTime now = LocalDateTime.now();
        Address address = new Address();
        address.setUserId(userId);
        fillAddress(address, request);
        address.setCreateTime(now);
        address.setUpdateTime(now);
        addressMapper.insert(address);
    }

    @Override
    public void updateAddress(Long id, AddressRequest request, Long userId) {
        Address oldAddress = addressMapper.selectByIdAndUserId(id, userId);
        if (oldAddress == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressMapper.clearDefaultByUserId(userId);
        }

        Address address = new Address();
        address.setId(id);
        address.setUserId(userId);
        fillAddress(address, request);
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.update(address);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        Address address = addressMapper.selectByIdAndUserId(id, userId);
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        addressMapper.deleteByIdAndUserId(id, userId);
    }

    private void fillAddress(Address address, AddressRequest request) {
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
    }

    private AddressVO toAddressVO(Address address) {
        if (address == null) {
            return null;
        }
        AddressVO addressVO = new AddressVO();
        addressVO.setId(address.getId());
        addressVO.setName(address.getName());
        addressVO.setPhone(address.getPhone());
        addressVO.setProvince(address.getProvince());
        addressVO.setCity(address.getCity());
        addressVO.setDistrict(address.getDistrict());
        addressVO.setDetail(address.getDetail());
        addressVO.setIsDefault(address.getIsDefault());
        return addressVO;
    }
}
