package vn.edu.fpt.idoctor.api.response;

import android.util.Log;

import java.util.List;

/**
 * Created by NamBC on 3/16/2018.
 */

public class PlaceSearchResponse {

    private List<Result> results;
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Result {
        private Geometry geometry;
        private String icon;
        private String id;
        private String name;
        private OpeningHour opening_hours;
        private List<Photo> photos;
        private String place_id;
        private String vicinity;


        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public OpeningHour getOpening_hours() {
            return opening_hours;
        }

        public void setOpening_hours(OpeningHour opening_hours) {
            this.opening_hours = opening_hours;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        public class Geometry {
            private Location location;

            public Location getLocation() {
                return location;
            }

            public void setLocation(Location location) {
                this.location = location;
            }

            public class Location {
                private Double lat;
                private Double lng;

                public Double getLat() {
                    return lat;
                }

                public void setLat(Double lat) {
                    this.lat = lat;
                }

                public Double getLng() {
                    return lng;
                }

                public void setLng(Double lng) {
                    this.lng = lng;
                }
            }

        }

        public class OpeningHour {
            private Boolean open_now;

            public Boolean getOpen_now() {
                return open_now;
            }

            public void setOpen_now(Boolean open_now) {
                this.open_now = open_now;
            }
        }

        public class Photo {
            public Long height;
            public String photo_reference;
            public Long width;

            public Long getHeight() {
                return height;
            }

            public void setHeight(Long height) {
                this.height = height;
            }

            public Long getWidth() {
                return width;
            }

            public void setWidth(Long width) {
                this.width = width;
            }

            public String getPhoto_reference() {
                return photo_reference;
            }

            public void setPhoto_reference(String photo_reference) {
                this.photo_reference = photo_reference;
            }
        }

    }
}