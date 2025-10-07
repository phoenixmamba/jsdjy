package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-19
 **/
@Data
public class TicketRedeemProjectWatchingNotice implements Serializable {


    private String projectId;

    /**
     * 演出时长；showLengthTips，例:"最少xx分钟"
     */
    private String showLengthTips;

    /**
     * 入场时间； entryTimeTips，例::"提前xx分钟"
     */
    private String entryTimeTips;

    /**
     * 儿童入场提示； childrenEntryTips，例::"儿童一律凭票入场"
     */
    private String childrenEntryTips;

    /**
     * 寄存说明； depositTips，例::"无寄存处"
     */
    private String depositTips;

    /**
     * 禁止携带的物品说明； prohibitGoodsTips:"禁止携带专业录像设备、饮料"
     */
    private String prohibitGoodsTips;


}
