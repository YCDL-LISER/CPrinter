package net.lingin.max.android.ui.activity;

import android.content.Intent;
import android.widget.EditText;

import net.lingin.max.android.R;
import net.lingin.max.android.logger.Log;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.widget.LoadingWidget;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_edtId)
    EditText loginEdtId;

    @BindView(R.id.login_edtPwd)
    EditText loginEdtPwd;

    @Override
    protected int onLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onObject() {

    }

    @Override
    protected void onView() {

    }

    @Override
    protected void onData() {

    }

    /**
     * 登录
     */
    @OnClick(R.id.login_btnLogin)
    public void onLoginClick() {
        String username = loginEdtId.getText().toString();
        String password = loginEdtPwd.getText().toString();
        Log.i("用户名：" + username + " 密码：" + password);
        LoadingWidget.instance().show("登陆中...");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
