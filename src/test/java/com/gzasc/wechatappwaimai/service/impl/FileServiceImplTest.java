package com.gzasc.wechatappwaimai.service.impl;

import com.gzasc.wechatappwaimai.entity.Product;
import com.gzasc.wechatappwaimai.entity.Shop;
import com.gzasc.wechatappwaimai.mapper.FileMapper;
import com.gzasc.wechatappwaimai.mapper.ProductMapper;
import com.gzasc.wechatappwaimai.mapper.ShopMapper;
import com.gzasc.wechatappwaimai.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @TempDir
    private Path tempDir;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ShopMapper shopMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileService, "accessUrl", "http://localhost:8080");
        lenient().when(fileMapper.selectByEntity(any(), any())).thenReturn(List.of());
    }

    @Test
    void uploadUserAvatarUpdatesUserAvatarUrlWithReturnedUrl() {
        String url = fileService.uploadUserAvatar(imageFile(), 1L);

        verify(userMapper).updateAvatarUrl(1L, url);
    }

    @Test
    void uploadShopLogoRequiresExistingOwnedShopAndUpdatesLogo() {
        Shop shop = new Shop();
        shop.setId(2L);
        shop.setOwnerUserId(1L);
        when(shopMapper.selectById(2L)).thenReturn(shop);

        String url = fileService.uploadShopLogo(imageFile(), 2L, 1L);

        verify(shopMapper).updateLogo(2L, url);
    }

    @Test
    void uploadShopLogoThrowsWhenShopDoesNotExist() {
        when(shopMapper.selectById(2L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileService.uploadShopLogo(imageFile(), 2L, 1L));

        assertEquals("店铺不存在", exception.getMessage());
        verifyNoInteractions(fileMapper);
    }

    @Test
    void uploadShopLogoThrowsWhenCurrentUserDoesNotOwnShop() {
        Shop shop = new Shop();
        shop.setId(2L);
        shop.setOwnerUserId(99L);
        when(shopMapper.selectById(2L)).thenReturn(shop);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileService.uploadShopLogo(imageFile(), 2L, 1L));

        assertEquals("无权操作该店铺", exception.getMessage());
        verifyNoInteractions(fileMapper);
    }

    @Test
    void uploadProductImageRequiresExistingProductOwnedThroughShopAndUpdatesImage() {
        Product product = new Product();
        product.setId(3L);
        product.setShopId(2L);
        Shop shop = new Shop();
        shop.setId(2L);
        shop.setOwnerUserId(1L);
        when(productMapper.selectById(3L)).thenReturn(product);
        when(shopMapper.selectById(2L)).thenReturn(shop);

        String url = fileService.uploadProductImage(imageFile(), 3L, 1L);

        verify(productMapper).updateImage(3L, url);
    }

    @Test
    void uploadProductImageThrowsWhenProductDoesNotExist() {
        when(productMapper.selectById(3L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileService.uploadProductImage(imageFile(), 3L, 1L));

        assertEquals("商品不存在", exception.getMessage());
        verifyNoInteractions(fileMapper);
    }

    @Test
    void uploadProductImageThrowsWhenCurrentUserDoesNotOwnProductShop() {
        Product product = new Product();
        product.setId(3L);
        product.setShopId(2L);
        Shop shop = new Shop();
        shop.setId(2L);
        shop.setOwnerUserId(99L);
        when(productMapper.selectById(3L)).thenReturn(product);
        when(shopMapper.selectById(2L)).thenReturn(shop);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileService.uploadProductImage(imageFile(), 3L, 1L));

        assertEquals("无权操作该商品", exception.getMessage());
        verifyNoInteractions(fileMapper);
    }

    private MockMultipartFile imageFile() {
        return new MockMultipartFile("file", "test.png", "image/png", new byte[]{1, 2, 3});
    }
}
