package com.mynews.common.core.auto.bundle.bridge;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SavedStateHandler {

    void saveInstanceState(@NonNull Object target, @NonNull Bundle state);

    void restoreInstanceState(@NonNull Object target, @Nullable Bundle state);

}
