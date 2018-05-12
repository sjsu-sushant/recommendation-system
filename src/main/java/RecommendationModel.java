public class RecommendationModel {
    String item;
    String value;

    public RecommendationModel(String item, String value) {
        this.item = item;
        this.value = value;
    }

    public String getItem() {
        return item;
    }

    public String getValue() {
        return value;
    }
}

