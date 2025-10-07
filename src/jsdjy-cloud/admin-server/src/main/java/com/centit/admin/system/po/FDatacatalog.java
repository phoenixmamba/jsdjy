package com.centit.admin.system.po;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

import com.centit.admin.util.CommonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-04
 **/
@Data
public class FDatacatalog implements Serializable {


    /**
     * 数据字典编码
     */
    private String catalogCode;

    /**
     * 字典名称
     */
    private String catalogName;

    /**
     * F : 框架固有的 U:用户 S：系统  G国标
     */
    private String catalogStyle;

    private String catalogStyleText;

    public String getCatalogStyleText() {
        if(StringUtils.isNotBlank(getCatalogStyle())){
            return CommonUtil.getCodeValue("CatalogStyle",getCatalogStyle());
        }else{
            return "";
        }

    }

    /**
     * 平台区分 G：运行支撑平台；U：统一用户平台
     */
    private String catalogType;

    /**
     * 描述
     */
    private String catalogDesc;

    /**
     * 字段描述，不同字段用分号隔开
     */
    private String fieldDesc;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 业务分类，使用数据字典DICTIONARYTYPE中数据
     */
    private String optId;

    /**
     * 是否需要缓存
     */
    private String needCache;

    private String creator;

    private String updator;

    private List<FDatadictionary> dataDictionaries = new ArrayList<>();

}
