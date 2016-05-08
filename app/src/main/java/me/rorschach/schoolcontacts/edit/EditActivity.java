package me.rorschach.schoolcontacts.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;

public class EditActivity extends AppCompatActivity implements EditContract.View {

    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.sp_college)
    Spinner mSpCollege;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private ActionBar mActionBar;

    private ArrayAdapter<String> spinnerAdapter;

    private Contact mContact;

    private List<String> mColleges;

    private EditContract.Presenter mPresenter;

    private boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        initView();

        setPresenter(new EditPresenter(ContactRepository.getInstance(), this));

        handleIntent();
    }

    private void initView() {

        mToolbar.inflateMenu(R.menu.menu_detail);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCollege.setAdapter(spinnerAdapter);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mContact = (Contact) intent.getSerializableExtra("CONTACT");

        if (mContact != null) {
            mEtName.setText(mContact.getName());
            mEtPhone.setText(mContact.getPhone());

            if (mActionBar != null) {
                mActionBar.setTitle("编辑联系人");
            }
        }else {
            if (mActionBar != null) {
                mActionBar.setTitle("创建联系人");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isActive = true;
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false;

        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onCollegesLoad(@NonNull List<String> colleges) {
        mColleges = colleges;
        spinnerAdapter.clear();
        spinnerAdapter.addAll(colleges);
        spinnerAdapter.notifyDataSetChanged();

        mSpCollege.setSelection(colleges.indexOf(mContact.getCollege()));
    }

    @Override
    public void onAdd(@NonNull Contact contact) {
        Intent intent = new Intent();
        intent.putExtra("CONTACT", mContact);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onEdit(@NonNull Contact contact) {
        Intent intent = new Intent();
        intent.putExtra("CONTACT", mContact);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onCancel() {
        onBackPressed();
    }

    @Override
    public void setPresenter(EditContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
