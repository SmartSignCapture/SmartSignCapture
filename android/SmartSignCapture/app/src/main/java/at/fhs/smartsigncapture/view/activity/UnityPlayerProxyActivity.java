package at.fhs.smartsigncapture.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import at.fhs.smartsigncapture.view.activity.unity.UnityPlayerActivity;

/**
 * @deprecated Use UnityPlayerActivity instead.
 */
public class UnityPlayerProxyActivity extends Activity
{
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(this, UnityPlayerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		Bundle extras = getIntent().getExtras();
		if (extras != null)
			intent.putExtras(extras);
		startActivity(intent);
	}
}
