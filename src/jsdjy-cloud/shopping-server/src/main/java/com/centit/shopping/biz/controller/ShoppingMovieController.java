package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingMovieService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 电影查看
 * Author: 苏依林
 * Create Data: 2021/4/12
 */
@RestController
@RequestMapping("/movie")
public class ShoppingMovieController {
    @Autowired
    private ShoppingMovieService movieService;

    @GetMapping("/list")
    public JSONObject list(HttpServletRequest request) {
        return movieService.getMovieList(RequestParametersUtil.getRequestParametersRetJson(request));
    }


}
