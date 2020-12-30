package com.netease.nim.uikit.common.media.imagepicker.data;


import com.netease.nim.uikit.common.media.imagepicker.option.ImagePickerOption;

import androidx.fragment.app.FragmentActivity;


/**
 */

public class DataSourceFactory {
    public static AbsDataSource create(FragmentActivity activity, String path, ImagePickerOption.PickType type) {
        switch (type) {
            case Image:
                return new ImageDataSource(activity, path);
            case Video:
                return new VideoDataSource(activity, path);
            case All:
                return new AllDataSource(activity, path);
        }

        return new AbsDataSource() {
            @Override
            public void reload() {

            }
        };
    }
}
