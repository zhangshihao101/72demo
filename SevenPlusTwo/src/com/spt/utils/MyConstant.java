package com.spt.utils;

public class MyConstant {

    public static final String APPID = "2088511896349033";
    // 商户收款账号
    public static final String SELLER = "admin72@7jia2.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAw"
            + "ggJcAgEAAoGBAKNbNe/LE94bTDAAryOAoO7i6iIryar+Xsmv9cHE6wda4VrZNFZ4iUM"
            + "+hLIr2+4sU7+Ci/zlefl9roTKKe/D20JopJRUznMn6eplCRmai92AjGtUN2Aud00OwT"
            + "HP5eRfc+0uVrKG7j7iHSTrtXdufJp3DOQhGrBX7k5BIlImfMUFAgMBAAECgYBla668F"
            + "1hZcJdHwlT6dNT2G/oDhy4pNM7C57VsPYcyRNFfZzVgbmvCTSFzwVlFsU25vCKPvCWT"
            + "qd1dYmrRCseooFRoyc9PauVWUDrIQ4J+Z7tOr2OWIw8Bu2FhVmwTzyioeASwPmUIDuS"
            + "FgsbpV39W74RLDFi9FHriLIiblpg+oQJBANRocUUSfAamGOhzOgJ7aImQN/kiYfYXWj"
            + "vKxe5sR5mEMgoV58YiJH2I1eOx37dQp5AZsFE2O6Cxvnwcn5x5bW0CQQDE4azaXjf7l"
            + "GGa+szzOAm90KDjfTKs/Tv2OFwDt78+V7iryUy+b6nmKhDWMbwUG3flPLxBKw7QsVYc"
            + "S3bpfu75AkEAtvTna/mJ/ygemEp4Ex89YhBpRhzJiHGA9bOpXb7CxYbKm0lXBKDwEXlF"
            + "0wSpkHrWehbGeo1eOwIe80Ssbtlg2QJAbBtHILjvoJL/bWwdscepgbLXRVLvypFapX6Y"
            + "9+mjd4YPfzSPkVobPqkGibbRgXD7ysIo1Nfh0LIviGXfq6LpcQJAYzdkCK5hGms7E0/T"
            + "MiEjFwXSkftNMsZvYgv3VAGSj4a71fyPmo5yGBrkraflLnmICE8Rj1Hx3fsIDTXlFNFfkA==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQ"
            + "Cnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmK"
            + "KMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UB"
            + "VOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    private static final int SDK_PAY_FLAG = 1;

    /**
     * 注册PostActon
     */
    public static final String HttpPostServiceAciton = "com.spt.utils.action.httpPostService";

    /**
     * 注册GetActon
     */
    public static final String HttpGetServiceAciton = "com.spt.utils.action.httpGetService";

    /**
     * 服务器域名
     */
    // 测试环境
    // public static final String SERVICENAME = "http://www.7jia2.org";
    // public static final String SERVICENAME = "http://www.dev.com";
    // 正式环境
    public static final String SERVICENAME = "http://www.7jia2.com";

    // 测试环境图片域名
    public static final String BASEIMG = "http://autoimg.7jia2.org";
    // 正式环境图片域名
    // public static final String BASEIMG = "http://autoimg.7jia2.com";

    // 测试的分享链接
    // public static final String SHAREURL =
    // "http://www.7jia2.org/index.php?app=distribution_goods&pf=wap&goods_id=";
    // 正式的分享链接
    public static final String SHAREURL = "http://www.7jia2.com/index.php?app=distribution_goods&pf=wap&goods_id=";

    public static final String UPDATE = "/index.php?pf=m_seller&act=check_version";// 版本更新
    public static final String LOGIN = "/index.php?pf=m_seller&app=member&act=login";// 登录接口
    public static final String USERINFO = "/index.php?pf=m_seller&app=member&act=info";// 用户信息
    public static final String SHOPMSG = "/index.php?pf=m_seller&app=seller_store";// 商户店铺基本信息
    public static final String CHOSEPLATFORM = "/index.php?pf=m_seller&app=member&act=check_seller";// 登陆后选择平台
    public static final String COMMITDIS = "/index.php?pf=m_seller&app=member&act= applyToDisMember";// 提交申请分销
    public static final String DISHOMEHOTGOODS = "/index.php?pf=m_seller&app=dis_set";// 分销热门商品
    public static final String ALLBRAN = "/index.php?pf=m_seller&app=dis_set&act=dis_all_brand";// 分销所有品牌
    public static final String DISALLGOODS = "/index.php?pf=m_seller&app=dis_goods";// 分销所有商品
    public static final String GOODSDETAILS = "/index.php?pf=m_seller&app=dis_goods&act=goods&goods_id=";// 分销单品详情
    public static final String DISGOODSDESC = "/index.php?pf=m_seller&app=dis_goods&act=desc&goods_id=";// 分销商品描述
    public static final String COMMITSHOPCART = "/index.php?pf=m_seller&app=dis_cart&act=add&spec_id=";// 分销添加购物车
    public static final String DISSHOPCART = "/index.php?pf=m_seller&app=dis_cart";// 分销购物车列表
    public static final String UPDATECOUNT = "/index.php?pf=m_seller&app=dis_cart&act=update&spec_id=";// 更新购物车单品数量
    public static final String DELGOODS = "/index.php?pf=m_seller&app=dis_cart&act=drop&id=";// 删除购物车单品
    public static final String DISCONFIRMORDER = "/index.php?pf=m_seller&app=dis_order&act=cartconfirm";// 分销确认订单
    public static final String ADDRESS = "/index.php?pf=m_seller&app=address&user_id=";// 收货地址列表
    public static final String ADDADDRESS = "/index.php?pf=m_seller &app=address&act=add";// 添加收货地址
    public static final String COMMITORDER = "/index.php?pf=m_seller&app=dis_order&act=cartsubmit";// 分销购物车订单提交
    public static final String DISORDER = "/index.php?pf=m_seller&app=my_dis_order";// 分销订单列表
    public static final String DISORDERDETAIL = "/index.php?pf=m_seller&app=my_dis_order&act=view";// 分销订单详情
    public static final String GOTOPAY = "/index.php?pf=m_seller&app=cashier&act=goto_pay";// 支付
    public static final String LOGISTICSINFO = "/index.php?pf=m_seller&app=my_dis_order&act=express";// 查询物流信息
    public static final String CANCELORDER = "/index.php?pf=m_seller&app=my_dis_order&act=cancel_order";// 取消订单
    public static final String CONFIRMGOODS = "/index.php?pf=m_seller&app=my_dis_order&act=confirm";// 确认收货
    public static final String DELETEADDRESS = "/index.php?pf=m_seller &app=address&act=drop";// 删除收货地址
    public static final String REGISTERCODE = "/index.php?pf=m_seller&app=member&act=phone_sms";// 发送验证码
    public static final String CHECKCODE = "/index.php?pf=m_seller&app=member&act=sms_ck_register"; // 验证手机验证码
    public static final String CHECKANDREGIS = "/index.php?pf=m_seller&app=member&act=phone_register";// 验证手机并注册
    public static final String GETDISGOODS = "/index.php?pf=m_seller&app=dis_goods&act=agent_spec_view";// 获得代销商品规格列表
    public static final String EDITDISPRICE = "/index.php?pf=m_seller&app=dis_goods&act=agent_spec_edit";// 编辑代销规格

    // ************************* jump resultCode *****************************
    public static final int RESULTCODE_10 = 10;
    public static final int RESULTCODE_11 = 11;
    public static final int RESULTCODE_12 = 12;
    public static final int RESULTCODE_13 = 13;
    public static final int RESULTCODE_14 = 14;
    public static final int RESULTCODE_15 = 15;
    public static final int RESULTCODE_16 = 16;
    public static final int RESULTCODE_17 = 17;
    public static final int RESULTCODE_18 = 18;
    public static final int RESULTCODE_19 = 19;
    public static final int RESULTCODE_20 = 20;
    public static final int RESULTCODE_21 = 21;
    public static final int RESULTCODE_22 = 22;
    public static final int RESULTCODE_23 = 23;
    public static final int RESULTCODE_24 = 24;
    public static final int RESULTCODE_25 = 25;
    public static final int RESULTCODE_26 = 26;
    public static final int RESULTCODE_27 = 27;
    public static final int RESULTCODE_28 = 28;
    public static final int RESULTCODE_29 = 29;
    public static final int RESULTCODE_30 = 30;
    public static final int RESULTCODE_31 = 31;
    public static final int RESULTCODE_32 = 32;
    public static final int RESULTCODE_33 = 33;
    public static final int RESULTCODE_34 = 34;
    public static final int RESULTCODE_35 = 35;
    public static final int RESULTCODE_36 = 36;
    public static final int RESULTCODE_37 = 37;
    public static final int RESULTCODE_38 = 38;
    public static final int RESULTCODE_39 = 39;
}
