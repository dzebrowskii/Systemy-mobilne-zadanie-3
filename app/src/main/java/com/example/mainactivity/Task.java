package com.example.mainactivity;

import java.util.Date;
import java.util.UUID;

public class Task {

    private UUID id;
    private String name;
    private String details; // Szczegóły zadania
    private Date date;
    private boolean done;
    private Category category;

    public Task() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public UUID getId() { return id; }
}
