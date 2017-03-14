package com.spt.utils;

public class MtsUrls {

     public static String base = "https://192.168.1.8:8443/";
//     public static String base = "https://metasolo.net:9519/";

//    public static String base = "https://www.metasolo.cn/";// 正式
    // public static String base = "https://172.24.134.1:8443/";
    // public static String base = "https://192.168.1.212:8443/";// 稳定版8443 太慢

    public static final String login = "openapi/control/login"; // 登录
    public static final String get_orderstatistics = "openapi/m2s/getOrderStatisticsForMulti"; // 销售展示页
    public static final String get_daysago = "openapi/m2s/getOrderStatisticsJson"; // 前n天数据
    public static final String get_orders = "openapi/m2s/searchOrders"; // 查询订单
    public static final String get_metasolo = "interface/control/getMappingMetasoloValue";// 得到源一用户名密码
    public static final String create_metasolo = "interface/control/createMappingMetasolo";// 记住密码，免登录

    public static final String get_enumsbytype = "openapi/m2s/getEnumsByType"; // 查询支付状态或销售渠道
    public static final String get_storelist = "openapi/m2s/getProductStoreList"; // 查询店铺
    public static final String get_orderdetails = "openapi/m2s/getOrderDetails"; // 获取订单详情
    public static final String get_orderitemdetails = "openapi/m2s/getOrderItemDetails"; // 获取订单详情
    public static final String get_top5inventorylist = "openapi/m2s/getTop5InventoryList";// 库存数量top5名称
    public static final String getTop5InventoryListGroupByCondition =
            "openapi/m2s/getTop5InventoryListGroupByCondition";
    public static final String order_bychannel = "openapi/m2s/orderStatisticsByChannel"; // 获取渠道订单详情
    public static final String getInvetoryChanged = "openapi/m2s/getInvetoryChanged";// 仓库库存变化趋势
    public static final String getFacilityInfo = "openapi/m2s/getFacilityInfo";// 获取当前用户仓库名称列表
    public static final String getInventoryBySearchParams = "openapi/m2s/getInventoryBySearchParams";// 库存明细、查询
    public static final String getMessageListInterfaceForTerminal = "openapi/crm/getMessageListInterfaceForTerminal";// 消息列表
    public static final String search_product = "openapi/catalog/searchProduct";// 搜索商品
    public static final String get_allclassify = "openapi/catalog/getMyCategories";// 获取所有分类
    public static final String get_childclassify = "openapi/catalog/getMyChildCategories";// 获取某个分类的子类
    public static final String get_publicclassify = "openapi/catalog/getPublicCategories";// 获取公共分类
    public static final String get_product = "openapi/catalog/getProduct";// 获取某个商品详情
    public static final String put_imagefile = "openapi/catalog/updateImageWithFile";// 使用文件更新商品图片
    public static final String update_manuvalue = "openapi/catalog/updateManuValue";// 更新厂家条码
    public static final String update_skuvalue = "openapi/catalog/updateSkuValue";// 更新商家条码
    public static final String update_shareprice = "openapi/catalog/updateSharePrice";// 更新分享价格
    public static final String update_product = "openapi/catalog/updateProduct";// 更新商品

    public static final String remove_product = "openapi/product/control/removeProduct";
    public static final String off_shelf = "openapi/product/control/offShelf";
    public static final String getProductSalingStores = "openapi/product/control/getProductSalingStores";

    // 新的登录部分
    public static final String sso_issole = "sso/uniqueId";// 验证唯一值
    public static final String sso_login = "sso/oauth2/authorizeLogin";// 登录
    public static final String sso_sendmsg = "sso/sendMessage";// 发送短信
    public static final String sso_validatecode = "sso/validateCode";// 验证短信校验码
    public static final String sso_register = "sso/userRegister";// 注册
    public static final String sso_profile = "sso/oauth2/userProfile";// 验证access_token
    public static final String find_psw = "openapi/oauthForApp/findBackPassword";// 找回密码
    public static final String change_psw = "openapi/oauthForApp/changePassword";// 修改密码
    public static final String consummate_infor = "openapi/oauthForApp/consummateInfor";// 完善资料

    // 人脉、我的
    public static final String get_userauthority = "openapi/individual/userAuthority";// 获取权限
    public static final String get_individual = "openapi/individual/getIndividual";// 获取个人信息
    public static final String update_logo = "openapi/individual/updateLogo";// 上传头像
    public static final String get_company = "openapi/business/getBusinessCompanyList";// 获取公司
    public static final String getCompanyInformation = "openapi/business/getCompanyInformation";// 获取公司详情信息
    public static final String update_ind = "openapi/individual/updateInd";// 提交个人信息
    public static final String my_group = "openapi/individual/myGroup";//
    public static final String certified_personal = "openapi/individual/certifiedPersonal";// 个人认证
    public static final String get_contacts = "openapi/individual/getContacts";// 获取公司成员
    public static final String get_friends = "openapi/individual/myFriends";// 我的/新的朋友
    public static final String get_join = "openapi/business/getBusinessCooperationList";// 获取合作列表
    public static final String updata_companylogo = "openapi/business/changeConpanyLogo";// 获取合作列表
    public static final String find_person = "openapi/individual/findIndividuals";// 找人
    public static final String applyCooperation = "openapi/business/applyCooperation";// 申请合作
    public static final String change_relation = "openapi/individual/ChangeRelationship";// 加好友等等
    public static final String applyForGroup = "openapi/individual/applyForGroup";// 申请公司成员
    public static final String operate_cooperation = "openapi/business/operateCooperation";// 申请公司成员
    public static final String get_users = "openapi/individual/getUsers";// 在新的朋友里查询买卖通账户
    public static final String applay_certificateInfo = "openapi/business/applayCertificateInfo";// 公司认证
    public static final String get_count = "/sso/getUserNum";// 获取商户通人数
    public static final String get_carousels = "openapi/individual/getCarousels";// 获取商户通人数
    public static final String get_connections = "openapi/individual/getConnections";// 公司、个人混合查询
    public static final String getRecommendation = "openapi/individual/getRecommendation";// 获取推荐用户
}
