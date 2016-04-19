package com.example.hp.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.movies.models.Example;
import com.example.hp.movies.models.MovieModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    public static final String rootURL="http://api.themoviedb.org/3/movie/";
    private static final String imageURL="https://image.tmdb.org/t/p/w780";
    private ListView listV;
    private List<MovieModel> movieModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listV=(ListView)findViewById(R.id.listView);
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        getMovies();
    }
    private void getMovies()
    {
        //While the app fetched data we are displaying a progress dialog
        //final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        //Creating a rest adapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(rootURL)
                .build();
        MovieAPI api=adapter.create(MovieAPI.class);
        api.getMovies(new Callback<Example>() {

            @Override
            public void success(Example movieModels, Response response)
            {
                movieModelList=movieModels.getResults();
                showList();
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(MainActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                Log.e("error retro",error.toString());
            }
        });

    }

   private void showList()
   {
       MovieAdapter adapter=new MovieAdapter(MainActivity.this,R.layout.row,movieModelList);
       listV.setAdapter(adapter);
   }

    public class MovieAdapter extends ArrayAdapter {
        private List<MovieModel> movieModelList;
        private int resource;
        private LayoutInflater inflater;
        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView=inflater.inflate(resource,null);
            }
            ImageView imageView=(ImageView)convertView.findViewById(R.id.image);
            TextView title;
            title=(TextView)convertView.findViewById(R.id.title);
            TextView year=(TextView)convertView.findViewById(R.id.release_date);
            RatingBar ratingBar;
            ratingBar=(RatingBar)convertView.findViewById(R.id.ratingBar);
            ImageLoader.getInstance().displayImage(imageURL+movieModelList.get(position).getPosterPath(), imageView); //first parameter is image url

            title.setText(movieModelList.get(position).getTitle());

            year.setText("Released on  :" + movieModelList.get(position).getReleaseDate());



            ratingBar.setRating((float) movieModelList.get(position).getVoteAverage());
            return convertView;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
