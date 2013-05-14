package br.feevale.applogistica;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import br.feevale.applogistica.adapter.EntregaList;
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

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class EntregasActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{
	
	private List<EntregaList> mClientesList, mEntregasOrdenadas;
	private ListView mListaClientes;
	private SQLiteDatabase db;
	private Cliente clienteDb;
	private Entrega entregaDb;
	private EntregaList entrega;
	private Motorista motistaDb;
	private Long idMotorista;
	private String nomeMotorista;
	private DevOpenHelper helper;
	private Bundle extras;
	private JSONObject job;
	
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
		iniciaDataBase();
        
		mClientesList = new ArrayList<EntregaList>();
		
		mListaClientes = (ListView)findViewById(R.id.lvClientes);
		
		entrega = new EntregaList();
        
        String dados = extras.getString("dados").replaceAll("\\[|\\]", "");
        String[] strs = dados.split("(?<=\\},)(?=\\{)");
        
        boolean firstLoop = true;
        
		for (String s : strs) {

			try {
				job = new JSONObject(s);
				
				//Insere motorista
				if(firstLoop){
					populaInfoMotorista();
			        firstLoop = false;
				}
				
				//Popula Adapter
				populaAdapterEntrega();				

				Long idCliente = Long.parseLong(job.get("id_cliente").toString());
				
				System.out.println("id cliente Long:"+idCliente);
				System.out.println("id cliente String:"+job.get("id_cliente").toString());
				
				//Cria cliente
				clienteDb = new Cliente(
						 idCliente
						,idCliente
						,job.get("razao_social").toString()
						,job.get("fantasia").toString()
						,job.get("logradouro").toString()
						,Integer.parseInt(job.get("numero").toString())
						,job.get("complemento").toString()
						,job.get("bairro").toString()
						,job.get("cidade").toString()
						,job.get("uf").toString()
						,job.get("cep").toString()
						,Long.getLong(job.get("latitude").toString())
						,Long.getLong(job.get("longitude").toString())
						);
				
				List<Cliente> clientesTest = clientesDao.queryBuilder()
				        .where(ClienteDao.Properties.Id_web.in(idCliente)).list();
				
				if(clientesTest.isEmpty()){
					clientesDao.insert(clienteDb);
				}
				
				//Criar entregas
				entregaDb = new Entrega(
						 Long.parseLong(job.get("id_entrega").toString())
						,Long.parseLong(job.get("id_entrega").toString())
						,idCliente
						,idMotorista
						,Integer.parseInt(job.get("ordem").toString())
						,Integer.parseInt(job.get("volumes").toString())
						,job.get("dh_maxima").toString()
						,job.get("gln").toString()
						,job.get("melhor_rota").toString()
						,job.get("nome_contato").toString()
						,job.get("telefone").toString()
						,""
						,""
						,""
						);
				
				List<Entrega> entregasTest = entregasDao.queryBuilder()
				        .where(EntregaDao.Properties.Id_web.in(Long.parseLong(job.get("id_entrega").toString()))).list();
				
				if(entregasTest.isEmpty()){
					entregasDao.insert(entregaDb);
				}
				
				System.out.println(job.get("placa").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mEntregasOrdenadas = EntregaList.ordenarEntrega(mClientesList);
		
		EntregasAdapter entregaAdapter = new EntregasAdapter(getBaseContext(), mEntregasOrdenadas);
		mListaClientes.setAdapter(entregaAdapter);
		mListaClientes.setOnItemClickListener(this);
		mListaClientes.setOnItemLongClickListener(this);
	}

	private void iniciaDataBase(){
		
		helper = new DaoMaster.DevOpenHelper(this, "applogistica-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		entregasDao = daoSession.getEntregaDao();
		clientesDao = daoSession.getClienteDao();
		motoristasDao = daoSession.getMotoristaDao();
        
	}
	
	private void populaInfoMotorista() throws JSONException{
		
		idMotorista = Long.parseLong(String.valueOf((extras.getInt("id"))));
		nomeMotorista = String.valueOf((extras.getInt("nome")));
        
		Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		motistaDb = new Motorista(
				idMotorista,
				idMotorista,
				nomeMotorista, 
				job.get("placa").toString(), 
				formatter.format(Calendar.getInstance().getTime()) );

		List<Motorista> motoristasTest = motoristasDao.queryBuilder()
		        .where(MotoristaDao.Properties.Id_web.in(idMotorista)).list();
		
		if(motoristasTest.isEmpty()){
	        motoristasDao.insert(motistaDb);
		}
	}
	
	private void populaAdapterEntrega() throws JSONException{
		
		entrega = new EntregaList();
		entrega.setFantasia(job.get("fantasia").toString());
		entrega.setBairro(  job.get("bairro").toString());
		entrega.setNumero(  Integer.parseInt(job.get("numero").toString()));
		entrega.setEndereco(job.get("logradouro").toString());
		entrega.setCidade(  job.get("cidade").toString());
		entrega.setCliente( job.get("razao_social").toString());
		entrega.setDh_maxima(job.get("dh_maxima").toString());
		entrega.setMelhor_rota(job.get("melhor_rota").toString());
		
		mClientesList.add(entrega);
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
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getBaseContext(), DetalhesEntregaActivity.class);
		Bundle params = new Bundle();
		params.putLong("entregaId", 1);
		intent.putExtras(params);
		startActivity(intent);
		return true;
	}

}
