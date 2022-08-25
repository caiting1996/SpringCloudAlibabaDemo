package com.example.providerservice.mapper;

import com.example.bean.Test;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface TestMapper {
    void add(@Param("test")Test test);
}
