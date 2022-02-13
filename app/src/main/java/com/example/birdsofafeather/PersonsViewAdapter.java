package com.example.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.IPerson;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PersonsViewAdapter extends RecyclerView.Adapter<PersonsViewAdapter.ViewHolder> {
    private final List<? extends IPerson> persons;

    public PersonsViewAdapter(List<? extends IPerson> persons) {
        super();
        this.persons = persons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.person_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPerson(persons.get(position));
    }

    @Override
    public int getItemCount() {
        return this.persons.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final TextView personNameView;
        private final ImageView imageView;
        private IPerson person;

        ViewHolder(View itemView) {
            super(itemView);
            this.personNameView = itemView.findViewById(R.id.person_row_name);
            //constructor on the ViewHolder
            this.imageView = itemView.findViewById(R.id.person_row_photo);
            itemView.setOnClickListener(this);
        }

        public void setPerson(IPerson person) {
            this.person = person;
            this.personNameView.setText(person.getName());
            //set up photo
            Picasso.get().load(person.getPhoto()).into(imageView);
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