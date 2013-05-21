package br.feevale.applogistica;

import java.util.HashMap;
import java.util.Map;

import br.feevale.applogistica.webservice.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class LoginActivity extends Activity {

	public static final String PREFS_NAME = "xincaPreferencias";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_ID = "id_motorista";
	boolean logarUltimoUsuario = false;
	
	static{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public static final String EXTRA_USUARIO = "com.example.android.authenticatordemo.extra.EMAIL";
	public static final String URL_AUTH = "https://online.viamarte.com.br/projetoandroid/auth/";
	public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";

	private UserLoginTask mAuthTask = null;

	// Valores para email e password no momento da solicitação de login.
	private String mUsuario;
	private String mPassword;

	// UI references.
	private EditText mUsuarioView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// Retornados do Webservice
	private int    idMotorista;
	private String nomeMotorista;
	private String dados;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_loging);
		
		// Set up login form.
		mUsuario = getIntent().getStringExtra(EXTRA_USUARIO);
		mUsuarioView = (EditText) findViewById(R.id.usuario);
		mUsuarioView.setText(mUsuario);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {						
						attemptLogin();
					}
				});
		
		SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
		if(pref.contains(PREF_ID)){
			
			String username   = pref.getString(PREF_USERNAME, null);
			String password   = pref.getString(PREF_PASSWORD, null);
			idMotorista = Integer.parseInt(pref.getString(PREF_ID, null));
			
			if (username != null && password != null && String.valueOf(idMotorista) != null) {
				dialogLogin(username);
			}
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsuarioView.setError(null);
		mPasswordView.setError(null);

		mUsuario = mUsuarioView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Checar por um password valido.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Checar por um username valido.
		if (TextUtils.isEmpty(mUsuario)) {
			mUsuarioView.setError(getString(R.string.error_field_required));
			focusView = mUsuarioView;
			cancel = true;
		} else if (mUsuario.length() < 4) {
			mUsuarioView.setError(getString(R.string.error_invalid_username));
			focusView = mUsuarioView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Exibe a UI de progresso e esconde o login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... parameters) {
	
			if(isCancelled()) return null;
			
			try {

				String response = "";
				
				Map<String, String> params = new HashMap<String, String>();
				
				response = ConsumerService.getInstance().login(mUsuario, mPassword);
				JSONObject o    = new JSONObject(response);
				int retorno      = Integer.parseInt(o.get("retorno").toString());

				//Senha invalida:
				if(retorno < 0){
					return false;
				}
				
				idMotorista   = Integer.parseInt(o.get("id_motorista").toString());
				nomeMotorista = o.get("nome").toString();
			    
				getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
			        .edit()
			        .putString(PREF_USERNAME, mUsuario)
			        .putString(PREF_PASSWORD, mPassword)
			        .putString(PREF_ID, o.get("id_motorista").toString())
			        .commit();
				
			    dados = ConsumerService.getInstance().buscaDadosEntregas(String.valueOf(idMotorista));
			    
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {

				dialogAtualizacao();
				
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	public void dialogLogin(String username){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		
			// set title
			alertDialogBuilder.setTitle("Atualizações");
			
			// set dialog message
			alertDialogBuilder
				.setMessage("O último usuário logado foi "+username+", deseja logar com este mesmo usuário?")
				.setCancelable(false)
				.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						logarUltimoUsuario = true;
						Intent i = new Intent( getApplication() , EntregasActivity.class );
						//i.putExtra("atualiza", false);
						dados = ConsumerService.getInstance().buscaDadosEntregas(String.valueOf(idMotorista));
					    i.putExtra("id", idMotorista);
					    i.putExtra("dados", dados);
					    //finish();
					    startActivity(i);
					}
				  })
				.setNegativeButton("Não",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						logarUltimoUsuario = false;
						dialog.cancel();
					}
				});
 
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				alertDialog.show();

	}
	
	public void dialogAtualizacao(){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
			// set title
			alertDialogBuilder.setTitle("Atualizações");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Deseja atualizar os dados da web?")
				.setCancelable(false)
				.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						getApplicationContext().deleteDatabase("applogistica.db");
						//Intent i = new Intent( getBaseContext() , LoginViewActivity.class );
						Intent i = new Intent( getApplication() , EntregasActivity.class );
						
					    	i.putExtra("id", idMotorista);
					    	i.putExtra("nome", nomeMotorista);
					    	i.putExtra("dados", dados);
					    	i.putExtra("atualiza", true);
					    	startActivity(i);
					}
				  })
				.setNegativeButton("Não",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						//Intent i = new Intent( getBaseContext() , LoginViewActivity.class );
						Intent i = new Intent( getApplication() , EntregasActivity.class );
							
					    	i.putExtra("id", idMotorista);
					    	i.putExtra("nome", nomeMotorista);
					    	i.putExtra("dados", dados);
					    	i.putExtra("atualiza", false);
					    	startActivity(i);
						dialog.cancel();
					}
				});
 
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				alertDialog.show();

	}
	
}
