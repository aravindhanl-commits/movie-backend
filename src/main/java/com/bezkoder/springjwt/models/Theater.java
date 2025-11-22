package com.bezkoder.springjwt.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "theaters")
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private int screens;

    @ElementCollection
    private List<String> facilities; // e.g. ["IMAX", "Dolby Atmos"]

    @Embedded
    private SeatingLayout seatingLayout;

    public Theater() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getScreens() { return screens; }
    public void setScreens(int screens) { this.screens = screens; }

    public List<String> getFacilities() { return facilities; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }

    public SeatingLayout getSeatingLayout() { return seatingLayout; }
    public void setSeatingLayout(SeatingLayout seatingLayout) { this.seatingLayout = seatingLayout; }
}
