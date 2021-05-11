package com.weymu.pdf.service;

import com.weymu.pdf.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Minio 文件存储
 *
 * @author weymu
 */
@Service
public class MinioService {

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient client;

    /**
     * 文件上传
     *
     * @param filePath 文件地址
     * @return 访问地址
     * @throws Exception
     */
    public String uploadFile(String filePath) {
        try {
            FileInputStream in = new FileInputStream(new File(filePath));
            String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String fileName = DateFormatUtils.format(new Date(), "yyyy/MM/dd") + "/" + uuid + "." + suffix;
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(in, in.available(), -1)
                    .build();
            client.putObject(args);
            return minioConfig.getUrl() + "/" + minioConfig.getBucketName() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            new PdfService().delFile(filePath);
        }
        return "err";
    }
}
