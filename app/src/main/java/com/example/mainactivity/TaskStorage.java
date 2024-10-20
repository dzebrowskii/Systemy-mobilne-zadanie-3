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
}
