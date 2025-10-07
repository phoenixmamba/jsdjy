package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/13
 */
public interface MovieService {
    JSONObject getMovieList(JSONObject reqJson);

    JSONObject add(JSONObject param);

    JSONObject modify(JSONObject param);

    JSONObject remove(JSONObject param);

    JSONObject detail(String id);
}
