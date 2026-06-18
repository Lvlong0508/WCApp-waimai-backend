package com.gzasc.wechatappwaimai.service.impl;

import com.gzasc.wechatappwaimai.dto.ShopRegisterRequest;
import com.gzasc.wechatappwaimai.entity.Shop;
import com.gzasc.wechatappwaimai.mapper.ShopMapper;
import com.gzasc.wechatappwaimai.service.FileService;
import com.gzasc.wechatappwaimai.service.ShopService;
import com.gzasc.wechatappwaimai.vo.ShopVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final FileService fileService;

    @Override
    public Map<String, Object> listShops(int page, int size) {
        int currentPage = page <= 0 ? 1 : page;
        int pageSize = size <= 0 ? 10 : size;
        int offset = (currentPage - 1) * pageSize;

        long total = shopMapper.countOpenShops();
        List<ShopVO> list = shopMapper.selectOpenShops(offset, pageSize).stream()
                .map(this::toShopVO)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    @Override
    public Shop getShopByOwnerUserId(Long userId) {
        return shopMapper.selectByOwnerUserId(userId);
    }

    @Override
    public void registerShop(ShopRegisterRequest request, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Shop shop = new Shop();
        shop.setName(request.getShopName());
        shop.setLogo(request.getLogo());
        shop.setPhone(request.getPhone());
        shop.setDescription(request.getDescription());
        shop.setScore(BigDecimal.ZERO);
        shop.setStatus(1);
        shop.setOwnerUserId(userId);
        shop.setCreateTime(now);
        shop.setUpdateTime(now);
        shopMapper.insert(shop);

        fileService.moveTempShopLogoToPermanent(request.getLogo(), shop.getId(), userId);
    }

    private ShopVO toShopVO(Shop shop) {
        ShopVO shopVO = new ShopVO();
        shopVO.setId(shop.getId());
        shopVO.setName(shop.getName());
        shopVO.setLogo(shop.getLogo());
        shopVO.setPhone(shop.getPhone());
        shopVO.setDescription(shop.getDescription());
        shopVO.setScore(shop.getScore());
        shopVO.setStatus(shop.getStatus());
        return shopVO;
    }
}
