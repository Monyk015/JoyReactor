package y2k.joyreactor;

import rx.Observable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by y2k on 9/27/15.
 */
public class Post {

    public String title;
    public String image;

    public int width;
    public int height;

    public String userImage;
    public String userName;

    public Date created;

    public float getAspect() {
        float aspect = height == 0 ? 1 : (float) width / height;
        return Math.min(2, Math.max(1, aspect));
    }

    public static class Collection extends ArrayList<Post> {

        public static Observable<Post.Collection> request() {
            return ObservableUtils.create(() -> new PostRequest().getPosts());
        }
    }
}