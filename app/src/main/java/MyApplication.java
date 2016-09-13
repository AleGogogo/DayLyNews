import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by rain on 2016/9/13.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }

    private void initImageLoader(Context applicationContext) {

        File cacheDir = StorageUtils.getCacheDirectory(applicationContext);
        //建造者模式
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(
//                applicationContext
//        );
//        ImageLoader.getInstance().init(configuration);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(applicationContext)
                 .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                //一张图，可以缓存成多种size显示
                .denyCacheImageMultipleSizesInMemory()
                //该类把源文件的名称同过md5加密后保存
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //设置一个不限制缓存大小的磁盘缓存
                .diskCache(new UnlimitedDiskCache(cacheDir)).writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

    }
}
