package com.jack.contacts;

import java.util.List;

import com.jack.chengshidingwei.ChengshidingweiTM;

public class AppContext {
	public static String keyString;

	public static boolean isZiDongDengLu = false;

	/**
	 * unLoginIndex 各个页面点击右上角图标，登录状态下，跳转至个人中心，再点击注销后，记录从哪跳转过来的
	 */
	public final static int unLoginIndex_Login = 0;
	public final static int unLoginIndex_Shequfuwu = 1;
	public final static int unLoginIndex_Dinggoupeisong = 2;
	public final static int unLoginIndex_temai = 21;
	public final static int unLoginIndex_Shoujichongzhi = 3;
	public final static int unLoginIndex_ShuiDianMei = 4;
	public final static int unLoginIndex_Hotel = 5;
	public final static int unLoginIndex_Tuisong = 6;
	public final static int unLoginIndex_Lianmeng = 7;
	public final static int unLoginIndex_Yanglao = 8;

	/**
	 * 城市定位
	 */
	public static ChengshidingweiTM chengshidingweiTM = new ChengshidingweiTM();

	// 李凯东开始
	// public static String siteUrl = "http://zhangzhongfu.verysite.net"; //北京
	public static String siteUrl = "http://www.czssd.com"; // 苏州
	// public static String siteUrl = "http://zsx.zsshopping.com";

	public static String unLogin = "http://service.sht315.com/Service/MemberRestService.svc/LogOut";
	// public static String unLogin =
	// "http://118.126.17.101:8801/Service/MemberRestService.svc/LogOut";
	/**
	 * 是否在加载城市信息
	 */
	public static boolean shifouJiazaiChengshi = false;
	/**
	 * 城市是否加载成功
	 */
	public static boolean shifouJiazaiChengshiWancheng = false;
	// 李凯东结束

	// 李胜伟开始
	/**
	 * 当前用户信息
	 */

	/**
	 * 注册的手机号
	 */
	public static String shoujihao;
	/**
	 * 登录账号
	 */
	public static String zhanghumingStr;
	/**
	 * 登录密码fd
	 */
	public static String mimaStr;

	public static String validCode;

	/**
	 * 注册验证码
	 */
	public static String yanzhengma;

	/**
	 * 注册时密码
	 */
	public static String mima;
	/**
	 * 注册时的推荐人掌富号
	 */
	public static String tuijianrenzhangfuhao;
	/**
	 * 注册时官方推荐人
	 */
	public static String guanfangtuijanren = "88888888";
	/**
	 * 注册时选择的掌富号
	 */
	public static String zhucexuanzhangfuhao;
	/**
	 * 找回密码时使用手机号
	 */
	public static String zhaohuimimashoujihao;
	/**
	 * 找回密码时使用验证码
	 */
	public static String zhaohuimimayanzhengma;
	/**
	 * 推送使用掌富号
	 */
	public static String zhangfuhao = "88888888";

	/**
	 * 添加编辑银行卡标识 添加，编辑
	 */
	public static String yinhangkabiaoshi;
	public static String userkey = "F7C90DF5-F613-4AEC-B3B1-C9EBCD46F61B";
	/**
	 * 会员头像本地路径
	 */
	public static String huiyuantouxiangFile;
	/**
	 * 易卡通或购物券余额
	 */
	public static String tongquanhuogouwuquanyue;

	/**
	 * 选择的积分广告类别
	 */
	public static String guanggaoleibie;
	/**
	 * 积分广告查看广告详情广告名称
	 */
	public static String guanggaomingcheng;
	/**
	 * 积分广告查看广告详情广告Key
	 */
	public static String guanggaoKey;
	/**
	 * 广告图片路径
	 */
	public static String guanggaoURL;
	/**
	 * 跳转标题
	 */
	public static String biaoti;
	/**
	 * 分享和推送titlebar标题
	 */
	public static String fenxianghetuisontitle = "推送";
	/**
	 * 是否pop到首页
	 */
	public static String shifouPop;
	/**
	 * 兑换跳转兑换详情活动key
	 */
	public static String duihuanhuodongKey;
	/**
	 * 兑换跳转兑换详情商品Key
	 */
	public static String shangpinKey;
	/**
	 * 兑换跳转兑换详情商品名称
	 */
	public static String shangpinmingchneg;
	/**
	 * 套餐详情参数comboKey
	 */
	public static String comboKey;
	/**
	 * 团购活动跳转活动key
	 */
	public static String huodongkey;
	/**
	 * 专题活动跳转活动key
	 */
	public static String zhuantikey;
	/**
	 * 0团购,1礼品,4分类,5兑换,2套餐,3专题,7收藏,6购物车跳转商品详情标识
	 */
	public static int huodongdaima = -1;
	/**
	 * 团购,礼品,分类跳转商品详情参数key
	 */
	public static String tuangoulipinfenleitiaozhuankey;
	/**
	 * 团购,礼品,兑换跳转商品详情参数type
	 */
	public static String tuangoulipintiaozhuantype;
	/**
	 * 跳转商品详情tilte名称
	 */
	public static String shangpinxiangqingbiaoti;
	/**
	 * 团购,礼品,分类跳转商品详情参数type
	 */
	public static int jiarugouwuchehuodongdaima;
	/**
	 * 商品详情详细URL
	 */
	public static String shangpinxiangqingURL;
	/**
	 * 积分广告详情参数shangpinbianhao
	 */
	public static String shangpinbianhao;
	/**
	 * 用户支付密码
	 */
	public static String zhifumima;
	/**
	 * 商品详情网页标题
	 */
	public static String webbiaoti;
	// 李胜伟结束
	// 郭思瑞开始
	/**
	 * 市区_sheng
	 */
	public static String shiqu_sheng;
	/**
	 * 市区_shi
	 */
	public static String shiqu_shi;
	/**
	 * 市区_qu
	 */
	public static String shiqu_qu;
	/**
	 * 充值支付类别 1商城购物，
	 */
	public static int zhifubaoChongzhiType;
	/**
	 * 充值的手机号
	 */
	public static String chongzhiShoujihaoString;
	/**
	 * 手机运营商状态
	 */
	public static int shoujiYunyingshangState;
	/**
	 * 手机充值选号
	 */
	public static int shoujichongzhiLianxi;
	/**
	 * 手机充值订单号
	 */
	public static String shoujichongzhiDingdanhao;
	/**
	 * 手机充值号码
	 */
	public static String lianxirenhaoma;
	/**
	 * 手机充值面额
	 */
	public static int shoujiChongzhiMiane;
	/**
	 * 手机充值成功
	 */
	public static boolean shoujiChongzhiCaozuo;
	/**
	 * 我的彩票点中的按钮类别
	 * 
	 */
	public static int caipiaoleibie;
	/**
	 * 全部 待开奖 0，已中奖 1，未中奖 2
	 * 
	 */
	public static int kaijiangxinxi;

	/**
	 * 银行状态显示
	 * 
	 */
	public static int yinhangstate;
	/**
	 * 是易卡通还是购物券
	 */
	public static boolean shiTongquan;
	/**
	 * 购物券总支出
	 */
	public static int gwqZongzhichu;
	/**
	 * 购物券总充值
	 */
	public static int gwqZongchongzhi;
	/**
	 * 易卡通使用类别
	 * 
	 */
	public static String tongquanLeibie;
	/**
	 * 易卡通在线充值
	 * 
	 */
	public static boolean tongquanZaixianchongzhi;
	/**
	 * 我的彩票页面 栏目
	 * 
	 */
	public static String stateString = "";
	/**
	 * 彩票购买记录页数
	 */
	public static int pageIndexCaipai = 1;
	/**
	 * 互益商城-收藏夹编辑/删除
	 */
	public static boolean isShanchu;
	/**
	 * 互益商城-收藏夹 选择删除的收藏集合
	 */
	public static List<Integer> ShoucangKeys;
	/**
	 * 汇款充值汇款单号
	 * 
	 */
	public static String huikuanchongzhidingdanhao;
	/**
	 * 汇款充值审核状态
	 * 
	 */
	public static int huikuanchongzhishenheState;
	/**
	 * 是否编辑
	 */
	public static String isbianji;
	/**
	 * 编辑下的 key
	 */
	public static String remitapllyKey;

	/**
	 * 已买彩票开奖信息
	 */
	public static int caipiaokaijiangxinxi;
	/**
	 * 彩票奖金余额
	 */
	public static int caipiaojiangjin;
	/**
	 * 是账户页面
	 * 
	 */
	public static boolean shiZhanghu;
	/**
	 * 城市
	 */
	public static String city;
	/**
	 * 彩票奖金提现
	 */
	public static int caipiaojiangjinState;
	/**
	 * 彩票奖金提现金额
	 */
	public static String caipiaojiangjintixianjine;
	/**
	 * 易卡通提现金额
	 */
	public static String tongquantixianjine;

	/**
	 * 银行
	 */
	public static String yinhang = "选择银行卡";

	/**
	 * 银行卡号
	 */
	public static String yinhangkahao = "卡号";

	/** 支付方式，和支付金额 */
	public static String zhifufangshi;
	public static double zhifujine;
	/**
	 * 三掌科技文章标题KEY
	 */
	public static String shouyezilanmuKey;
	/**
	 * 查看联系人状态
	 * 
	 */
	public static int chakanlianxirenstate;
	/**
	 * 兑换主图地址
	 */
	public static String duihuanZhutuDizhi;
	/**
	 * 积分兑换订单提交是否成功
	 */
	public static boolean duihuandingdanTijiao;
	/**
	 * 收藏listbox
	 */
	/*
	 * public static ListBox shoucangListBox;
	 */

	/**
	 * 是否是收藏界面跳转
	 */
	public static boolean shiShoucang;
	/**
	 * 要编辑的地址key
	 */
	public static String dizhiKey;
	/**
	 * 地址是编辑还是更改
	 */
	public static int dizhibianji;
	/**
	 * 订单Type
	 * 
	 */
	public static int dingdanType;
	/**
	 * 是兑换或订单，查看详情使用
	 * 
	 */
	public static boolean shiduihuanhuodingdan;
	/**
	 * 订单管理或者我的兑换
	 */
	public static boolean shiduihuan;
	/**
	 * 兑换state
	 */
	public static boolean shiduiuhanTiaozhuan;
	/**
	 * 发货时间state
	 */
	public static int songhuoshijianInt = 3;
	/**
	 * 配送方式state
	 */
	public static int peisongfangshiInt = 3;
	/**
	 * 首页图标
	 */
	// public static int[] shouyetubiao1 = {
	// //
	// R.drawable.huafeichongzhi,
	// //
	// R.drawable.caipiaogoumai, R.drawable.huyishangcheng,
	// R.drawable.jifenguanggao, R.drawable.tuisong,
	// R.drawable.tongchengjingcai, };
	// public static String[] shouyewenzi1 = { "话费充值", "彩票购买", "互益商城", "积分广告",
	// "推送", "同城精彩"
	//
	// };

	// public static int[] shouyetubiao1 = {
	// R.drawable.huafei, R.drawable.huoche,
	// R.drawable.jipiao, R.drawable.jiudian, R.drawable.temai,
	// R.drawable.fanli, R.drawable.shenghuo, R.drawable.dianying };
	//
	// public static String[] shouyewenzi1 = { "话费充值", "火车票", "飞机票", "酒店",
	// "特卖商城",
	// "商城返利", "生活团购", "电影票" };
	//
	// public static int[] shouyetubiao2 = { R.drawable.tongquan,
	// R.drawable.shui,
	// R.drawable.dian, R.drawable.ranqi, R.drawable.jingqu,
	// R.drawable.cai, R.drawable.jiameng, R.drawable.tuisong };
	// public static String[] shouyewenzi2 = { "易卡通充值", "水费", "电费", "燃气费",
	// "景区门票",
	// "签约商户", "加盟", "推送" };
	//

	/**
	 * 首页弹屏广告计时
	 */
	public static int guanggaoJishi = 0;

	public static String zhuanxiangActivityString = "";

	/**
	 * 领取奖品，提交订单
	 * 
	 */
	public static String jiangpinKey;
	public static String yonghuzhenshixingming;
	public static String dingdanKey;
	public static String jiangpinmingcheng;
	/**
	 * 积分兑换state
	 */
	public static boolean shiJifenduihuan;
	/**
	 * 商品详情界面的购物数量
	 * */
	public static String gouwushuliang = "0";
	/**
	 * 分类商品类别表识
	 * */
	public static String leibieKey;
	/**
	 * 分类和专题活动跳转标题
	 * */
	public static String tiaozhuanbiaoti;
	/**
	 * 分类排序参数
	 * */
	public static String orderKey;
	/**
	 * 分类跳转到商品详情参数商品key
	 */
	public static String fenleishangpinkey;
	/**
	 * 团购——2专题和1分类跳转标识key
	 */
	public static int zhuantihefenleitiaozhuanbiaoshi;
	/**
	 * 加入购物车图片路径
	 * */
	public static String jiarugouwuchetupianlujing;
	/**
	 * 从购物车取得活动代码
	 */
	public static int gouwuchehuodongdaima;
	/**
	 * 从购物车跳转到商品详情,套餐详情,兑换详情的商品key
	 */
	public static String gouwucheshangpinkey;
	// 董雪莹结束
	// 罗京雨开始
	/**
	 * public 3d福彩标记传值
	 */

	public static String sanDbiaoji;
	/**
	 * public 排列三标记传值
	 */

	public static String pailiesanbiaoji;

	/**
	 * 彩票名称
	 */
	public static String shuangseqiu = "双色球";
	public static String daletou = "大乐透";
	public static String sanDfucaiKaijianghao = "3D福彩";
	public static String qilecai = "七乐彩";
	public static String pailiesan = "排列三";
	public static String pailiewu = "排列五";
	public static int shuangseqiuInt = 101;
	public static int daletouInt = 149;
	public static int sanDfucaiInt = 103;
	public static int qilecaiInt = 102;
	public static int pailiesanInt = 146;
	public static int pailiewuInt = 147;
	/**
	 * 彩票玩法
	 */
	public static String zusan = "组三";
	public static String zuliu = "组六";
	public static String zhixuan = "直选";
	public static String danshi = "单式";
	public static String fushi = "复式";
	/** 终端类型 */
	public static int zhongduanleixing = 0;

	/** 彩票玩法中的彩票名称 */
	public static String jilucaipiaomingcheng;
	/**
	 * 购物车送货时间
	 */
	public static String songhuoshijian;
	/**
	 * 购物车配送方式
	 */
	public static String peisongfangshi;
	/**
	 * 购物车订单备注
	 */
	public static String dingdanbeizhu;
	/**
	 * 收获地址跳转
	 */
	public static String shouhuodizhi;
	/**
	 * 需要支付的金额
	 */
	public static double xuyaozhifujine;
	/**
	 * 购物车易卡通支付页面跳转标记
	 */
	public static String zhifutiaozhuan;
	/**
	 * 购物车订单号
	 */
	public static String dingdanhaokey;
	/**
	 * 购物车商品详情商品key
	 */
	public static String shangpinkey;
	/**
	 * 商品活动类型
	 */
	public static String huodongleixing;
	/**
	 * 订单详情标识1,从购物车跳过来，2从我的订单查看详情跳过来
	 */
	public static int dingdanbiaoshi;
	// 罗京雨结束
	/**
	 * 同城类别key
	 */
	public static String tongchengLeibiaoName;
	public static String tongchengleibiekey;
	public static String tongchengShanghuKey;
	public static String tongchengshanghumingcheng;
	/**
	 * 领取奖品的物流单号
	 */
	public static String wuliudanhao;
	/**
	 * 大转盘是否结束
	 */
	public static String shifoujieshu;
	/**
	 * apk路径
	 */
	public static String lujing;

}
