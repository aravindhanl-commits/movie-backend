package com.bezkoder.springjwt.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.util.List;

@Embeddable
public class SeatingLayout {

    @Column(name = "seating_rows")  // ✅ Fix reserved keyword
    private int rows;

    @Column(name = "seats_per_row") // ✅ Safe column name
    private int seatsPerRow;

    @ElementCollection
    private List<Integer> aisles;

    public SeatingLayout() {}

    // Getters & Setters
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }

    public List<Integer> getAisles() { return aisles; }
    public void setAisles(List<Integer> aisles) { this.aisles = aisles; }
}
