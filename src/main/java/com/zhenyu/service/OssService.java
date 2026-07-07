package com.zhenyu.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class OssService {

    @Value("${alioss.endpoint}")
    private String endpoint;

    @Value("${alioss.access-key-id}")
    private String accessKeyId;

    @Value("${alioss.access-key-secret}")
    private String accessKeySecret;

    @Value("${alioss.bucket-name}")
    private String bucketName;

    public String upload(MultipartFile file, String dir) throws IOException {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String objectKey = dir + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, objectKey, inputStream);

            return "https://" + bucketName + "." + endpoint + "/" + objectKey;
        } finally {
            ossClient.shutdown();
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("https://")) return;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            String prefix = "https://" + bucketName + "." + endpoint + "/";
            if (fileUrl.startsWith(prefix)) {
                String objectKey = fileUrl.substring(prefix.length());
                ossClient.deleteObject(bucketName, objectKey);
            }
        } finally {
            ossClient.shutdown();
        }
    }
}
