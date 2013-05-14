package br.feevale.applogistica.adapter;

import java.util.List;

import br.feevale.applogistica.R;
import br.feevale.applogistica.database.orm.Produto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ProdutoAdapter extends BaseAdapter {
	private Context mContext;
	private List<Produto> mListaProdutos;
	
	public ProdutoAdapter(Context context, List<Produto> listaProdutos){
		this.mContext = context;
		this.mListaProdutos = listaProdutos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListaProdutos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mListaProdutos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.item_produtos_entrega, null);
		
		TextView tvProduto = (TextView)convertView.findViewById(R.id.tvProduto);
		CheckBox ckbProduto = (CheckBox)convertView.findViewById(R.id.cbProduto);
		
		tvProduto.setText(mListaProdutos.get(position).getDescricao());
		
		if(mListaProdutos.get(position).getDh_leitura() != null){
			ckbProduto.setChecked(true);
		}
		
		return convertView;
	}

}