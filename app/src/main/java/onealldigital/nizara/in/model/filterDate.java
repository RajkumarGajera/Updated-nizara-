package onealldigital.nizara.in.model;

public class filterDate {

    public filterDate(String start_date, String end_date) {
        Start_date = start_date;
//        Start_month = start_month;
//        Start_year = start_year;
        End_date = end_date;
//        End_month = end_month;
//        End_year = end_year;
    }

    String Start_date;

    public filterDate() {

    }


    public String getStart_date() {
        return Start_date;
    }

    public void setStart_date(String start_date) {
        Start_date = start_date;
    }

//    public String getStart_month() {
//        return Start_month;
//    }
//
//    public void setStart_month(String start_month) {
//        Start_month = start_month;
//    }
//
//    public String getStart_year() {
//        return Start_year;
//    }
//
//    public void setStart_year(String start_year) {
//        Start_year = start_year;
//    }

    public String getEnd_date() {
        return End_date;
    }

    public void setEnd_date(String end_date) {
        End_date = end_date;
    }

//    public String getEnd_month() {
//        return End_month;
//    }
//
//    public void setEnd_month(String end_month) {
//        End_month = end_month;
//    }
//
//    public String getEnd_year() {
//        return End_year;
//    }
//
//    public void setEnd_year(String end_year) {
//        End_year = end_year;
//    }
//
//    String Start_month;
//    String Start_year;
    String End_date;
//    String End_month;
//    String End_year;
}
