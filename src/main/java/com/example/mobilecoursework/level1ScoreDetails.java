package com.example.mobilecoursework;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.example.mobilecoursework.R.id.playerInfo;


public class level1ScoreDetails extends Fragment {

    ListView listView;

    public level1ScoreDetails() {

        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level1_score_details, container, false);
        listView=view.findViewById(R.id.listView1);
        progressBar = view.findViewById(R.id.progressBar2);
        loadData();
        return view;
    }

    ArrayList<String> playerList;
    ArrayList<Player> playerListSorted;
    List<Player> newList;
    ArrayAdapter<String> adapter;
    Player player;

    ProgressBar progressBar;

    /**
     * retrieve all player details and populate the UI with player name & Level 1 score.
     */
    public void loadData(){
        progressBar.setVisibility(View.VISIBLE);

        playerListSorted = new ArrayList<>();
        player = new Player();
        playerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), R.layout.player_layout, playerInfo,playerList);

        //database reference
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference().child("Scores");
        dbf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    player = ds.getValue(Player.class);
                    if(!(player.getLev1score()==0 )) {
                        playerListSorted.add(player);
                    }
                    //Sort descending
                    playerListSorted= sortList(playerListSorted);
                }
                for (Player i : playerListSorted) {
                    playerList.add(Long.toString(i.getLev1score()) + " - " + i.getUserName().toString());

                }

                listView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }

    //bubble sort
    public ArrayList sortList(ArrayList playerList){
        Player tempVar;

        for (int i = 0; i < playerList.size()-1; i++)
        {
            for(int j = 0; j < playerList.size()-i-1; j++)
            {
                if((int)((Player)playerList.get(j)).getLev1score() < (int)((Player) playerList.get(j + 1)).getLev1score())
                {
                    tempVar = (Player) playerList.get(j + 1);
                    playerList.set(j + 1,playerList.get(j));
                    playerList.set(j,tempVar);
                }
            }
        }
        return playerList;
    }

}
