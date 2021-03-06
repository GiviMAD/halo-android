package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationEventListener;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;
import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.testing.CallbackFlag;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NotificationListenerInstruments {

    public static HaloNotificationListener givenANotificationListener(@NotNull final HaloNotificationsApi api, @NonNull final CallbackFlag flag, final boolean isSilent) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isEqualTo(NotificationInstruments.NOTIFICATION_FROM);
                if (isSilent) {
                    assertThat(api.getNotificationId(data)).isNull();
                } else {
                    assertThat(api.getNotificationId(data)).isNotNull();
                }
                if (data.getString("extra") != null) {

                    try {
                        JSONObject jsonExtra = new JSONObject(data.getString("extra"));
                        assertThat(data.getString("instanceId")).isEqualTo("58594e203bb27211009ccc58");
                    } catch (JSONException jsonException) {
                        assertThat(data.getString("extra")).isEqualTo("myextradata");
                    }
                }
            }
        };
    }

    public static HaloNotificationListener givenAnAllNotificationListener(@NonNull final CallbackFlag flag) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
            }
        };
    }

    public static HaloNotificationListener givenATwoFactorListener(@NonNull final CallbackFlag flag) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
            }
        };
    }


    public static HaloNotificationListener givenANotificationWithImageListener(@NonNull final CallbackFlag flag) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
                assertThat(data.get("image")).isNotNull();
            }
        };
    }

    public static HaloNotificationListener givenAnNotificationListenerWithHaloNotificationId(@NonNull final CallbackFlag flag, final String notificationId) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
                assertThat(data.getString("halo_ui_notification_id")).isEqualTo(notificationId);
            }
        };
    }

    public static HaloNotificationListener givenAnNotificationListenerWithCustomId(@NonNull final CallbackFlag flag, final String notificationId) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
                assertThat(data.getBoolean("modifyBundle")).isTrue();
                assertThat(data.getString("halo_ui_notification_id")).isEqualTo(notificationId);
            }
        };
    }

    public static HaloNotificationDecorator givenANotificationDecoratorWithAChannel(@NonNull final CallbackFlag flag, final String channelId) {
        return new HaloNotificationDecorator() {
            @Override
            public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
                flag.flagExecuted();
                assertThat(bundle).isNotNull();
                String channelIdReceived = null;
                Field field = null;
                try {
                    field = builder.getClass().getDeclaredField("mChannelId");
                    field.setAccessible(true);
                    channelIdReceived = (String) field.get(builder);
                    assertThat(channelIdReceived).isEqualTo(channelId);
                } catch (NoSuchFieldException noSuchFieldException) {
                } catch (IllegalAccessException illegalAccessException) {
                }
                return null;
            }
        };
    }

    public static HaloNotificationEventListener givenANotificationEventActionListenerWithAction(@NonNull final CallbackFlag flag, @NotificationEventsActions.EventType final String actionType) {
        return new HaloNotificationEventListener() {
            @Override
            public void onEventReceived(@NonNull HaloPushEvent haloPushEvent) {
                flag.flagExecuted();
                assertThat(haloPushEvent.getAction()).isEqualTo(actionType);

            }
        };
    }

}
