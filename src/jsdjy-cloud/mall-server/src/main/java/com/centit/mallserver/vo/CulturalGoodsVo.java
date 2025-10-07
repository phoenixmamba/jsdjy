package com.centit.mallserver.vo;

import com.centit.mallserver.model.CulturalGoodsInfo;
import com.centit.mallserver.po.ShoppingEvaluatePo;
import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 文创商品信息
 * @Date : 2025/8/27 15:46
 **/
@Data
public class CulturalGoodsVo {
    private CulturalGoodsInfo culturalGoodsInfo;

    private ShoppingEvaluatePo evaluate;

    private Boolean isFav;

    private CulturalGoodsVo(CulturalGoodsInfo culturalGoodsInfo, ShoppingEvaluatePo evaluate, Boolean isFav) {
        this.culturalGoodsInfo = culturalGoodsInfo;
        this.evaluate = evaluate;
        this.isFav = isFav;
    }

    // 静态方法返回Builder实例
    public static Builder build() {
        return new Builder();
    }

    // Builder内部类
    public static class Builder {
        private CulturalGoodsInfo culturalGoodsInfo;
        private ShoppingEvaluatePo evaluate;
        private Boolean isFav;

        public Builder goodsInfo(CulturalGoodsInfo culturalGoodsInfo) {
            this.culturalGoodsInfo = culturalGoodsInfo;
            return this;
        }

        public Builder evaluateInfo(ShoppingEvaluatePo evaluate) {
            this.evaluate = evaluate;
            return this;
        }

        public Builder favInfo(Boolean isFav) {
            this.isFav = isFav;
            return this;
        }

        public CulturalGoodsVo build() {
            return new CulturalGoodsVo(culturalGoodsInfo, evaluate, isFav);
        }
    }
}
