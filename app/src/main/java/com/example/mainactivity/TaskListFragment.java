package com.example.mainactivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Date;
import java.util.List;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    public static final String KEY_EXTRA_TASK_ID = "KEY_EXTRA_TASK_ID";
    private boolean subtitleVisible;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean("subtitleVisible");
        }

        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupObservers(); // Inicjalizacja obserwatora dla LiveData

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.fragment_task_menu, menu);
                MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
                subtitleItem.setTitle(subtitleVisible ? R.string.hide_subtitle : R.string.show_subtitle);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.new_task) {
                    showNewTaskDialog();
                    return true;
                } else if (itemId == R.id.show_subtitle) {
                    subtitleVisible = !subtitleVisible;
                    getActivity().invalidateOptionsMenu();
                    updateSubtitle();
                    return true;
                } else {
                    return false;
                }
            }

        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showNewTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_new_task, null);

        EditText taskNameInput = dialogView.findViewById(R.id.task_name_input);
        EditText taskDetailsInput = dialogView.findViewById(R.id.task_details_input);
        Spinner categorySpinner = dialogView.findViewById(R.id.task_category_spinner);
        TextView dateTextView = dialogView.findViewById(R.id.task_date_text);

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Category.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        dateTextView.setOnClickListener(v -> new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            dateTextView.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        new AlertDialog.Builder(getContext())
                .setTitle("Nowe Zadanie")
                .setView(dialogView)
                .setPositiveButton("Dodaj", (dialog, which) -> {
                    String name = taskNameInput.getText().toString();
                    String details = taskDetailsInput.getText().toString();
                    Category selectedCategory = (Category) categorySpinner.getSelectedItem();
                    Date selectedDate = calendar.getTime();
                    addNewTask(name, details, selectedCategory, selectedDate);
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void addNewTask(String name, String details, Category category, Date date) {
        Task newTask = new Task();
        newTask.setName(name);
        newTask.setDetails(details);
        newTask.setCategory(category);
        newTask.setDate(date);

        TaskStorage.getInstance().addTask(newTask); // Dodaje zadanie do TaskStorage i powiadamia obserwatorów
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> tasks;

        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            Task task = tasks.get(position);
            CheckBox checkBox = holder.getCheckBox();

            checkBox.setChecked(task.isDone());

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setDone(isChecked);
                TaskStorage.getInstance().updateTasksLiveData(); // Powiadomienie obserwatorów
            });

            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView dateTextView;
        private Task task;
        private ImageView iconImageView;
        private CheckBox taskCheckBox;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            nameTextView = itemView.findViewById(R.id.task_item_name);
            dateTextView = itemView.findViewById(R.id.task_item_date);
            iconImageView = itemView.findViewById(R.id.category_icon);
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
            itemView.setOnClickListener(this);
        }

        public void bind(Task task) {
            this.task = task;
            nameTextView.setText(task.getName());
            dateTextView.setText(task.getDate().toString());

            if (task.getCategory().equals(Category.HOME)) {
                iconImageView.setImageResource(R.drawable.ic_house);
            } else if (task.getCategory().equals(Category.STUDIES)) {
                iconImageView.setImageResource(R.drawable.ic_study);
            }

            if (task.isDone()) {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        public CheckBox getCheckBox() {
            return taskCheckBox;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("KEY_EXTRA_TASK_ID", task.getId());
            startActivity(intent);
        }
    }

    public void updateSubtitle() {
        List<Task> tasks = TaskStorage.getInstance().getTasksList();

        int todoTasksCount = 0;
        for (Task task : tasks) {
            if (!task.isDone()) {
                todoTasksCount++;
            }
        }

        String subtitle = getString(R.string.subtitle_format, todoTasksCount);
        if (!subtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null && appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setSubtitle(subtitleVisible ? subtitle : null);
        }
    }


    private void setupObservers() {
        TaskStorage.getInstance().getTasksLiveData().observe(getViewLifecycleOwner(), tasks -> {
            if (adapter == null) {
                adapter = new TaskAdapter(tasks);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            updateSubtitle();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("subtitleVisible", subtitleVisible);
    }
}
