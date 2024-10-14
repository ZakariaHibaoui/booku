package ma.ac.uit.ensa.ssi.Booku.BookAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/";
    private static API retrofit = null;

    public static API getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(API.class);
        }
        return retrofit;
    }
}