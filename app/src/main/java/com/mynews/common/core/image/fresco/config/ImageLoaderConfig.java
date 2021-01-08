package com.mynews.common.core.image.fresco.config;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class ImageLoaderConfig {

    protected static final String IMAGE_PIPELINE_CACHE_DIR = "image_cache";

    protected static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "image_small_cache";

    protected static final int MAX_DISK_SMALL_CACHE_SIZE = 10 * ByteConstants.MB;

    protected static final int MAX_DISK_SMALL_ONLOWDISKSPACE_CACHE_SIZE = 5 * ByteConstants.MB;

    protected Context mContext;
    protected ImagePipelineConfig.Builder mImagePipelineConfigBuilder;

    private static ImageLoaderConfig sImageLoaderConfig;

    protected ImageLoaderConfig(Context context) {
        mContext = context.getApplicationContext();
        toggleLog();
    }

    public static ImageLoaderConfig getInstance(Context context) {
        if (sImageLoaderConfig == null) {
            synchronized (ImageLoaderConfig.class) {
                if (sImageLoaderConfig == null) {
                    sImageLoaderConfig = new ImageLoaderConfig(context);
                }
            }
        }
        return sImageLoaderConfig;
    }

    public ImagePipelineConfig.Builder getImagePipelineConfigBuilder() {
        if (mImagePipelineConfigBuilder == null) {
            mImagePipelineConfigBuilder = createConfigBuilder()
                    .setBitmapsConfig(Bitmap.Config.ARGB_8888) // 若不是要求忒高清显示应用，就用使用RGB_565吧（默认是ARGB_8888)
                    .setDownsampleEnabled(true) // 在解码时改变图片的大小，支持PNG、JPG以及WEBP格式的图片，与ResizeOptions配合使用
                    // 设置Jpeg格式的图片支持渐进式显示
//                    .setProgressiveJpegConfig(new ProgressiveJpegConfig() {
//                        @Override
//                        public int getNextScanNumberToDecode(int scanNumber) {
//                            return scanNumber + 2;
//                        }
//
//                        public QualityInfo getQualityInfo(int scanNumber) {
//                            boolean isGoodEnough = (scanNumber >= 5);
//                            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
//                        }
//                    })
                    .setRequestListeners(getRequestListeners())
                    .setMemoryTrimmableRegistry(getMemoryTrimmableRegistry()) // 报内存警告时的监听
                    // 设置内存配置
                    .setBitmapMemoryCacheParamsSupplier(new BitmapMemoryCacheParamsSupplier(
                            (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)))
                    .setMainDiskCacheConfig(getMainDiskCacheConfig()) // 设置主磁盘配置
                    .setSmallImageDiskCacheConfig(getSmallDiskCacheConfig()); // 设置小图的磁盘配置
        }
        return mImagePipelineConfigBuilder;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient()
                    .newBuilder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;

                        }
                    })
                    .build();
        } catch (Exception e) {
            return null;
        }

    }

    protected ImagePipelineConfig.Builder createConfigBuilder() {
        OkHttpClient client = getUnsafeOkHttpClient();
        if (client != null) {
            return OkHttpImagePipelineConfigFactory.newBuilder(mContext, client);
        } else {
            return ImagePipelineConfig.newBuilder(mContext);
        }
    }

    /**
     * 当内存紧张时采取的措施
     *
     * @return
     */
    protected MemoryTrimmableRegistry getMemoryTrimmableRegistry() {
        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                Log.i("Image", "Fresco onCreate suggestedTrimRatio = " + suggestedTrimRatio);

                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                ) {
                    // 清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
                }
            }
        });
        return memoryTrimmableRegistry;
    }

    /**
     * LOG开关
     */
    protected void toggleLog() {
//        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }

    /**
     * 设置网络请求监听
     *
     * @return
     */
    protected Set<RequestListener> getRequestListeners() {
        Set<RequestListener> requestListeners = new HashSet<>();
//        requestListeners.add(new RequestLoggingListener());
        return requestListeners;
    }

    /**
     * 获取主磁盘配置
     *
     * @return
     */
    protected DiskCacheConfig getMainDiskCacheConfig() {
        /**
         * 推荐缓存到应用本身的缓存文件夹，这么做的好处是:
         * 1、当应用被用户卸载后能自动清除缓存，增加用户好感（可能以后用得着时，还会想起我）
         * 2、一些内存清理软件可以扫描出来，进行内存的清理
         */
        File fileCacheDir = mContext.getCacheDir();
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                fileCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fresco");
//            }

        return DiskCacheConfig.newBuilder(mContext)
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                .setBaseDirectoryPath(fileCacheDir)
                .build();
    }

    /**
     * 获取小图的磁盘配置（辅助）
     *
     * @return
     */
    protected DiskCacheConfig getSmallDiskCacheConfig() {
        /**
         * 推荐缓存到应用本身的缓存文件夹，这么做的好处是:
         * 1、当应用被用户卸载后能自动清除缓存，增加用户好感（可能以后用得着时，还会想起我）
         * 2、一些内存清理软件可以扫描出来，进行内存的清理
         */
        File fileCacheDir = mContext.getCacheDir();
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                fileCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fresco");
//            }

        return DiskCacheConfig.newBuilder(mContext)
                .setBaseDirectoryPath(fileCacheDir)
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)
                .setMaxCacheSize(MAX_DISK_SMALL_CACHE_SIZE)
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_SMALL_ONLOWDISKSPACE_CACHE_SIZE)
                .build();
    }

}
