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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.movies.models.Example;
import com.example.hp.movies.models.MovieModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final String rootURL="http://api.themoviedb.org/3/movie/"; //base url for movies
    private static final String imageURL="https://image.tmdb.org/t/p/w780"; //base url for for images
    private ListView listV;
    private List<MovieModel> movieModelList;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Title");
        categories.add("Rating");
        categories.add("Year");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        //set drop down view for list of categories
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        listV=(ListView)findViewById(R.id.listView);
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
     pos=position;
        getMovies();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
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
            {  // loading.dismiss();  //if successful in fetching the json, stop the progress dialog
                movieModelList=movieModels.getResults(); //get movie model object
                switch(pos)
                {
                    case 0:
                        Collections.sort(movieModelList,MovieModel.titleComparator);
                        break;
                    case 1:Collections.sort(movieModelList,MovieModel.ratingComparator);
                        break;
                    case 2:Collections.sort(movieModelList,MovieModel.dateComparator);
                        break;

                }


                showList();
            }

            @Override
            public void failure(RetrofitError error) {
                //unable to fetch json from server
                Toast.makeText(MainActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                //Log.e("error retro",error.toString());
            }
        });

    }

   private void showList()
   {
       MovieAdapter adapter=new MovieAdapter(MainActivity.this,R.layout.row,movieModelList);
       listV.setAdapter(adapter);
   }
   //adapter to set rows in listview

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
            TextView ratingText=(TextView)convertView.findViewById(R.id.rating_text);
            ImageLoader.getInstance().displayImage(imageURL+movieModelList.get(position).getPosterPath(), imageView); //first parameter is image url

            title.setText(movieModelList.get(position).getTitle());

            year.setText("Released on  :" + movieModelList.get(position).getReleaseDate());

             ratingText.setText(movieModelList.get(position).getVoteAverage().toString());

            ratingBar.setRating( movieModelList.get(position).getVoteAverage());
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
