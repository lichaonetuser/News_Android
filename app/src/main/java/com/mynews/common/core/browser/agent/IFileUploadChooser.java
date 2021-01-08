package com.mynews.common.core.browser.agent;

import android.content.Intent;


public interface IFileUploadChooser {



    void openFileChooser();

    void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data);
}
