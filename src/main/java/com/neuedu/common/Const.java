package com.neuedu.common;

public class Const {
    public static final String CURRENTUSER = "current_user";
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    public enum ReponseCodeEnum {
        NEED_LOGIN(2, "需要登录"),
        NO_PRIVILEGE(3, "没有权限操作");;
        private int code;
        private String dese;

        private ReponseCodeEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
    }

    public enum RoleEnum {
        ROLE_ADMIN(0, "管理员"),
        ROLE_CUSTOMER(1, "普通用户");

        private int code;
        private String dese;

        private RoleEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
    }

    public enum ProductStatusEnum {
        PRODUCT_ONLINE(1, "在售"),
        PRODUCT_OFFLINE(2, "下架"),
        PRODUCT_DELETE(3, "删除");
        private int code;
        private String dese;

        private ProductStatusEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
    }

    public enum CartCheckedEnum {
        PRODUCT_CHECKED(1, "已勾选"),
        PRODUCT_UNCHECKED(0, "未勾选"),
        ;
        private int code;
        private String dese;

        private CartCheckedEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
    }

    //订单状态
    public enum OrderStatusEnum {
        ORDER_CANCELED(0, "已取消"),
        ORDER_UN_PAY(10, "未付款"),
        ORDER_PAYED(20, "已付款"),
        ORDER_SEND(40, "已发货"),
        ORDER_SUCCESS(50, "交易成功"),
        ORDER_CLOSED(60, "交易关闭");
        private int code;
        private String dese;

        private OrderStatusEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
        public static OrderStatusEnum codeOf(Integer code){
            for (OrderStatusEnum orderStatusEnum:values()){
                if (code==orderStatusEnum.getCode()){
                    return orderStatusEnum;
                }
            }
            return null;
        }
    }

    //支付类型
    public enum PaymentEnum {
        ONLINE(1, "线上支付"),
        ;
        private int code;
        private String dese;

        private PaymentEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }
        public static PaymentEnum codeOf(Integer code){
            for (PaymentEnum PaymentEnum:values()){
                if (code==PaymentEnum.getCode()){
                    return PaymentEnum;
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
    }
    public enum PaymentPlatformEnum {
        ALLPAY(1, "支付宝"),
        ;
        private int code;
        private String dese;

        private PaymentPlatformEnum(int code, String dese) {
            this.code = code;
            this.dese = dese;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDese() {
            return dese;
        }

        public void setDese(String dese) {
            this.dese = dese;
        }
    }
}
