package com.mobgen.halo.android.app.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobgen.halo.android.app.BuildConfig;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloApplication;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.translations.spec.HaloTranslationsContract;

/**
 * The settings activity for halo.
 */
public class SettingsActivity extends MobgenHaloActivity {

    /**
     * Preferences name for this the current halo environment.
     */
    public static final String PREFERENCES_HALO_ENVIRONMENT = "environment";

    private SettingsViewHolder mViewHolder;

    private AlertDialog mPreviousDialog;

    /**
     * Starts this activity.
     *
     * @param context The context to start this activity.
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        mViewHolder = new SettingsViewHolder(getWindow().getDecorView());
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        //The String for the environment
        final String[] environmentNames = new String[]{
                getString(R.string.qa_environment),
                getString(R.string.int_environment),
                getString(R.string.stage_environment),
                getString(R.string.production_environment),
                getString(R.string.uat_environment)};

        //Set the HALO env text
        final HaloStorageApi storage = Halo.instance().getCore().manager().storage();
        final HaloStorageApi storageContent = Halo.instance().framework().storage(HaloContentContract.HALO_CONTENT_STORAGE);
        final HaloStorageApi storageTranslation = Halo.instance().framework().storage(HaloTranslationsContract.HALO_TRANSLATIONS_STORAGE);
        @MobgenHaloApplication.Environment final Integer env = storage.prefs().getInteger("environment", MobgenHaloApplication.PROD);
        if (env != null && env < environmentNames.length) {
            mViewHolder.mCurrentEnvironment.setText(environmentNames[env]);
        }

        //Reload HALO with the dialog
        mViewHolder.mEnvironmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.select_dialog_singlechoice, environmentNames);
                int selectedItem = env != null ? env : 0;
                mPreviousDialog = new AlertDialog.Builder(SettingsActivity.this)
                        .setSingleChoiceItems(arrayAdapter, selectedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, @MobgenHaloApplication.Environment int env1) {
                                storage.prefs().edit().clear().putInt(PREFERENCES_HALO_ENVIRONMENT, env1).commit();
                                //Remove the databases for the current environment
                                storage.db().deleteDatabase();
                                if(storageContent!=null) {
                                    storageContent.db().deleteDatabase();
                                    storageContent.prefs().edit().clear().commit();
                                }
                                if(storageTranslation!=null) {
                                    storageTranslation.db().deleteDatabase();
                                    storageTranslation.prefs().edit().clear().commit();
                                }
                                Halo.instance().uninstall();
                                ((HaloApplication) getApplication()).installHalo();
                                ModulesActivity.start(SettingsActivity.this, true);
                            }
                        })
                        .setTitle(getString(R.string.environment_select))
                        .setCancelable(true)
                        .show();
            }
        });

        mViewHolder.mDbContainer.setVisibility(View.VISIBLE);
        mViewHolder.mDeleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storage!=null) {
                    storage.db().deleteDatabase();
                    storage.prefs().edit().clear().commit();
                }
                if(storageContent!=null) {
                    storageContent.db().deleteDatabase();
                    storageContent.prefs().edit().clear().commit();
                }
                if(storageTranslation!=null) {
                    storageTranslation.db().deleteDatabase();
                    storageTranslation.prefs().edit().clear().commit();
                }

            }
        });

        setApplicationVars();
    }

    private void setApplicationVars() {
        Credentials credentials = Halo.core().credentials();
        mViewHolder.mClientId.setText(credentials.getUsername());
        Device device = Halo.core().manager().getDevice();
        if(device != null) {
            mViewHolder.mUserAlias.setText(device.getAlias());
            mViewHolder.mNotificationToken.setText(device.getNotificationsToken());
        }
        try {
            mViewHolder.mApplicationVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Halog.e(getClass(), "Error while bringing the application version name");
        }
        mViewHolder.mBambooBuild.setText(BuildConfig.BAMBOO_BUILD != null ? BuildConfig.BAMBOO_BUILD.toString() : "IDE");

    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.settings_title);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreviousDialog != null) {
            mPreviousDialog.cancel();
        }
    }

    private static class SettingsViewHolder {
        private View mEnvironmentContainer;
        private TextView mCurrentEnvironment;
        private TextView mClientId;
        private TextView mUserAlias;
        private TextView mApplicationVersion;
        private TextView mBambooBuild;
        private TextView mNotificationToken;
        private View mDeleteDatabaseButton;
        private View mDbContainer;

        public SettingsViewHolder(View container) {
            mEnvironmentContainer = container.findViewById(R.id.ll_environment_container);
            mCurrentEnvironment = (TextView) container.findViewById(R.id.tv_environment);
            mClientId = (TextView) container.findViewById(R.id.tv_client_id);
            mUserAlias = (TextView) container.findViewById(R.id.tv_user_alias);
            mApplicationVersion = (TextView) container.findViewById(R.id.tv_version);
            mBambooBuild = (TextView) container.findViewById(R.id.tv_bamboo_build);
            mNotificationToken = (TextView) container.findViewById(R.id.tv_gcm);
            mDeleteDatabaseButton = container.findViewById(R.id.bt_delete_db);
            mDbContainer = container.findViewById(R.id.ll_database_container);
        }
    }

}
