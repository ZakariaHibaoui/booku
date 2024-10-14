package ma.ac.uit.ensa.ssi.Booku.BookAPI;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("volumes")
    Call<BookResponse> getBooks(@Query("q") String query);
}
