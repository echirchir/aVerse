package com.simpledeveloper.averse.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.simpledeveloper.averse.R;


public class AuthorViewHolder extends RecyclerView.ViewHolder {

    TextView mAuthor;
    ImageView mTotal;

    public AuthorViewHolder(View itemView) {
        super(itemView);

        mAuthor = (TextView) itemView.findViewById(R.id.author);
        mTotal = (ImageView) itemView.findViewById(R.id.total);
    }
}
