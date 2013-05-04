package br.feevale.applogistica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.feevale.applogistica.adapter.ProdutoAdapter;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoMaster.DevOpenHelper;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.Produtos;
import br.feevale.applogistica.database.orm.ProdutosDao;
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
	private List<Produtos> mListaProdutos;
    private SQLiteDatabase db;

    public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";
    
    private EditText editText;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ProdutosDao produtoDao;

    private Cursor cursor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produtos_entrega);
		
		mLvProdutosEntrega = (ListView)findViewById(R.id.lvProdutosEntrega);
		
		Bundle extras = getIntent().getExtras();
		
		//Iniciar banco de dados...
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
		db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        produtoDao = daoSession.getProdutosDao();
        
        /* Testes Eduardo;
         * String idColumn = ProdutosDao.Properties.Id.columnName;
        String orderBy = idColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(produtoDao.getTablename(), produtoDao.getAllColumns(), null, null, null, null, orderBy);
        String[] from = { idColumn, ProdutosDao.Properties.Descricao.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };
		*/
        
        //Busca produtos do webservice
		WebService webService = new WebService(URL_DADOS);
		String response = "";
		
		Map<String, String> params = new HashMap<String, String>();
        params.put("u", extras.getString("id"));

        response = webService.webGet("", params);
        
		mListaProdutos = new ArrayList<Produtos>();

		String str = "{id:\"123\",name:\"myName\"}{id:\"456\",name:\"yetanotherName\"}{id:\"456\",name:\"anotherName\"}";
		String[] strs = str.split("(?<=\\})(?=\\{)");
		for (String s : strs) {
		    System.out.println(s);          
		}
		
		
		Produtos prod = new Produtos();
		prod.setDescricao("TELEVISAO SAMSUNG 32 POLEG.");
		mListaProdutos.add(prod);
		
		prod = new Produtos();
		prod.setDescricao("NOTEBOOK POSITIVO");
		mListaProdutos.add(prod);
		
		prod = new Produtos();
		prod.setDescricao("TABLET SAMSUNG NOTE");
		mListaProdutos.add(prod);
		
		prod = new Produtos();
		prod.setDescricao("MESA PRIOR");
		mListaProdutos.add(prod);
		
		ProdutoAdapter produtoAdapter = new ProdutoAdapter(getBaseContext(), mListaProdutos);
		mLvProdutosEntrega.setAdapter(produtoAdapter);
		
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
