package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neuedu.VO.ProductDetailVO;
import com.neuedu.VO.ProductListVO;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.FTPUtil;
import com.neuedu.utils.PropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService categoryService;
    //商品的添加和更新
    @Override
    public ServerResponse saveOrUpdate(Product product) {
        //step1：参数非空判断
        if (product == null){
            return ServerResponse.createServerResponseByError("参数为空");
        }
        //step2：设置商品的主图 sub_images--->1.jpg,2.jpg,3.png
        String subImages = product.getSubImages();
        if (subImages != null&&!subImages.equals("")){
            String[] subImageArr = subImages.split(",");
            if (subImageArr.length>0){
                product.setMainImage(subImageArr[0]);
            }
        }
        //step3：商品save or update
        if (product.getId() == null){
            //添加
            int insert = productMapper.insert(product);
            if (insert>0){
                return ServerResponse.createServerResponseBySuccess("添加成功");
            }else {
                return ServerResponse.createServerResponseByError("添加失败");
            }
        }else {
            //更新
            int i = productMapper.updateByPrimaryKey(product);
            if (i>0){
                return ServerResponse.createServerResponseBySuccess("更新成功");
            }else {
                return ServerResponse.createServerResponseByError("更新失败");
            }

        }
        //step4：返回结果在if else中

    }

    //产品上下架
    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {
        //step1：参数的非空校验
        if (productId == null) {
            return ServerResponse.createServerResponseByError("商品id不能为空");
        }
        if (status == null) {
            return ServerResponse.createServerResponseByError("商品状态参数不能为空");
        }
        //step2：更新产品的状态
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int i = productMapper.updateProductKeySelective(product);
        //step3：返回结果
        if (i>0){
            return ServerResponse.createServerResponseBySuccess("更新成功");
        }else {
            return ServerResponse.createServerResponseByError("更新失败");
        }
    }
    //或商品详细信息
    @Override
    public ServerResponse detail(Integer productId) {
        //step1:参数的非空校验
        if (productId == null){
            return ServerResponse.createServerResponseByError("商品id不能为空");
        }
        //step2：查询商品（product）
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product == null){
                return ServerResponse.createServerResponseByError("商品不存在");
            }
        //step3：product--》productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //step4：返回结果
        return ServerResponse.createServerResponseBySuccess(null,productDetailVO);
    }
    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToString(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToString(product.getUpdateTime()));
        //查询父类ID
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category != null){
            productDetailVO.setParentCategoryId(category.getParentId());
        }else {
            //默认根节点
            productDetailVO.setParentCategoryId(0);
        }
        return productDetailVO;
    }
    //后台---查询商品列表（分页）
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        //作用是给下面查询的语句后面加上
        //select * from product + limit (pageNum-1)*pageSize.pageSize
        //step1：查询商品数据
        List<Product> products = productMapper.selectAll();
        List<ProductListVO> productListVOList = Lists.newArrayList();
        //step2：返回结果
        if (products!=null&&products.size()>0){
            for (Product product:products){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        //返回前端的数据
        PageInfo pageInfo = new PageInfo(productListVOList);
        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }
    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        return productListVO;
    }
    //后台产品搜索
    @Override
    public ServerResponse search(Integer productId, String productName,
                                 Integer pageNum, Integer pageSize) {
        //select * from prodct where productID ? and productName like %name%
        PageHelper.startPage(pageNum,pageSize);
        if (productName!=null&&!productName.equals("")){
            productName="%"+productName+"%";
        }else{
            productName=null;
        }
        List<Product> productList = productMapper.findProductByProductIdAndProductName(productId, productName);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if (productList!=null&&productList.size()>0){
            for (Product product:productList){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        PageInfo pageInfo = new PageInfo(productListVOList);
        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }
//上传图片
    @Override
    public ServerResponse upload(MultipartFile file, String path) {
        //step1:非空判断
        if (file == null){
            return ServerResponse.createServerResponseByError("图片不能为空");
        }
        //step2：修改图片名称---》唯一名称
        //先获取图片名称
        String originalFilename = file.getOriginalFilename();
        //图片的扩展名不能变,获取图片的扩展名
        String exName = originalFilename.substring(originalFilename.lastIndexOf("."));//.jpg
        //为图片生成新的唯一的名字newFileName
        String newFileName = UUID.randomUUID().toString()+exName;
        File pathFile = new File(path);
        if (!pathFile.exists()){
            pathFile.setWritable(true);
            pathFile.mkdirs();
        }
        //path文件的目录
        File file1 = new File(path, newFileName);//创建文件
        try {
            //将文件file写到path目录下的file1文件下面
            file.transferTo(file1);
            //上传到图片服务器
            FTPUtil.uploadFile(Lists.newArrayList(file1));
            //。。。。。
            Map<String,String> map = Maps.newHashMap();
            map.put("uri",newFileName);
            map.put("url",PropertiesUtils.readByKey("imageHost")+"/"+newFileName);
            //删除应用服务器上的图片
            file1.delete();
            return ServerResponse.createServerResponseBySuccess(null,map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //前台接口---商品详情
    @Override
    public ServerResponse detail_portal(Integer productId) {
        //step1：参数校验
        //step1:参数的非空校验
        if (productId == null){
            return ServerResponse.createServerResponseByError("商品id不能为空");
        }
        //step2：查询商品（product）
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        //step3：校验商品状态
        if (product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
            return ServerResponse.createServerResponseByError("商品已下架或删除");
        }
        //step4：获取productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //step5：返回结果
        return ServerResponse.createServerResponseBySuccess(null,productDetailVO);

    }
//前台商品搜索以及排序
    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        //step1:参数校验 categoryId和keyword不能同时为空
        if (categoryId == null&&(keyword ==null||keyword.equals(""))){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //step2：categoryId
        Set<Integer> integerSet = Sets.newHashSet();
        if (categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null&&(keyword==null||keyword.equals(""))){
                //说明没有商品数据
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.createServerResponseBySuccess(null,pageInfo);
            }
            ServerResponse serverResponse = categoryService.get_deep_category(categoryId);
            //查出类别下面所有的子类

            if (serverResponse.isSucess()){
                integerSet = (Set<Integer>)serverResponse.getDate();
            }
        }
        //step3：keyword
        //判断关键字
        if (keyword!=null&&!keyword.equals("")){
            keyword = "%"+keyword+"%";
        }else {
            keyword = null;
        }
        if (orderBy.equals("")){
            PageHelper.startPage(pageNum,pageSize);
        }else {
            String[] orderByArr = orderBy.split("_");
            if (orderByArr.length>1){
                PageHelper.startPage(pageNum,pageSize,orderByArr[0]+""+orderByArr[1]);
            }else {
                PageHelper.startPage(pageNum,pageSize);
            }
        }
        //step4：List<Product>---->List<ProuctListVO>
        List<Product> productList = productMapper.searchProduct(integerSet, keyword);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if (productList!=null&&productList.size()>0){
            for(Product product:productList){
               ProductListVO productListVO = assembleProductListVO(product);
                    productListVOList.add(productListVO);
            }
        }

        //step5:分页
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(productListVOList);
        //step6：返回
        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }


}
