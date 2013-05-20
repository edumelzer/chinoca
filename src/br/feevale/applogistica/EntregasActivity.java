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
import br.feevale.applogistica.database.orm.Produto;
import br.feevale.applogistica.database.orm.ProdutoDao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class EntregasActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{
	
	private List<EntregaList> mClientesList, mEntregasOrdenadas;
	private ListView mListaClientes;
	private TextView mTvPlaca;
	private SQLiteDatabase db;
	private Cliente clienteDb;
	private Entrega entregaDb;
	private Produto produtoDb;
	private EntregaList entrega;
	private Motorista motistaDb;
	private Long idMotorista;
	private String nomeMotorista;
	private DevOpenHelper helper;
	private Bundle extras;
	private JSONObject job;
	boolean atualiza = true;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private EntregaDao entregasDao;
	private ClienteDao  clientesDao;
	private MotoristaDao  motoristasDao;
	private ProdutoDao produtoDao;

    //private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entregas);
		
		extras = getIntent().getExtras();
		idMotorista = Long.parseLong(String.valueOf((extras.getInt("id"))));
		nomeMotorista = String.valueOf((extras.getString("nome")));
		
		//Iniciar banco de dados...
		iniciaDataBase();
        
		mClientesList = new ArrayList<EntregaList>();
		
		mListaClientes = (ListView)findViewById(R.id.lvClientes);
		mTvPlaca       = (TextView)findViewById(R.id.tvPlaca);
		
		entrega = new EntregaList();
		
		//dialogAtualizacao();
		
		if(atualiza){
				
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
					
					//Criar clientes
					criarCliente();
					
					//Criar entregas
					criarEntrega();
					
					//Criar Produtos
					criarProduto();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//Popula Adapter
			populaAdapterEntregaLocal();
			
		}else{
			populaAdapterEntregaLocal();
		}

		mEntregasOrdenadas = EntregaList.ordenarEntrega(mClientesList);
		
		mTvPlaca.setText("Placa:" + motistaDb.getPlaca().toString());
		
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
		produtoDao = daoSession.getProdutoDao();
	}
	
	private void populaInfoMotorista() throws JSONException{
        
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
		entrega.setIdEntrega(Long.valueOf(job.get("id_entrega").toString()));
		
		mClientesList.add(entrega);
		
	}
	
	private void populaAdapterEntregaLocal(){
		//Popula adapter do banco:
		List<Entrega> entregas = entregasDao.loadAll();

		for(Entrega ent : entregas){
			
			System.out.println("Ent: " + ent.getId()+" id_web:"+ent.getId_web());
			
			Cliente cli = clientesDao.load(ent.getId_cliente());

			entrega = new EntregaList();
			entrega.setFantasia(cli.getFantasia());
			entrega.setBairro(  cli.getBairro());
			entrega.setNumero(  cli.getNumero());
			entrega.setEndereco(cli.getLogradouro());
			entrega.setCidade(  cli.getCidade());
			entrega.setCliente( cli.getRazao_social());
			entrega.setDh_maxima(ent.getDh_maxima());
			entrega.setMelhor_rota(ent.getMelhor_rota());
			entrega.setIdEntrega(ent.getId_web());
			mClientesList.add(entrega);
		}
	}
	
	private void criarCliente() throws JSONException{
		
		Long idCliente = Long.parseLong(job.get("id_cliente").toString());
		
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
		
	}
	
	private void criarEntrega() throws JSONException{
		
		entregaDb = new Entrega(
				 Long.valueOf(job.get("id_entrega").toString())
				,Long.valueOf(job.get("id_entrega").toString())
				,Long.valueOf(job.get("id_cliente").toString())
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
		        .where(EntregaDao.Properties.Id_web.in(job.get("id_entrega").toString())).list();
		
		if(entregasTest.isEmpty()){
			entregasDao.insert(entregaDb);
		}
		
	}
	
	private void criarProduto() throws JSONException{

		produtoDb = new Produto(
				 Long.valueOf(job.get("id_produto").toString())
				,Long.valueOf(job.get("id_produto").toString())
				,Integer.valueOf(job.get("id_entrega").toString())
				,job.get("descricao").toString()
				,job.get("especie").toString()
				,Long.valueOf(job.get("valor").toString())
				,job.get("sscc").toString()
				,""
				,""
				);

		List<Produto> produtosTest = produtoDao.queryBuilder()
		        .where(ProdutoDao.Properties.Id_web.in(
		        		Long.valueOf(job.get("id_produto").toString()))
		        		).list();
		
		if(produtosTest.isEmpty()){
			produtoDao.insert(produtoDb);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.entregas, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Intent intent = new Intent(getBaseContext(), ProdutosEntregaActivity.class);
		intent.putExtra("entregaId", mEntregasOrdenadas.get(arg2).getIdEntrega().toString());
		startActivity(intent);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Intent intent = new Intent(getBaseContext(), DetalhesEntregaActivity.class);
		Bundle params = new Bundle();
		params.putLong("entregaId", mEntregasOrdenadas.get(arg2).getIdEntrega());
		intent.putExtras(params);
		startActivity(intent);
		return true;
		
	}

	public void dialogAtualizacao(){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getBaseContext());
 
			// set title
			alertDialogBuilder.setTitle("Atualizações");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Deseja atualizar os dados da web?")
				.setCancelable(false)
				.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						atualiza = true;
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						atualiza = false;
						dialog.cancel();
					}
				});
 
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				alertDialog.show();

	}
	
}
