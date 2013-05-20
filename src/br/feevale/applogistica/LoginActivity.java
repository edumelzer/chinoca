package br.feevale.applogistica;

import java.util.HashMap;
import java.util.Map;

import br.feevale.applogistica.webservice.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

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
	
			try {
				//Thread.sleep(1000);
				String response = "";

				WebService webService = new WebService(URL_AUTH);

				Map<String, String> params = new HashMap<String, String>();
		        params.put("u", mUsuario);
		        params.put("p", mPassword);

		        response = webService.webGet("", params);

				JSONObject o    = new JSONObject(response);

				int retorno      = Integer.parseInt(o.get("retorno").toString());

				//Senha invalida:
				if(retorno < 0){
					return false;
				}

				idMotorista   = Integer.parseInt(o.get("id_motorista").toString());
				nomeMotorista = o.get("nome").toString();
				
				ConsumerService c = new ConsumerService();
				webService = new WebService(URL_DADOS);
				params = new HashMap<String, String>();
			    params.put("id", String.valueOf(idMotorista ));
			    dados = webService.webGet("", params);
				
			//} catch (InterruptedException e) {
			//	return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
				//Intent i = new Intent( getBaseContext() , LoginViewActivity.class );
				Intent i = new Intent( getBaseContext() , EntregasActivity.class );
				
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
}
