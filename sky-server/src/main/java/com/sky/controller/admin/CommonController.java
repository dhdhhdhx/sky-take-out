package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.impl.OssServiceLmpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RequestMapping("/admin/common")
@Slf4j
@RestController
@Api(tags = "通用接口")
public class CommonController {

    @Resource
    private OssServiceLmpl oss;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传： {}",file);

        String url = oss.upload(file);

        return url.length() > 5 ? Result.success(url) : Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
