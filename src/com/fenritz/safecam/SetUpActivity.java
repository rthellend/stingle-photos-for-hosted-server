package com.fenritz.safecam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.fenritz.safecam.util.AESCrypt;
import com.fenritz.safecam.util.AESCryptException;
import com.fenritz.safecam.util.Helpers;

public class SetUpActivity  extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		Helpers.fixBackgroundRepeat(findViewById(R.id.parentLayout));
		
		((Button) findViewById(R.id.setupButton)).setOnClickListener(setup());
	}
	
	private OnClickListener setup() {
		return new OnClickListener() {
			public void onClick(View v) {
				String password1 = ((EditText)findViewById(R.id.password1)).getText().toString();
				String password2 = ((EditText)findViewById(R.id.password2)).getText().toString();
				
				if(password1.equals("")){
					Helpers.showAlertDialog(SetUpActivity.this, getString(R.string.password_empty));
					return;
				}
				
				if(!password1.equals(password2)){
					Helpers.showAlertDialog(SetUpActivity.this, getString(R.string.password_not_match));
					return;
				}
				
				if(password1.length() < Integer.valueOf(getString(R.string.min_pass_length))){
					Helpers.showAlertDialog(SetUpActivity.this, String.format(getString(R.string.password_short), getString(R.string.min_pass_length)));
					return;
				}
				
				SharedPreferences preferences = getSharedPreferences(SafeCameraActivity.DEFAULT_PREFS, MODE_PRIVATE);
				try {
					String loginHash = AESCrypt.byteToHex(AESCrypt.getHash(AESCrypt.byteToHex(AESCrypt.getHash(password1)) + password1));
					preferences.edit().putString(SafeCameraActivity.PASSWORD, loginHash).commit();
				}
				catch (AESCryptException e) {
					Helpers.showAlertDialog(SetUpActivity.this, String.format(getString(R.string.unexpected_error), "102"));
					e.printStackTrace();
				}
				
				try {
					((SafeCameraApplication) SetUpActivity.this.getApplication()).setKey(AESCrypt.byteToHex(AESCrypt.getHash(password1)));
				}
				catch (AESCryptException e) {
					Helpers.showAlertDialog(SetUpActivity.this, getString(R.string.unexpected_error));
					return;
				}
				
				Intent intent = new Intent();
				intent.setClass(SetUpActivity.this, DashboardActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		};
	}
}
