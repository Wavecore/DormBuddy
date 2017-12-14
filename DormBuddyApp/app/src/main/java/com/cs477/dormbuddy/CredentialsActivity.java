package com.cs477.dormbuddy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.*; //responses are in JSON

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.READ_CONTACTS;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ICON;
import static com.cs477.dormbuddy.LocalUserHelper.USER_IS_ADMIN;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NET_ID;

/**
 * A login screen that offers login via email/password.
 */
public class CredentialsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursor;
    final static String[] columns = { USER_LOGGED_IN };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_USER, columns, USER_LOGGED_IN+" = 1", new String[] {}, null, null,
                null);
        try { //if the user ever logged in, there will be a row in the database, but check that they didnt log out
            mCursor.moveToPosition(0);
            int isLoggedIn = mCursor.getInt(0);
            if (isLoggedIn == 1) {
                //user is logged in, therefore show activity_main
                startActivity(new Intent(this,MainActivity.class));
                finish();
                return;
            }
        } catch (Exception e) { //otherwise direct to login
            System.out.println("Not Logged in. Please log in first");
        }
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
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
            //attempt logging in
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true; //email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(CredentialsActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String requestURL;
        private Context context;
        private String netId;

        UserLoginTask(String email, String password, Context context) {
            netId = email;
            requestURL = String.format("https://hidden-caverns-60306.herokuapp.com/login?netID=%s&password=%s", email, password);
            this.context = context;
        }

        //sends the request in background
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(requestURL);

                //uses HttpsURLConnection to make the request(HttpClient is outdated and deprecated)
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                //get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;

                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }

                //parse the response JSON
                JSONObject response = new JSONObject(content);
                //use .getJSONObject("object") to get inner JSONs
                String Name = response.getString("Name");

                if (Name.length() > 1) { //user successfully logged in if a name was returned
                    String BuildingID = response.getString("BuildingID");
                    String GNumber = response.getString("GNumber");
                    int RoomNum = response.getInt("RoomNum");
                    Boolean isAdmin = response.getBoolean("isAdmin");
                    try {
                        //CHECKS if user has ever logged in on this phone
                        mCursor = db.query(TABLE_USER, columns, USER_NET_ID+"='"+netId+"'", new String[] {}, null, null,
                                null);
                        mCursor.moveToFirst();
                        System.out.println("User logged in: " + mCursor.getInt(0));
                        ContentValues cv = new ContentValues(1);
                        cv.put(USER_LOGGED_IN, 1); //makes the logged out user logged in
                        db.update(TABLE_USER, cv, USER_NET_ID+"='"+netId+"'", null);
                    } catch (Exception e) {
                        //ELSE insert user information in local database
                        ContentValues cv = new ContentValues(8);
                        cv.put(USER_ID, GNumber); //g number
                        cv.put(USER_NET_ID, netId);
                        cv.put(USER_NAME, Name);
                        cv.put(USER_LOGGED_IN, 1);
                        cv.put(BUILDING_NAME, BuildingID);
                        cv.put(BUILDING_ID, BuildingID);
                        cv.put(ROOM_NUMBER, RoomNum);
                        cv.put(USER_IS_ADMIN, isAdmin);
                        cv.put(USER_ICON, new byte[]{}); //icon is just an empty byte array to start
                        db.insert(TABLE_USER, null, cv);
                    }
                    mCursor.close();



                    System.out.printf("%s, %s, %s, %s, %s\n", Name, BuildingID, GNumber, RoomNum, isAdmin);
                }

                /* array code snippet
                JSONArray arr = obj.getJSONArray("posts");
                for (int i = 0; i < arr.length(); i++)
                {
                    String post_id = arr.getJSONObject(i).getString("post_id");
                }
                */

                //password is correct if content is returned lol*/
                return Name.length() > 1;
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }

            /*
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
            return true; //every password is correct*/
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) { //correct password
                //go to house picking
                startActivity(new Intent(context, MainActivity.class));
                finish(); //finish ends this activity for good
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
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

