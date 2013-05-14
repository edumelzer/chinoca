package br.feevale.applogistica;

import br.feevale.applogistica.database.orm.Cliente;
import br.feevale.applogistica.database.orm.Entrega;
import android.os.Bundle;
import android.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalhesEntregaActivity extends Activity {
	private TextView tvClient, tvAddress, tvResponsable, tvPhone, tvDhMax, tvRota, tvOrder, tvVolume, tvGln;
	private ImageView imgDoc;
	private long mEntregaId;
	private Entrega mEntrega;
	private Cliente mCliente;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_entrega);
		
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		mEntregaId    = params.getLong("entregaId");
		
		startData();
		startComponents();
	}

	private void startData() {
		mCliente = new Cliente();
		mCliente.setFantasia("Lojas Colombo");
		mCliente.setBairro("canudos");
		mCliente.setNumero(89);
		mCliente.setLogradouro("Rua bartolomeu");
		mCliente.setCidade("Novo Hamburgo");
		
		mEntrega = new Entrega();
		mEntrega.setDh_maxima("12/06/2013 15:00");
		mEntrega.setOrdem(132);
		mEntrega.setVolumes(7);
		mEntrega.setGln("w132 f423");
		mEntrega.setNome_contato("Guilherme Finotti");
		mEntrega.setMelhor_rota("rua a, dobre em...............");
		mEntrega.setTelefone("(51) 7568-0789");
		
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
		String address = mCliente.getLogradouro() + ", " + mCliente.getNumero() + ", bairro " + mCliente.getBairro() + ", " + mCliente.getCidade() + ", CEP " + mCliente.getCep();
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