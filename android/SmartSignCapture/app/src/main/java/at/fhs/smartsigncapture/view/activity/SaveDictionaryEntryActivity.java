package at.fhs.smartsigncapture.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.SignController;
import at.fhs.smartsigncapture.model.Sign;
import at.fhs.smartsigncapture.model.Tag;
import at.fhs.smartsigncapture.view.autocomplete.TagCompletionView;

public class SaveDictionaryEntryActivity extends AppCompatActivity {

    public static final String EXTRAS_BUNDLE_KEY_SIGN = "sign";

    private String signContent;

    private SignController signController;

    private EditText nameTextField;

    private TagCompletionView tokenCompletionTextView;

    private ArrayAdapter<Tag> tagsAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_dictionary_entry);

        this.signController = SignController.getInstance(this);

        if (getIntent().hasExtra(EXTRAS_BUNDLE_KEY_SIGN)) {
            this.signContent = getIntent().getStringExtra(EXTRAS_BUNDLE_KEY_SIGN);
        }

        initActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_dictionary_entry, menu);
        return true;
    }


    private void saveSign(){
        String name = this.nameTextField.getText().toString();
        Sign sign = new Sign(name,"",new Date(),signContent, this.tokenCompletionTextView.getObjects());
        this.signController.storeSign(sign, new Callback<Sign>() {
            @Override
            public void success(Sign sign) {
                //Intent intent = new Intent(SaveDictionaryEntryActivity.this, HomeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                onBackPressed();
            }

            @Override
            public void failure(String errorMessage) {
                Toast.makeText(SaveDictionaryEntryActivity.this,R.string.error_msg_dict_entry_save,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initActivity(){

        List<Tag> tags = this.signController.getAllTags();

        tags.add(new Tag("tag"));

        tagsAdpater =  new FilteredArrayAdapter<Tag>(this, R.layout.layout_tag, tags) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.layout_tag, parent, false);
                }

                Tag t = getItem(position);
                ((TextView)convertView.findViewById(R.id.edTag)).setText(t.getTag());
                return convertView;
            }

            @Override
            protected boolean keepObject(Tag tag, String mask) {
                mask = mask.toLowerCase();
                return tag.getTag().toLowerCase().startsWith(mask);
            }
        };

        tokenCompletionTextView = (TagCompletionView)findViewById(R.id.tokenView);
        tokenCompletionTextView.performBestGuess(false);
        tokenCompletionTextView.setSplitChar(' ');
        tokenCompletionTextView.setAdapter(tagsAdpater);

        tokenCompletionTextView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

        this.nameTextField = (EditText) findViewById(R.id.edName);

        findViewById(R.id.fabSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             saveSign();
            }
        });
    }
}
