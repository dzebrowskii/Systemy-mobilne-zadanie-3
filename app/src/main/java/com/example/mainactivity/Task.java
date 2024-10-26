package com.example.mainactivity;

import java.util.Date;
import java.util.UUID;

public class Task {

    private UUID id;        // Unikalny identyfikator zadania
    private String name;    // Nazwa zadania
    private Date date;      // Data zadania
    private boolean done;   // Czy zadanie zostało ukończone



    private Category category;

    public Task() {
        id = UUID.randomUUID(); // Generowanie unikalnego identyfikatora
        date = new Date();      // Ustawienie bieżącej daty
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    // Getter - metoda pozwalająca na pobranie wartości category
    public Category getCategory() {
        return category;
    }

    // Setter - metoda pozwalająca na ustawienie wartości category
    public void setCategory(Category category) {
        this.category = category;
    }

}
