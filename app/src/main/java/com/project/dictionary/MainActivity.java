package com.project.dictionary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText searchEt;
    private Button searchBtn;
    private LinearLayout ll;
    private HawkUtil hu;
    private ProgressBar pb;
    private TextView response_tv;

    private ArrayList<DictionaryData> dataSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSearched = new ArrayList<>();

        initView();
    }

    private void initView() {
        Hawk.init(this).build();

        searchEt = findViewById(R.id.srchv);
        searchBtn = findViewById(R.id.btn_search);
        pb = findViewById(R.id.progressBar);
        ll = findViewById(R.id.ll);
        response_tv = findViewById(R.id.tv_response);
        response_tv.setText("Search the data in tab and press SEARCH button");

        pb.setVisibility(View.GONE);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String wordToBeSearched = searchEt.getText().toString();
                if (wordToBeSearched.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter the word.", Toast.LENGTH_SHORT).show();

//                    def.setText("");
                } else {
                    hideKeyboard();
                    callApiToSearch(wordToBeSearched.toLowerCase());
                }
            }
        });
    }

    private void callApiToSearch(final String word) {
        response_tv.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://owlbot.info/api/v2/dictionary/" + word;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        pb.setVisibility(View.GONE);

                        ll.removeAllViews();

                        dataSearched = new Gson().fromJson(response, new TypeToken<ArrayList<DictionaryData>>() {
                        }.getType());

                        if (dataSearched.isEmpty()) {
                            response_tv.setVisibility(View.VISIBLE);
                            response_tv.setText("No data found for " + word);
                        }

                        for (DictionaryData d : dataSearched) {
                            addToLayout(d, word);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.setVisibility(View.GONE);
                response_tv.setVisibility(View.VISIBLE);
                response_tv.setText("Couldn't get the data from server. Try again later.");
            }
        });

        queue.add(stringRequest);
    }


    private void addToLayout(final DictionaryData data, String word_) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dictionary_layout_resource, null);

        TextView def, type, example, word;
        final ImageView star;

        word = view.findViewById(R.id.tv_word);
        def = view.findViewById(R.id.tv_def);
        type = view.findViewById(R.id.tv_type);
        example = view.findViewById(R.id.tv_example);
        star = view.findViewById(R.id.iv_star);

        word.setText(word_);
        data.word = word_;

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
                    star.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_star_filled));
                } else {
                    hu.remove(data);
                    toast("Removed from fav list");
                    star.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_star_empty));
                }
            }
        });

        if (hu.has(data)) {
            data.isFavorite = true;
            star.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_filled));
        } else {
            data.isFavorite = false;
            star.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_empty));
        }

        ll.addView(view);
    }

    @Override
    protected void onPause() {
        hu.overwriteList();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ll.removeAllViews();
        response_tv.setText("Search the data in tab and press SEARCH button");
        pb.setVisibility(View.GONE);
        hu = new HawkUtil(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.favorites) {
            Intent i = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
