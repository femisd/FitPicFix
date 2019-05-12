package com.example.offlinemaps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowersSearch extends AppCompatActivity {
    //fields for nav view.
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialisation of fields.
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_search_friends);
        setupDrawerContent(mNavView);

        Button search = (Button) findViewById(R.id.bt_search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateSearchResults();
            }
        });
    }

    public void populateSearchResults() {
        EditText searchField = (EditText) findViewById(R.id.et_search_user);
        final ListView displayResults = (ListView) findViewById(R.id.lv_search_results);
        final ArrayList<User> userResults = new ArrayList<>();
        final FriendAdapterClass friendsAdapter = new FriendAdapterClass(this, userResults);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        final String query = searchField.getText().toString();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsAdapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getmUsername().equalsIgnoreCase(query)) {
                        Log.d("SEARCHABLE", user.getmUsername());
                        friendsAdapter.add(user);
                        friendsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FollowersSearch.this, "No users found with that name, try again", Toast.LENGTH_SHORT).show();
                    }
                }
                displayResults.setAdapter(friendsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        displayResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = userResults.get(position);
                Intent viewFriend = new Intent(FollowersSearch.this, ViewFriend.class);
                viewFriend.putExtra("user", user);
                startActivity(viewFriend);
            }
        });

    }

    /**
     * Setup the navigation drawer.
     *
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    /**
     * Select an item from menu and perform an action.
     *
     * @param menuItem
     */
    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_leaderboard:
                //Go to leader board activity.
                Intent leaderboard = new Intent(FollowersSearch.this, Leaderboard.class);
                startActivity(leaderboard);
                finish();
                break;
            case R.id.nav_logout:
                //Go to main activity.
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                finish();
                break;
            case R.id.nav_profile:
                Intent profile = new Intent(FollowersSearch.this, ProfileUI.class);
                startActivity(profile);
                finish();
                break;
        }
        menuItem.setChecked(true);
        mDrawer.closeDrawers();
    }

}
