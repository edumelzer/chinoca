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
        
		mClientesList = new ArrayList<EntregaList>();
		
		mListaClientes = (ListView)findViewById(R.id.lvClientes);
		
		EntregaList entrega = new EntregaList();
        
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
			        motistaDb = new Motorista(
			        		idMotorista,
			        		idMotorista,
			        		nomeMotorista, 
			        		o.get("placa").toString(), 
			        		formatter.format(Calendar.getInstance().getTime()) );
			        
			        //
			        firstLoop = false;
			        //Long id, String nome, String placa, String dh_sincronismo
			        //
			        motoristasDao.insert(motistaDb);
				}
				
				//Popula Adapter
				entrega = new EntregaList();
				entrega.setFantasia(o.get("fantasia").toString());
				entrega.setBairro(  o.get("bairro").toString());
				entrega.setNumero(  Integer.parseInt(o.get("numero").toString()));
				entrega.setEndereco(o.get("logradouro").toString());
				entrega.setCidade(  o.get("cidade").toString());
				entrega.setCliente( o.get("razao_social").toString());
				mClientesList.add(entrega);
				
				//
				Long idCliente = Long.parseLong(o.get("id_cliente").toString());
				
				System.out.println("id cliente Long:"+idCliente);
				System.out.println("id cliente String:"+o.get("id_cliente").toString());
				
				//Cria cliente
				clienteDb = new Cliente(
						 idCliente
						,idCliente
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
				
				List<Cliente> clientesTest = clientesDao.queryBuilder()
				        .where(ClienteDao.Properties.Id_web.in(idCliente)).list();
				
				if(clientesTest.isEmpty()){
					clientesDao.insert(clienteDb);
				}
				
				/*Cliente clienteTest = clientesDao.load(idCliente);
				if(clienteTest.getId() != idCliente){
					System.out.println("Nome do cliente:"+clienteTest.getRazao_social());
					clientesDao.insertOrReplace(clienteDb);
				}*/
				
				
				//Criar entregas
				entregaDb = new Entrega(
						 Long.parseLong(o.get("id_entrega").toString())
						,Long.parseLong(o.get("id_entrega").toString())
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

		mEntregasOrdenadas = EntregaList.ordenarEntrega(mClientesList);
		
		EntregasAdapter entregaAdapter = new EntregasAdapter(getBaseContext(), mEntregasOrdenadas);
		mListaClientes.setAdapter(entregaAdapter);
		mListaClientes.setOnItemClickListener(this);
		mListaClientes.setOnItemLongClickListener(this);
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
