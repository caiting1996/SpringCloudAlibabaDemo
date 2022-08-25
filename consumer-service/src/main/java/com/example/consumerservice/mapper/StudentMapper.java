package com.example.consumerservice.mapper;

import com.example.bean.Student;
import org.apache.ibatis.annotations.Param;


public interface StudentMapper {
    void add(@Param("student") Student student);

}
