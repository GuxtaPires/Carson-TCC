package com.example.carson_umaplicativoparadescartedemedicamentos.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Certifique-se de ter o Glide no build.gradle
import com.example.carson_umaplicativoparadescartedemedicamentos.R;
import com.example.carson_umaplicativoparadescartedemedicamentos.models.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private List<Article> articles;

    public NewsAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);

        // 1. Título
        holder.txtTitle.setText(article.getTitle());

        // 2. Descrição (Evita mostrar vazio)
        if (article.getDescription() != null && !article.getDescription().isEmpty()) {
            holder.txtDesc.setText(article.getDescription());
            holder.txtDesc.setVisibility(View.VISIBLE);
        } else {
            holder.txtDesc.setVisibility(View.GONE);
        }

        // 3. Data Formatada (Ex: 2023-11-21 -> 21/11/2023)
        holder.txtDate.setText(formatarData(article.getPublishedAt()));

        // 4. Imagem (Usando Glide)
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            holder.imgNews.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.ic_logo) // Imagem enquanto carrega
                    .error(R.drawable.ic_logo)       // Imagem se der erro
                    .centerCrop()
                    .into(holder.imgNews);
        } else {
            holder.imgNews.setVisibility(View.GONE);
        }

        // 5. Clique para abrir no navegador
        holder.itemView.setOnClickListener(v -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                context.startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    // Formata a data feia da API para o padrão BR
    private String formatarData(String dataApi) {
        if (dataApi == null) return "";
        try {
            // Formato que vem da API: 2023-11-21T10:30:00Z
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dataApi);

            // Formato que queremos: 21/11/2023
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return ""; // Retorna vazio se der erro
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, txtDate;
        ImageView imgNews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitleNews);
            txtDesc = itemView.findViewById(R.id.txtDescNews);
            txtDate = itemView.findViewById(R.id.txtDateNews);
            imgNews = itemView.findViewById(R.id.imgNews);
        }
    }
}