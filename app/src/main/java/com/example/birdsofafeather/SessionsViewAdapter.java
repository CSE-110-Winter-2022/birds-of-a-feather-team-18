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


public class SessionsViewAdapter extends RecyclerView.Adapter<SessionsViewAdapter.ViewHolder> {
    private final List<Session> sessions;
    private final AppDatabase db;

    public SessionsViewAdapter(List<Session> sessions, AppDatabase db) {
        super();
        this.sessions = sessions;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.session_row, parent, false);
        return new ViewHolder(view, this::showRenameSessionDialog);

    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setSession(sessions.get(position));

    }

    @Override
    public int getItemCount() {
        return this.sessions.size();
    }

    public List<Session> getSessions() {
        return sessions;
    }

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


    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final TextView sessionTextView;
        private Session session;

        ViewHolder(View itemView, BiConsumer<Context, Integer> showRenameSessionDialog) {
            super(itemView);
            this.sessionTextView = itemView.findViewById(R.id.session_row_name);
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
            Intent intent;
            if(session.sessionId.equals("favoritesSession")){
                intent = new Intent(context, FavoriteListActivity.class);
            }
            else {
                intent = new Intent(context, PrevPersonListActivity.class);
                intent.putExtra("session_id", this.session.sessionId);
            }
            context.startActivity(intent);
        }

    }
}