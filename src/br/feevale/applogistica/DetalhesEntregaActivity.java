package br.feevale.applogistica;

import java.util.List;

import br.feevale.applogistica.database.orm.Cliente;
import br.feevale.applogistica.database.orm.ClienteDao;
import br.feevale.applogistica.database.orm.DaoMaster;
import br.feevale.applogistica.database.orm.DaoSession;
import br.feevale.applogistica.database.orm.Entrega;
import br.feevale.applogistica.database.orm.EntregaDao;
import br.feevale.applogistica.database.orm.MotoristaDao;
import br.feevale.applogistica.database.orm.ProdutoDao;
import br.feevale.applogistica.database.orm.DaoMaster.DevOpenHelper;
import android.os.Bundle;
import android.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalhesEntregaActivity extends Activity {
	private TextView tvClient, tvAddress, tvResponsable, tvPhone, tvDhMax, tvRota, tvOrder, tvVolume, tvGln;
	private ImageView imgDoc;
	private Long mEntregaId;
	private Entrega mEntrega;
	private Cliente mCliente;
	private EntregaDao entregaDao;
	List<Entrega> entregaList;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private EntregaDao entregasDao;
	private ClienteDao  clientesDao;
	private MotoristaDao  motoristasDao;
	private ProdutoDao produtoDao;
	private SQLiteDatabase db;
	private DevOpenHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_entrega);
		
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		mEntregaId     = params.getLong("entregaId");
		
		//entregaList = entregaDao.queryBuilder()
		//		.where(EntregaDao.Properties.Id_web.in(mEntregaId)).list();
		
		iniciaDataBase();
		startComponents();
	}


	private void iniciaDataBase(){
		helper = new DaoMaster.DevOpenHelper(this, "applogistica-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		entregasDao = daoSession.getEntregaDao();
		clientesDao = daoSession.getClienteDao();
		
		mEntrega = entregasDao.load(mEntregaId);
		mCliente = clientesDao.load(mEntrega.getId_cliente());
	}
	

	private void startComponents() {
		tvClient       = (TextView)findViewById(R.id.tvClient);
		tvAddress      = (TextView)findViewById(R.id.tvAddress);
		tvResponsable  = (TextView)findViewById(R.id.tvResponsible);
		tvPhone        = (TextView)findViewById(R.id.tvPhone);
		tvDhMax        = (TextView)findViewById(R.id.tvDhMax);
		tvRota         = (TextView)findViewById(R.id.tvMelhorRota);
		tvOrder        = (TextView)findViewById(R.id.tvOrder);
		tvVolume       = (TextView)findViewById(R.id.tvVolume);
		tvGln          = (TextView)findViewById(R.id.tvGln);
		imgDoc         = (ImageView)findViewById(R.id.imgDoc);
		
		tvClient.setText(mCliente.getFantasia());
		String address = mCliente.getLogradouro() + ", " + mCliente.getNumero() + ", bairro " + mCliente.getBairro() + ", " + mCliente.getCidade() + ", ";
		if(mCliente.getComplemento() != null)
			address += mCliente.getComplemento() + ", ";
		address += "CEP " + mCliente.getCep();
		tvAddress.setText(address);
		tvResponsable.setText(mEntrega.getNome_contato());
		tvPhone.setText(mEntrega.getTelefone());
		tvDhMax.setText(mEntrega.getDh_maxima());
		tvRota.setText(mEntrega.getMelhor_rota());
		tvOrder.setText(mEntrega.getOrdem().toString());
		tvVolume.setText(mEntrega.getVolumes().toString());
		tvGln.setText(mEntrega.getGln());
		
		if(mEntrega.getImagem_documento() == null){
			Bitmap bmp = BitmapFactory.decodeFile(mEntrega.getImagem_documento());
			imgDoc.setImageBitmap(bmp);		
		}
		else{
			imgDoc.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_detalhes_entrega, menu);
		return true;
	}

}
