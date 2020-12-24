package com.box.app.news.data.adapter;

import androidx.annotation.NonNull;

import com.github.gfx.android.orma.annotation.StaticTypeAdapter;
import com.github.gfx.android.orma.annotation.StaticTypeAdapters;

import java.util.ArrayList;

import com.box.app.news.bean.Article;
import com.box.app.news.bean.Comment;
import com.box.app.news.bean.Essay;
import com.box.app.news.bean.GIF;
import com.box.app.news.bean.Image;
import com.box.app.news.bean.ImageInfo;
import com.box.app.news.bean.RatioImagesInfo;
import com.box.app.news.bean.Tag;
import com.box.app.news.bean.Video;
import com.box.app.news.bean.VideoInGIF;
import com.box.common.core.json.gson.util.CoreGsonUtils;

/**
 * 用来翻译ImageInfo对象
 */
@StaticTypeAdapters({
        @StaticTypeAdapter(
                targetType = ImageInfo.class,
                serializedType = String.class,
                serializer = "serializerImageInfo",
                deserializer = "deserializerImageInfo"
        ),
        @StaticTypeAdapter(
                targetType = Tag.class,
                serializedType = String.class,
                serializer = "serializerTag",
                deserializer = "deserializerTag"
        ),
        @StaticTypeAdapter(
                targetType = ArrayList.class,
                serializedType = String.class,
                serializer = "serializerArrayList",
                deserializer = "deserializerArrayList"
        ),
        @StaticTypeAdapter(
                targetType = RatioImagesInfo.class,
                serializedType = String.class,
                serializer = "serializerRatioImagesInfo",
                deserializer = "deserializerRatioImagesInfo"
        ),
        @StaticTypeAdapter(
                targetType = VideoInGIF.class,
                serializedType = String.class,
                serializer = "serializerVideoInGIF",
                deserializer = "deserializerVideoInGIF"
        ),
        @StaticTypeAdapter(
                targetType = Article.class,
                serializedType = String.class,
                serializer = "serializerArticle",
                deserializer = "deserializerArticle"
        ),
        @StaticTypeAdapter(
                targetType = Video.class,
                serializedType = String.class,
                serializer = "serializerVideo",
                deserializer = "deserializerVideo"
        ),
        @StaticTypeAdapter(
                targetType = Image.class,
                serializedType = String.class,
                serializer = "serializerImage",
                deserializer = "deserializerImage"
        ),
        @StaticTypeAdapter(
                targetType = GIF.class,
                serializedType = String.class,
                serializer = "serializerGIF",
                deserializer = "deserializerGIF"
        ),
        @StaticTypeAdapter(
                targetType = Comment.class,
                serializedType = String.class,
                serializer = "serializerComment",
                deserializer = "deserializerComment"
        ),
        @StaticTypeAdapter(
                targetType = Essay.class,
                serializedType = String.class,
                serializer = "serializerEssay",
                deserializer = "deserializerEssay"
        )
})
@SuppressWarnings({"WeakerAccess", "unused"})
public class DBNewsAdapter {

    public static String serializerImageInfo(@NonNull ImageInfo info) {
        return CoreGsonUtils.Companion.toJson(info);
    }

    @NonNull
    public static ImageInfo deserializerImageInfo(String json) {
        try {
            ImageInfo info = CoreGsonUtils.Companion.fromJson(json, ImageInfo.class);
            if (info == null) {
                info = new ImageInfo();
            }
            return info;
        } catch (Exception e) {
            return new ImageInfo();
        }
    }

    public static String serializerVideoInGIF(@NonNull VideoInGIF videoInGIF) {
        return CoreGsonUtils.Companion.toJson(videoInGIF);
    }

    @NonNull
    public static VideoInGIF deserializerVideoInGIF(String json) {
        try {
            VideoInGIF info = CoreGsonUtils.Companion.fromJson(json, VideoInGIF.class);
            if (info == null) {
                info = new VideoInGIF();
            }
            return info;
        } catch (Exception e) {
            return new VideoInGIF();
        }
    }

    public static String serializerTag(@NonNull Tag tag) {
        return CoreGsonUtils.Companion.toJson(tag);
    }

    @NonNull
    public static Tag deserializerTag(String json) {
        try {
            Tag info = CoreGsonUtils.Companion.fromJson(json, Tag.class);
            if (info == null) {
                info = new Tag();
            }
            return info;
        } catch (Exception e) {
            return new Tag();
        }
    }

    public static String serializerArrayList(@NonNull ArrayList list) {
        return CoreGsonUtils.Companion.toJson(list);
    }

    @NonNull
    public static ArrayList deserializerArrayList(String json) {
        try {
            ArrayList list = CoreGsonUtils.Companion.fromJson(json, ArrayList.class);
            if (list == null) {
                list = new ArrayList();
            }
            return list;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public static String serializerRatioImagesInfo(@NonNull RatioImagesInfo info) {
        return CoreGsonUtils.Companion.toJson(info);
    }

    @NonNull
    public static RatioImagesInfo deserializerRatioImagesInfo(String json) {
        try {
            RatioImagesInfo info = CoreGsonUtils.Companion.fromJson(json, RatioImagesInfo.class);
            if (info == null) {
                info = new RatioImagesInfo();
            }
            return info;
        } catch (Exception e) {
            return new RatioImagesInfo();
        }
    }

    public static String serializerArticle(@NonNull Article article) {
        return CoreGsonUtils.Companion.toJson(article);
    }

    @NonNull
    public static Article deserializerArticle(String json) {
        try {
            Article article = CoreGsonUtils.Companion.fromJson(json, Article.class);
            if (article == null) {
                article = new Article();
            }
            return article;
        } catch (Exception e) {
            return new Article();
        }
    }

    public static String serializerVideo(@NonNull Video video) {
        return CoreGsonUtils.Companion.toJson(video);
    }

    @NonNull
    public static Video deserializerVideo(String json) {
        try {
            Video video = CoreGsonUtils.Companion.fromJson(json, Video.class);
            if (video == null) {
                video = new Video();
            }
            return video;
        } catch (Exception e) {
            return new Video();
        }
    }

    public static String serializerImage(@NonNull Image image) {
        return CoreGsonUtils.Companion.toJson(image);
    }

    @NonNull
    public static Image deserializerImage(String json) {
        try {
            Image image = CoreGsonUtils.Companion.fromJson(json, Image.class);
            if (image == null) {
                image = new Image();
            }
            return image;
        } catch (Exception e) {
            return new Image();
        }
    }

    public static String serializerGIF(@NonNull GIF gif) {
        return CoreGsonUtils.Companion.toJson(gif);
    }

    @NonNull
    public static GIF deserializerGIF(String json) {
        try {
            GIF gif = CoreGsonUtils.Companion.fromJson(json, GIF.class);
            if (gif == null) {
                gif = new GIF();
            }
            return gif;
        } catch (Exception e) {
            return new GIF();
        }
    }

    public static String serializerComment(@NonNull Comment comment) {
        return CoreGsonUtils.Companion.toJson(comment);
    }

    @NonNull
    public static Comment deserializerComment(String json) {
        try {
            Comment comment = CoreGsonUtils.Companion.fromJson(json, Comment.class);
            if (comment == null) {
                comment = new Comment();
            }
            return comment;
        } catch (Exception e) {
            return new Comment();
        }
    }

    public static String serializerEssay(@NonNull Essay essay) {
        return CoreGsonUtils.Companion.toJson(essay);
    }

    @NonNull
    public static Essay deserializerEssay(String json) {
        try {
            Essay essay = CoreGsonUtils.Companion.fromJson(json, Essay.class);
            if (essay == null) {
                essay = new Essay();
            }
            return essay;
        } catch (Exception e) {
            return new Essay();
        }
    }
}