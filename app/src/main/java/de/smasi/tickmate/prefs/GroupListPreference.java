package de.smasi.tickmate.prefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.MultiSelectListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.smasi.tickmate.R;
import de.smasi.tickmate.database.DataSource;
import de.smasi.tickmate.models.Group;
import de.smasi.tickmate.models.Track;
import de.smasi.tickmate.views.EditGroupsActivity;

/**
 * Created by hannes on 08.10.15.
 */
public class GroupListPreference extends MultiSelectListPreference {
    private static DataSource mDataSource = DataSource.getInstance();
    private Track track;
    static private String TAG = "GroupListPreference";

    public GroupListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNeutralButton(R.string.menu_edit_groups, (DialogInterface dialog, int which) -> {
            Intent intent = new Intent(getContext(), EditGroupsActivity.class);
            getContext().startActivity(intent);
        });
    }

    @Override
    protected void onClick() {
        /*
         * If there are no groups when this preference is clicked, open the "Edit
         * Groups" activity.
         */
        if (mDataSource.getGroups().size() == 0) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.no_groups_found)
                    .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                        Intent intent = new Intent(getContext(), EditGroupsActivity.class);
                        getContext().startActivity(intent);
                    })
                    .setNegativeButton(android.R.string.no, (DialogInterface dialog, int which) -> {})
                    .show();
        }
        else {
            super.onClick();
        }
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public void populate() {
        setValues(getGroupIdsForTrackAsSet(track));
        setEntries(getAllGroupNamesAsCharSeq());
        setEntryValues(getAllGroupIdsAsCharSeq());
        setSummary(getGroupNamesForSummary(track));
    }

    // used by Preference.setSummary
    // Get the name of the Groups that this track belongs to
    private static CharSequence[] getGroupNamesForTrackAsCharSeq(Track track) {
        List<String> names = new ArrayList<>();
        for (Group group : mDataSource.getGroupsForTrack(track.getId())){
            names.add(group.getName());
        }
        return names.toArray(new CharSequence[names.size()]);
    }

    private static Set<String> getGroupIdsForTrackAsSet(Track track) {
        Set<String> ids = new HashSet<>();
        for (Group g : mDataSource.getGroupsForTrack(track.getId())) {
            ids.add(Integer.toString(g.getId()));
        }
        Log.d(TAG, "getGroupIdsForTrackAsSet is returning: " + TextUtils.join(",", ids));
        return ids;
    }

    // Preference.setEntries requires a CharSequence[]
    private static CharSequence[] getAllGroupNamesAsCharSeq() {
        List<Group> groups = mDataSource.getGroups();
        List<String> names = new ArrayList<>();
        for (Group g : groups) {
            names.add(g.getName());
        }
        CharSequence[] cs = names.toArray(new CharSequence[names.size()]);
        Log.d(TAG, "getAllGroupNamesAsCharSeq is returning: " + TextUtils.join(",", cs));
        return cs;
    }

    // Preferences.setEntryValues requires a CharSequence[]
    private static CharSequence[] getAllGroupIdsAsCharSeq() {
        List<Group> groups = mDataSource.getGroups();
        List<String> ids = new ArrayList<>();
        for (Group g : groups) {
            ids.add(String.valueOf(g.getId()));
        }
        CharSequence[] cs = ids.toArray(new CharSequence[ids.size()]);
        Log.d(TAG, "getAllGroupIdsAsCharSeq is returning: " + TextUtils.join(",", cs));
        return cs;
    }

    // Concatenates the names of the groups associated with this track into a readable comma separated list.
    // Consider internationalizing this better - how are lists done in other languages? Joined with other characters?
    private String getGroupNamesForSummary(Track track) {
        return TextUtils.join(", ", getGroupNamesForTrackAsCharSeq(track));
    }

}
