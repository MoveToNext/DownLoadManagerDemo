/*
 * Copyright (C) 2011 The Android Open Source Project
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
 *
 */

package com.example.down.download;

import android.annotation.TargetApi;
import android.content.Context;
import android.drm.DrmManagerClient;
import android.os.Build;

import java.io.File;

class DownloadDrmHelper {

    /**
     * The MIME type of special DRM files
     */
    public static final String MIMETYPE_DRM_MESSAGE = "application/vnd.oma.drm.message";

    /**
     * The extensions of special DRM files
     */
    public static final String EXTENSION_DRM_MESSAGE = ".dm";

    public static final String EXTENSION_INTERNAL_FWDL = ".fl";

    /**
     * Checks if the Media Type needs to be DRM converted
     *
     * @param mimetype Media type of the content
     * @return True if convert is needed else false
     */
    public static boolean isDrmConvertNeeded(String mimetype) {
        return MIMETYPE_DRM_MESSAGE.equals(mimetype);
    }

    /**
     * Modifies the file extension for a DRM Forward Lock file NOTE: This
     * function shouldn't be called if the file shouldn't be DRM converted
     */
    public static String modifyDrmFwLockFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex != -1) {
            filename = filename.substring(0, extensionIndex);
        }
        return filename.concat(EXTENSION_INTERNAL_FWDL);
    }

    /**
     * Return the original MIME type of the given file, using the DRM framework
     * if the file is protected content.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static String getOriginalMimeType(Context context, File file, String currentMime) {
        final DrmManagerClient client = new DrmManagerClient(context);
        try {
            final String rawFile = file.toString();
            if (client.canHandle(rawFile, null)) {
                return client.getOriginalMimeType(rawFile);
            } else {
                return currentMime;
            }
        } finally {
            client.release();
        }
    }


//    String filename = cursor.getString(cursor.getColumnIndexOrThrow(Downloads.Impl._DATA));
//    String mimetype = cursor.getString(cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_MIME_TYPE));
//    Uri path = Uri.parse(filename);
//    // If there is no scheme, then it must be a file
//	if (path.getScheme() == null) {
//        path = Uri.fromFile(new File(filename));
//    }
//    Intent activityIntent = new Intent(Intent.ACTION_VIEW);
//    mimetype = DownloadDrmHelper.getOriginalMimeType(context, filename, mimetype);
//	activityIntent.setDataAndType(path, mimetype);
//	activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	try {
//        context.startActivity(activityIntent);
//    } catch (ActivityNotFoundException ex) {
//        Log.d(Constants.TAG, "no activity for " + mimetype, ex);
//    }
}
