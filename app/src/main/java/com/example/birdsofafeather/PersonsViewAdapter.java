package com.example.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;

import java.util.List;


public class PersonsViewAdapter extends RecyclerView.Adapter<PersonsViewAdapter.ViewHolder> {
    private final List<? extends IPerson> persons;
    private final AppDatabase db;

    public PersonsViewAdapter(List<? extends IPerson> persons, AppDatabase db) {
        super();
        this.persons = persons;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.person_row, parent, false);

        return new ViewHolder(view, db);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPerson(persons.get(position));
    }

    @Override
    public int getItemCount() {
        return this.persons.size();
    }

    public List<? extends IPerson> getPersons() {
        return persons;
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final TextView personNameView;
        private final ImageView imageView;
        private final TextView commonCourseView;
        private final CheckBox listFavoriteStar;
        private final ImageView listWaveReceive;
        private IPerson person;

        ViewHolder(View itemView, AppDatabase db) {
            super(itemView);
            this.personNameView = itemView.findViewById(R.id.person_row_name);
            //constructor on the ViewHolder
            this.imageView = itemView.findViewById(R.id.person_row_photo);
            itemView.setOnClickListener(this);
            this.commonCourseView = itemView.findViewById(R.id.common_course_num);
            this.listFavoriteStar = itemView.findViewById(R.id.list_star);
            this.listWaveReceive = itemView.findViewById(R.id.list_wave);

            this.listFavoriteStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton favStar, boolean isChecked) {
                    if (favStar.isChecked()) {
                        db.personWithCoursesDao().updateFavorite(true, person.getId());
                    }
                    else
                    {
                        db.personWithCoursesDao().updateFavorite(false, person.getId());
                    }
                }
            });

        }

        public void setPerson(IPerson person) {
            this.person = person;
            this.personNameView.setText(person.getName());

            //set up photo
            Glide.with(imageView.getContext()).load(person.getPhoto()).into(imageView);
            Integer num = (Integer)person.getCourses().size();
            this.commonCourseView.setText(num.toString());
            this.listFavoriteStar.setChecked(person.getFavorite());
            //If there's a waving, set image visible
            //Set the to getWaveReceive
            if (person.getWavingToUs()) {
                this.listWaveReceive.setVisibility(View.VISIBLE);
            } else {
                this.listWaveReceive.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PersonDetailActivity.class);
            intent.putExtra("person_id", this.person.getId());
            context.startActivity(intent);
        }
    }
}