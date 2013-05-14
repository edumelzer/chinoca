package br.feevale.applogistica.adapter;


import java.util.Date;
import java.util.List;

import br.feevale.applogistica.DetalhesEntregaActivity;
import br.feevale.applogistica.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class EntregasAdapter extends BaseAdapter {
	private List<EntregaList> mListEntregas;
	private Context mContext;
	
	public EntregasAdapter(Context context, List<EntregaList> listaEntregas) {
		this.mContext = context;
		this.mListEntregas = listaEntregas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListEntregas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mListEntregas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mListEntregas.get(position).getIdEntrega();
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.item_lista_entrega, null);
		
		TextView tvEndereco     = (TextView)view.findViewById(R.id.tvEndereco);
		TextView tvCliente      = (TextView)view.findViewById(R.id.tvCliente);
		TextView tvMelhorRota   = (TextView)view.findViewById(R.id.tvMelhorRota);
		//TextView tvProssimidade = (TextView)view.findViewById(R.id.tvProssimidade);
		View barra              = (View)view.findViewById(R.id.barra);
		
		
		
		String strEndereco = mListEntregas.get(position).getEndereco() + ", " + mListEntregas.get(position).getNumero() + ", Bairro " + mListEntregas.get(position).getBairro() + ", " + mListEntregas.get(position).getCidade();
		
		tvEndereco.setText(strEndereco);
		tvCliente.setText(mListEntregas.get(position).getCliente());
		tvMelhorRota.setText("Melhor rota: " + mListEntregas.get(position).getMelhor_rota());
		barra.setVisibility(View.INVISIBLE);
		
		Date d = new Date();
		String[] data_hora = mListEntregas.get(position).getDh_maxima().split(" "); 
		String[] hora = data_hora[1].split(":");
		int horas = Integer.parseInt(hora[0]);
		int minutos = Integer.parseInt(hora[1]);
		int horaAtual = d.getHours();
		int minutoAtual = d.getMinutes();
		//horaAtual = 20;
		//minutoAtual = 40;

		if(mListEntregas.get(position).getDh_entrega() == null){
			//tvProssimidade.setText("hora =" + horaAtual + ":" + minutoAtual);
			if((horas < horaAtual) || ((horas == horaAtual) && (minutos < minutoAtual))){
				tvCliente.setBackgroundColor(Color.RED);
			}
			else if(horas == horaAtual && minutos >= minutoAtual){
				int calculaMinutos = minutos - minutoAtual;
				if(calculaMinutos <= 30){
					tvCliente.setBackgroundColor(Color.YELLOW);
				}
				else{
					tvCliente.setBackgroundColor(Color.GREEN);
				}
			}
			else{
				int calculaMinutos = 30 - minutos;
				int proximaHora = horaAtual + 1;
				if(horas ==proximaHora && minutos <= calculaMinutos){
					tvCliente.setBackgroundColor(Color.YELLOW);
				}
				else{
					tvCliente.setBackgroundColor(Color.GREEN);
				}
			}
		}
		else if(mListEntregas.get(position).getDh_entrega() != null){
			//view.setBackgroundColor(@+d);
			barra.setVisibility(View.VISIBLE);
		}
		
		return view;
	}

}
