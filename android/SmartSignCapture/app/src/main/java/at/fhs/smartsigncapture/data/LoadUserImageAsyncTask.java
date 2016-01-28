package at.fhs.smartsigncapture.data;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.fhs.smartsigncapture.model.Contact;
import at.fhs.smartsigncapture.utils.ImageUtils;

/**
 * Created by MartinTiefengrabner on 17/09/15.
 */
public class LoadUserImageAsyncTask extends AsyncTask<Contact,Void,List<File>> {

    private Context context;

    public LoadUserImageAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected List<File> doInBackground(Contact... params) {

        List<File> result = new ArrayList<>();

        for(Contact c : params) {
            result.add(ImageUtils.downloadContactImage(c, context));
        }

        return result;
    }
}
