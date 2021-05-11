package com.weymu.pdf.service;

import org.apache.commons.io.FileDeleteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * pdf工具类
 */
@Service
public class PdfService {

    @Autowired
    private MinioService minioService;

    /**
     * 文件转pdf
     *
     * @param type   文件类型（word、ppt、excel）
     * @param inPath 文件路径(支持绝对路径、远程路径)
     */
    public String toPdf(String type, String inPath) {
        if (inPath.startsWith("http://") || inPath.startsWith("https://")) {
            return getRemoteFile(type, inPath);
        }
        return _toPdf(type, inPath);
    }

    /**
     * 文件转pdf
     *
     * @param type   文件类型（word、ppt、excel）
     * @param inPath 文件路径(仅支持绝对路径)
     */
    public String _toPdf(String type, String inPath) {
        try {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String outPath = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + uuid + ".pdf";
            String wordToPdf = System.getProperty("user.dir") + File.separator + "word2pdf" + File.separator + "WordToPdf.exe";
            String _type = "-word";
            switch (type) {
                case "word":
                    _type = "-word";
                    break;
                case "ppt":
                    _type = "-ppt";
                    break;
                case "excel":
                    _type = "-execl";
                    break;
            }
            String[] cmd = {wordToPdf, _type, inPath, outPath};
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            p.destroy();
            String url = minioService.uploadFile(outPath);
            delFile(outPath);
            System.out.println(url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            new PdfService().delFile(inPath);
        }
        return "err";
    }

    /**
     * 删除文件
     *
     * @param path 文件路径(绝对路径)
     */
    public void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            System.gc();
            try {
                FileDeleteStrategy.FORCE.delete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取远程文件转pdf
     *
     * @param type 文件类型（word、ppt、excel）
     * @param url  远程路径
     */
    public String getRemoteFile(String type, String url) {
        String filePath = "";
        try {
            String suffix = url.substring(url.lastIndexOf(".") + 1);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            filePath = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + uuid + "." + suffix;
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(file);
            URLConnection uc = new URL(url).openConnection();
            uc.setDoInput(true);
            uc.connect();
            InputStream is = uc.getInputStream();
            byte[] buffer = new byte[4 * 1024];
            int byteRead = -1;
            while ((byteRead = (is.read(buffer))) != -1) {
                os.write(buffer, 0, byteRead);
            }
            os.flush();
            is.close();
            os.close();
            String _url = _toPdf(type, filePath);
            delFile(filePath);
            return _url;
        } catch (Exception e) {
            e.printStackTrace();
            new PdfService().delFile(filePath);
        }
        return "err";
    }
}
