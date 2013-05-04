package br.feevale.applogistica.adapter;

import java.util.List;

import br.feevale.applogistica.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EntregasAdapter extends BaseAdapter {
	private List<ClienteList> mListEntregas;
	private Context mContext;
	
	public EntregasAdapter(Context context, List<ClienteList> listaEntregas) {
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
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.item_lista_entrega, null);
		
		TextView tvEndereco = (TextView)view.findViewById(R.id.tvEndereco);
		TextView tvCliente = (TextView)view.findViewById(R.id.tvCliente);
		
		String strEndereco = mListEntregas.get(position).getEndereco() + ", " + mListEntregas.get(position).getNumero() + ", Bairro " + mListEntregas.get(position).getBairro() + ", " + mListEntregas.get(position).getCidade();
		
		tvEndereco.setText(strEndereco);
		tvCliente.setText(mListEntregas.get(position).getCliente());

		return view;
	}

}
