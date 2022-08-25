package com.example.consumerservice.service;

import com.example.basecommon.utils.utils.RedisUtil;
import com.example.bean.Student;
import com.example.consumerservice.config.dataSource.DataSourceKey;
import com.example.consumerservice.config.dataSource.TargetDataSource;
import com.example.consumerservice.mapper.StudentMapper;
import com.example.service.ConsumerService;
import com.example.service.test;
import org.apache.dubbo.config.annotation.Reference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StudentMapper studentMapper;
    @Reference
    private test test1;
    private int i=100;
    private int j=0;

    //@GlobalTransactional
    //@Transactional
    @Override
    @TargetDataSource(name = DataSourceKey.DB_1)
    @Transactional
    public void manage() {
        Student student=Student.builder().studentName("zhangsan").studentNo("9").subjectName("shuxue").subjectNo("1").score("90").build();
        studentMapper.add(student);

        m();
        test1.test3();


    }
    @Transactional
    @Override
    public void m() {
        Student student=Student.builder().studentName("zhangsan1").studentNo("9").subjectName("yuwen").subjectNo("1").score("90").build();
       //
        studentMapper.add(student);
        throw new RuntimeException();

    }
    @Override
    public String kill() throws InterruptedException {
        RLock lock=redissonClient.getLock("any");
        if(i<=0){
            System.out.println("数量不足");
            return "数量不足";
        }
        boolean res=lock.tryLock(3,10, TimeUnit.SECONDS);
        if(!res){
            System.out.println("获取锁失败");
            return "获取锁失败";
        }
        try{

            if(i<=0){
                System.out.println("数量不足");
                return "数量不足";
            }
            Thread.sleep(100);
            i=i-1;
            j=j+1;
            System.out.println("扣减成功，当前值为"+i+",已卖出"+j);
            return "扣减成功";
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "success";
    }
}
