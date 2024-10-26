package com.example.mainactivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskStorage {
    // Tworzymy jedyną instancję klasy TaskStorage (Singleton)
    private static final TaskStorage taskStorage = new TaskStorage();

    private final List<Task> tasks;

    // Metoda zwracająca jedyną instancję klasy TaskStorage
    public static TaskStorage getInstance() {
        return taskStorage;
    }

    // Prywatny konstruktor (tylko klasa TaskStorage może się zainicjować)
    private TaskStorage() {
        tasks = new ArrayList<>();

        // Dodawanie zadan
        for (int i = 1; i <= 150; i++) {
            Task task = new Task();
            task.setName("Pilne zadanie numer " + i);
            task.setDone(i % 3 == 0);  // Co trzecie zadanie jest oznaczone jako wykonane

            if (i % 3 == 0) {
                task.setCategory(Category.STUDIES); // Co trzecie zadanie ma kategorię STUDIES
            } else {
                task.setCategory(Category.HOME); // Pozostałe zadania mają kategorię HOME
            }

            tasks.add(task);
        }
    }

    // Metoda zwracająca całą listę zadań
    public List<Task> getTasks() {
        return tasks;
    }

    // Metoda zwracająca zadanie o podanym ID
    public Task getTask(UUID id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;  // Jeśli nie znaleziono zadania o danym ID zwracamy nulla
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
