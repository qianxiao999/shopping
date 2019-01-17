package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    ShippingMapper shippingMapper;
    //添加地址
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        //step1：参数校验
        if (shipping == null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //step2：添加
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        //step3：返回结果
        Map<String,Integer> map = Maps.newHashMap();
        map.put("shippingId",shipping.getId());
        return ServerResponse.createServerResponseBySuccess(null,map);
    }
    //删除地址
    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        //step1:参数校验
        if (shippingId == null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //step2：删除
        int i = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        //step3：返回结果
        if (i>0){
            return ServerResponse.createServerResponseBySuccess("删除成功");
        }
        return ServerResponse.createServerResponseByError("删除失败");
    }
    //登录状态更行地址
    @Override
    public ServerResponse update(Shipping shipping) {
        //step1:参数校验
        if (shipping == null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //step2：更新
        int i = shippingMapper.updateBySelectiveKey(shipping);
        //step3：返回结果
        if (i>0){
            return ServerResponse.createServerResponseBySuccess("更新成功");
        }
        return ServerResponse.createServerResponseByError("更新失败");
    }
//选中查看集体地址
    @Override
    public ServerResponse select(Integer shippingId) {
        //step1:参数校验
        if (shippingId == null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //step2：查询
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        //step3：返回结果
        return ServerResponse.createServerResponseBySuccess(null,shipping);
    }
    //地址列表--分页查询
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        ////step1：查询
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.selectAll();
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }
}
