package com.centit.jobserver.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/10/25 21:13
 **/
@Component
public class SampleXxlJob {
    @XxlJob("demoTaskHandler")
    public void demoTaskHandler() throws Exception {
        System.out.println("XXL-JOB, Hello World.");
    }
}
