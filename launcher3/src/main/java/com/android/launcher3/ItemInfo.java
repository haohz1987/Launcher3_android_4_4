/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.launcher3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;

import java.util.Arrays;

/**
 * Represents an item in the launcher.
 *
 *  在桌面绘制一个图标块,桌面上每个Item的信息数据结构，包括在第几屏、第几行、第几列、宽高等信息；
 *  该对象与数据库中记录一一对应；该类有多个子类，
 *  譬如FolderIcon的FolderInfo、BubbleTextView的ShortcutInfo等
 *
 */
public class ItemInfo {

    /**
     * Intent extra to store the profile. Format: UserHandle
     */
    static final String EXTRA_PROFILE = "profile";
    
    public static final int NO_ID = -1;
    
    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;
    
    /**
     * One of {@link LauncherSettings.Favorites#ITEM_TYPE_APPLICATION},
     * {@link LauncherSettings.Favorites#ITEM_TYPE_SHORTCUT},
     * {@link LauncherSettings.Favorites#ITEM_TYPE_FOLDER}, or
     * {@link LauncherSettings.Favorites#ITEM_TYPE_APPWIDGET}.
     */
    public int itemType;
    
    /**
     * The id of the container that holds this item. For the desktop, this will be 
     * {@link LauncherSettings.Favorites#CONTAINER_DESKTOP}. For the all applications folder it
     * will be {@link #NO_ID} (since it is not stored in the settings DB). For user folders
     * it will be the id of the folder.
     */
    public long container = NO_ID;
    
    /**
     * Iindicates the screen in which the shortcut appears.
     */
    public long screenId = -1;
    
    /**
     * Indicates the X position of the associated cell.
     */
    public int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    public int cellY = -1;

    /**
     * Indicates the X cell span.
     */
    public int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    public int spanY = 1;

    /**
     * Indicates the minimum X cell span.
     */
    public int minSpanX = 1;

    /**
     * Indicates the minimum Y cell span.
     */
    public int minSpanY = 1;

    /**
     * Indicates the position in an ordered list.
     */
    public int rank = 0;

    /**
     * Indicates that this item needs to be updated in the db
     */
    public boolean requiresDbUpdate = false;

    /**
     * Title of the item
     */
    public CharSequence title;

    /**
     * Content description of the item.
     */
    public CharSequence contentDescription;

    /**
     * The position of the item in a drag-and-drop operation.
     */
    public int[] dropPos = null;

    public UserHandleCompat user;

    public ItemInfo() {
        user = UserHandleCompat.myUserHandle();
    }

    ItemInfo(ItemInfo info) {
        copyFrom(info);
        // tempdebug:
        LauncherModel.checkItemInfo(this);
    }

    public void copyFrom(ItemInfo info) {
        id = info.id;
        cellX = info.cellX;
        cellY = info.cellY;
        spanX = info.spanX;
        spanY = info.spanY;
        rank = info.rank;
        screenId = info.screenId;
        itemType = info.itemType;
        container = info.container;
        user = info.user;
        contentDescription = info.contentDescription;
    }

    public Intent getIntent() {
        throw new RuntimeException("Unexpected Intent");
    }

    /**
     * Write the fields of this item to the DB
     * 
     * @param context A context object to use for getting UserManagerCompat
     * @param values
     */

    void onAddToDatabase(Context context, ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, itemType);
        values.put(LauncherSettings.Favorites.CONTAINER, container);
        values.put(LauncherSettings.Favorites.SCREEN, screenId);
        values.put(LauncherSettings.Favorites.CELLX, cellX);
        values.put(LauncherSettings.Favorites.CELLY, cellY);
        values.put(LauncherSettings.Favorites.SPANX, spanX);
        values.put(LauncherSettings.Favorites.SPANY, spanY);
        values.put(LauncherSettings.Favorites.RANK, rank);
        long serialNumber = UserManagerCompat.getInstance(context).getSerialNumberForUser(user);
        values.put(LauncherSettings.Favorites.PROFILE_ID, serialNumber);

        if (screenId == Workspace.EXTRA_EMPTY_SCREEN_ID) {
            // We should never persist an item on the extra empty screen.
            throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
        }
    }

    static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
            byte[] data = Utilities.flattenBitmap(bitmap);
            values.put(LauncherSettings.Favorites.ICON, data);
        }
    }

    /**
     * It is very important that sub-classes implement this if they contain any references
     * to the activity (anything in the view hierarchy etc.). If not, leaks can result since
     * ItemInfo objects persist across rotation and can hence leak by holding stale references
     * to the old view hierarchy / activity.
     */
    void unbind() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"user\":")
                .append(user);
        sb.append(",\"title\":")
                .append(title);
        sb.append(",\"spanY\":")
                .append(spanY);
        sb.append(",\"spanX\":")
                .append(spanX);
        sb.append(",\"screenId\":")
                .append(screenId);
        sb.append(",\"requiresDbUpdate\":")
                .append(requiresDbUpdate);
        sb.append(",\"rank\":")
                .append(rank);
        sb.append(",\"minSpanY\":")
                .append(minSpanY);
        sb.append(",\"minSpanX\":")
                .append(minSpanX);
        sb.append(",\"itemType\":")
                .append(itemType);
        sb.append(",\"intent\":")
                .append(getIntent());
        sb.append(",\"id\":")
                .append(id);
        sb.append(",\"dropPos\":")
                .append(Arrays.toString(dropPos));
        sb.append(",\"contentDescription\":")
                .append(contentDescription);
        sb.append(",\"container\":")
                .append(container);
        sb.append(",\"cellY\":")
                .append(cellY);
        sb.append(",\"cellX\":")
                .append(cellX);
        sb.append('}');
        return sb.toString();
    }
}
