package br.feevale.applogistica;

import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import br.feevale.applogistica.adapter.ClienteList;
import br.feevale.applogistica.adapter.EntregasAdapter;
import br.feevale.applogistica.database.orm.Cliente;
import br.feevale.applogistica.database.orm.ClienteDao;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.Entrega;
import br.feevale.applogistica.database.orm.EntregaDao;
import br.feevale.applogistica.database.orm.MotoristaDao;
import br.feevale.applogistica.database.orm.Motorista;
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
    private Cliente clienteDb;
    private Entrega entregaDb;
    private Motorista motistaDb;
    private Long idMotorista;
    private String nomeMotorista;
    
    private EditText editText;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private EntregaDao entregasDao;
    private ClienteDao  clientesDao;
    private MotoristaDao  motoristasDao;

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
        entregasDao = daoSession.getEntregaDao();
        clientesDao = daoSession.getClienteDao();
        motoristasDao = daoSession.getMotoristaDao();
        
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
        
        boolean firstLoop = true;
        
		for (String s : strs) {
		    //System.out.println(s);     
			try {
				JSONObject o = new JSONObject(s);
				
				//Insere motorista
				if(firstLoop){
			        idMotorista = Long.parseLong(String.valueOf((extras.getInt("id"))));
			        nomeMotorista = String.valueOf((extras.getInt("nome")));
			        
			        Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			        //String s = formatter.format(date);
			        //System.out.println("Data atual:"+formatter.format(Calendar.getInstance().getTime()));
			        motistaDb = new Motorista(idMotorista, nomeMotorista, o.get("placa").toString(), formatter.format(Calendar.getInstance().getTime()) );
			        
			        //
			        firstLoop = false;
			        //Long id, String nome, String placa, String dh_sincronismo
			        motoristasDao.insertWithoutSettingPk(motistaDb);
				}
				
				//Popula Adapter
				cliente = new ClienteList();
				cliente.setFantasia(o.get("fantasia").toString());
				cliente.setBairro(  o.get("bairro").toString());
				cliente.setNumero(  Integer.parseInt(o.get("numero").toString()));
				cliente.setEndereco(o.get("logradouro").toString());
				cliente.setCidade(  o.get("cidade").toString());
				cliente.setCliente( o.get("razao_social").toString());
				mClientesList.add(cliente);
				
				//
				Long idCliente = Long.parseLong(o.get("id_cliente").toString());
				
				System.out.println("id cliente Long:"+idCliente);
				System.out.println("id cliente String:"+o.get("id_cliente").toString());
				
				//Cria cliente
				clienteDb = new Clientes(
						idCliente
						,o.get("razao_social").toString()
						,o.get("fantasia").toString()
						,o.get("logradouro").toString()
						,Integer.parseInt(o.get("numero").toString())
						,o.get("complemento").toString()
						,o.get("bairro").toString()
						,o.get("cidade").toString()
						,o.get("uf").toString()
						,o.get("cep").toString()
						, Long.getLong(o.get("latitude").toString())
						, Long.getLong(o.get("longitude").toString())
						);
				
				//A tabela precisa ser um cliente para ser inserida...
				Clientes clienteTest = clientesDao.load(idCliente);
				if(clienteTest.getId() != idCliente){
					System.out.println("Nome do cliente:"+clienteTest.getRazao_social());
					clientesDao.insertOrReplace(clienteDb);
				}
				
				//Criar entregas
				entregaDb = new Entregas(
						 Long.parseLong(o.get("id_entrega").toString())
						,idCliente
						,idMotorista
						,Integer.parseInt(o.get("ordem").toString())
						,Integer.parseInt(o.get("volumes").toString())
						,o.get("dh_maxima").toString()
						,o.get("gln").toString()
						,o.get("melhor_rota").toString()
						,o.get("nome_contato").toString()
						,o.get("telefone").toString()
						,""
						,""
						,""
						);
				
				entregasDao.insertOrReplace(entregaDb);
				
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
