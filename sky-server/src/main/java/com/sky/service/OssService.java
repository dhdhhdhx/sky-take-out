package com.sky.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/11
 * @Version: 1.0
 */


public interface OssService {
    /**
     * ⽂件上传
     *
     * @param file ⽂件
     */
    String upload(MultipartFile file);
}
