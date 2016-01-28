package at.fhs.smartsigncapture.controller;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import at.fhs.smartsigncapture.data.API.adapter.SSCUserAPIAdapter;
import at.fhs.smartsigncapture.data.SignDAL;
import at.fhs.smartsigncapture.model.Sign;
import at.fhs.smartsigncapture.model.Tag;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */
public class SignController {

    //region attributes

    //region static

    private static SignController instance;

    //endregion

    //region private

    private Context context;

    private SignDAL dal;

    //endregion

    //endregion

    //region Methods

    //region static

    public static SignController getInstance(Context context){
        if(instance == null){
            instance = new SignController(context);
        }
        return instance;
    }

    //endregion

    //region Constructor

    private SignController(Context context){
        this.context = context;
        dal = new SignDAL(context);
    }

    private SignController(){

    }

    //endregion

    //region Public

    public boolean storeSign(final Sign sign, final at.fhs.smartsigncapture.Callback<Sign> callback){
        boolean result = false;

        SSCUserAPIAdapter.APIInterface.postSign(UserController.getInstance(context).getCurrentUser().getId(), sign, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                dal.storeSign(sign);
                callback.success(sign);
            }

            @Override
            public void failure(RetrofitError error) {

                callback.failure(error.getMessage());
            }
        });

        return result;
    }

    public List<Sign> getAllSigns(){
        List<Sign> result = new ArrayList<>();

        result = this.dal.selectAllSigns();

        return result;
    }

    public List<Tag> getAllTags(){
        return this.dal.selectAllTags();
    }

    //endregion

    //endregion


}
