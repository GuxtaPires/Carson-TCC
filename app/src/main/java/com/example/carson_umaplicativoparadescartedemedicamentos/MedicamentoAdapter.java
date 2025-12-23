package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carson_umaplicativoparadescartedemedicamentos.controller.MedicamentoController;
import com.example.carson_umaplicativoparadescartedemedicamentos.model.Medicamento;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {

    private List<Medicamento> lista;
    private final Context context;
    private final MedicamentoController controller;

    public MedicamentoAdapter(List<Medicamento> lista, Context context, MedicamentoController controller) {
        this.lista = lista;
        this.context = context;
        this.controller = controller;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamento m = lista.get(position);

        holder.txtNome.setText("Nome: " + m.getNome());
        holder.txtSituacao.setText("Situação: " + m.getSituacao());
        holder.txtValidade.setText("Validade: " + m.getValidade());

        // ============================================================
        // 🟢 BOTÃO DESCARTE (AGORA REMOVE DA TELA TAMBÉM)
        // ============================================================
        holder.btnDescarte.setOnClickListener(v -> {

            // 1. Incrementa a estatística GLOBAL (+1 descarte realizado)
            DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference("estatisticas");
            statsRef.child("totalDescartes").setValue(ServerValue.increment(1));

            // 2. Remove do Banco de Dados do usuário (para não voltar quando abrir o app de novo)
            // Estou assumindo que "descartar" retira o item da posse do usuário.
            controller.excluirMedicamento(m.getUserId(), m.getId());

            // 3. Remove VISUALMENTE da lista na hora
            int posicaoAtual = holder.getAdapterPosition();
            if (posicaoAtual != RecyclerView.NO_POSITION) {
                lista.remove(posicaoAtual);
                notifyItemRemoved(posicaoAtual);
                notifyItemRangeChanged(posicaoAtual, lista.size()); // Ajusta os índices restantes
            }

            Toast.makeText(context, "Medicamento descartado! ♻️", Toast.LENGTH_SHORT).show();
        });

        // ============================================================
        // 🔴 BOTÃO EXCLUIR (APENAS REMOVE)
        // ============================================================
        holder.btnExcluir.setOnClickListener(v -> {
            controller.excluirMedicamento(m.getUserId(), m.getId());

            int posicaoAtual = holder.getAdapterPosition();
            if (posicaoAtual != RecyclerView.NO_POSITION) {
                lista.remove(posicaoAtual);
                notifyItemRemoved(posicaoAtual);
                notifyItemRangeChanged(posicaoAtual, lista.size());
            }

            Toast.makeText(context, "Medicamento excluído.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void atualizarLista(List<Medicamento> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtSituacao, txtValidade;
        Button btnDescarte, btnExcluir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtSituacao = itemView.findViewById(R.id.txtSituacao);
            txtValidade = itemView.findViewById(R.id.txtValidade);
            btnDescarte = itemView.findViewById(R.id.btnDescarte);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}