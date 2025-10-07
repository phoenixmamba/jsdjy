package com.centit.shopping.utils;

import com.centit.shopping.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 
 * @Version : 1.0
 * @Author : cui_jian
 * @Description : 通用装载类
 * @Date : 2017年11月14日 下午4:33:20
 */
@Component
public class CommonInit {

	@Autowired
	private ShoppingGoodsDao shoppingGoodsDao;
	@Autowired
	private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;
	@Autowired
	private ShoppingStoreDao shoppingStoreDao;
	@Autowired
	private ShoppingStorecartDao shoppingStorecartDao;
	@Autowired
	private ShoppingGoodsclassDao shoppingGoodsclassDao;
	@Autowired
	private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;
	@Autowired
	private ShoppingSysconfigDao shoppingSysconfigDao;
	@Autowired
	private ShoppingUserDao shoppingUserDao;
	@Autowired
	private ShoppingAddressDao shoppingAddressDao;
	@Autowired
	private ShoppingGoodscartDao shoppingGoodscartDao;
	@Autowired
	private ShoppingOrderformDao shoppingOrderformDao;
	@Autowired
	private ShoppingExpressCompanyDao shoppingExpressCompanyDao;
	@Autowired
	private ShoppingGoodstypeDao shoppingGoodstypeDao;
	@Autowired
	private ShoppingEvaluatePhotoDao shoppingEvaluatePhotoDao;
	@Autowired
	private ShoppingArtactivityDao shoppingArtactivityDao;
	@Autowired
	private ShoppingArtplanDao shoppingArtplanDao;
	@Autowired
	private ShoppingArtclassDao shoppingArtclassDao;
	@Autowired
	private ShoppingArtactivitySignupinfoDao shoppingArtactivitySignupinfoDao;
	@Autowired
	private ShoppingArtplanSignupinfoDao shoppingArtplanSignupinfoDao;
	@Autowired
	private ShoppingArtclassSignupinfoDao shoppingArtclassSignupinfoDao;
	@Autowired
	private TicketEventDao ticketEventDao;
	@Autowired
	private TicketProjectDao ticketProjectDao;
	@Autowired
	private ParkOrderDao parkOrderDao;
	@Autowired
	private TOnOndemandDao tOnOndemandDao;
	@Autowired
	private ShoppingRefundDao shoppingRefundDao;
	@Autowired
	private CrmAuthorizationDao crmAuthorizationDao;
	@Autowired
	private ShoppingRechargeDao shoppingRechargeDao;
	@Autowired
	private ShoppingRechargeDiscountDao shoppingRechargeDiscountDao;
	@Autowired
	private ShoppingCouponDao shoppingCouponDao;
	@Autowired
	private ShoppingCouponUsertempDao shoppingCouponUsertempDao;
	@Autowired
	private ShoppingMembershipDao shoppingMembershipDao;
	@Autowired
	private TLmThirdlogDao tLmThirdlogDao;
	@Autowired
	private ShoppingIntegralSetDao shoppingIntegralSetDao;
	@Autowired
	private FUserinfoDao fUserinfoDao;
	@Autowired
	private FDatadictionaryDao fDatadictionaryDao;
	@Autowired
	private ShoppingPayLimitDao shoppingPayLimitDao;
	@Autowired
	private ShoppingAssetRuleDao shoppingAssetRuleDao;
	@Autowired
	private ShoppingIntegralTotalDao shoppingIntegralTotalDao;
	@Autowired
	private TInvoiceTokenDao tInvoiceTokenDao;
	@Autowired
	private TicketCouponBindDao ticketCouponBindDao;
	@Autowired
	private ShoppingCouponGrantDao shoppingCouponGrantDao;
	@Autowired
	private ShoppingWriteoffCouponDao shoppingWriteoffCouponDao;
	@Autowired
	private ShoppingPaymentDao shoppingPaymentDao;

	public static ShoppingPaymentDao staticShoppingPaymentDao;
	public static ShoppingGoodsInventoryDao staticShoppingGoodsInventoryDao;
	public static ShoppingWriteoffCouponDao staticShoppingWriteoffCouponDao;
	public static ShoppingCouponGrantDao staticShoppingCouponGrantDao;
	public static TicketCouponBindDao staticTicketCouponBindDao;
	public static TInvoiceTokenDao staticTInvoiceTokenDao;
	public static ShoppingIntegralTotalDao staticShoppingIntegralTotalDao;
	public static ShoppingAssetRuleDao staticShoppingAssetRuleDao;
	public static ShoppingPayLimitDao staticShoppingPayLimitDao;
	public static FDatadictionaryDao staticFDatadictionaryDao;
	public static FUserinfoDao staticFUserinfoDao;
	public static ShoppingIntegralSetDao staticShoppingIntegralSetDao;
	public static TLmThirdlogDao staticTLmThirdlogDao;
	public static ShoppingMembershipDao staticShoppingMembershipDao;
	public static ShoppingCouponUsertempDao staticShoppingCouponUsertempDao;
	public static ShoppingCouponDao staticShoppingCouponDao;
	public static CrmAuthorizationDao staticCrmAuthorizationDao;
	public static ShoppingGoodsDao staticShoppingGoodsDao;
	public static ShoppingStoreDao staticShoppingStoreDao;
	public static ShoppingStorecartDao staticshoppingStorecartDao;
	public static ShoppingGoodsclassDao staticShoppingGoodsclassDao;
	public static ShoppingGoodsspecpropertyDao staticShoppingGoodsspecpropertyDao;
	public static ShoppingSysconfigDao staticShoppingSysconfigDao;
	public static ShoppingUserDao staticShoppingUserDao;
	public static ShoppingAddressDao staticShoppingAddressDao;
    public static ShoppingGoodscartDao staticShoppingGoodscartDao;
	public static ShoppingOrderformDao staticShoppingOrderformDao;
	public static ShoppingExpressCompanyDao staticShoppingExpressCompanyDao;
	public static ShoppingGoodstypeDao staticShoppingGoodstypeDao;
	public static ShoppingEvaluatePhotoDao staticShoppingEvaluatePhotoDao;

	public static ShoppingArtactivityDao staticShoppingArtactivityDao;
	public static ShoppingArtplanDao staticShoppingArtplanDao;
	public static ShoppingArtclassDao staticShoppingArtclassDao;

	public static  ShoppingArtactivitySignupinfoDao staticShoppingArtactivitySignupinfoDao;
	public static  ShoppingArtplanSignupinfoDao staticShoppingArtplanSignupinfoDao;
	public static  ShoppingArtclassSignupinfoDao staticShoppingArtclassSignupinfoDao;

	public static TicketEventDao staticTicketEventDao;
	public static TicketProjectDao staticTicketProjectDao;

	public static ParkOrderDao staticParkOrderDao;

	public static TOnOndemandDao staticTOnOndemandDao;

	public static ShoppingRefundDao staticShoppingRefundDao;

	public static ShoppingRechargeDao staticShoppingRechargeDao;
	public static ShoppingRechargeDiscountDao staticShoppingRechargeDiscountDao;
	@PostConstruct
	public void init(){
		System.out.println("============222");
		staticShoppingPaymentDao = shoppingPaymentDao;
		staticShoppingGoodsInventoryDao = shoppingGoodsInventoryDao;
		staticShoppingWriteoffCouponDao = shoppingWriteoffCouponDao;
		staticShoppingCouponGrantDao=shoppingCouponGrantDao;
		staticTicketCouponBindDao=ticketCouponBindDao;
		staticTInvoiceTokenDao=tInvoiceTokenDao;
		staticShoppingIntegralTotalDao=shoppingIntegralTotalDao;
		staticShoppingAssetRuleDao=shoppingAssetRuleDao;
		staticShoppingPayLimitDao=shoppingPayLimitDao;
		staticFDatadictionaryDao=fDatadictionaryDao;
		staticFUserinfoDao = fUserinfoDao;
		staticShoppingGoodsDao = shoppingGoodsDao;
		staticShoppingStoreDao=shoppingStoreDao;
		staticshoppingStorecartDao=shoppingStorecartDao;
		staticShoppingGoodsclassDao=shoppingGoodsclassDao;
		staticShoppingGoodsspecpropertyDao = shoppingGoodsspecpropertyDao;
		staticShoppingSysconfigDao = shoppingSysconfigDao;
		staticShoppingUserDao = shoppingUserDao;
		staticShoppingAddressDao = shoppingAddressDao;
		staticShoppingGoodscartDao = shoppingGoodscartDao;
		staticShoppingOrderformDao = shoppingOrderformDao;
		staticShoppingExpressCompanyDao = shoppingExpressCompanyDao;
		staticShoppingGoodstypeDao=shoppingGoodstypeDao;
		staticShoppingEvaluatePhotoDao = shoppingEvaluatePhotoDao;
		staticShoppingArtactivityDao = shoppingArtactivityDao;
		staticShoppingArtplanDao = shoppingArtplanDao;
		staticShoppingArtclassDao=shoppingArtclassDao;

		staticShoppingArtactivitySignupinfoDao = shoppingArtactivitySignupinfoDao;
		staticShoppingArtplanSignupinfoDao = shoppingArtplanSignupinfoDao;
		staticShoppingArtclassSignupinfoDao = shoppingArtclassSignupinfoDao;

		staticTicketEventDao =ticketEventDao;
		staticTicketProjectDao = ticketProjectDao;

		staticParkOrderDao = parkOrderDao;
		staticTOnOndemandDao = tOnOndemandDao;
		staticShoppingRefundDao = shoppingRefundDao;

		staticShoppingRechargeDao = shoppingRechargeDao;
		staticShoppingRechargeDiscountDao = shoppingRechargeDiscountDao;
		staticCrmAuthorizationDao = crmAuthorizationDao;

		staticShoppingCouponDao = shoppingCouponDao;
		staticShoppingCouponUsertempDao=shoppingCouponUsertempDao;
		staticShoppingMembershipDao=shoppingMembershipDao;
		staticTLmThirdlogDao = tLmThirdlogDao;

		staticShoppingIntegralSetDao = shoppingIntegralSetDao;
	}



}
