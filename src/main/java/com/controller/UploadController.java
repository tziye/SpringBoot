package com.controller;

import com.pojo.vo.UploadVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * 文件上传入口
 */
@Slf4j
@Controller
@RequestMapping("/file")
public class UploadController {

    @GetMapping("/")
    public String toUpload() {
        return "upload";
    }

    @PostMapping("/upload")
    public String upload(@RequestPart("uploads") List<MultipartFile> uploads, @RequestParam("text") String text) throws Exception {
        log.info("接收到的文件数：{}，text：{}", uploads.size(), text);
        upload(uploads);
        return "redirect:/file/";
    }

    @PostMapping("/uploadByVo")
    public String uploadByVo(UploadVo form) throws Exception {
        List<MultipartFile> uploads = form.getUploads();
        log.info("接收到的文件数：{}，text：{}", uploads.size(), form.getText());
        upload(uploads);
        return "redirect:/file/";
    }

    private void upload(List<MultipartFile> uploads) throws IOException {
        for (MultipartFile upload : uploads) {
            InputStream input = upload.getInputStream();
            String name = upload.getOriginalFilename();
            String filename = name.substring(name.lastIndexOf("\\") + 1);
            OutputStream output = new FileOutputStream(
                    System.getProperty("user.dir") + File.separator + "target" + File.separator + filename);
            FileCopyUtils.copy(input, output);
        }
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        String fileName = "head.jpg";
        try (InputStream input = new FileInputStream(ResourceUtils.getFile("classpath:static/" + fileName));
             OutputStream output = response.getOutputStream()) {
            response.setHeader("content-disposition", "attachment;filename=" + fileName);
            response.setContentType("application/octet-stream");
            FileCopyUtils.copy(input, output);
        }
    }

}
