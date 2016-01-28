package at.fhs.smartsigncapture.view.autocomplete;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.model.Tag;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */
public class TagCompletionView extends TokenCompleteTextView<Tag>{

    public TagCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }



    @Override
    protected View getViewForObject(Tag tag) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.token_tag, (ViewGroup)TagCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(tag.getTag());

        return view;
    }

    @Override
    protected Tag defaultObject(String s) {
        return new Tag(s);
    }

}
