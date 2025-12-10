package com.px.pictureend.controller;

import com.px.pictureend.common.BaseResponse;
import com.px.pictureend.common.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName: com.px.pictureend.controller
 *
 * @author: idpeng
 * @version: 1.0
 * @className: MainController
 * @date: 2025/12/4 14:14
 * @description: 健康检查控制器
 */
@RestController
@RequestMapping("/")
@Api("健康检查")
public class MainController {

    /**
     * 检查接口
     *
     * @return 响应结果
     */
    @ApiOperation("检查检查接口")
    @RequestMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
