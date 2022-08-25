package onealldigital.nizara.in.model;

public class SearchQueryEvent {
    String query;

    public SearchQueryEvent(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}