package onealldigital.nizara.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton btnSignUp;
    private EditText edtName, edtNo, edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.btn_signup);
        edtName = findViewById(R.id.edt_signup_name);
        edtNo = findViewById(R.id.edt_signup_mobile);
        edtEmail = findViewById(R.id.edt_signup_email);

        btnSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        /*int id = v.getId();
        if (id == R.id.btn_signup) {
            String name = edtName.getText().toString().trim();
            String email = "" + edtEmail.getText().toString().trim();
            final String password = edtNo.getText().toString().trim();
            if (ApiConfig.CheckValidattion(name, false, false)) {
                edtName.requestFocus();
                edtName.setError(getString(R.string.enter_name));
            } else if (ApiConfig.CheckValidattion(email, false, false)) {
                edtEmail.requestFocus();
                edtEmail.setError(getString(R.string.enter_email));
            } else if (ApiConfig.CheckValidattion(email, true, false)) {
                edtEmail.requestFocus();
                edtEmail.setError(getString(R.string.enter_valid_email));
            } else if (ApiConfig.CheckValidattion(password, false, true)) {
                edtcpsw.requestFocus();
                edtNo.setError(getString(R.string.enter_pass));
            } else if (ApiConfig.CheckValidattion(cpassword, false, false)) {
                edtcpsw.requestFocus();
                edtcpsw.setError(getString(R.string.enter_confirm_pass));
            } else if (!password.equals(cpassword)) {
                edtcpsw.requestFocus();
                edtcpsw.setError(getString(R.string.pass_not_match));
            } else if (!chPrivacy.isChecked()) {
                Toast.makeText(activity, getString(R.string.alert_privacy_msg), Toast.LENGTH_LONG).show();
            } else if (ApiConfig.isConnected(activity)) {
                userSignUpSubmit(name, email, password);
            }
        }*/
    }

    /*public void userSignUpSubmit(String name, String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.TYPE, Constant.REGISTER);
        params.put(Constant.NAME, name);
        params.put(Constant.EMAIL, email);
        params.put(Constant.MOBILE, mobile);
        params.put(Constant.PASSWORD, password);
        params.put(Constant.COUNTRY_CODE, session.getData(Constant.COUNTRY_CODE));
        params.put(Constant.FCM_ID, "" + session.getData(Constant.FCM_ID));
        params.put(Constant.FRIEND_CODE, edtRefer.getText().toString().trim());
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject objectbject = new JSONObject(response);
                    if (!objectbject.getBoolean(Constant.ERROR)) {
                        StartMainActivity(objectbject, password);
                    }
                    Toast.makeText(activity, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.REGISTER_URL, params, true);
    }*/
}