package me.rorschach.schoolcontacts.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.MaterialLockView;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.lock_view)
    MaterialLockView mLockView;

    private String CorrectPattern = "321478965";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {

            @DebugLog
            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                if (!SimplePattern.equals(CorrectPattern)) {
                    mLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
                } else {
                    mLockView.setDisplayMode(MaterialLockView.DisplayMode.Correct);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                super.onPatternDetected(pattern, SimplePattern);
            }
        });
    }
}
