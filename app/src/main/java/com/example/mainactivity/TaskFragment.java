package com.example.mainactivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TaskFragment extends Fragment {

    private Task task;
    public static final String ARG_TASK_ID = "task_id";

    private EditText dateField;
    private final Calendar calendar = Calendar.getInstance();


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
        dateField = view.findViewById(R.id.task_date);
        CheckBox doneCheckBox = view.findViewById(R.id.task_done);

        nameField.setText(task.getName());
        doneCheckBox.setChecked(task.isDone());
        setupDateFieldValue(task.getDate());

        Spinner categorySpinner = view.findViewById(R.id.task_category);

        // Ustawienie adaptera z wartościami kategorii
        categorySpinner.setAdapter(new ArrayAdapter<>(
                requireContext(), // użycie kontekstu
                android.R.layout.simple_spinner_item, // układ dla pozycji w Spinnerze
                Category.values() // wartości kategorii z typu wyliczeniowego
        ));

        // Ustawienie słuchacza na Spinner aby reagować na wybór elementu
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ustawienie kategorii zadania na podstawie wyboru
                task.setCategory(Category.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Obsługa sytuacji gdy nic nie jest wybrane
            }
        });

        // Ustawienie domyślnego wyboru kategorii w Spinnerze na podstawie aktualnej kategorii zadania
        categorySpinner.setSelection(task.getCategory().ordinal());


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


        dateField.setOnClickListener(view1 -> {
            new DatePickerDialog(getContext(), (view2, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                setupDateFieldValue(calendar.getTime());
                task.setDate(calendar.getTime()); // Aktualizacja daty zadania
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        doneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));

        // Zwracanie widoku
        return view;
    }

    private void setupDateFieldValue(Date date) {
        Locale locale = new Locale("pl", "PL");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
        dateField.setText(dateFormat.format(date));
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
