package org.dci.theratrack.dto;

public class TherapistDTO {
    private Long id;
    private String name; // Add other desired fields

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}