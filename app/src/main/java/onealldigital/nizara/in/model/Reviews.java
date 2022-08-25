package onealldigital.nizara.in.model;

import java.util.ArrayList;

public class Reviews {

    String re,name;
    float rate;
    ArrayList list;

    public Reviews() {

    }

    public Reviews(String Review, float rate, String name) {
        this.re = Review;
        this.rate = rate;
        this.name = name;
    }

    public Reviews(ArrayList list) {
        this.re = (String) list.get(0);
        this.rate = (float) list.get(1);
    }

    public String getRe() {
        return re;
    }

    public float getRate() {
        return rate;
    }

    public String getname(){ return name; }
}
