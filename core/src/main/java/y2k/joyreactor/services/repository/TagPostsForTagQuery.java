package y2k.joyreactor.services.repository;

import y2k.joyreactor.Tag;
import y2k.joyreactor.TagPost;

/**
 * Created by y2k on 11/9/15.
 */
@Deprecated
public class TagPostsForTagQuery extends Repository.Query<TagPost> {

    private Tag tag;

    public TagPostsForTagQuery(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean compare(TagPost row) {
        return row.getTagId() == tag.getId();
    }
}