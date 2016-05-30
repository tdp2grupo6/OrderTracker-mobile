package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.AutenticacionBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.PushBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.view.app.OrderTrackerApplication;

public class LoginActivity extends AppCompatActivity { //implements LoaderCallbacks<Cursor> {

    public static final String ARG_SHOW_MESSAGE = "show_message";

    private OrderTrackerApplication mApplication = null;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        ImageButton mEmailSignInButton = (ImageButton) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //TODO SACARRRR
        //mEmailView.setText("vendedor");
        //mPasswordView.setText("vendedor");

        //Verifica si ya esta logueado
        mApplication = (OrderTrackerApplication)this.getApplication();
        if (mApplication.getAutentication() != null) {
            navigateNext();
        }

        Bundle mExtras = getIntent().getExtras();
        if (mExtras != null) {
            boolean showMessage = mExtras.getBoolean(LoginActivity.ARG_SHOW_MESSAGE);
            if (showMessage)
                Toast.makeText(this, R.string.msg_sesion_vencida, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(this, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void navigateNext() {
        mApplication.runService();

        Intent c = new Intent(this, AgendaActivity.class);
        c.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(c);
    }


    public class UserLoginTask extends AsyncTask<Void, Void, AutenticacionResponse> {
        private Context mContext;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(Context context, String email, String password) {
            mContext = context;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected AutenticacionResponse doInBackground(Void... params) {
            AutenticacionResponse autenticacion = null;
            try {
                AutenticacionBZ autenticacionBZ = new AutenticacionBZ(mContext);
                autenticacion = autenticacionBZ.login(mEmail, mPassword);

                if (autenticacion != null && autenticacion.accessToken.length() > 0) {
                    //Envia el token push al servidor
                    try {
                        PushBZ pushBZ = new PushBZ(mContext);
                        pushBZ.reEnviarPushToken(autenticacion);
                    } catch (Exception e){}

                    //Verifica que tenga los datos cargados
                    try {
                        ClienteBZ clienteBZ = new ClienteBZ(mContext);
                        ArrayList<Cliente> clientes = clienteBZ.listar();
                        if (clientes == null || clientes.size() == 0) {
                            clienteBZ.sincronizar();
                        }
                    } catch (Exception e){}
                    }

            } catch (AutorizationException ae) {
                autenticacion = null;
            } catch (Exception e) {
                autenticacion = null;
            }

            return autenticacion;
        }

        @Override
        protected void onPostExecute(final AutenticacionResponse autenticacion) {
            mAuthTask = null;
            showProgress(false);

            if (autenticacion != null) {
                navigateNext();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_login));
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

