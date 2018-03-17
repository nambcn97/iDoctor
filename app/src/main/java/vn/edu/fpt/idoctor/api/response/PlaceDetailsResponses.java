package vn.edu.fpt.idoctor.api.response;

/**
 * Created by NamBC on 3/16/2018.
 */

public class PlaceDetailsResponses {
    private Result result;
    private String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Result{
        private String formatted_address;
        private String formatted_phone_number;
        private String icon;
        private Double rating;
        private String website;
        private String url;

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public String getFormatted_phone_number() {
            return formatted_phone_number;
        }

        public void setFormatted_phone_number(String formatted_phone_number) {
            this.formatted_phone_number = formatted_phone_number;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
