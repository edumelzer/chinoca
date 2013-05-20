package br.feevale.applogistica;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class LoginViewActivity extends Activity {

	public static final String PREFS_NAME = "xincaPreferencias";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_view);
		
		TextView nomeMotorista = (TextView) findViewById(R.id.nome_motorista);
		TextView idMotorista = (TextView) findViewById(R.id.id_motorista);
		
		Bundle extras = getIntent().getExtras();
		
		nomeMotorista.setText(nomeMotorista.getText().toString() + ": "+ extras.getString("nome"));
		idMotorista.setText(idMotorista.getText().toString() + ": "+ extras.getInt("id"));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_view, menu);
		return true;
	}

}
