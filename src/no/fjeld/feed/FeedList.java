package no.fjeld.feed;

import java.util.*;

public class FeedList extends ArrayList <FeedItem> {

    /**
     * Actually not necessary since the list will be cleared before an item is
     * saved to the database.
     */
    private static final long serialVersionUID = -6728285211029336860L;
    public ArrayList <String> readItems;

    @Override
    public boolean add(FeedItem item) {
        
        int i = 0;
        boolean added = false;

        /* If the item is marked as read, don't add it. */
        for (i = 0; i < readItems.size(); i++)
            if (item.getUrl().equals(readItems.get(i)))
                return false;

        /* If the item is already in the list, don't add it. */
        for (i = 0; i < super.size() && i < 20; i++) 
            if (item.getUrl().equals(super.get(i).getUrl())) 
                return false;
             
        /* Adds the items by date. */
        for (i = 0; i < super.size() && i < 20; i++) {
            if (item.compareTo(super.get(i)) >= 0) {
                super.add(i, item);
                added = true;
                break;
            }
        }

        if (!added && i < 20) 
            super.add(item);

        return true;

    }

}
