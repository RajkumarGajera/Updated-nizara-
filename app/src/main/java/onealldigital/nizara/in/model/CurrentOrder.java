package onealldigital.nizara.in.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentOrder {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("otp")
        @Expose
        private String otp;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("order_note")
        @Expose
        private String orderNote;
        @SerializedName("total")
        @Expose
        private String total;
        @SerializedName("delivery_charge")
        @Expose
        private String deliveryCharge;
        @SerializedName("tax_amount")
        @Expose
        private String taxAmount;
        @SerializedName("tax_percentage")
        @Expose
        private String taxPercentage;
        @SerializedName("wallet_balance")
        @Expose
        private String walletBalance;
        @SerializedName("discount")
        @Expose
        private String discount;
        @SerializedName("promo_code")
        @Expose
        private String promoCode;
        @SerializedName("promo_discount")
        @Expose
        private String promoDiscount;
        @SerializedName("final_total")
        @Expose
        private String finalTotal;
        @SerializedName("payment_method")
        @Expose
        private String paymentMethod;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("delivery_time")
        @Expose
        private String deliveryTime;
        @SerializedName("date_added")
        @Expose
        private String dateAdded;
        @SerializedName("order_from")
        @Expose
        private String orderFrom;
        @SerializedName("pincode_id")
        @Expose
        private String pincodeId;
        @SerializedName("area_id")
        @Expose
        private String areaId;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("discount_rupees")
        @Expose
        private String discountRupees;
        @SerializedName("items")
        @Expose
        private List<Item> items = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOrderNote() {
            return orderNote;
        }

        public void setOrderNote(String orderNote) {
            this.orderNote = orderNote;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getDeliveryCharge() {
            return deliveryCharge;
        }

        public void setDeliveryCharge(String deliveryCharge) {
            this.deliveryCharge = deliveryCharge;
        }

        public String getTaxAmount() {
            return taxAmount;
        }

        public void setTaxAmount(String taxAmount) {
            this.taxAmount = taxAmount;
        }

        public String getTaxPercentage() {
            return taxPercentage;
        }

        public void setTaxPercentage(String taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        public String getWalletBalance() {
            return walletBalance;
        }

        public void setWalletBalance(String walletBalance) {
            this.walletBalance = walletBalance;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getPromoCode() {
            return promoCode;
        }

        public void setPromoCode(String promoCode) {
            this.promoCode = promoCode;
        }

        public String getPromoDiscount() {
            return promoDiscount;
        }

        public void setPromoDiscount(String promoDiscount) {
            this.promoDiscount = promoDiscount;
        }

        public String getFinalTotal() {
            return finalTotal;
        }

        public void setFinalTotal(String finalTotal) {
            this.finalTotal = finalTotal;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public void setDeliveryTime(String deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        public String getDateAdded() {
            return dateAdded;
        }

        public void setDateAdded(String dateAdded) {
            this.dateAdded = dateAdded;
        }

        public String getOrderFrom() {
            return orderFrom;
        }

        public void setOrderFrom(String orderFrom) {
            this.orderFrom = orderFrom;
        }

        public String getPincodeId() {
            return pincodeId;
        }

        public void setPincodeId(String pincodeId) {
            this.pincodeId = pincodeId;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getDiscountRupees() {
            return discountRupees;
        }

        public void setDiscountRupees(String discountRupees) {
            this.discountRupees = discountRupees;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public static class Item {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("user_id")
            @Expose
            private String userId;
            @SerializedName("order_id")
            @Expose
            private String orderId;
            @SerializedName("product_name")
            @Expose
            private String productName;
            @SerializedName("variant_name")
            @Expose
            private String variantName;
            @SerializedName("product_variant_id")
            @Expose
            private String productVariantId;
            @SerializedName("delivery_boy_id")
            @Expose
            private String deliveryBoyId;
            @SerializedName("quantity")
            @Expose
            private String quantity;
            @SerializedName("price")
            @Expose
            private String price;
            @SerializedName("discounted_price")
            @Expose
            private String discountedPrice;
            @SerializedName("tax_amount")
            @Expose
            private String taxAmount;
            @SerializedName("tax_percentage")
            @Expose
            private String taxPercentage;
            @SerializedName("discount")
            @Expose
            private String discount;
            @SerializedName("sub_total")
            @Expose
            private String subTotal;
            @SerializedName("status")
            @Expose
            private List<List<String>> status = null;
            @SerializedName("active_status")
            @Expose
            private String activeStatus;
            @SerializedName("date_added")
            @Expose
            private String dateAdded;
            @SerializedName("seller_id")
            @Expose
            private String sellerId;
            @SerializedName("is_credited")
            @Expose
            private String isCredited;
            @SerializedName("variant_id")
            @Expose
            private String variantId;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("manufacturer")
            @Expose
            private String manufacturer;
            @SerializedName("made_in")
            @Expose
            private String madeIn;
            @SerializedName("return_status")
            @Expose
            private String returnStatus;
            @SerializedName("cancelable_status")
            @Expose
            private String cancelableStatus;
            @SerializedName("till_status")
            @Expose
            private String tillStatus;
            @SerializedName("measurement")
            @Expose
            private String measurement;
            @SerializedName("unit")
            @Expose
            private String unit;
            @SerializedName("seller_name")
            @Expose
            private String sellerName;
            @SerializedName("seller_store_name")
            @Expose
            private String sellerStoreName;
            @SerializedName("return_days")
            @Expose
            private String returnDays;
            @SerializedName("applied_for_return")
            @Expose
            private Boolean appliedForReturn;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getVariantName() {
                return variantName;
            }

            public void setVariantName(String variantName) {
                this.variantName = variantName;
            }

            public String getProductVariantId() {
                return productVariantId;
            }

            public void setProductVariantId(String productVariantId) {
                this.productVariantId = productVariantId;
            }

            public String getDeliveryBoyId() {
                return deliveryBoyId;
            }

            public void setDeliveryBoyId(String deliveryBoyId) {
                this.deliveryBoyId = deliveryBoyId;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getDiscountedPrice() {
                return discountedPrice;
            }

            public void setDiscountedPrice(String discountedPrice) {
                this.discountedPrice = discountedPrice;
            }

            public String getTaxAmount() {
                return taxAmount;
            }

            public void setTaxAmount(String taxAmount) {
                this.taxAmount = taxAmount;
            }

            public String getTaxPercentage() {
                return taxPercentage;
            }

            public void setTaxPercentage(String taxPercentage) {
                this.taxPercentage = taxPercentage;
            }

            public String getDiscount() {
                return discount;
            }

            public void setDiscount(String discount) {
                this.discount = discount;
            }

            public String getSubTotal() {
                return subTotal;
            }

            public void setSubTotal(String subTotal) {
                this.subTotal = subTotal;
            }

            public List<List<String>> getStatus() {
                return status;
            }

            public void setStatus(List<List<String>> status) {
                this.status = status;
            }

            public String getActiveStatus() {
                return activeStatus;
            }

            public void setActiveStatus(String activeStatus) {
                this.activeStatus = activeStatus;
            }

            public String getDateAdded() {
                return dateAdded;
            }

            public void setDateAdded(String dateAdded) {
                this.dateAdded = dateAdded;
            }

            public String getSellerId() {
                return sellerId;
            }

            public void setSellerId(String sellerId) {
                this.sellerId = sellerId;
            }

            public String getIsCredited() {
                return isCredited;
            }

            public void setIsCredited(String isCredited) {
                this.isCredited = isCredited;
            }

            public String getVariantId() {
                return variantId;
            }

            public void setVariantId(String variantId) {
                this.variantId = variantId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getManufacturer() {
                return manufacturer;
            }

            public void setManufacturer(String manufacturer) {
                this.manufacturer = manufacturer;
            }

            public String getMadeIn() {
                return madeIn;
            }

            public void setMadeIn(String madeIn) {
                this.madeIn = madeIn;
            }

            public String getReturnStatus() {
                return returnStatus;
            }

            public void setReturnStatus(String returnStatus) {
                this.returnStatus = returnStatus;
            }

            public String getCancelableStatus() {
                return cancelableStatus;
            }

            public void setCancelableStatus(String cancelableStatus) {
                this.cancelableStatus = cancelableStatus;
            }

            public String getTillStatus() {
                return tillStatus;
            }

            public void setTillStatus(String tillStatus) {
                this.tillStatus = tillStatus;
            }

            public String getMeasurement() {
                return measurement;
            }

            public void setMeasurement(String measurement) {
                this.measurement = measurement;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public String getSellerName() {
                return sellerName;
            }

            public void setSellerName(String sellerName) {
                this.sellerName = sellerName;
            }

            public String getSellerStoreName() {
                return sellerStoreName;
            }

            public void setSellerStoreName(String sellerStoreName) {
                this.sellerStoreName = sellerStoreName;
            }

            public String getReturnDays() {
                return returnDays;
            }

            public void setReturnDays(String returnDays) {
                this.returnDays = returnDays;
            }

            public Boolean getAppliedForReturn() {
                return appliedForReturn;
            }

            public void setAppliedForReturn(Boolean appliedForReturn) {
                this.appliedForReturn = appliedForReturn;
            }

        }

    }

}
