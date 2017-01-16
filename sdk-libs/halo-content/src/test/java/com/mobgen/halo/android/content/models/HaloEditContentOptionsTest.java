package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloEditContentOptionsTest extends HaloRobolectricTest {

    private static Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("");
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatCanSerializeObject() throws JSONException, HaloParsingException {
        Date now = new Date();
        JSONObject json = new JSONObject("{foo: 'bar'}");
        HaloEditContentOptions instance = new HaloEditContentOptions("fakeId","fakeModuleName", "fakeModule", "fakeName", json, now, now, now,new ArrayList<HaloSegmentationTag>());
        String objectSerialized = HaloEditContentOptions.serialize(instance, mHalo.framework().parser());
        JSONObject jsonObject = new JSONObject(objectSerialized);
        assertThat(jsonObject.get("id")).isEqualTo("fakeId");
    }

    @Test
    public void thatCanDeserializeObject() throws JSONException, HaloParsingException {
        Date now = new Date();
        JSONObject json = new JSONObject("{foo: 'bar'}");
        HaloEditContentOptions instance = new HaloEditContentOptions("fakeId", "fakeModuleName", "fakeModule", "fakeName", json, now, now, now,new ArrayList<HaloSegmentationTag>());
        String objectSerialized = HaloEditContentOptions.serialize(instance, mHalo.framework().parser());
        HaloEditContentOptions objectDeserialized = HaloEditContentOptions.deserialize(objectSerialized, mHalo.framework().parser());
        JSONObject jsonObject = new JSONObject(objectSerialized);
        assertThat(objectDeserialized.getModuleId()).isEqualTo(instance.getModuleId());
    }

    @Test
    public void thatAParcelOperationKeepsTheSameData() throws JSONException {
        Date now = new Date();
        JSONObject json = new JSONObject("{foo: 'bar'}");
        HaloEditContentOptions instance = new HaloEditContentOptions("fakeId", "fakeModuleName", "fakeModule", "fakeName", json, now, now, now,new ArrayList<HaloSegmentationTag>());
        HaloEditContentOptions parcelInstance = TestUtils.testParcel(instance, HaloEditContentOptions.CREATOR);
        assertThat(instance.getItemId()).isEqualTo(parcelInstance.getItemId());
        assertThat(instance.getModuleName()).isEqualTo(parcelInstance.getModuleName());
        assertThat(instance.getModuleId()).isEqualTo(parcelInstance.getModuleId());
        assertThat(instance.getName()).isEqualTo(parcelInstance.getName());
        assertThat(instance.getValues().toString()).isEqualTo(parcelInstance.getValues().toString());
        assertThat(instance.getPublishedDate()).isEqualTo(parcelInstance.getPublishedDate());
        assertThat(instance.getRemoveDate()).isEqualTo(parcelInstance.getRemoveDate());
        assertThat(instance.describeContents()).isEqualTo(0);
        assertThat(instance.getArchivedDate()).isEqualTo(now);
    }


    @Test
    public void thatConvertObjectToString() throws JSONException {
        Date now = new Date();
        JSONObject json = new JSONObject("{foo: 'bar'}");
        HaloEditContentOptions instance = new HaloEditContentOptions("fakeId", "fakeModuleName", "fakeModule", "fakeName", json, now, now, now,new ArrayList<HaloSegmentationTag>());
        assertThat(instance.toString()).isNotNull();
    }
}
