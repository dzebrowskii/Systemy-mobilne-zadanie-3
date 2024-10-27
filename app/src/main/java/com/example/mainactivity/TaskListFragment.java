package com.example.mainactivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainactivity.R;


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

        // Inflatujemy widok z pliku fragment_task_list.xml
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Pobieramy odniesienie do RecyclerView z układu
        recyclerView = view.findViewById(R.id.task_recycler_view);

        // Ustawiamy LayoutManager dla RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateView();

        return view; // Zwracamy widok dla fragmentu
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { // po stworzeniu menu dodajemy menu do widoku
        super.onViewCreated(view, savedInstanceState);

        requireActivity().addMenuProvider(new MenuProvider() { //dodajemy opcje menu
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.fragment_task_menu, menu); // Ładowanie menu z pliku XML

                MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
                if (subtitleVisible) {
                    subtitleItem.setTitle(R.string.hide_subtitle);
                } else {
                    subtitleItem.setTitle(R.string.show_subtitle);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.new_task) {
                    // Obsługa tworzenia nowego zadania
                    Task task = new Task();
                    TaskStorage.getInstance().getTasks().add(task);
                    updateView();
                    return true;
                } else if (itemId == R.id.show_subtitle) {
                    // Wywołaj metodę do aktualizacji podtytułu
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




    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> tasks;



        // Konstruktor przyjmujący listę zadań
        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        // Tworzy nowy TaskHolder (dla każdego elementu listy)
        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        // Łączymy zadanie z widokami w TaskHolderze
        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            Task task = tasks.get(position);

            // Pobieramy referencję do CheckBox z TaskHoldera
            CheckBox checkBox = holder.getCheckBox();

            // Ustawiamy stan CheckBox zgodnie z wartością isDone dla danego zadania
            checkBox.setChecked(task.isDone());

            // Dodajemy listener, który ustawia stan zadania na podstawie zaznaczenia CheckBox
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setDone(isChecked);
                recyclerView.post(() -> adapter.notifyItemChanged(holder.getBindingAdapterPosition()));
            });


            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }


    }

    private void updateView() {
        // Pobranie instancji TaskStorage (singleton)
        TaskStorage taskStorage = TaskStorage.getInstance();
        // Pobranie listy zadań z TaskStorage
        List<Task> tasks = taskStorage.getTasks();

        if (adapter == null) {
            adapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        updateSubtitle(); // Aktualizacja podtytułu
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView dateTextView;
        private Task task;
        private ImageView iconImageView;
        private CheckBox taskCheckBox;

        // Konstruktor TaskHolder
        public TaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));

            // Inicjalizacja widoków
            nameTextView = itemView.findViewById(R.id.task_item_name);
            dateTextView = itemView.findViewById(R.id.task_item_date);
            iconImageView = itemView.findViewById(R.id.category_icon);
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);

            // Ustawienie nasłuchiwania kliknięć na cały element listy
            itemView.setOnClickListener(this);
        }

        // Metoda wiążąca dane zadania z widokami
        public void bind(Task task) {
            this.task = task;  // Przechowujemy zadanie
            nameTextView.setText(task.getName());  // Ustawiamy nazwę zadania w widoku
            dateTextView.setText(task.getDate().toString());  // Ustawiamy datę zadania w widoku


            // Ustawienie ikony w zależności od kategorii
            if (task.getCategory().equals(Category.HOME)) {
                iconImageView.setImageResource(R.drawable.ic_house);
            } else if (task.getCategory().equals(Category.STUDIES)) {
                iconImageView.setImageResource(R.drawable.ic_study);
            }

            // Ustawienie przekreślenia, jeśli zadanie jest wykonane
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
            // do uruchomienia main activity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("KEY_EXTRA_TASK_ID", task.getId());
            startActivity(intent);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        updateSubtitle(); // do aktualizowania zadan ktore jeszcze musimy wykonac (update subtitle)
        updateView();  // Zaktualizowanie widoku listy
    }

    public void updateSubtitle() {
        // Pobierz instancję TaskStorage
        TaskStorage taskStorage = TaskStorage.getInstance();
        // Pobierz listę wszystkich zadań
        List<Task> tasks = taskStorage.getTasks();

        // Zlicz niewykonane zadania
        int todoTasksCount = 0;
        for (Task task : tasks) {
            if (!task.isDone()) {
                todoTasksCount++;
            }
        }

        // Utwórz tekst podtytułu z formatem tekstu
        String subtitle = getString(R.string.subtitle_format, todoTasksCount);
        if (!subtitleVisible) {
            subtitle = null; // Ukrycie podtytułu
        }

        // Ustaw podtytuł na ActionBar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null && appCompatActivity.getSupportActionBar() != null) {
            if (subtitleVisible) {
                appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
            } else {
                appCompatActivity.getSupportActionBar().setSubtitle(null);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("subtitleVisible", subtitleVisible);
    }






}
