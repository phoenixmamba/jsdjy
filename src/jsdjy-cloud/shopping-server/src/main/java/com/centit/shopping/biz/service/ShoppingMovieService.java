package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/13
 */
public interface ShoppingMovieService {
    JSONObject getMovieList(JSONObject reqJson);
}
