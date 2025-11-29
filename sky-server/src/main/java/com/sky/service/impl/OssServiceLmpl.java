package com.sky.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.sky.properties.AliOssProperties;
import com.sky.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/11
 * @Version: 1.0
 */

@Service
@Slf4j
public class OssServiceLmpl implements OssService {
    @Resource
    private AliOssProperties ossConfig;
    @Override
    public String upload(MultipartFile file) {
        if (file != null) {
            // 获取原始⽂件名
            String originalFilename = file.getOriginalFilename();
            // 获取⽂件后缀
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // ⽣成新的⽂件名
            String newFileName = UUID.randomUUID() + suffix;
            log.info("新⽂件名：{}", newFileName);
            // 读取配置⽂件中的配置值
            String endpoint = ossConfig.getEndpoint();
            String bucket = ossConfig.getBucket();
            String accessKey = ossConfig.getAccessKey();
            String secretKey = ossConfig.getSecretKey();
            String dir = ossConfig.getDir();
            // 创建OSS客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
            // 创建⽂件元信息,设置⽂件类型为jpg,默认为application/octet-stream（⼆进制流）浏览器打开会下载⽽⾮预览

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("image/jpg");
            // 拼接最终⽂件在服务器的路径如： img/xxx.jpg
            String uploadPath = dir + newFileName;
            InputStream inputStream;
            try {
                // 获取⽂件输⼊流
                inputStream = file.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 使⽤putObject⽅法上传，指明⽂件的存储空间名称、⽂件名称、⽂件输⼊流，以及⽂件元信息
            ossClient.putObject(bucket, uploadPath, inputStream, meta);
            // 关闭OSS客户端
            ossClient.shutdown();
            // 返回⽂件访问路径

            return "https://" + bucket + "." + endpoint + "/" + uploadPath;
        }
        return "上传失败";
    }
}
