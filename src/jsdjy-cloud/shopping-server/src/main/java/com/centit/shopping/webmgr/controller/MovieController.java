package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 电影查看
 * Author: 苏依林
 * Create Data: 2021/4/12
 */
@RestController
@RequestMapping("/webmgr/movie")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/list")
    public JSONObject list(HttpServletRequest request) {
        return movieService.getMovieList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("/detail/{id}")
    public JSONObject detail(@PathVariable String id) {
        return movieService.detail(id);
    }

    /**
     * 新增电影
     *
     * @param param
     * @return
     */
    @PostMapping("/add")
    public JSONObject add(@RequestBody JSONObject param) {
        return movieService.add(param);
    }

    /**
     * 编辑电影
     *
     * @param param
     * @return
     */
    @PostMapping("/modify")
    public JSONObject modify(@RequestBody JSONObject param) {
        return movieService.modify(param);
    }

    /**
     * 删除电影
     *
     * @param param
     * @return
     */
    @PostMapping("/remove")
    public JSONObject remove(@RequestBody JSONObject param) {
        return movieService.remove(param);
    }

}
