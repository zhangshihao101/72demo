package com.spt.bean;

public class VariantsInfo {

    private String skuImage;
    private String SkuColor;
    private String SkuColorId;
    private Boolean isLocal;
    private String productId;
    private String imgId;

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSkuColorId() {
        return SkuColorId;
    }

    public void setSkuColorId(String skuColorId) {
        SkuColorId = skuColorId;
    }

    public Boolean getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(Boolean isLocal) {
        this.isLocal = isLocal;
    }

    public String getSkuImage() {
        return skuImage;
    }

    public void setSkuImage(String skuImage) {
        this.skuImage = skuImage;
    }

    public String getSkuColor() {
        return SkuColor;
    }

    public void setSkuColor(String skuColor) {
        SkuColor = skuColor;
    }

}
