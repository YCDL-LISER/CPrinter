package net.lingin.max.android.model;

import java.io.Serializable;

/**
 * @author Administrator
 * @date 2019/12/26 15:27
 */
public class GoodsPrintDTO implements Serializable {

    private String itemName;

    private String itemNo;

    private String unitNo;

    private Double salePrice;

    private String itemSize;

    private int goodsCounts = 1;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public int getGoodsCounts() {
        return goodsCounts;
    }

    public void setGoodsCounts(int goodsCounts) {
        this.goodsCounts = goodsCounts;
    }
}
