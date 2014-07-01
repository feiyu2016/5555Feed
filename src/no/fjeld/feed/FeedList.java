package no.fjeld.feed;

import java.util.*;

public class FeedList extends ArrayList <FeedItem> {

    private static final long serialVersionUID = -6728285211029336860L;
    public HashSet <String> readItems;
    public ArrayList <FeedItem> savedItems;

    // TODO: This function needs some serious optimization.
    @Override
    public boolean add(FeedItem item) {

        /* If the item is a saved item. */
        if (item.getFeed() == null) 
            super.add(item);

        int i = 0, j = 0, k = 0;
        boolean added = false;
        boolean looping = true;

        while(looping) {

            if (i < super.size())
                if (item.getUrl().equals(super.get(i++).getUrl()))
                    return false;

            if (k < savedItems.size())
                if (item.getUrl().equals(savedItems.get(k++).getUrl()))
                    return false;

        }
        
        if (readItems.contains(item.getUrl()))
            return false;

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
