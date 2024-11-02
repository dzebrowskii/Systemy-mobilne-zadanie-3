package com.example.mainactivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskStorage {
    private static final TaskStorage instance = new TaskStorage();
    private final MutableLiveData<List<Task>> tasksLiveData;

    private TaskStorage() {
        tasksLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public static TaskStorage getInstance() {
        return instance;
    }

    public void addTask(Task task) {
        List<Task> currentTasks = tasksLiveData.getValue();
        if (currentTasks != null) {
            currentTasks.add(task);
            tasksLiveData.setValue(currentTasks); // Aktualizacja LiveData
        }
    }

    public LiveData<List<Task>> getTasksLiveData() {
        return tasksLiveData;
    }

    public List<Task> getTasksList() {
        return tasksLiveData.getValue() != null ? tasksLiveData.getValue() : new ArrayList<>();
    }

    public void updateTasksLiveData() {
        tasksLiveData.setValue(tasksLiveData.getValue());
    }

    // Dodaj metodę `getTask(UUID id)` do wyszukiwania zadania po ID
    public Task getTask(UUID id) {
        List<Task> currentTasks = getTasksList();
        for (Task task : currentTasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;  // Zwraca null, jeśli zadanie o podanym ID nie zostanie znalezione
    }
}
