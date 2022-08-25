package com.example.providerservice.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.bean.Test;
import com.example.providerservice.mapper.TestMapper;
import com.example.service.test;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service
public class testImpl implements test {
    @Autowired
    private TestMapper testMapper;
    @Override
    @SentinelResource("test1")
    public String test1() throws Exception {
        try(Entry entry= SphU.entry("Hello")){
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "系统繁忙";
        }


    }

    @Override
    public String test2() throws Exception {
        Entry entry = null;
        try{
            entry= SphU.entry("HelloWorld");
            int i=1/0;
            return "success";
        }catch (BlockException e){
            e.printStackTrace();
            return "系统繁忙";
        }catch (Exception ex) {
            // 若需要配置降级规则，需要通过这种方式记录业务异常
            Tracer.traceEntry(ex, entry);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
        return null;

    }
    @PostConstruct  // 初始化
    public void initDegradeRule(){
        /*降级规则 异常*/
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule degradeRule = new DegradeRule();
        degradeRule.setResource("HelloWorld");
        // 设置规则侧率： 异常数
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        // 触发熔断异常数 ： 2
        degradeRule.setCount(2);
        // 触发熔断最小请求数:2
        degradeRule.setMinRequestAmount(2);
        degradeRule.setTimeWindow(60*1000);
        // 统计时长：  单位：ms    1分钟
        //degradeRule.setStatIntervalMs(60*1000); //  时间太短不好测

        // 一分钟内： 执行了2次  出现了2次异常  就会触发熔断

        // 熔断持续时长 ： 单位 秒
        // 一旦触发了熔断， 再次请求对应的接口就会直接调用  降级方法。
        // 10秒过了后——半开状态： 恢复接口请求调用， 如果第一次请求就异常， 再次熔断，不会根据设置的条件进行判定
        degradeRule.setTimeWindow(10);


        degradeRules.add(degradeRule);
        DegradeRuleManager.loadRules(degradeRules);

        /*
        慢调用比率--DEGRADE_GRADE_RT
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        degradeRule.setCount(100);
        degradeRule.setTimeWindow(10);
        //请求总数小于minRequestAmount时不做熔断处理
        degradeRule.setMinRequestAmount(2);
        // 在这个时间段内2次请求
        degradeRule.setStatIntervalMs(60*1000*60);   //  时间太短不好测
        // 慢请求率：慢请求数/总请求数> SlowRatioThreshold ，
        // 这里要设置小于1   因为慢请求数/总请求数 永远不会大于1
        degradeRule.setSlowRatioThreshold(0.9);*/

    }

    @PostConstruct
    private static void initFlowRules() {

        // 流控规则
        List<FlowRule> rules = new ArrayList<>();
        // 流控
        FlowRule rule = new FlowRule();
        rule.setResource("Hello");
        // 设置流控规则 QPS  (1秒钟访问量)
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置受保护的资源阈值(每秒最多允许通过1次请求)
        rule.setCount(1);
        rules.add(rule);



        FlowRuleManager.loadRules(rules);
    }

    @SentinelResource(value = "HelloWorld",entryType = EntryType.IN,
            blockHandler = "blockHandlerForFb")
    public String degrade() throws InterruptedException {
        // 异常数\比例
        throw new RuntimeException("异常");

        /* 慢调用比例
        TimeUnit.SECONDS.sleep(1);
        return new User("正常");*/
    }

    public String blockHandlerForFb( BlockException ex) {
        return "熔断降级";
    }

    @Override
    @Transactional
    public void test3()  {

        Test test=Test.builder().hash("123").isUpdated(true).isUsed(true).localUrl("123").productId("123").version("123").build();
        testMapper.add(test);

            int i=1/0;


    }
}
