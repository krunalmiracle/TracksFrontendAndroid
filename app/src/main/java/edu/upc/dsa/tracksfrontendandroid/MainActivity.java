package edu.upc.dsa.tracksfrontendandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Implementing RecyclerView
        recyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(false);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            input.add("Test" + i);
        }// define an adapter
        mAdapter = new MyAdapter(input);
        recyclerView.setAdapter(mAdapter);


    }
    public void onButtonClick(View view) {
        //Retrofit Implementation on Button Press
        Button button1 = findViewById(R.id.button);
        final TextView textView = findViewById(R.id.textView);
        Toast toast= Toast.makeText(MainActivity.this,"Button Pressed!",Toast.LENGTH_SHORT);
        toast.show();
        textView.setText("Button Pressed!");
        //Retrofit Implementation
        //Adding Interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        // Running Retrofit to get result from Github service Interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        Call<List<Repo>> repos = service.listRepos("krunalmiracle");
        /* Android Doesn't allow synchronous execution of Http Request and so we have to change this*/
        repos.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                //textView.setText("Completed!");
                Toast toast= Toast.makeText(MainActivity.this,"Response Ok",Toast.LENGTH_SHORT);
                toast.show();
                String res_str = "";
                List<Repo> result = response.body();
                assert result != null;
                for (Repo r:result) {
                    res_str = r.toString() + "\n" + res_str;
                }
                textView.setText(res_str);
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
                Toast toast= Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
