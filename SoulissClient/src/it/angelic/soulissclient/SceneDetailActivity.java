package it.angelic.soulissclient;

import static junit.framework.Assert.assertTrue;
import it.angelic.soulissclient.R.color;
import it.angelic.soulissclient.adapters.SceneCommandListAdapter;
import it.angelic.soulissclient.db.SoulissDBHelper;
import it.angelic.soulissclient.helpers.AlertDialogHelper;
import it.angelic.soulissclient.helpers.ScenesDialogHelper;
import it.angelic.soulissclient.helpers.SoulissPreferenceHelper;
import it.angelic.soulissclient.model.SoulissCommand;
import it.angelic.soulissclient.model.SoulissScene;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SceneDetailActivity extends SherlockActivity {
	private SoulissScene collected;
	private SoulissPreferenceHelper opzioni;
	private ListView listaComandiView;
	private SoulissDBHelper datasource;
	// private boolean thru;
	private SceneCommandListAdapter ta;
	private TextView upda;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		opzioni = new SoulissPreferenceHelper(this.getApplicationContext());
		// recuper nodo da extra
		Bundle extras = getIntent().getExtras();
		collected = (SoulissScene) extras.get("SCENA");
		assertTrue("SCENA NULLO", collected != null);
		if (opzioni.isLightThemeSelected())
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
		else
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.app_name) + " - " + getString(R.string.scene) + " "
				+ Constants.int2roman(collected.getId()));
		setContentView(R.layout.main_scenedetail);

		// Backg. stuff
		SoulissClient.setBackground((RelativeLayout) findViewById(R.id.containerlista), getWindowManager());
		// Testata, info scena
		createHeader();

		listaComandiView = (ListView) findViewById(R.id.ListViewListaScene);
		ImageView nodeic = (ImageView) findViewById(R.id.scene_icon);

		nodeic.setImageResource(collected.getDefaultIconResourceId());
		nodeic.setColorFilter(getResources().getColor(color.aa_yellow), PorterDuff.Mode.SRC_ATOP);
		datasource = new SoulissDBHelper(this);
		// datasource.open();

		// typs = gottardo.toArray(typs);
		registerForContextMenu(listaComandiView);

	}

	@SuppressLint("NewApi")
	@Override
	protected void onStart() {
		int versionNumber = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		super.onStart();
		if (versionNumber >= 11) {
			ActionBar actionBar = this.getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		Bundle extras = getIntent().getExtras();
		collected = (SoulissScene) extras.get("SCENA");

		createHeader();

		// tipici dal DB
		datasource.open();
		ArrayList<SoulissCommand> gottardo = datasource.getSceneCommands(this, collected.getId());
		SoulissCommand[] comandiArray = new SoulissCommand[gottardo.size()];
		comandiArray = gottardo.toArray(comandiArray);

		// typs = gottardo.toArray(typs);
		ta = new SceneCommandListAdapter(this.getApplicationContext(), comandiArray, opzioni);
		// Adapter della lista
		listaComandiView.setAdapter(ta);

		listaComandiView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO exec single command ?
			}
		});
	}

	/**
	 * Riga grigia crea spazio
	 * 
	 * @param activity
	 * @return
	 */
	private void createHeader() {
		// nodoInfo.removeAllViews();
		// titolo = (TextView) findViewById(R.id.TextViewNodeTitle);
		TextView tt = (TextView) findViewById(R.id.TextViewTypicals);
		TextView health = (TextView) findViewById(R.id.TextViewHealth);
		upda = (TextView) findViewById(R.id.TextViewNodeUpdate);
		ImageView icon = (ImageView) findViewById(R.id.scene_icon);
		// titolo.setText(getString(R.string.scene) + " " +
		// Constants.int2roman(collected.getId()));
		icon.setImageResource(collected.getDefaultIconResourceId());
		// Animazione icona nodo
		if (opzioni.getTextFx()) {
			Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scalerotale);
			a.reset();
			icon.startAnimation(a);
		}

		upda.setText(collected.toString());
		health.setText("Scene with " + collected.getCommandArray().size() + " commands");

		// Font dei titoli
		if ("def".compareToIgnoreCase(opzioni.getPrefFont()) != 0) {
			Typeface font = Typeface.createFromAsset(getAssets(), opzioni.getPrefFont());
			tt.setTypeface(font, Typeface.NORMAL);
			// titolo.setTypeface(font, Typeface.NORMAL);
		}

		return;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		android.view.MenuInflater inflater = getMenuInflater();
		// Rinomina nodo e scelta icona
		inflater.inflate(R.menu.scenes_commands_ctx_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		SceneCommandListAdapter ada = (SceneCommandListAdapter) listaComandiView.getAdapter();

		long arrayAdapterPosition = info.position;
		final SoulissCommand todoItem = (SoulissCommand) ada.getItem((int) arrayAdapterPosition);

		switch (item.getItemId()) {
		case R.id.eliminaComando:
			ScenesDialogHelper.removeCommandDialog(this, listaComandiView, datasource, collected, todoItem, opzioni);
			return true;
		case R.id.eseguiComando:
			Toast.makeText(this, "TOIMPLEMENT", Toast.LENGTH_SHORT).show();
			// nodesAdapter.notifyDataSetChanged();
			// listaNodiView.invalidateViews();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.scenedetail_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ImageView icon = (ImageView) findViewById(R.id.scene_icon);
		switch (item.getItemId()) {
		case R.id.Opzioni:
			Intent settingsActivity = new Intent(getBaseContext(), PreferencesActivity.class);
			startActivity(settingsActivity);
			final Intent preferencesActivity = new Intent(getBaseContext(), PreferencesActivity.class);
			// evita doppie aperture per via delle sotto-schermate
			preferencesActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(preferencesActivity);
			return true;
		case R.id.AddScene:
			//FrameLayout f1 = (FrameLayout) findViewById(android.R.id.custom);
			ScenesDialogHelper.addSceneCommandDialog(SceneDetailActivity.this, listaComandiView, datasource, collected,
					opzioni).show();
			return true;
		case R.id.CambiaIconaScena:
			AlertDialog.Builder alert2 = AlertDialogHelper.chooseIconDialog(this, icon, null, datasource, collected);
			alert2.show();
			return true;
		case R.id.Esegui:
			ScenesDialogHelper.executeSceneDialog(SceneDetailActivity.this, collected, opzioni);
			return true;
		case R.id.RinominaScena:
			AlertDialog.Builder alert = AlertDialogHelper.renameSoulissObjectDialog(this, upda, listaComandiView,
					datasource, collected);
			alert.show();
			return true;
		case android.R.id.home:
			finish();
			if (opzioni.isAnimationsEnabled())
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// thru = opzioni.isSoulissReachable();
		datasource.open();
		// IntentFilter filtere = new IntentFilter();
		// filtere.addAction("it.angelic.soulissclient.GOT_DATA");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
