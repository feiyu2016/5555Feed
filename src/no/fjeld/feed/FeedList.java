package no.fjeld.feed;

import java.util.*;

public class FeedList extends ArrayList <FeedItem> {

    private static final long serialVersionUID = -6728285211029336860L;
    public HashSet <String> readItems;
    public HashSet <String> savedItems;

    // TODO: This function needs some serious optimization.
    @Override
    public boolean add(FeedItem item) {

        /* If the item is a saved item. */
        if (item.getFeed() == null) 
            super.add(item);

        /* If the item is marked as read */
        if (readItems.contains(item.getUrl()))
            return false;
    
        /* If the item is saved. */
        if (savedItems.contains(item.getUrl()))
            return false;
        
        /* If the item already is in the list */
        for (FeedItem iterItem : this)
            if (item.getUrl().equals(iterItem.getUrl()))
                return false;

        /* Add the item at the correct position in
         * the list. (Sorted by time and date) */
        for (int i = 0; i < super.size(); i++) {
            if (item.compareTo(super.get(i)) >= 0) {
                super.add(i, item);
                return true;
            }
        }

        super.add(item);
        return true;

    }

}
