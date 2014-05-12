package no.fjeld.feed;

import java.util.*;

public class FeedList extends ArrayList <FeedItem> {

    /**
     * Actually not necessary since the list will be cleared before an item is
     * saved to the database.
     */
    private static final long serialVersionUID = -6728285211029336860L;
    public ArrayList <String> readItems;
    public ArrayList <FeedItem> savedItems;

    @Override
    public boolean add(FeedItem item) {

        /* If the item is a saved item. */
        if (item.getFeed() == null) 
            super.add(item);

        int i = 0, j = 0, k = 0;
        boolean added = false;
        boolean looping = true;

        while(looping) {

            if (i == super.size() && j == readItems.size() && k == savedItems.size())
                looping = false;

            if (i < super.size())
                if (item.getUrl().equals(super.get(i++).getUrl()))
                    return false;

            if (j < readItems.size())
                if (item.getUrl().equals(readItems.get(j++)))
                    return false;

            if (k < savedItems.size())
                if (item.getUrl().equals(savedItems.get(k++).getUrl()))
                    return false;

        }

        for (i = 0; i < super.size(); i++) {
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
