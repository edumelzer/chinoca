package br.feevale.applogistica;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import br.feevale.applogistica.adapter.ClienteList;
import br.feevale.applogistica.adapter.EntregasAdapter;
import br.feevale.applogistica.database.orm.Clientes;
import br.feevale.applogistica.database.orm.ClientesDao;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.EntregasDao;
import br.feevale.applogistica.database.orm.DaoMaster.DevOpenHelper;
import br.feevale.applogistica.webservice.ConsumerService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class EntregasActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{
	private List<ClienteList> mClientesList;
	private ListView mListaClientes;
    private SQLiteDatabase db;
    private Clientes clientes;
    
    private EditText editText;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private EntregasDao entregasDao;
    private ClientesDao  clientesDao;

    private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entregas);
		
		
		Bundle extras = getIntent().getExtras();
		
		//Iniciar banco de dados...
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
		db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        entregasDao = daoSession.getEntregasDao();
        clientesDao = daoSession.getClientesDao();
        
        /* Testes Eduardo;
         * String idColumn = ProdutosDao.Properties.Id.columnName;
        String orderBy = idColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(produtoDao.getTablename(), produtoDao.getAllColumns(), null, null, null, null, orderBy);
        String[] from = { idColumn, ProdutosDao.Properties.Descricao.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };
		*/
        
        /* TESTE STRING JSON
		String str = "{id:\"123\",name:\"myName\"},{id:\"456\",name:\"yetanotherName\"},{id:\"456\",name:\"anotherName\"}";
		String[] strs = str.split("(?<=\\},)(?=\\{)");
		for (String s : strs) {
		    System.out.println(s);     
			try {
				JSONObject o = new JSONObject(s);
				//int retorno      = Integer.parseInt(o.get("retorno").toString());
				System.out.println(o.get("name").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
        
		mClientesList = new ArrayList<ClienteList>();
		
		mListaClientes = (ListView)findViewById(R.id.lvClientes);
		
		ClienteList cliente = new ClienteList();
        
        String dados = extras.getString("dados").replaceAll("\\[|\\]", "");
        String[] strs = dados.split("(?<=\\},)(?=\\{)");
		for (String s : strs) {
		    //System.out.println(s);     
			try {
				JSONObject o = new JSONObject(s);
				cliente = new ClienteList();
				cliente.setFantasia(o.get("fantasia").toString());
				cliente.setBairro(  o.get("bairro").toString());
				cliente.setNumero(  Integer.parseInt(o.get("numero").toString()));
				cliente.setEndereco(o.get("logradouro").toString());
				cliente.setCidade(  o.get("cidade").toString());
				cliente.setCliente( o.get("razao_social").toString());
				mClientesList.add(cliente);
				
				//Cria cliente
				clientes = new Clientes(
						Long.getLong(o.get("id_cliente").toString())
						,o.get("razao_social").toString()
						,o.get("fantasia").toString()
						,o.get("logradouro").toString()
						,Integer.parseInt(o.get("numero").toString())
						,o.get("complemento").toString()
						,o.get("bairro").toString()
						,o.get("cidade").toString()
						,o.get("uf").toString()
						,o.get("cep").toString()
						,Long.getLong(o.get("latitude").toString())
						,Long.getLong(o.get("longitude").toString())
						);
				
				//A tabela precisa ser um cliente para ser inserida...
				clientesDao.insertOrReplace(clientes);
				
				System.out.println(o.get("placa").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/* Valores ficticios para teste:
		cliente = new ClienteList();
		cliente.setFantasia("tiririca");
		cliente.setBairro("canudos");
		cliente.setNumero(231);
		cliente.setEndereco("Rua Icaro");
		cliente.setCidade("Novo Hamburgo");
		cliente.setCliente("Cliente A");
		mClientesList.add(cliente);
		
		cliente = new ClienteList();
		cliente.setFantasia("erica");
		cliente.setBairro("centro");
		cliente.setNumero(2317);
		cliente.setEndereco("Rua mauricio cardoso");
		cliente.setCidade("Novo Hamburgo");
		cliente.setCliente("Cliente B");
		mClientesList.add(cliente);
		
		cliente = new ClienteList();
		cliente.setFantasia("pedro");
		cliente.setBairro("canudos");
		cliente.setNumero(89);
		cliente.setEndereco("Rua bartolomeu");
		cliente.setCidade("Novo Hamburgo");
		cliente.setCliente("Cliente C");
		mClientesList.add(cliente);
		*/
		EntregasAdapter entregaAdapter = new EntregasAdapter(getBaseContext(), mClientesList);
		mListaClientes.setAdapter(entregaAdapter);
		mListaClientes.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entregas, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getBaseContext(), ProdutosEntregaActivity.class);
		Bundle params = new Bundle();
		params.putLong("entregaId", 1);
		intent.putExtras(params);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
