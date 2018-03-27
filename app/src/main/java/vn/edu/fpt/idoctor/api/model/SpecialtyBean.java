package vn.edu.fpt.idoctor.api.model;

/**
 * Created by NamBC on 3/27/2018.
 */

public class SpecialtyBean {
    private Long id;
    private String name;
    private String description;

    public SpecialtyBean() {
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
