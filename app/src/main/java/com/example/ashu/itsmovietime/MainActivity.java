package com.example.ashu.itsmovietime;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private static ArrayList<MovieItem> mGridData;
    boolean InternetStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView=findViewById(R.id.gridView);
        makeMovieQuery();

       }

    private void makeMovieQuery() {
        //mHeadingDisplay.setText("Now Playing");
       // isFilterOn=false;
        mGridData = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(this, R.layout.single_movie_item, mGridData);
        mGridView.setAdapter(mMovieAdapter);

        URL movieSearchUrl = NetworkUtility.buildUrl();
        new MovieQueryTask().execute(movieSearchUrl);
    }

    public static void showErrorMessage() {
        mGridView.setVisibility(View.INVISIBLE);
       /* mHeadingDisplay.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);*/
    }

    public void showJsonData() {
        //mHeadingDisplay.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.VISIBLE);
        //mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }
    public  boolean checkInternet(){
        boolean internetStatus=true;
        ConnectivityManager connec=(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if(connec.getNetworkInfo(0).getState()== android.net.NetworkInfo.State.CONNECTED||connec.getNetworkInfo(1).getState()==android.net.NetworkInfo.State.CONNECTED
                ||connec.getNetworkInfo(0).getState()== NetworkInfo.State.CONNECTING||connec.getNetworkInfo(1).getState()== NetworkInfo.State.CONNECTING)
            internetStatus=true;
        else if(connec.getNetworkInfo(0).getState()== NetworkInfo.State.DISCONNECTED||connec.getNetworkInfo(1).getState()== NetworkInfo.State.DISCONNECTED)
            internetStatus=false;

        return internetStatus;
    }
    private class MovieQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieSearchResults = null;
            try {
                InternetStatus=checkInternet();
                if(InternetStatus==true)
                    movieSearchResults = NetworkUtility.getResponseFromHttpUrl(searchUrl);
                else movieSearchResults=null;
            } catch (Exception e) {
                e.printStackTrace();
                movieSearchResults=null;
            }
            MoviesJson.getJsonData(movieSearchResults);
            return movieSearchResults;
        }

        @Override
        protected void onPostExecute(String movieSearchResults) {
          //  mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieSearchResults != null && !movieSearchResults.equals("")) {
                showJsonData();
                mMovieAdapter.setGridData(mGridData);

            } else {
                showErrorMessage();
            }
        }
    }
}
