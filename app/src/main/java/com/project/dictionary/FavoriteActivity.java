package com.project.dictionary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private LinearLayout ll;
    private TextView tv;
    private HawkUtil hu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ll = findViewById(R.id.ll_fav);
        tv = findViewById(R.id.tv_no_data);


    }

    private void addToLayout(final DictionaryData data, String word_) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dictionary_layout_resource, null);

        TextView def, type, example, word;
        final ImageView star;

        word = view.findViewById(R.id.tv_word);
        def = view.findViewById(R.id.tv_def);
        type = view.findViewById(R.id.tv_type);
        example = view.findViewById(R.id.tv_example);
        star = view.findViewById(R.id.iv_star);

        word.setText(word_);

        def.setText(data.definition == null ? "" : data.definition);
        type.setText(data.type == null ? "" : data.type);
        example.setText(data.example == null ? "" : data.example);

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.isFavorite = !data.isFavorite;
                if (data.isFavorite) {
                    hu.add(data);
                    toast("Added to fav list");
                    star.setImageDrawable(ContextCompat.getDrawable(FavoriteActivity.this, R.drawable.ic_star_filled));
                } else {
                    hu.remove(data);
                    if (hu.isEmpty())
                        tv.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    toast("Removed from fav list");
                    star.setImageDrawable(ContextCompat.getDrawable(FavoriteActivity.this, R.drawable.ic_star_empty));
                }
            }
        });

        data.isFavorite = true;
        star.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_filled));

        ll.addView(view);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hu = new HawkUtil(this);
        ArrayList<DictionaryData> temp = hu.getAll();

        tv.setVisibility(View.VISIBLE);

        for (DictionaryData t : temp) {
            tv.setVisibility(View.GONE);
            addToLayout(t, t.word);
        }
    }

    @Override
    protected void onPause() {
        hu.overwriteList();
        super.onPause();
    }
}
