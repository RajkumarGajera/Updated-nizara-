package onealldigital.nizara.in.model;

public class getbrand {

    public getbrand(String toString) {
        this.Brand_ID=toString;
    }

    public getbrand() {

    }

    public String getBrand_ID() {
        return Brand_ID;
    }

    public String setBrand_ID(String brand_ID) {
        Brand_ID = brand_ID;
        return brand_ID;
    }

    String Brand_ID;
}
