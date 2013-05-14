package br.feevale.applogistica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.feevale.applogistica.adapter.ProdutoAdapter;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoMaster.DevOpenHelper;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.Produto;
import br.feevale.applogistica.database.orm.ProdutoDao;
import br.feevale.applogistica.webservice.WebService;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProdutosEntregaActivity extends Activity implements OnItemClickListener{
	
	private ListView mLvProdutosEntrega;
	private List<Produto> mListaProdutos;
    private SQLiteDatabase db;
    private DevOpenHelper helper;

    public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ProdutoDao produtoDao;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produtos_entrega);
		
		
		Bundle extras = getIntent().getExtras();
		
		//Iniciar banco de dados...
		iniciaDataBase();

		mLvProdutosEntrega = (ListView)findViewById(R.id.lvProdutosEntrega);
        
		mListaProdutos = new ArrayList<Produto>();

		
		Produto prod = new Produto();
		prod.setDescricao("TELEVISAO SAMSUNG 32 POLEG.");
		mListaProdutos.add(prod);
		
		prod = new Produto();
		prod.setDescricao("NOTEBOOK POSITIVO");
		mListaProdutos.add(prod);
		
		prod = new Produto();
		prod.setDescricao("TABLET SAMSUNG NOTE");
		mListaProdutos.add(prod);
		
		prod = new Produto();
		prod.setDescricao("MESA PRIOR");
		mListaProdutos.add(prod);
		
		ProdutoAdapter produtoAdapter = new ProdutoAdapter(getBaseContext(), mListaProdutos);
		mLvProdutosEntrega.setAdapter(produtoAdapter);
		
	}

	private void iniciaDataBase(){
		
		helper = new DaoMaster.DevOpenHelper(this, "applogistica-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		produtoDao = daoSession.getProdutoDao();
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_produtos_entrega, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(getApplicationContext(), mListaProdutos.get(arg2).getDescricao(), Toast.LENGTH_SHORT);
		
	}
	
}
