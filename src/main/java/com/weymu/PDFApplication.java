package com.weymu;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动程序
 *
 * @author weymu
 */
@SpringBootApplication
public class PDFApplication {
  public static void main(String[] args) {
    SpringApplication.run(PDFApplication.class, args);
    LoggerFactory.getLogger(PDFApplication.class).info(">>>>>>  The PDF service started successfully.  >>>>>>  PDF服务启动成功！");
  }
}
