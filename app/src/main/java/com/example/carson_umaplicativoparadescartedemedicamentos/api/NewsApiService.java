package com.example.carson_umaplicativoparadescartedemedicamentos.api;

import com.example.carson_umaplicativoparadescartedemedicamentos.models.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/everything")
    Call<NewsResponse> getNews(
            @Query("q") String query,
            @Query("domains") String domains,
            @Query("from") String fromDate, // 🆕 NOVO PARÂMETRO
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );
}