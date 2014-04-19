public class NavigationDrawer {

    private Activity activity;
    private View view;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private DrawerAdapter mDrawerAdapter;

    /**
     * Constructor for the class NavgigationDrawer.
     */
    public NavigationDrawer(Activity activity, View view) {

        this.activity = activity;
        this.view = view;

    }

    /**
     * Initializes the DrawerLayout.
     */
    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

    }

    /**
     * Initializes the DrawerLayouts ListView.
     */ 
    private void initDrawerListView() {

        mDrawerListView = (ListView) view.findViewById(R.id.drawer_list);

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initDrawerAdapter() {

        mDrawerAdapter = new DrawerAdapter(
                activity, R.layout.drawer_item, getDrawerList());

    }

    /**
     * Returns an ArrayList loaded from the SharedPreferences-set "drawer_items".
     */
    private ArrayList <DrawerItem> getDrawerList() {

        ArrayList <DrawerItem> mDrawerList = new ArrayList <DrawerItem> (); 

        Gson mGson = new GsonBuilder().setPrettyPrinting().create();
        SharedPreferences mSharedPrefs = PreferenceManager.
            getDefaultSharedPreferences(activity.getBaseContext());

        Set <String> mDrawerSet = mSharedPrefs.
            getStringSet("drawer_items", new LinkedHashSet <String> ());

        for (String item : mDrawerSet)
            mDrawerList.add(mGson.fromJson(item, DrawerItem.class));

        if (mDrawerList.size() > 1)
            mDrawerList = sorted(mDrawerList);

        return mDrawerList;

    }

    /**
     * Sorts the items alphabetically based on their titles. 
     *
     * @param  mTempItems The list to be sorted.
     * @return mTempItems The sorted list.
     */
    private ArrayList <DrawerItem> sorted(ArrayList <DrawerItem> mTempList) {

        Collections.sort(mTempList, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo(
                    second.getFeedName().toLowerCase());
            }

        });

        return mTempList;

    }

}
