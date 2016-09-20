package com.simpledeveloper.averse.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.simpledeveloper.averse.R;
import com.simpledeveloper.averse.db.Poem;
import com.simpledeveloper.averse.ui.Author;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class AuthorAdapter extends RecyclerView.Adapter<AuthorViewHolder>{

    private List<Author> authors;
    private Context mContext;

    public AuthorAdapter(List<Author> authors, Context context) {
        this.authors = authors;
        this.mContext = context;
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_card_layout, parent, false);

        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {
        holder.mAuthor.setText(authors.get(position).getAuthor());

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Poem> poems =  realm.where(Poem.class)
                .equalTo("author", authors.get(position).getAuthor())
                .findAllSorted("id");

        TextDrawable drawable = TextDrawable.builder()
                .buildRound((!poems.isEmpty() ? ""+poems.size() : "0"), ContextCompat.getColor(mContext,
                        R.color.colorDivider));
        holder.mTotal.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    public void remove(int position){
        authors.remove(position);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setModels(List<Author> questionList){
        authors = new ArrayList<>(questionList);
    }

    public void animateTo(List<Author> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Author> newModels) {
        for (int i = authors.size() - 1; i >= 0; i--) {
            final Author model = authors.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Author> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Author model = newModels.get(i);
            if (!authors.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Author> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Author model = newModels.get(toPosition);
            final int fromPosition = authors.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private Author removeItem(int position) {
        final Author model = authors.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, Author model) {
        authors.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Author model = authors.remove(fromPosition);
        authors.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public Author getItem(int position){
        return authors.get(position);
    }
}
