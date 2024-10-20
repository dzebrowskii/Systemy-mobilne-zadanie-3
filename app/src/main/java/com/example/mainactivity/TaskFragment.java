package com.example.mainactivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class TaskFragment extends Fragment {

    private Task task;
    public static final String ARG_TASK_ID = "task_id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        // Pobranie zadania z TaskStorage na podstawie taskId
        task = TaskStorage.getInstance().getTask(taskId);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflatujemy widok z pliku fragment_task.xml
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        EditText nameField = view.findViewById(R.id.task_name);
        Button dateButton = view.findViewById(R.id.task_date);
        CheckBox doneCheckBox = view.findViewById(R.id.task_done);

        nameField.setText(task.getName());
        dateButton.setText(task.getDate().toString());
        doneCheckBox.setChecked(task.isDone());

        // Obsługa pola EditText
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gdy tekst się zmienia aktualizujemy nazwę zadania
                task.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Obsługa przycisku daty
        dateButton.setText(task.getDate().toString());
        dateButton.setEnabled(false);

        // Obsługa CheckBox
        doneCheckBox.setChecked(task.isDone());
        doneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Aktualizacja stanu zadania
            task.setDone(isChecked);
        });

        // Zwracanie widoku
        return view;
    }

    public static TaskFragment newInstance(UUID taskId) {
        // Tworzymy nowy obiekt Bundle (klucz - wartosc)
        Bundle bundle = new Bundle();

        // Przekazujemy taskId do argumentów fragmentu
        bundle.putSerializable(ARG_TASK_ID, taskId);

        TaskFragment taskFragment = new TaskFragment();

        // Ustawiamy argumenty w fragmencie
        taskFragment.setArguments(bundle);

        // Zwracamy nową instancję fragmentu
        return taskFragment;
    }





}
