package com.mobgen.halo.android.auth.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;

/**
 * Common interface for the social providers.
 */
public interface SocialProvider {

    /**
     * Get the name of the provider
     *
     * @return The name of the provider selected
     */
    String getSocialNetworkName();

    /**
     * Checks if the library is available.
     *
     * @return True if the library is available. False otherwise.
     */
    boolean isLibraryAvailable(@NonNull Context context);

    /**
     * Checks if the linked app is available.
     *
     * @param context The context to make the checks.
     * @return The linked app.
     */
    boolean linkedAppAvailable(@NonNull Context context);

    /**
     * Authenticates using the halo instance and providing the result in the callback.
     *
     * @param halo     The halo instance.
     * @param haloAuthProfile The halo authentication profile.
     * @param callback The callback.
     */
    void authenticate(@NonNull Halo halo, @Nullable HaloAuthProfile haloAuthProfile, @Nullable CallbackV2<IdentifiedUser> callback);

    /**
     * Set the social token
     *
     * @param socialToken The social token of the social provider.
     */
    void setSocialToken(@Nullable String socialToken);

    /**
     * Releases all the resources reserved by this provider.
     */
    void release();
}
