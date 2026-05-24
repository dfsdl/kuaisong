package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

//文件上传和下载
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要存放到指定位置，否则本次请求后临时文件消失
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid重新生成文件名，防止文件名称重复
        String fileName = UUID.randomUUID().toString();
//创建一个目录对象
        File dir = new File(basePath);
        if(!dir.exists()){
            //目录不存在需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存
            file.transferTo(new File(basePath+fileName+suffix));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName+suffix);
    }
    //文件下载
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        log.info(name);
        try {
            //输入流读取
            FileInputStream fis = new FileInputStream(new File(basePath+name));
            //输出流回写
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fis.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
