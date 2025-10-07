package com.centit.shopping.biz.service.impl;

import com.centit.core.result.Result;
import com.centit.shopping.biz.service.TestService;
import org.springframework.stereotype.Service;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/22 0:52
 **/
@Service
public class TestServiceImpl implements TestService {
    @Override
    public Result getRule() {
//        JSONObject obj = MzService.getAssetRule();
//        return Result.defaultSuccess(obj);
        return null;
    }
}
