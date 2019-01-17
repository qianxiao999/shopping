package com.neuedu.controller.backend;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/manage/product")
public class UploadController {
    @Autowired
    IProductService productService;
    //两个方法名相同，怎么区分 ,通过访问方法不同method
    //浏览器访问请求
    @RequestMapping(value = "/upload",method = RequestMethod.GET)
    public  String upload(){
        return "upload";//逻辑视图  前缀+逻辑视图+后缀----》/templates/upload.html
    }
    //提交图片的访问方法
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody//返回值转为jns格式
    public ServerResponse upload2(@RequestParam(value = "upload_file",required = false)MultipartFile file){
        String path = "D:\\ftpFile";
        return productService.upload(file,path);
    }
}
