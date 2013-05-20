package br.feevale.applogistica.adapter;

import java.util.List;

import br.feevale.applogistica.R;
import br.feevale.applogistica.database.orm.Produto;
import br.feevale.applogistica.database.orm.ProdutoDao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ProdutoAdapter extends BaseAdapter{
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
		final CheckBox ckbProduto = (CheckBox)convertView.findViewById(R.id.cbProduto);
		if(mListaProdutos.get(position).getDh_leitura() != null && !mListaProdutos.get(position).getDh_leitura().equals("")){
			ckbProduto.setChecked(true);
		}
		/* TODO
		ckbProduto.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View arg0)  {
	            //
	        	if(ckbProduto.isChecked()){
	        		Integer realPosition = (Integer) .getTag();
	                //ListedPuzzle obj = mListaProdutos.get(realPosition);
	                //starListedPuzzle(obj.getId(), isChecked);
	        		mListaProdutos.get(realPosition).setDh_leitura(dh_leitura);
	        	}

	        }
	    }); */
		
		tvProduto.setText(mListaProdutos.get(position).getDescricao());
		
		return convertView;
	}
	
}