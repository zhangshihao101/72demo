package com.mts.pos.Common;

public class Urls {

 	public static String base = "https://192.168.1.8:8443/"; // 测试
 	
//  public static String base = "https://192.168.1.8:443/";
//	public static String base = "https://www.metasolo.cn/";//正式
 // public static String base = "https://111.160.216.4:1719/";//测试外网

	public static String base_url = "https://111.160.216.4:7217";// 图片地址
	public static final String login = "webpos/control/loginForPOS";// 登录
	// public static final String search_pos = "getPOSTerminal";// 搜索pos机
	public static final String search_pos = "webpos/control/getProductStoreList";// 搜索pos机
//	public static final String search_pos = "openapi/pos/getProductStoreList";// 搜索pos机
	public static final String search_product = "webpos/control/findProductsForPadPos";// 搜索商品
	public static final String search_price = "webpos/control/getProductPriceForPOS";// 得到商品价格
	// public static final String search_member = "findPartiesInterface";// 搜索会员
	// public static final String settle_account = "submitShoppingCart";// 结算
	public static final String shopping_cart = "webpos/control/submitShoppingCart";// 提交购物车
	public static final String store_info = "webpos/control/getProductStore";// 店铺信息
	public static final String transaction_id = "webpos/control/getTransactionId";// 获取交易单号
	public static final String alipay_saoma = "webpos/control/payAlipayInterface";// 支付宝扫码支付
	public static final String alipay_erweima = "webpos/control/payAlipayQRInterface";// 支付宝生成二维码
	public static final String qrcode = "https://www.metasolo.cn/control/qrcode";// 二维码图片（正式）
	public static final String zuiLe = "webpos/control/checkAlipayResultInterface";// 每隔五秒钟请求服务器，看是否完成支付宝二维码支付
	public static final String search_guide = "webpos/control/searchProductList";// 导购模式搜索
	public static final String detail_guide = "webpos/control/findProductDetail";// 导购模式产品详情
	public static final String wechat_scan = "webpos/control/payWeChatInterface";// 微信扫码支付
	public static final String single_product = "webpos/control/findProductVariants";// 单品数据列表
	public static final String member_creat = "partymgr/control/createTradingPartnerForAndroid";//新增会员
//	public static final String member_creat = "createTradingPartnerForAndroid";// 新增会员
	public static final String search_member = "webpos/control/findPartiesInterface";// 查询会员
	public static final String check_promo = "webpos/control/checkPromoForShoppingCart";// 计算购物车促销
	public static final String search_keepbills = "webpos/control/getShoppingListIF";// 获取挂单列表
	public static final String cancel_keepbills = "webpos/control/removeShoppingListIF";// 删除挂单
	public static final String add_keepbills = "webpos/control/addShoppingListFromCartIF";// 请求挂单
	public static final String add_billstoCart = "webpos/control/addShoppingListToCartIF";// 取单
	public static final String logout_message = "webpos/control/getSalesDailyData";// 换班数据
	public static final String search_product_id = "webpos/control/getVariantByVariantIdIF";// 单品ID查询
	public static final String get_orderlist = "webpos/control/getOrderList";// 订单列表
	public static final String get_orderdetails = "webpos/control/getOrderDetails";// 订单详情
	public static final String get_returnreason = "webpos/control/getReturnReason";// 退货原因
	public static final String get_facility = "webpos/control/getFacilityByProdcutStoreId";// 获取仓库
	public static final String get_facilityposition = "webpos/control/getPFLocations";// 获取仓库位置
	public static final String return_goods = "webpos/control/returnOrderItem";// 退货
	public static final String get_paymentmethods = "webpos/control/getProductStorePaymentMethods";// 获取支付方式
	public static final String return_money = "webpos/control/orderRefund";// 退款
	public static final String statistic_client = "webpos/control/createUpdateClientFlowStatistic";// 提交客流量信息
	public static final String get_clientflow = "webpos/control/getClientFlowStatisticResultForPOS";// 获取客流量统计数据
	public static final String save_clientflow = "webpos/control/createUpdateClientFlowStatisticForPOS";// 批量保存客流量信息
	
}
