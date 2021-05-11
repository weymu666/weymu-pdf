package com.weymu.pdf.controller;

import com.weymu.pdf.domain.ReqData;
import com.weymu.pdf.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * pdf控制层
 *
 * @author weymu
 * @date 2021-03-10
 */
@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    /**
     * 文件转pdf
     *
     * @param reqData type 文件类型（word、ppt、excel） url 文件路径(支持绝对路径、远程路径)
     */
    @PostMapping("/toPdf")
    public String toPdf(@RequestBody ReqData reqData) throws Exception {
        return pdfService.toPdf(reqData.getType(), reqData.getUrl());
    }

}
