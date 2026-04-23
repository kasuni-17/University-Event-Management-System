package com.unievent.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String facilities; // Description of available facilities (projector, AC, etc.)

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VenueType venueType = VenueType.AUDITORIUM;

    @Column(name = "is_unlimited_capacity", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUnlimitedCapacity = false;

    public enum VenueType {
        AUDITORIUM, HALL, LAB, SPORTS_AREA, MEETING_ROOM, OTHER, OUTDOOR, SPORTS
    }

    public Venue() {}

    public Venue(Long id, String name, String location, Integer capacity, String facilities, String imageUrl, VenueType venueType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.facilities = facilities;
        this.imageUrl = imageUrl;
        this.venueType = venueType;
        this.isUnlimitedCapacity = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public VenueType getVenueType() { return venueType; }
    public void setVenueType(VenueType venueType) { this.venueType = venueType; }

    public Boolean getIsUnlimitedCapacity() { return isUnlimitedCapacity; }
    public void setIsUnlimitedCapacity(Boolean isUnlimitedCapacity) { this.isUnlimitedCapacity = isUnlimitedCapacity; }
}
