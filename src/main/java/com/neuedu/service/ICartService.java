package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface ICartService {
    //购物车中添加商品
    public ServerResponse add(Integer userId,Integer productId,Integer count);
    //查询某一用户的购物车列表
    ServerResponse list(Integer userId);
    //查询某一用户的购物车列表
    ServerResponse update(Integer userId, Integer productId, Integer count);
    //移除购物车中的某个/某些商品
    ServerResponse delete_product(Integer userId, String productIds);
    //购物车中选中某个商品
    ServerResponse select(Integer userId, Integer productId,Integer check);
    //查询购物车中产品的数量
    ServerResponse get_cart_product_count(Integer userId);
}
