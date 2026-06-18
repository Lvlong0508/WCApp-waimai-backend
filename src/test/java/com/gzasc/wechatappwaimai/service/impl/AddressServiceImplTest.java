package com.gzasc.wechatappwaimai.service.impl;

import com.gzasc.wechatappwaimai.dto.AddressRequest;
import com.gzasc.wechatappwaimai.entity.Address;
import com.gzasc.wechatappwaimai.mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void addAddressTreatsNullIsDefaultAsZero() {
        AddressRequest request = new AddressRequest();
        request.setName("张三");
        request.setPhone("13800138000");
        request.setDetail("测试地址");

        addressService.addAddress(request, 1L);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressMapper).insert(captor.capture());
        assertEquals(0, captor.getValue().getIsDefault());
    }

    @Test
    void updateAddressTreatsNullIsDefaultAsZero() {
        AddressRequest request = new AddressRequest();
        request.setName("张三");
        request.setPhone("13800138000");
        request.setDetail("测试地址");
        when(addressMapper.selectByIdAndUserId(10L, 1L)).thenReturn(new Address());

        addressService.updateAddress(10L, request, 1L);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressMapper).update(captor.capture());
        assertEquals(0, captor.getValue().getIsDefault());
    }
}
