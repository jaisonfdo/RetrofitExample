package droidmentor.retrofitexample;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import Json_Model.UserDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static droidmentor.retrofitexample.R.id.btn_add;
import static droidmentor.retrofitexample.R.id.btn_get;
import static droidmentor.retrofitexample.R.id.btn_update;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG="Retrofit Example";
    ApiInterface apiService;

    private EditText etUserName, etUserEmail;
    private TextInputLayout inputUserName,inputUserEmail;
    Button btnAdd,btnGet,btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputUserName = (TextInputLayout) findViewById(R.id.input_layout_userName);
        inputUserEmail = (TextInputLayout) findViewById(R.id.input_layout_userEmail);

        etUserName = (EditText) findViewById(R.id.etUsername);
        etUserEmail = (EditText) findViewById(R.id.etUserEmail);

        btnAdd=(Button)findViewById(btn_add);
        btnGet=(Button)findViewById(btn_get);
        btnUpdate=(Button)findViewById(btn_update);

        btnAdd.setOnClickListener(this);
        btnGet.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case btn_add:
                if (validateText(etUserName,inputUserName)&&validateEmail(etUserEmail,inputUserEmail))
                {
                    if (Network_check.isNetworkAvailable(this))
                        addUser();
                }

                break;
            case btn_get:
                if(validateEmail(etUserEmail,inputUserEmail))
                {
                    if (Network_check.isNetworkAvailable(this))
                        getUserDetails();
                }

                break;
            case R.id.btn_update:
                if (validateText(etUserName,inputUserName)&&validateEmail(etUserEmail,inputUserEmail))
                {
                    if (Network_check.isNetworkAvailable(this))
                        updateUser();
                }

                break;

        }
    }

    public void addUser()
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("user[email]",etUserEmail.getText().toString());
        queryParams.put("user[name]",etUserName.getText().toString());
        ProgressDialogLoader.progressdialog_creation(this, "Adding");

        Call<UserDetails> call = apiService.postUser(queryParams);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails>call, Response<UserDetails> response) {
                if(response.body().getDetails()!=null)
                {
                    Log.d(TAG, "User ID: " + response.body().getDetails().getId());
                    Toast.makeText(getApplicationContext(), "Successfully Added!", Toast.LENGTH_SHORT).show();
                    etUserEmail.setText("");
                    etUserName.setText("");
                    inputUserEmail.setErrorEnabled(false);
                    inputUserName.setErrorEnabled(false);
                    requestFocus(etUserEmail);
                }
                else
                {
                    Log.d(TAG, "Something missing");
                }

                ProgressDialogLoader.progressdialog_dismiss();
            }

            @Override
            public void onFailure(Call<UserDetails>call, Throwable t) {
                ProgressDialogLoader.progressdialog_dismiss();
                Log.e(TAG, t.toString());
            }
        });

    }

    public void getUserDetails()
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("user[email]",etUserEmail.getText().toString());
        ProgressDialogLoader.progressdialog_creation(this, "Loading");

        Call<UserDetails> call = apiService.getUser(queryParams);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails>call, Response<UserDetails> response) {
                if(response.body().getDetails()!=null)
                {
                    Log.d(TAG, "User ID: " + response.body().getDetails().getId());
                    etUserName.setText(response.body().getDetails().getName());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User details does not exist");
                }

                ProgressDialogLoader.progressdialog_dismiss();
            }

            @Override
            public void onFailure(Call<UserDetails>call, Throwable t) {
                Log.e(TAG, t.toString());
                ProgressDialogLoader.progressdialog_dismiss();
            }
        });

    }

    public void updateUser()
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("user[email]",etUserEmail.getText().toString());
        queryParams.put("user[name]",etUserName.getText().toString());
        ProgressDialogLoader.progressdialog_creation(this, "Updating");

        Call<UserDetails> call = apiService.updateUser(queryParams);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails>call, Response<UserDetails> response) {
                if(response.body().getDetails()!=null)
                {
                    Log.d(TAG, "User ID: " + response.body().getDetails().getId());
                    Toast.makeText(getApplicationContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    requestFocus(etUserEmail);
                    Log.d(TAG, "Something missing");
                }
                ProgressDialogLoader.progressdialog_dismiss();
            }

            @Override
            public void onFailure(Call<UserDetails>call, Throwable t) {
                ProgressDialogLoader.progressdialog_dismiss();
                Log.e(TAG, t.toString());
            }
        });

    }
    private boolean validateText(EditText etText,TextInputLayout inputlayout) {
        if (etText.getText().toString().trim().isEmpty()) {
            inputlayout.setError("Enter valid text");
            requestFocus(etText);
            return false;
        } else {
            inputlayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail(EditText etText,TextInputLayout inputlayout) {
        String email = etText.getText().toString().trim();

        if (!isValidEmail(email)) {
            inputlayout.setError("Enter valid Mail ID");
            requestFocus(etText);
            return false;
        } else {
            inputlayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
