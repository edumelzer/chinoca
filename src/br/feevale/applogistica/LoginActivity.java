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

	
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"eduardo:swordfish", "admin:swordfish" };

	public static final String EXTRA_USUARIO = "com.example.android.authenticatordemo.extra.EMAIL";
	public static final String URL_AUTH = "https://online.viamarte.com.br/projetoandroid/auth/";
	public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";

	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
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
		
		SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
		String username   = pref.getString(PREF_USERNAME, null);
		String password   = pref.getString(PREF_PASSWORD, null);
		idMotorista = Integer.parseInt(pref.getString(PREF_ID, null));
		
		// Set up the login form.
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
		
		if (username != null && password != null && String.valueOf(idMotorista) != null) {
			
			dialogLogin(username);
			
			/*if(logarUltimoUsuario){
				Intent i = new Intent( getApplication() , EntregasActivity.class );
				dados = ConsumerService.getInstance().buscaDadosEntregas(idMotorista);
		    	i.putExtra("id", idMotorista);
		    	i.putExtra("dados", dados);
		    	finish();
		    	startActivity(i);
			}*/
	    	
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

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid username.
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
	 * Shows the progress UI and hides the login form.
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
				//Thread.sleep(1000);
				System.out.println("HUE");
				/*try{
					int qtdProdutosEntregues = ConsumerService.getInstance().registroEntrega("37");
					String message = qtdProdutosEntregues + " produtos foram entregues com sucesso!";
					Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
				}catch(JSONException j){
					System.out.println("Erro ao comunicar com o webservice: "+j.getMessage());
					Toast.makeText(LoginActivity.this, "Não foi possível salvar a entrega!", Toast.LENGTH_SHORT).show();
				}
				
				System.out.println("HUEHUE");
				*/
				String response = "";
				
				Map<String, String> params = new HashMap<String, String>();
				/*
				WebService webService = new WebService(URL_AUTH);

				Map<String, String> params = new HashMap<String, String>();
		        params.put("u", mUsuario);
		        params.put("p", mPassword);

		        response = webService.webGet("", params);
				*/
				
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
			    
				//ConsumerService c2 = new ConsumerService();
				/*WebService webService2 = new WebService(URL_DADOS);
				params = new HashMap<String, String>();
			    params.put("id", String.valueOf(idMotorista ));
			    dados = webService2.webGet("", params);
			    webService2.abort();
			    webService2 = null; */
			    
			    
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

				//Intent i = new Intent( getBaseContext() , LoginViewActivity.class );
				Intent i = new Intent( getApplication() , EntregasActivity.class );
				
		    	i.putExtra("id", idMotorista);
		    	i.putExtra("nome", nomeMotorista);
		    	i.putExtra("dados", dados);
		    	startActivity(i);
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
						dados = ConsumerService.getInstance().buscaDadosEntregas(String.valueOf(idMotorista));
				    	i.putExtra("id", idMotorista);
				    	i.putExtra("dados", dados);
				    	finish();
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
	
}
