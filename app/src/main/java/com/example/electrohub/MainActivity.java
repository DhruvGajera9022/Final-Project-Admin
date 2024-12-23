package com.example.electrohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.electrohub.Fragments.CategoryFragment;
import com.example.electrohub.Fragments.HomeFragment;
import com.example.electrohub.Fragments.OrderFragment;
import com.example.electrohub.Fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bnv;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bnv = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Add_Product_Activity.class);
                startActivity(i);
                // overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
            }
        });

        set_Fragment(new HomeFragment(),0);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home)
                {
                    set_Fragment(new HomeFragment(),1);
                }
                if (id == R.id.category)
                {
                    set_Fragment(new CategoryFragment(),1);
                }
                if (id == R.id.order)
                {
                    set_Fragment(new OrderFragment(),1);
                }
                if (id == R.id.setting)
                {
                    set_Fragment(new SettingFragment(),1);
                }

                return true;
            }
        });

    }

    public void set_Fragment(Fragment fragment, int flag)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag == 0)
        {
            ft.add(R.id.main_container,fragment);
        }
        else
        {
            ft.replace(R.id.main_container,fragment);
        }
        ft.commit();
    }
}