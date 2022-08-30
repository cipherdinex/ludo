package com.codezon.ludofantacy.fragment;

import androidx.fragment.app.DialogFragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.codezon.ludofantacy.R;
import com.codezon.ludofantacy.view.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DialogFullscreenFragment extends DialogFragment {

    public TouchImageView photoIv;
    public ImageButton closeBt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fullscreen, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = Objects.requireNonNull(getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.black));
        }

        photoIv = view.findViewById(R.id.photoIv);
        closeBt = view.findViewById(R.id.closeBt);

        Bundle bundle;
        bundle = getArguments();
        if (bundle != null) {
            Picasso.get().load(bundle.getString("POST_KEY")).placeholder(R.drawable.placeholder_post).into(photoIv);
        }

        closeBt.setOnClickListener(v -> dismiss());

        return view;
    }
}