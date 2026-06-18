package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.entity.UploadFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {
    
    int insert(UploadFile file);
    
    UploadFile selectById(@Param("id") Long id);
    
    List<UploadFile> selectByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    int deleteById(@Param("id") Long id);
    
    int deleteByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
}
