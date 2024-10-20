package com.example.mainactivity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class TaskListActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list); // Ustawiamy istniejący układ fragmentu

        // do zarzadzania fragmentami
        FragmentManager fragmentManager = getSupportFragmentManager();


        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {

            fragment = new TaskListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected Fragment createFragment() {
        return new TaskListFragment();
    }
}