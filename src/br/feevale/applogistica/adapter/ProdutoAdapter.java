package br.feevale.applogistica.adapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.feevale.applogistica.R;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.Produto;
import br.feevale.applogistica.database.orm.ProdutoDao;
import br.feevale.applogistica.database.orm.DaoMaster.DevOpenHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ProdutoDao produtoDao;
    private SQLiteDatabase db;
    private DevOpenHelper helper;
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.item_produtos_entrega, null);
		
		TextView tvProduto = (TextView)convertView.findViewById(R.id.tvProduto);
		final CheckBox ckbProduto = (CheckBox)convertView.findViewById(R.id.cbProduto);
		
		if(mListaProdutos.get(position).getDh_leitura() != null && !mListaProdutos.get(position).getDh_leitura().equals("")){
			ckbProduto.setChecked(true);
		}
		
		ckbProduto.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View arg0)  {
	            //
	        	if(ckbProduto.isChecked()){
	        		Format formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	        		String dhEntrega = formatter.format(Calendar.getInstance().getTime());
	        		mListaProdutos.get(position).setDh_leitura(dhEntrega);
	        		iniciaDataBase();
	        		produtoDao.update(mListaProdutos.get(position));
	        	}

	        }
	    });
		
		tvProduto.setText(mListaProdutos.get(position).getDescricao());
		
		return convertView;
	}
	
	private void iniciaDataBase(){
		
		helper = new DaoMaster.DevOpenHelper(mContext, "applogistica-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		produtoDao = daoSession.getProdutoDao();
        
	}
	
}