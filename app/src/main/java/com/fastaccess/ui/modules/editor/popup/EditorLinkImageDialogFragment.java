package com.fastaccess.ui.modules.editor.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.FontButton;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 15 Apr 2017, 9:14 PM
 */

public class EditorLinkImageDialogFragment extends BaseDialogFragment<EditorLinkImageMvp.View, EditorLinkImagePresenter>
        implements EditorLinkImageMvp.View {

    private EditorLinkImageMvp.EditorLinkCallback callback;

    @BindView(R.id.title) TextInputLayout title;
    @BindView(R.id.link) TextInputLayout link;
    @BindView(R.id.select) FontButton select;

    public static EditorLinkImageDialogFragment newInstance(boolean isLink) {
        EditorLinkImageDialogFragment fragment = new EditorLinkImageDialogFragment();
        fragment.setArguments(Bundler
                .start()
                .put(BundleConstant.YES_NO_EXTRA, isLink)
                .end());
        return fragment;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditorLinkImageMvp.EditorLinkCallback) {
            callback = (EditorLinkImageMvp.EditorLinkCallback) context;
        }
    }

    @Override public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override public void onUploaded(@Nullable String title, @Nullable String link) {
        hideProgress();
        if (callback != null) {
            callback.onAppendLink(title, link, isLink());
        }
        dismiss();
    }

    @Override protected int fragmentLayout() {
        return R.layout.markdown_link_image_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        select.setVisibility(isLink() ? View.GONE : View.VISIBLE);
    }

    @NonNull @Override public EditorLinkImagePresenter providePresenter() {
        return new EditorLinkImagePresenter();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            if (data != null && data.getData() != null) {
                String path = FileHelper.getPath(getContext(), data.getData());
                if (!InputHelper.isEmpty(path)) {
                    getPresenter().onSubmit(InputHelper.toString(title), new File(path));
                }
            }
        }
    }

    @OnClick(R.id.select) public void onSelectClicked() {
        if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), BundleConstant.REQUEST_CODE);
        }
    }

    @OnClick(R.id.cancel) public void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.insert) public void onInsertClicked() {
        if (callback != null) {
            callback.onAppendLink(InputHelper.toString(title), InputHelper.toString(link), isLink());
        }
        dismiss();
    }

    private boolean isLink() {
        return getArguments() != null && getArguments().getBoolean(BundleConstant.YES_NO_EXTRA);
    }
}