package br.feevale.applogistica;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.demo.notepad3.R;

import br.feevale.applogistica.LoginActivity.UserLoginTask;
import br.feevale.applogistica.adapter.ProdutoAdapter;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoMaster.DevOpenHelper;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.Entrega;
import br.feevale.applogistica.database.orm.Produto;
import br.feevale.applogistica.database.orm.ProdutoDao;
import br.feevale.applogistica.database.orm.EntregaDao;
import br.feevale.applogistica.webservice.ConsumerService;
import br.feevale.applogistica.webservice.WebService;

import android.R.bool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ProdutosEntregaActivity extends Activity implements OnItemClickListener, android.widget.CompoundButton.OnCheckedChangeListener {
	
    static{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	private ListView mLvProdutosEntrega;
	ProdutoAdapter produtoAdapter;
	private List<Produto> mListaProdutos;
    private SQLiteDatabase db;
    private DevOpenHelper helper;
    private Integer idEntrega;
    List<Produto> produtosList;
    private List<Integer> clicados = new ArrayList<Integer>();
    
    
    public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ProdutoDao produtoDao;
    private EntregaDao entregaDao;
    private Entrega mEntrega;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produtos_entrega);
		
		
		Bundle extras = getIntent().getExtras();
		idEntrega = Integer.valueOf(extras.getString("entregaId"));
		//Iniciar banco de dados...
		iniciaDataBase();
		
		mLvProdutosEntrega = (ListView)findViewById(R.id.lvProdutosEntrega);
        	mEntrega = entregaDao.load(Long.parseLong(extras.getString("entregaId")));
        	
		mListaProdutos = new ArrayList<Produto>();
		
		produtoDao.queryBuilder().LOG_SQL = true;
		
		produtosList = produtoDao.queryBuilder()
				.where(ProdutoDao.Properties.Id_entrega.in(idEntrega)).list();
		
		for(Produto prod : produtosList){
			mListaProdutos.add(prod);
		}
		
		produtoAdapter = new ProdutoAdapter(getBaseContext(), mListaProdutos);
		mLvProdutosEntrega.setAdapter(produtoAdapter);		
		
	}

	private void iniciaDataBase(){
		
		helper = new DaoMaster.DevOpenHelper(this, "applogistica-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		produtoDao = daoSession.getProdutoDao();
		entregaDao = daoSession.getEntregaDao();
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		int countClick = clicados.size();
		if(countClick == mListaProdutos.size()){
			getMenuInflater().inflate(R.menu.activity_produtos_entrega, menu);
			super.onCreateOptionsMenu(menu);
			//chamar método para sincronizar
	        //menu.add(0, INSERT_ID, 0, "Sincronizar");
		}else{
			
			Toast.makeText(getApplicationContext(), "faltam produtos a serem entregues", Toast.LENGTH_SHORT);
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		System.out.println("Item Clicado");
		CheckBox chk = (CheckBox) arg1;
        if(chk.isChecked()) {
        	clicados.add(1);
        }
		Toast.makeText(getApplicationContext(), mListaProdutos.get(arg2).getDescricao(), Toast.LENGTH_SHORT);
		
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:

			Toast.makeText(ProdutosEntregaActivity.this, "Efetivando dados da entrega...", Toast.LENGTH_SHORT).show();

			try{
				int qtdProdutosEntregues = ConsumerService.getInstance().registroEntrega(idEntrega.toString());
				Format formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				String dhEntrega = formatter.format(Calendar.getInstance().getTime());
				mEntrega.setDh_entrega(dhEntrega);
				mEntrega.setDh_sincronismo(dhEntrega);
				entregaDao.update(mEntrega);
				
				String message = qtdProdutosEntregues + " produtos foram entregues com sucesso!";
				Toast.makeText(ProdutosEntregaActivity.this, message, Toast.LENGTH_SHORT).show();
			}catch(JSONException j){
				System.out.println("Erro ao comunicar com o webservice: "+j.getMessage());
				Toast.makeText(ProdutosEntregaActivity.this, "NÃƒÂ£o foi possÃƒÂ­vel salvar a entrega!", Toast.LENGTH_SHORT).show();
			}
			
			return true;
		case R.id.chkAll:
			
			for ( int i=0; i< produtoAdapter.getCount(); i++ ) {
				mLvProdutosEntrega.setItemChecked(i, true);
			}
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         System.out.println("Button: "+buttonView.getId());
         
    }
	
}
