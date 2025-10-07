package com.centit.admin.util;


import com.centit.admin.system.po.FDatadictionary;
import com.centit.admin.system.po.FUnitinfo;
import com.centit.admin.system.po.FUserunit;

import java.util.HashMap;
import java.util.List;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：通用工具类
 */
public class CommonUtil {

    /**
     * 获取字典项对应值
     *
     * @param catalogCode,dataCode
     * @return
     */
    public static String getCodeValue(String catalogCode,String dataCode) {
        HashMap<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("catalogCode",catalogCode);
        reqMap.put("dataCode",dataCode);
        List<FDatadictionary> list= CommonInit.staticFDatadictionaryDao.queryList(reqMap);
        if(!list.isEmpty()){
            return list.get(0).getDataValue();
        }
        return "";
    }

    public static String getUserPrimaryUnit(String userCode){
        FUserunit fUserunit = new FUserunit();
        fUserunit.setUserCode(userCode);
        fUserunit.setIsPrimary("T");
        fUserunit = CommonInit.staticFUserunitDao.queryDetail(fUserunit);
        if(null !=fUserunit){
            return fUserunit.getUnitCode();
        }else{
            HashMap<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("userCode",userCode);
            List<FUserunit> list=CommonInit.staticFUserunitDao.queryList(reqMap);
            if(!list.isEmpty()){
                return list.get(0).getUnitCode();
            }else{
                return "qs00001";
            }

        }
    }

    /**
     * 获取机构信息
     *
     * @param unitCode
     * @return
     */
    public static FUnitinfo getUnitinfo(String unitCode) {
        FUnitinfo fUnitinfo = new FUnitinfo();
        fUnitinfo.setUnitCode(unitCode);
        return CommonInit.staticFUnitinfoDao.queryDetail(fUnitinfo);
    }

}