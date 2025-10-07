package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/22
 */
public interface CarouselService {
    JSONObject queryList(JSONObject requestParametersRetJson);
}
