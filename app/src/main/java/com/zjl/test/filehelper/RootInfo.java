/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjl.test.filehelper;


import java.util.Objects;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Root;
import android.text.TextUtils;

/**
 * Representation of a {@link Root}.
 * @see {@link com.android.documentsui.model.RootInfo}
 */
public class RootInfo implements Parcelable, Comparable<RootInfo> {

    private static final String TAG = "FM_RootInfo";
    private static final int VERSION_INIT = 1;
    private static final int VERSION_DROP_TYPE = 2;

    public @interface RootType {}
    public static final int TYPE_IMAGES = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_RECENTS = 4;
    public static final int TYPE_DOWNLOADS = 5;
    public static final int TYPE_LOCAL = 6;
    public static final int TYPE_MTP = 7;
    public static final int TYPE_SD = 8;
    public static final int TYPE_USB = 9;
    public static final int TYPE_OTHER = 10;

    public String authority;
    public String rootId;
    public int flags;
    public int icon;
    public String title;
    public String summary;
    public String documentId;
    public long availableBytes;
    public String mimeTypes;

    /** Derived fields that aren't persisted */
    public String[] derivedMimeTypes;
    public int derivedIcon;
    public @RootType int derivedType;

    public String fsUuid;
    public String path;
    public String memoryInfo;

    public RootInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Creator<RootInfo> CREATOR = new Creator<RootInfo>() {
        @Override
        public RootInfo createFromParcel(Parcel in) {
            final RootInfo root = new RootInfo();
            return root;
        }

        @Override
        public RootInfo[] newArray(int size) {
            return new RootInfo[size];
        }
    };

    public Uri getUri() {
        return DocumentsContract.buildRootUri(authority, rootId);
    }

    public boolean isRecents() {
        return authority == null && rootId == null;
    }

    public boolean isHome() {
        // Note that "home" is the expected root id for the auto-created
        // user home directory on external storage. The "home" value should
        // match ExternalStorageProvider.ROOT_ID_HOME.
        return isExternalStorage() && "home".equals(rootId);
    }

    public boolean isExternalStorage() {
        return "com.android.externalstorage.documents".equals(authority);
    }

    public boolean isDownloads() {
        return "com.android.providers.downloads.documents".equals(authority);
    }

    public boolean isImages() {
        return "com.android.providers.media.documents".equals(authority)
                && "images_root".equals(rootId);
    }

    public boolean isVideos() {
        return "com.android.providers.media.documents".equals(authority)
                && "videos_root".equals(rootId);
    }

    public boolean isAudio() {
        return "com.android.providers.media.documents".equals(authority)
                && "audio_root".equals(rootId);
    }

    public boolean isMtp() {
        return "com.android.mtp.documents".equals(authority);
    }

    public boolean isLibrary() {
        return derivedType == TYPE_IMAGES
                || derivedType == TYPE_VIDEO
                || derivedType == TYPE_AUDIO
                || derivedType == TYPE_RECENTS;
    }

    public boolean hasSettings() {
        return false;//(flags & Root.FLAG_HAS_SETTINGS) != 0;
    }

    public boolean supportsChildren() {
        return (flags & StorageHelper.FLAG_SUPPORTS_IS_CHILD) != 0;
    }

    public boolean supportsCreate() {
        return (flags & Root.FLAG_SUPPORTS_CREATE) != 0;
    }

    public boolean supportsRecents() {
        return (flags & Root.FLAG_SUPPORTS_RECENTS) != 0;
    }

    public boolean supportsSearch() {
        return (flags & Root.FLAG_SUPPORTS_SEARCH) != 0;
    }

    public boolean isLocalOnly() {
        return (flags & Root.FLAG_LOCAL_ONLY) != 0;
    }


    public boolean isSd() {
        return (flags & StorageHelper.FLAG_REMOVABLE_SD) != 0;
    }

    public boolean isUsb() {
        return (flags & StorageHelper.FLAG_REMOVABLE_USB) != 0;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (o instanceof RootInfo) {
            RootInfo other = (RootInfo) o;
            return Objects.equals(authority, other.authority)
                    && Objects.equals(rootId, other.rootId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority, rootId);
    }

    @Override
    public int compareTo(RootInfo other) {
        // Sort by root type, then title, then summary.
        int score = derivedType - other.derivedType;
        if (score != 0) {
            return score;
        }

        score = compareToIgnoreCaseNullable(title, other.title);
        if (score != 0) {
            return score;
        }

        return compareToIgnoreCaseNullable(summary, other.summary);
    }
    /**
     * Compare two strings against each other using system default collator in a
     * case-insensitive mode. Clusters strings prefixed with {@link DIR_PREFIX}
     * before other items.
     */
    public static int compareToIgnoreCaseNullable(String lhs, String rhs) {
        final boolean leftEmpty = TextUtils.isEmpty(lhs);
        final boolean rightEmpty = TextUtils.isEmpty(rhs);

        if (leftEmpty && rightEmpty) return 0;
        if (leftEmpty) return -1;
        if (rightEmpty) return 1;

        return lhs.compareToIgnoreCase(rhs);
    }

    @Override
    public String toString() {
        return "Root{"
                + "authority=" + authority
                + ", rootId=" + rootId
                + ", title=" + title
                + ", path=" + path
                + ", isUsb=" + isUsb()
                + ", isSd=" + isSd()
                + ", isMtp=" + isMtp()
                + "}";
    }

    public String getDirectoryString() {
        return !TextUtils.isEmpty(summary) ? summary : title;
    }
}
