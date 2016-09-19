package com.simpledeveloper.averse.helpers;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.simpledeveloper.averse.R;

public class Utils {

    public static void showSnackBar(Context context, View layout, int resId){
        Snackbar snackbar = Snackbar
                .make(layout, context.getResources().getString(resId), Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void prettyPrintJson(String completeEntity) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(completeEntity);
        String prettyJsonString = gson.toJson(je);

        Log.d("PRETTYPRINTJSON", "THE JSON: " + prettyJsonString);
    }

}
