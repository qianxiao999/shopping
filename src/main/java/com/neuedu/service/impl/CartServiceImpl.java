package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.VO.CartProductVO;
import com.neuedu.VO.CartVO;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICartService;
import com.neuedu.service.ICategoryService;
import com.neuedu.utils.BigDecimalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
//购物车添加商品
    @Override
    public ServerResponse add(Integer userId,Integer productId, Integer count) {
        //step1:参数的非空校验
        if (productId == null ||count == null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createServerResponseByError("要添加的商品不存在");
        }

        //step：根据productId和userid查询购物信息
        Cart cart = cartMapper.selectCartByUseridAndProductId(userId, productId);
        if (cart == null){
            //添加
            Cart cart1 = new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());
            cartMapper.insert(cart1);
        }else {
            //更新
            Cart cart1 = new Cart();
            cart1.setId(cart.getId());
            cart1.setProductId(productId);
            cart1.setUserId(userId);
            cart1.setQuantity(count);
            cart1.setChecked(cart.getChecked());
            cartMapper.updateByPrimaryKey(cart1);
        }
        CartVO cartVOLimit = getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySuccess(null,cartVOLimit);
    }

    //获取cartVO
    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO = new CartVO();
        //step1：根据userid插询购物信息---》list<Cart>
        List<Cart> cartList = cartMapper.selectCartByUserid(userId);
        //step2:List<Cart> ---->List<CartProductVO>
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        //定义购物车的总价格
        BigDecimal carttotalprice = new BigDecimal("0");
        if (cartList != null&&cartList.size()>0){
            for (Cart cart:cartList){
                CartProductVO cartProductVO = new CartProductVO();
                    cartProductVO.setId(cart.getId());
                    cartProductVO.setQuantity(cart.getQuantity());
                    cartProductVO.setUserId(cart.getUserId());
                    cartProductVO.setProductChecked(cart.getChecked());
                    //查询商品
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null){
                    cartProductVO.setProductId(cart.getProductId());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductStock(product.getStock());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    //查商品的库存
                    int stock = product.getStock();
                    int limitProductCount = 0;
                    //库存足够
                    if (stock>=cart.getQuantity()){
                        limitProductCount = cart.getQuantity();
                        cartProductVO.setLimitQuantity("LIMIT_NUM_SUCCESS");
                    }else {//库存不足
                        limitProductCount = stock;
                        //更新购物车中商品的数量
                        Cart cart1 = new Cart();
                        cart1.setId(cart.getId());
                        cart1.setQuantity(stock);
                        cart1.setProductId(cart.getProductId());
                        cart1.setChecked(cart.getChecked());
                        cart1.setUserId(userId);
                        cartMapper.updateByPrimaryKey(cart1);
                        cartProductVO.setLimitQuantity("LIMIT_NUM_FAIL");
                    }
                    cartProductVO.setQuantity(limitProductCount);
                    cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),Double.valueOf(cartProductVO.getQuantity())));
                }
                //总价格
                if (cartProductVO.getProductChecked() ==Const.CartCheckedEnum.PRODUCT_CHECKED.getCode()){
                    carttotalprice = BigDecimalUtils.add(carttotalprice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
                cartProductVOList.add(cartProductVO);
            }
        }
        cartVO.setCartProductVOList(cartProductVOList);
        //step3:计算总价格
        cartVO.setCarttotalprice(carttotalprice);
        //step4:判断购物车是否全选
        int checkedAll = cartMapper.isCheckedAll(userId);
        if (checkedAll>0){
            cartVO.setIsallchecked(false);
        }else {
            cartVO.setIsallchecked(true);
        }
        //step5：返回结果
        return cartVO;
    }
    //查询某一用户的购物车列表
    @Override
    public ServerResponse list(Integer userId) {
        CartVO cartVOLimit = getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySuccess(null,cartVOLimit);
    }
    //更新购物车某个商品的数量
    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //Step1:参数判断
        if (productId == null ||count == null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //Step2：查询购物车中商品
        Cart cart = cartMapper.selectCartByUseridAndProductId(userId, productId);
        if (cart != null){
            //Step3：更新数量
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        //Step4：返回cartVO
        return ServerResponse.createServerResponseBySuccess(null,getCartVOLimit(userId));
    }
    //移除购物车中的某个/某些商品
    @Override
    public ServerResponse delete_product(Integer userId, String productIds) {
        //Step1：参数校验
        if (productIds == null ||productIds.equals("")){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //Step2：productIds--->List<Integer>
        List<Integer> productIdList = Lists.newArrayList();
        String[] productIdsArr = productIds.split(",");
        if (productIdsArr != null&&productIdsArr.length>0){
            for (String productIdstr:productIdsArr){
                Integer productId = Integer.parseInt(productIdstr);
            }
        }
        //Step3:调用dao
        cartMapper.deleteByUseridAndProductIds(userId,productIdList);
        //Step4：返回结果
        return ServerResponse.createServerResponseBySuccess("删除成功",getCartVOLimit(userId));
    }
    //购物车中选中某个商品
    @Override
    public ServerResponse select(Integer userId, Integer productId,Integer check) {
        //step1：参数校验：
//        if (productId == null){
//            return ServerResponse.createServerResponseByError("参数不能为空");
//        }
        //step2：dao接口：
        cartMapper.selectOrUnselectProduct(userId,productId,check);

        //step3：返回结果：
        return ServerResponse.createServerResponseBySuccess(null,getCartVOLimit(userId));
    }
    //查询购物车中产品的数量
    @Override
    public ServerResponse get_cart_product_count(Integer userId) {
        int quantity = cartMapper.get_cart_product_count(userId);
        return ServerResponse.createServerResponseBySuccess(null,quantity);
    }
}
