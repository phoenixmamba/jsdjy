package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-04
 **/
@Data
public class TicketCouponExchange implements Serializable {


    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 说明
     */
    private String remark;

    /**
     * 生成优惠码数量
     */
    private Integer amount;

    /**
     * 已兑换的优惠码数量
     */
    private Integer usedAmount;

    /**
     * 绑定的麦座优惠码ID
     */
    private String promotionId;

    /**
     * 是否需要生成兑换密码，'0'：否;‘1’：是
     */
    private String needPwd;

    /**
     * 兑换开始时间，为空时表示不限制
     */
    private String startTime;

    /**
     * 兑换截止时间，为空时表示不限制
     */
    private String endTime;

    /**
     * 单个用户最多兑换次数，0表示不限制
     */
    private Integer singleLimit;

    private String createTime;

    private String createUser;

    /**
     * 发布状态 '0':未发布;'1':已发布
     */
    private String pubStatus;

    /**
     * '0'：未删除;'1'：已删除
     */
    private String isDelete;


}
