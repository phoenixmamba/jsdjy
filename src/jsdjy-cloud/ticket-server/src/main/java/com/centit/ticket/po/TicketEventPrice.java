package com.centit.ticket.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-08
 **/
@Data
public class TicketEventPrice implements Serializable {


    /**
     * 票档id
     */
    private String priceId;

    /**
     * 麦座场次id
     */
    private String eventId;

    /**
     * 票档名称
     */
    private String priceName;

    /**
     * 票档销售状态；1=可售，2=禁售
     */
    private Integer priceSaleState;

    /**
     * 票档类型；1=单票，2=套票；暂仅支持单票返回
     */
    private Integer priceType;

    /**
     * 票档价格；单位：分
     */
    private Integer priceMoneyFen;

    /**
     * 票档价格；单位：元
     */
    private BigDecimal priceMoneyYuan;

    /**
     * 票档颜色；项目座位购买类型为 有座（自助选座）时非空；选座渲染时可使用
     */
    private String priceColor;

    /**
     * 剩余库存数量；仅项目座位购买类型为 无座时返回
     */
    private Integer marginStockNum;


}
