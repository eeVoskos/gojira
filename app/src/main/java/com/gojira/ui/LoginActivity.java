package com.gojira.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.gojira.R;
import com.gojira.app.GojiraApp;
import com.gojira.data.api.JiraService;
import com.gojira.data.io.LoginResponse;
import com.gojira.data.model.Credentials;
import com.gojira.util.Preferences;
import com.gojira.util.ViewUtils;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class LoginActivity extends BaseActivity implements Callback<LoginResponse> {

    @Inject
    JiraService mService;

    @InjectView(R.id.root)
    View mRoot;

    @InjectView(R.id.input_server)
    EditText mInputServer;

    @InjectView(R.id.input_username)
    EditText mInputUsername;

    @InjectView(R.id.input_password)
    EditText mInputPassword;

    @InjectView(R.id.action_login)
    Button mButtonLogin;

    @InjectView(R.id.input_server_container)
    TextInputLayout mServerContainer;

    @InjectView(R.id.input_username_container)
    TextInputLayout mUsernameContainer;

    @InjectView(R.id.input_password_container)
    TextInputLayout mPasswordContainer;

    @InjectView(R.id.progress)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GojiraApp.get(this).getGraph().inject(this);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        setInputEnabled(true);
    }

    @OnClick(R.id.action_login)
    void onLogin() {
        // Read user input
        String server = mInputServer.getText().toString();
        String username = mInputUsername.getText().toString();
        String password = mInputPassword.getText().toString();

        // Validate user input
        if (!TextUtils.isEmpty(server) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

            // Attempt to login
            tryLogin(server, username, password);

        } else {
            // TODO Consider a more sophisticated validation
            mServerContainer.setError(TextUtils.isEmpty(server) ? getString(R.string.error_server) : null);
            mUsernameContainer.setError(TextUtils.isEmpty(username) ? getString(R.string.error_username) : null);
            mPasswordContainer.setError(TextUtils.isEmpty(password) ? getString(R.string.error_password) : null);
        }
    }

    void tryLogin(String server, String username, String password) {
        // Show progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        // Hide keyboard
        ViewUtils.hideKeyboard(mInputPassword);

        // Disable user input
        setInputEnabled(false);

        // Save server url for use by endpoints
        Hawk.put(Preferences.KEY_SERVER, server);

        // Perform login with credentials
        Credentials credentials = new Credentials(username, password);
        mService.login(credentials, this);
    }

    @Override
    public void success(LoginResponse loginResponse, Response response) {
        Timber.i("Success: %s", loginResponse.session.name);

        // Hide progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        // Save credentials for later use
        String server = mInputServer.getText().toString();
        String username = mInputUsername.getText().toString();
        String password = mInputPassword.getText().toString();

        // Save server and credentials
        Hawk.chain()
                .put(Preferences.KEY_SERVER, server)
                .put(Preferences.KEY_USERNAME, username)
                .put(Preferences.KEY_PASSWORD, password)
                .commit();

        // Start main app flow
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.w(error, "Failure [kind=%s]", error.getKind());

        // Hide progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        // Re-enable input
        setInputEnabled(true);

        // Remove store credentials and server
        Hawk.clear();

        // Alert user
        int message = error.getKind() == RetrofitError.Kind.NETWORK ?
                R.string.error_network : R.string.error_login;
        Snackbar.make(mRoot, message, Snackbar.LENGTH_LONG).show();
    }

    @OnTextChanged(R.id.input_server)
    void onServerTextChanged(CharSequence text) {
        mServerContainer.setError(null);
    }

    @OnTextChanged(R.id.input_username)
    void onUsernameTextChanged(CharSequence text) {
        mUsernameContainer.setError(null);
    }

    @OnTextChanged(R.id.input_password)
    void onPasswordTextChanged(CharSequence text) {
        mPasswordContainer.setError(null);
    }

    @OnFocusChange(R.id.input_server)
    void onServerInputFocusChanged(boolean focused) {
        // Sanitize server url when focus is lost
        if (!focused) {
            String server = mInputServer.getText().toString();

            // There's not much you can do on an empty string
            if (TextUtils.isEmpty(server)) {
                return;
            }

            Uri uri = Uri.parse(server);

            // Add http scheme if not present in url
            if (TextUtils.isEmpty(uri.getScheme())) {
                server = "http://" + server;
            }

            // Add default port if not present in url
            if (uri.getPort() == -1) {
                server = server + ":8080";
            }

            mInputServer.setText(server);
        }
    }

    void setInputEnabled(boolean enabled) {
        mInputServer.setEnabled(enabled);
        mInputUsername.setEnabled(enabled);
        mInputPassword.setEnabled(enabled);
        mButtonLogin.setEnabled(enabled);
    }

}
