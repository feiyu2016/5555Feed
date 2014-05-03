package no.fjeld.feed;

import java.util.*;

public class FeedList extends ArrayList <FeedItem> {

    public ArrayList <String> mReadItems;

    @Override
    public boolean add(FeedItem item) {

        boolean added = false;

        if (super.size() == 20)
            return false;

        /* If the item is marked as read, don't add it. */
        for (int i = 0; i < mReadItems.size(); i++)
            if (item.getUrl().equals(mReadItems.get(i)))
                return false;

        /* If the item is already in the list, don't add it. */
        for (int i = 0; i < super.size(); i++) 
            if (item.getUrl().equals(super.get(i).getUrl())) 
                return false;
             
        /* Adds the items by date. */
        for (int i = 0; i < super.size(); i++) {
            if (item.compareTo(super.get(i)) >= 0) {
                super.add(i, item);
                added = true;
                break;
            }
        }

        if (!added) 
            super.add(item);

        return true;

    }

}
