package com.example.birdsofafeather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class SessionsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Session> sessions;
    private final AppDatabase db;

    public SessionsViewAdapter(List<Session> sessions, AppDatabase db) {
        super();
        this.sessions = sessions;
        this.db = db;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // Switch Case allows Favorites Session to not have "Session Rename Edit Button"
        switch (viewType) {
            // Favorites Session has viewType of 0
            case 0:
                // Favorites Session uses favorites_session_row layout (no edit button)
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.favorites_session_row, parent, false);
                return new FavoriteViewHolder(view);
            // All other Sessions
            default:
                // All other Sessions uses session_row layout (has edit button)
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.session_row, parent, false);
                return new ViewHolder(view, this::showRenameSessionDialog);
        }

    }

    @Override
    public int getItemViewType(int position){
        // Favorites Session will have viewType 0
        if(position == 0){
            return 0;
        }
        // All other Sessions will have viewType 1
        else{
            return 1;
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            // Favorite Session will always be position 0
            case 0:
                // Favorite Session uses FavoriteViewHolder
                FavoriteViewHolder favoriteViewHolder = (FavoriteViewHolder)holder;
                favoriteViewHolder.setSession(sessions.get(position));
                break;
            // All other Sessions
            default:
                // All other Sessions uses ViewHolder
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.setSession(sessions.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.sessions.size();
    }

    public List<Session> getSessions() {
        return sessions;
    }

    // AlertDialog pop-up when renaming a session
    public void showRenameSessionDialog(Context c, int position) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Rename Session")
                .setView(taskEditText)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(taskEditText.getText());
                        db.sessionsDao().updateSessionName(name, sessions.get(position).sessionId);
                        sessions.get(position).setSessionName(name);
                        SessionsViewAdapter.this.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    // All Sessions (except Favorites Session) ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView sessionTextView;
        private Session session;

        ViewHolder(View itemView, BiConsumer<Context, Integer> showRenameSessionDialog) {
            super(itemView);
            this.sessionTextView = itemView.findViewById(R.id.session_row_name);

            // "Session Rename Button" functionality
            FloatingActionButton renameFab = itemView.findViewById(R.id.rename_fab);
            itemView.setOnClickListener(this);

            renameFab.setOnClickListener((view) -> {
                showRenameSessionDialog.accept(view.getContext(), this.getAdapterPosition());
            });

        }

        public void setSession(Session session) {
            this.session = session;
            this.sessionTextView.setText(session.sessionName);
        }


        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PrevPersonListActivity.class);
            intent.putExtra("session_id", this.session.sessionId);
            context.startActivity(intent);
        }
    }

    // Favorites Session ViewHolder
    public static class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView sessionTextView;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            this.sessionTextView = itemView.findViewById(R.id.session_row_name);
            itemView.setOnClickListener(this);
        }

        public void setSession(Session session) {
            this.sessionTextView.setText(session.sessionName);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, FavoriteListActivity.class);
            context.startActivity(intent);
        }
    }
}