package com.isoft.dictionary;

import java.util.Locale;

import com.isoft.consts.Constant;

public enum ColumnStatusEnum {
    
    /** 货币单位:USD */
    CURRENCY_USD("USD - US Dollar"),
    /** 货币单位:RMB */
    CURRENCY_RMB("RMB- Yuan Renminbi"),
    
    /** 英语:en */
    NATIVE_EN(Locale.ENGLISH.toString()),
    /** 中文:zh_CN */
    NATIVE_ZH_CN(Locale.CHINA.toString()),
    
    /** 审核状态->提交待审核:"01" */
    CHECK_STATUS_WAITCHECK("01"),
    /** 审核状态->审核通过:"02" */
    CHECK_STATUS_PASSCHECK("02"),
    /** 审核状态->审核未通过:"03" */
    CHECK_STATUS_UNPASSCHECK("03"),    
    
	/*************公司模块******************/
	/** 公司品牌图片审核状态->提交待审核:"01" */
    CORPBRAND_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
	/** 公司品牌图片审核状态->审核通过:"02" */
    CORPBRAND_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
	/** 公司品牌图片审核状态->审核未通过:"03" */
    CORPBRAND_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	/** 公司证书图片审核状态->提交待审核:"01" */
	CORPCERT_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
	/** 公司证书图片审核状态->审核通过:"02" */
	CORPCERT_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
	/** 公司证书图片审核状态->审核未通过:"03" */
	CORPCERT_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	/** 公司logo审核状态->提交待审核:"01" */
	CORPLOGO_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
	/** 公司logo审核状态->审核通过:"02" */
	CORPLOGO_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
	/** 公司logo审核状态->审核未通过:"03" */
	CORPLOGO_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	/** 公司图片审核状态->提交待审核:"01" */
	CORPIMG_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
	/** 公司图片审核状态->审核通过:"02" */
	CORPIMG_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
	/** 公司图片审核状态->审核未通过:"03" */
	CORPIMG_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	/** 公司档案审核状态->提交待审核:"01" */
	CORPINFO_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
	/** 公司档案审核状态->审核通过:"02" */
	CORPINFO_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
	/** 公司档案审核状态->审核未通过:"03" */
	CORPINFO_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	/** 公司URL审核状态->提交待审核:"01" */
	CORPURL_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
	/** 公司URL审核状态->审核通过:"02" */
	CORPURL_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
	/** 公司URL审核状态->审核未通过:"03" */
	CORPURL_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	   /** 用户头像审核状态->提交待审核:"01" */
    USERPHOTO_STATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
    /** 用户头像审核状态->审核通过:"02" */
    USERPHOTO_STATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
    /** 用户头像审核状态->审核未通过:"03" */
    USERPHOTO_STATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),
	
	/** 用户联系方式->MSN:"01" */
    USER_IMTYPE_MSN("01"),
    /** 用户联系方式->Skype:"02" */
    USER_IMTYPE_SKYPE("02"),
    /** 用户联系方式->QQ:"03" */
    USER_IMTYPE_QQ("03"),
    /** 用户联系方式->AIM:"04" */
    USER_IMTYPE_AIM("04"),
    /** 用户联系方式->ICQ:"05" */
    USER_IMTYPE_ICQ("05"),
    /** 用户联系方式->Google Talk:"06" */
    USER_IMTYPE_GTALK("06"),
	
    
    /**首页动态更新类型 */
    HOME_EDTYPE_EDITCONTINFO("00"),
    HOME_EDTYPE_USERACTIVE("01"),
    HOME_EDTYPE_CONTREGISTER("02"),
    
    /**自动审核类型 -> LOGO:"00" */
    AUTOCHECK_TYPE_CORPLOGO("00"),
    /**自动审核类型 -> 公司名称和简介:"01" */
    AUTOCHECK_TYPE_CORPNOTE("01"),
    /**自动审核类型 -> 公司图片:"02" */
    AUTOCHECK_TYPE_CORPIMG("02"),
    /**自动审核类型 -> 公司品牌:"03" */
    AUTOCHECK_TYPE_BRAND("03"),
    /**自动审核类型 -> 公司证书:"04" */
    AUTOCHECK_TYPE_CERT("04"),
    /**自动审核类型 -> 产品:"05" */
    AUTOCHECK_TYPE_ITEM("05"),
    
    
	/*************产品模块******************/
	
	/** 货品状态->在用:"00" */
    ITEM_STATUS_ACTIVE("00"),
    /** 货品状态->已删除:"01" */
    ITEM_STATUS_DELETED("01"),

    /** 货品审核状态->提交待审核:"01" */
    ITEM_CHECKSTATUS_WAITCHECK(CHECK_STATUS_WAITCHECK.magic),
    /** 货品审核状态->审核通过:"02" */
    ITEM_CHECKSTATUS_PASSCHECK(CHECK_STATUS_PASSCHECK.magic),
    /** 货品审核状态->审核未通过:"03") */
    ITEM_CHECKSTATUS_UNPASSCHECK(CHECK_STATUS_UNPASSCHECK.magic),

    /** 货品定制状态->私有产品:"Y" */
    ITEM_PRIV_YES(Constant.YES),
    /** 货品定制状态->非私有产品:"N" */
    ITEM_PRIV_NO(Constant.NO),

    /** 货品展示状态->已展示:"01" */
    ITEM_PUB_YES("01"),
    /** 货品展示状态->未展示:"00" */
    ITEM_PUB_NO("00"),
    
    /** 货品包装方案是否是默认->默认:"Y" */
    ITEM_PACKPLAN_DEFAULT_YES(Constant.YES),
    /** 货品包装方案是否是默认->非默认:"N" */
    ITEM_PACKPLAN_DEFAULT_NO(Constant.NO),
    
    /** 货品图片是否是封面->是封面: "Y" */
    ITEMIMG_COVER_YES(Constant.YES),
    /** 货品图片是否是封面->不是封面: "N" */
    ITEMIMG_COVER_NO(Constant.NO),
    
    /** 联系人类型->线下会员:"00" */
    CONT_TYPE_OFFLINE("00"),
    /** 联系人类型->线上会员:"01" */
    CONT_TYPE_ONLINE("01"),
    
    /** 联系人是否被公司用户添加过->添加过:"Y" */
    CONT_OWNFLAG_YES(Constant.YES),
    /** 联系人是否被公司用户添加过->未添加过:"N" */
    CONT_OWNFLAG_NO(Constant.NO),    
    
    /** 联系人联系方式->MSN:"01" */
    CONT_IMTYPE_MSN("01"),
    /** 联系人联系方式->Skype:"02" */
    CONT_IMTYPE_SKYPE("02"),
    /** 联系人联系方式->QQ:"03" */
    CONT_IMTYPE_QQ("03"),
    /** 联系人联系方式->AIM:"04" */
    CONT_IMTYPE_AIM("04"),
    /** 联系人联系方式->ICQ:"05" */
    CONT_IMTYPE_ICQ("05"),
    /** 联系人联系方式->Google Talk:"06" */
    CONT_IMTYPE_GTALK("06"),
    
    /** 联系人属主类型->自己添加的联系人:"00" */
    CONT_STATUS_OWNER("00"),
    /** 联系人属主类型->别人共享给我的联系人:"01" */
    CONT_STATUS_SHARE("01"),
    
    /** 联系人是否加入黑名单->已加入黑名单:"Y" */
    CONT_BLOCKED_YES(Constant.YES),
    /** 联系人是否加入黑名单->未加入黑名单:"N" */
    CONT_BLOCKED_NO(Constant.NO),
    
    /** 是否我的邀请联系人->是的:"Y" */
    CONT_INVITE_YES(Constant.YES),
    /** 是否我的邀请联系人->不是:"N" */
    CONT_INVITE_NO(Constant.NO),
    
    /** 联系人共享权限->不公开:"00" */
    CONT_SHAREACCESS_PRIVATE("00"),
    /** 联系人共享权限->只读:"01" */
    CONT_SHAREACCESS_READ("01"),
    
    /** 邀请联系人状态->Wait Accepted:"00" */
    CONT_INVITE_WAIT("00"),
    /** 邀请联系人状态->Accepted:"00" */
    CONT_INVITE_ACCEPTED("01"),
    
    /*************单据模块******************/
    
    /** 单据类型->消息:"01" */
    CM_TYPE_MSG("01"),
    /** 单据类型->询盘单:"02" */
    CM_TYPE_INQ("02"),
    /** 单据类型->报价单:"03" */
    CM_TYPE_QUO("03"),
    /** 单据类型->采购订单:"04" */
    CM_TYPE_PO("04"),
    /** 单据类型->销售单:"05" */
    CM_TYPE_SO("05"),
    /** 单据类型->形式发票:"06" */
    CM_TYPE_PI("06"),
    /** 单据类型->商业发票:"07" */
    CM_TYPE_INV("07"),
    /** 单据类型->装箱单:"08" */
    CM_TYPE_PL("08"),
    /** 单据类型->图片页:"10" */
    CM_TYPE_PP("10"),
    
    /** 单据类型->消息:"01" */
    CM_BILL_MSG("MSG",false),
    /** 单据类型->询盘单:"02" */
    CM_BILL_INQ("INQ",false),
    /** 单据类型->报价单:"03" */
    CM_BILL_QUO("QUO",true),
    /** 单据类型->采购订单:"04" */
    CM_BILL_PO("PO",false),
    /** 单据类型->销售单:"05" */
    CM_BILL_SO("SO",true),
    /** 单据类型->形式发票:"06" */
    CM_BILL_PI("PI",true),
    /** 单据类型->商业发票:"07" */
    CM_BILL_INV("INV",true),
    /** 单据类型->装箱单:"08" */
    CM_BILL_PL("PL",true),
    /** 单据类型->图片页:"10" */
    CM_BILL_PP("PP",true),
    
    /** 单据收发类型->收到单据:"00" */
    CM_RSTYPE_RECEIVE("00"),
    /** 单据收发类型->创建单据:"01" */
    CM_RSTYPE_SEND("01"),
    
    /** 单据收发类型->收到单据:"IN" */
    CM_ORG_I("IN"),
    /** 单据收发类型->创建单据:"OUT" */
    CM_ORG_O("OUT"),
    
    /** 单据收发状态->未发送:"00" */
    CM_SENDSTATUS_UNSENT("00"),
    /** 单据发送状态->已发送:"01" */
    CM_SENDSTATUS_SENT("01"),    

    /** CM状态->已删除:"01" */
    CM_STATUS_DELETED("01"),
    /** CM状态->在用:"00" */
    CM_STATUS_INUSE("00"),

    
    /** CM查看状态->已查看:"01" */
    CM_VIEWSTATUS_YES("01"),
    /** CM查看状态->未查看:"00" */
    CM_VIEWSTATUS_NO("00"),

    /** CM审核状态->已创建:"00" */
    CM_CHECKSTATUS_CREATED("00"),
    /** 创建采购订单审核状态->已确认:"01" */
    CM_CHECKSTATUS_APPROVED("01"),
    /** 创建采购订单审核状态->已交付:"02" */
    CM_CHECKSTATUS_DELIVERED("02"),
    /** 创建采购订单审核状态->已作废:"03" */
    CM_CHECKSTATUS_CANCELLED("03"),
   
    /** 附加费用类型->Plus:"+"*/
    CM_ATTACH_PLUS("+"),
    /** 附加费用类型->Minus:"-"*/
    CM_ATTACH_MINUS("-"),
    
    /** 单据发送类型 -> Online:"00"*/
    CM_SENDTYPE_ONLINE("00"),
    /** 单据发送类型 -> Email:"01"*/
    CM_SENDTYPE_EMAIL("01"),
    
    
    
    /** 是否将联系人加入黑名单 ->是:"Y" */
    CM_SPAM_BLOCKCONT_YES(Constant.YES),
    /** 是否将联系人加入黑名单 ->否:"N" */
    CM_SPAM_BLOCKCONT_NO(Constant.NO),
    
    /** 是否将联系人从黑名单解除 ->是:"Y" */
    CM_RESTORE_UNBLOCKCONT_YES(Constant.YES),
    /** 是否将联系人从黑名单解除 ->否:"N" */
    CM_RESTORE_UNBLOCKCONT_NO(Constant.NO),

    /**PDF 类型*/
    CM_PDF_ID_TYPE("04"),
    
    /** 关联单据类型 -> 询盘单:"1"*/
    CM_ASSOBILL_TYPE_INQ("1"),
    /** 关联单据类型 -> 采购订单:"2"*/
    CM_ASSOBILL_TYPE_PO("2"),
    /** 关联单据类型 -> 报价单:"3"*/
    CM_ASSOBILL_TYPE_QUO("3"),
    /** 关联单据类型 -> 图片页:"4"*/
    CM_ASSOBILL_TYPE_PP("4"),
    /** 关联单据类型 -> 销售订单:"5"*/
    CM_ASSOBILL_TYPE_SO("5"),
    /** 关联单据类型 -> 形式发票:"6"*/
    CM_ASSOBILL_TYPE_PI("6"),
    /** 关联单据类型 -> 商业发票:"7"*/
    CM_ASSOBILL_TYPE_INV("7"),
    /** 关联单据类型 -> 装箱单:"8"*/
    CM_ASSOBILL_TYPE_PL("8"),
    
    /*************A&S设置模块******************/
    /** 消息提醒->提醒:"Y" */
    EMAILNOTICE_MSG_YES(Constant.YES),
    /** 消息提醒->不提醒:"N" */
    EMAILNOTICE_MSG_NO(Constant.NO),
    /** 询盘单提醒->提醒:"Y" */
    EMAILNOTICE_INQ_YES(Constant.YES),
    /** 询盘单提醒->不提醒:"N" */
    EMAILNOTICE_INQ_NO(Constant.NO),
    /** 报价单提醒->提醒:"Y" */
    EMAILNOTICE_QUO_YES(Constant.YES),
    /** 报价单提醒->不提醒:"N" */
    EMAILNOTICE_QUO_NO(Constant.NO),
    /** 采购订单提醒->提醒:"Y" */
    EMAILNOTICE_PO_YES(Constant.YES),
    /** 采购订单提醒->不提醒:"N" */
    EMAILNOTICE_PO_NO(Constant.NO),
    /** 销售单提醒->提醒:"Y" */
    EMAILNOTICE_SC_YES(Constant.YES),
    /** 销售单提醒->不提醒:"N" */
    EMAILNOTICE_SC_NO(Constant.NO),
    /** 形式发票提醒->提醒:"Y" */
    EMAILNOTICE_PI_YES(Constant.YES),
    /** 形式发票提醒->不提醒:"N" */
    EMAILNOTICE_PI_NO(Constant.NO),
    /** 商业发票提醒->提醒:"Y" */
    EMAILNOTICE_INV_YES(Constant.YES),
    /** 商业发票提醒->不提醒:"N" */
    EMAILNOTICE_INV_NO(Constant.NO),
    /** 装箱单提醒->提醒:"Y" */
    EMAILNOTICE_PL_YES(Constant.YES),
    /** 装箱单提醒->不提醒:"N" */
    EMAILNOTICE_PL_NO(Constant.NO),
    /** 图片页提醒->提醒:"Y" */
    EMAILNOTICE_PP_YES(Constant.YES),
    /** 图片页提醒->不提醒:"N" */
    EMAILNOTICE_PP_NO(Constant.NO),
    
    /** 模板类型->消息:"01" */
    TEMPLATE_TYPE_MSG("01"),
    /** 模板类型->询盘单:"02" */
    TEMPLATE_TYPE_INQ("02"),
    /** 模板类型->报价单:"03" */
    TEMPLATE_TYPE_QUO("03"),
    /** 模板类型->采购订单:"04" */
    TEMPLATE_TYPE_PO("04"),
    /** 模板类型->销售单:"05" */
    TEMPLATE_TYPE_SC("05"),
    /** 模板类型->形式发票:"06" */
    TEMPLATE_TYPE_PI("06"),
    /** 模板类型->商业发票:"07" */
    TEMPLATE_TYPE_INV("07"),
    /** 模板类型->装箱单:"08" */
    TEMPLATE_TYPE_PL("08"),
    /** 模板类型->邀请:"09" */
    TEMPLATE_TYPE_INVITE("09"),
    /** 模板类型->邮件:"10" */
    TEMPLATE_TYPE_EMAIL("10"),
    
    /** 模板级别->用户级别:"00" */
    TEMPLATE_LEVEL_USER("00"),
    /** 模板级别->公司级别:"01" */
    TEMPLATE_LEVEL_CORP("01"),
    
    /** 公司状态->在用:"00" */
    CORP_STATUS_ACTIVE("00"),
    /** 公司状态->关闭中:"01" */
    CORP_STATUS_CLOSED("01"),
    
    /** 买家用户状态->在用:"00" */
    USER_STATUS_ACTIVE("00"),
    /** 买家用户状态->关闭中:"01" */
    USER_STATUS_CLOSED("01"),

    /** 公司是否被监控->是:"Y" */
    CORP_WATCHED_YES(Constant.YES),
    /** 公司是否被监控->否:"N" */
    CORP_WATCHED_NO(Constant.NO),
    
    /** 货品展示排列顺序->按编码升序:"00" */
    ITEM_DISPORDER_CODE_ASC("00"),
    /** 货品展示排列顺序->按编码降序:"01" */
    ITEM_DISPORDER_CODE_DESC("01"),
    /** 货品展示排列顺序->按创建时间升序:"02" */
    ITEM_DISPORDER_CT_ASC("02"),
    /** 货品展示排列顺序->按创建时间降序:"03" */
    ITEM_DISPORDER_CT_DESC("03"),
    
    /** 公司是否开启广告业务->是:"Y" */
    CORP_ADS_YES(Constant.YES),
    /** 公司是否开启广告业务->否:"N" */
    CORP_ADS_NO(Constant.NO),
    
    /** 公司流水号类型->业务流水号:"00" */
    CORP_FLOWCODE_BIZ("00"),
    /** 公司流水号类型->普通流水号:"01" */
    CORP_FLOWCODE_COMM("01"),
    
    /** 用户帐号状态->在用:"00" */
    USER_STATUS_INUSE("00"),
    /** 用户帐号状态->休眠:"01" */
    USER_STATUS_SLEEP("01"),
    /** 用户帐号状态->待激活确认:"02" */
    USER_STATUS_PEND("02"),
    /** 用户帐号状态->过期未启用:"03" */
    USER_STATUS_OVERDUE("03"),
    
    /** 用户是否公司联系人->是:"Y" */
    CORP_LINKMAN_YES(Constant.YES),
    /** 用户是否公司联系人->否:"N" */
    CORP_LINKMAN_NO(Constant.NO),
    
    /** 用户是否公司管理员->是:"Y" */
    CORP_ISADMIN_YES(Constant.YES),
    /** 用户是否公司管理员->否:"N" */
    CORP_ISADMIN_NO(Constant.NO),

    /** 用户贸易语言->中文:"zh_CN" */
    USER_LANGUAGE_ZH(Locale.CHINA.toString()),
    /** 用户贸易语言->英语:"en" */
    USER_LANGUAGE_EN(Locale.ENGLISH.toString()),
    
    
    /**********计费***************/
    
    /** 公司计费基数->货品:"00" */
    CORP_EXPENSE_RADIX_ITEM("00"),
    /** 公司计费基数->单据:"01" */
    CORP_EXPENSE_RADIX_BILL("01"),
    /** 公司计费基数->URL:"02" */
    CORP_EXPENSE_RADIX_SUBDOMAIN("02"),
    /** 公司计费基数->初始广告计划返还费用:"03" */
    CORP_EXPENSE_RADIX_MFADS("03"),
    /** 公司计费基数->付款:"04" */
    CORP_EXPENSE_RADIX_PAYMENT("04"),
    /** 公司计费基数->导出数据:"05" */
    CORP_EXPENSE_RADIX_EXP("05"),
    /** 公司计费基数->批量转移联系人:"06" */
    CORP_EXPENSE_RADIX_TRANSFER("06"),
    /** 公司计费基数->广告计划竞价成功实际费用:"07" */
    CORP_EXPENSE_RADIX_ADSUCCESS("07"),
    /** ECOMM赠予金额->注册激活:"80" */
    ECOMM_GRANT_EXPENSE_INITIAL("80"),
    
    /** 公司计费记录结算状态->未结算:"N" */
    CORP_EXPENSE_STATUS_NO(Constant.NO),
    /** 公司计费记录结算状态->已结算:"Y" */
    CORP_EXPENSE_STATUS_YES(Constant.YES),
    
    /**********GLOBAL***************/
    /** 公司注册语言->中文:"zh_CN" */
    @Deprecated
    CORP_LANGUAGE_ZH("zh_CN"),
    /** 公司注册语言->英语:"en" */
    @Deprecated
    CORP_LANGUAGE_EN("en"),
    
    /** 激活链接类型->注册类型:"01" */
    LINKTYPE_NEWCORP("01"),
    /** 激活链接类型->新用户:"02" */
    LINKTYPE_NEWUSER("02"),
    /** 激活链接类型->换邮箱:"03" */
    LINKTYPE_CHANGEEMAIL("03"),
    /** 激活链接类型->邀请联系人:"04" */
    LINKTYPE_INVITE("04"),
    
    /** 转移联系人队列状态 -> 未转移:"00" */
    TRANSFER_CONT_STATUS_WAIT("00"),
    /** 转移联系人队列状态 -> 已转移:"01" */
    TRANSFER_CONT_STATUS_FINISH("01"),
    
    /** 买家关闭账号状态 -> 未执行:"00" */
    CLOSE_ACCOUNT_STATUS_WAIT("00"),
    /** 买家关闭账号状态 -> 已结束:"01" */
    CLOSE_ACCOUNT_STATUS_FINISH("01"),
    
    /**********ECOMM***************/
    /** 后台审核开启超时审核机制 ->开启:"Y" */
    GSTL_TIMEOUT_AUDIT_YES(Constant.YES),
    /** 后台审核开启超时审核机制 ->未开启:"N" */
    GSTL_TIMEOUT_AUDIT_NO(Constant.NO),
    
    /** 付款单状态 ->待确认:"00" */
    PAY_STATUS_PENDCONF("00"),
    /** 付款单状态 ->待充值:"01" */
    PAY_STATUS_PENDCHARGE("01"),
    /** 付款单状态 ->待作废:"02" */
    PAY_STATUS_PENDCANCEL("02"),
    /** 付款单状态 ->已充值:"03" */
    PAY_STATUS_SUCCESS("03"),
    /** 付款单状态 ->充值失败:"04" */
    PAY_STATUS_FAIL("04"),
    
    /** 付款单是否寄送增值税发票 -> 否:"N" */
    PAY_INVOICE_NO(Constant.NO),
    /** 付款单是否寄送增值税发票 -> 是:"Y" */
    PAY_INVOICE_YES(Constant.YES),
    
    /** 付款单发票是否已寄送 -> 否:"00" */
    PAY_SENDSTATUS_NO("00"),
    /** 付款单发票是否已寄送 -> 是:"01" */
    PAY_SENDSTATUS_YES("01"),
    /**付款单银行信息*/
    PAY_PDF_PU("PU"),
    /**付款单明细*/
    PAY_PDF_PV("PV"),
    
    /** 付款货币单位:USD */
    PAY_CURRENCY_USD("USD"),
    /** 付款货币单位:CNY */
    PAY_CURRENCY_CNY("CNY"),
    
    /** 品牌证书审核类型 -> 证书:"00" */
    GSTL_CHECKBCN_CERT("00"),
    /** 品牌证书审核类型 -> 品牌:"01" */
    GSTL_CHECKBCN_BRAND("01"),
    
    /** 公司信息审核类型 -> 公司信息:"00" */
    GSTL_CHECKCORP_CORPINFO("00"),
    /** 公司信息审核类型 -> 公司logo:"01" */
    GSTL_CHECKCORP_LOGO("01"),
    /** 公司信息审核类型 -> 公司图片:"02" */
    GSTL_CHECKCORP_CORPIMG("02"),
    
    
    /** 公司审核未通过类型 -> 公司logo:"01" */
    GSTL_UNPASS_LOGO("01"),
    /** 公司审核未通过类型 -> 公司图片:"02" */
    GSTL_UNPASS_CORPIMG("02"),
    
    /** 品牌证书审核未通过类型 -> 证书:"00" */
    GSTL_UNPASS_CERT("00"),
    /** 品牌证书审核未通过类型 -> 品牌:"01" */
    GSTL_UNPASS_BRAND("01"),
    
    /** 后台管理公司状态 -> 待关闭:"01" */
    GSTL_CORPSTATUS_PENDCLOSE("01"),
    /** 后台管理公司状态 -> 已关闭:"02" */
    GSTL_CORPSTATUS_CLOSED("02"),
    /** 后台管理公司状态 -> 监控中:"03" */
    GSTL_CORPSTATUS_WATCHED("03"),
    /** 后台管理公司状态 -> 待解除监控:"04" */
    GSTL_CORPSTATUS_PENDUNWATCH("04"),
    
    /** 后台广告审核状态->待审核:"00" */
    GSTL_ADS_CHECKSTATUS_WAITCHECK("00"),
    /** 后台广告审核状态->待批准:"01" */
    GSTL_ADS_CHECKSTATUS_WAITAPPROVE("01"),
    
    /** 反馈重要级别-> 无级别:"00" */
    GSTL_FEEDBACK_LEVEL_ZERO("00"),
    /** 反馈重要级别-> 一星级:"01" */
    GSTL_FEEDBACK_LEVEL_FIRST("01"),
    /** 反馈重要级别-> 二星级:"02" */
    GSTL_FEEDBACK_LEVEL_SECOND("02"),
    
    /** 后台流水号类型 -> 员工:"00" */
    GSTL_FLOWCODE_TYPE_STAFF("00"),
    /** 后台流水号类型 -> 角色:"01" */
    GSTL_FLOWCODE_TYPE_ROLE("01"),
    /** 后台流水号类型 -> IP:"02" */
    GSTL_FLOWCODE_TYPE_IP("02"),
    /** 后台流水号类型 -> 公告:"03" */
    GSTL_FLOWCODE_TYPE_NOTICE("03"),
    /** 后台流水号类型 -> 新闻:"04" */
    GSTL_FLOWCODE_TYPE_NEWS("04"),
    
    /** 后台公告/新闻类型 -> 公告:"00" */
    PUB_TYPE_NOTICE("00"),
    /** 后台公告/新闻类型 -> 新闻:"01" */
    PUB_TYPE_NEWS("00"),
    
    /** 后台公告/新闻视图 -> 未发布:"00" */
    PUB_VIEW_NOPUB("00"),
    /** 后台公告/新闻视图 -> 发布中:"01" */
    PUB_VIEW_PUBING("01"),
    /** 后台公告/新闻视图 -> 已关闭:"02" */
    PUB_VIEW_CLOSED("02"),
    
    /** 后台公告/新闻状态 -> 草稿:"00" */
    PUB_STATUS_DRAFT("00"),
    /** 后台公告/新闻状态 -> 发布申请中:"01" */
    PUB_STATUS_PUBAPPLY("01"),
    /** 后台公告/新闻状态 -> 待发布:"02" */
    PUB_STATUS_WAITPUB("02"),
    /** 后台公告/新闻状态 -> 正在发布中:"03" */
    PUB_STATUS_PUBING("03"),
    /** 后台公告/新闻状态 -> 关闭申请中:"04" */
    PUB_STATUS_CLOSEAPPLY("04"),
    /** 后台公告/新闻状态 -> 提前关闭:"05" */
    PUB_STATUS_AHEADCLOSE("05"),
    /** 后台公告/新闻状态 -> 到期关闭:"06" */
    PUB_STATUS_MATURCLOSE("06"),
    
    /** 后台提醒类型 -> 发布中心[公告]:"00" */
    RAISE_TYPE_NOTICE("00"),
    /** 后台提醒类型 -> 审核中心:"01" */
    RAISE_TYPE_CHECK("01"),
    /** 后台提醒类型 -> 财务中心:"02" */
    RAISE_TYPE_FINANCE("02"),
    /** 后台提醒类型 -> 发布中心[新闻]:"03" */
    RAISE_TYPE_NEWS("03"),
    /** 后台提醒类型 -> 帐户中心:"04" */
    RAISE_TYPE_ACCOUNT("04"),
    
    /** 后台发布中心提醒状态 -> 申请发布:"00" */
    RAISE_NOTICE_STATUS_PUBAPPLY("00"),
    /** 后台发布中心提醒状态 -> 申请关闭:"01" */
    RAISE_NOTICE_STATUS_CLOSEAPPLY("01"),
    /** 后台发布中心提醒状态 -> 驳回发布申请:"02" */
    RAISE_NOTICE_STATUS_REJPUB("02"),
    /** 后台发布中心提醒状态 -> 驳回关闭申请:"03" */
    RAISE_NOTICE_STATUS_REJCLOSE("03"),
    
    /** 后台审核中心提醒状态 -> 申请关闭帐号:"00" */
    RAISE_CHECK_STATUS_CLOSEAPPLY("00"),
    /** 后台审核中心提醒状态 -> 驳回关闭账号:"01" */
    RAISE_CHECK_STATUS_REJCLOSE("01"),
    /** 后台审核中心提醒状态 -> 申请解除监控:"02" */
    RAISE_CHECK_STATUS_UNWATCHAPPLY("02"),
    /** 后台审核中心提醒状态 -> 驳回解除监控申请:"03" */
    RAISE_CHECK_STATUS_REJUNWATCH("03"),
    
    /** 后台审核中心提醒状态 -> 申请充值:"00" */
    RAISE_FINANCE_STATUS_APPLYCHARGE("00"),
    /** 后台审核中心提醒状态 -> 申请作废:"01" */
    RAISE_FINANCE_STATUS_APPLYCANCEL("01"),
    /** 后台审核中心提醒状态 -> 驳回充值申请:"02" */
    RAISE_FINANCE_STATUS_REJCHARGE("02"),
    /** 后台审核中心提醒状态 -> 驳回作废申请:"03" */
    RAISE_FINANCE_STATUS_REJCANCEL("03"),
    
    /** 产品超时审核状态 -> 否:"N" */
    TIMEOUT_CHECKSTATUS_NO(Constant.NO),
    /** 产品超时审核状态 -> 是:"Y" */
    TIMEOUT_CHECKSTATUS_YES(Constant.YES),
    
    /** 后台关闭帐户:严重违规 -> 是:"Y" */
    CLOSEACCOUNT_ILLEGAL_YES(Constant.YES),
    /** 后台关闭帐户:严重违规 -> 否:"N" */
    CLOSEACCOUNT_ILLEGAL_NO(Constant.NO),
    /** 后台关闭帐户:长期未登录 -> 是:"Y" */
    CLOSEACCOUNT_NOLOGIN_YES(Constant.YES),
    /** 后台关闭帐户:长期未登录 -> 否:"N" */
    CLOSEACCOUNT_NOLOGIN_NO(Constant.NO),
    /** 后台关闭帐户:欠费 -> 是:"Y" */
    CLOSEACCOUNT_OWE_YES(Constant.YES),
    /** 后台关闭帐户:欠费 -> 否:"N" */
    CLOSEACCOUNT_OWE_NO(Constant.NO),
    /** 后台关闭帐户:买家主动注销 -> 是:"Y" */
    CLOSEACCOUNT_CLOSE_YES(Constant.YES),
    /** 后台关闭帐户:买家主动注销 -> 否:"N" */
    CLOSEACCOUNT_CLOSE_NO(Constant.NO),

    
    /** 后台关闭帐户是否清空数据 -> 是:"Y" */
    CLOSEACCOUNT_CLEAR_YES(Constant.YES),
    /** 后台关闭帐户是否清空数据 -> 否:"N" */
    CLOSEACCOUNT_CLEAR_NO(Constant.NO),
    
    
    /**********ADS**************/
    /** 广告方案语言->中文:"zh_CN" */
    @Deprecated
    ADS_DRAFT_LANGUAGE_ZH("zh_CN"),
    /** 广告方案语言->英语:"en" */
    @Deprecated
    ADS_DRAFT_LANGUAGE_EN("en"),
    
    /** 广告方案审核状态->待审核:"01" */
    DRAFT_CHECKSTATUS_WAITCHECK("01"),
    /** 广告方案审核状态->审核通过:"02" */
    DRAFT_CHECKSTATUS_PASSCHECK("02"),
    /** 广告方案审核状态->审核未通过:"03" */
    DRAFT_CHECKSTATUS_UNPASSCHECK("03"),
    
    /** 广告审核申请状态->待审核:"00" */
    ADS_APPLYSTATUS_WAITDEAL("00"),
    /** 广告审核申请状态->审核通过:"01" */
    ADS_APPLYSTATUS_PASSAPPLY("01"),
    /** 广告审核申请状态->审核未通过:"02" */
    ADS_APPLYSTATUS_APPLYFORBID("02"),
    
    /** 广告审核状态->待审核:"00" */
    ADS_CHECKSTATUS_WAITCHECK("00"),
    /** 广告审核状态->待批准:"01" */
    ADS_CHECKSTATUS_WAITAPPROVE("01"),
    /** 广告审核状态->审核通过:"02" */
    ADS_CHECKSTATUS_PASSCHECK("02"),
    /** 广告审核状态->已禁止:"03" */
    ADS_CHECKSTATUS_FORBID("03"),
    
    /** 竞价状态->竞价中:"00" */
    ADS_BIDSTATUS_BIDING("00"),
    /** 竞价状态->竞价成功:"01" */
    ADS_BIDSTATUS_SUCCESS("01"),
    /** 竞价状态->被屏蔽:"02" */
    ADS_BIDSTATUS_BLOKCED("02"),
    /** 竞价状态->竞价失败:"03" */
    ADS_BIDSTATUS_FAILURE("03"),
    /** 竞价状态->被禁止:"04" */
    ADS_BIDSTATUS_FORBID("04"),
    
    /** 竞价结果状态->成功:"01" */
    ADS_BIDRESULT_SUCCESS("01"),
    /** 竞价结果状态->被屏蔽:"02" */
    ADS_BIDRESULT_BLOCKED("02"),
    /** 竞价结果状态->屏蔽申请中:"03" */
    ADS_BIDRESULT_BLOCKAPPLY("03"),
    
    /** 广告流水号类型->广告方案流水号:"00" */
    ADS_FLOWCODE_DRAFT("00"),
    /** 广告流水号类型->广告计划流水号:"01" */
    ADS_FLOWCODE_SCHEDULE("01"),
    
    /** 广告一级审核是否结束状态->未结束:"00"*/
    ADS_STATUS_LEVL1_NO("00"),
    /** 广告一级审核是否结束状态->已结束:"01"*/
    ADS_STATUS_LEVL1_YES("01"),
    
    /** 广告二级审核是否结束状态->未结束:"00"*/
    ADS_STATUS_LEVL2_NO("00"),
    /** 广告二级审核是否结束状态->已结束:"01"*/
    ADS_STATUS_LEVL2_YES("01"),
    
    
    /** 后台JOB是否被执行-> 是:"Y" */
    JOB_STATUS_YES(Constant.YES),
    /** 后台JOB是否被执行-> 是:"N" */
    JOB_STATUS_NO(Constant.NO),
    
    
    ;

    private String magic;
    private Object radix;

    private ColumnStatusEnum(String magic) {
        this.magic = magic;
    }
    
    private ColumnStatusEnum(String magic, Boolean radix) {
        this.magic = magic;
        this.radix = radix;
    }

    public String magic() {
        return magic;
    }
    
    public Object radix() {
        return radix;
    }

    @Override
    public String toString() {
        return magic;
    }

}
