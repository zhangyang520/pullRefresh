package strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;

/**
 * 这个类实现了ListView下拉刷新，上加载更多和滑到底部自动加载
 * @author Li Hong
 * @since 2013-8-15
 */
public class PullToRefreshRecycleView extends PullToRefreshBase<RecyclerView>{;
    /**ListView*/
    private  RecyclerView mListView;
    /**用于滑到底部自动加载的Footer*/
    private LoadingLayout mLoadMoreFooterLayout;
    /**滚动的监听器*/
    private RecyclerView.OnScrollListener mScrollListener;


	/**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshRecycleView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs attrs
     */
    public PullToRefreshRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //FIXME 修改了内容
        setPullLoadEnabled(true);
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView listView = new RecyclerView(context);
        mListView = listView;
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        return listView;
    }
    /**
     * 设置是否有更多数据的标志
     * 
     * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHasMoreData(boolean hasMoreData) {
        if (!hasMoreData) {
            if (null != mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }
            
            LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }
        }
    }


    /**
     * 设置滑动的监听器
     * 
     * @param l 监听器
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener l) {
        mScrollListener = l;
    }
    
    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected void startLoading() {
        super.startLoading();
        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(ILoadingLayout.State.REFRESHING);
        }
    }
    
    @Override
    public void onPullUpRefreshComplete() {
        super.onPullUpRefreshComplete();
        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(ILoadingLayout.State.RESET);
        }
    }
    
    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollLoadEnabled(scrollLoadEnabled);
//        if (scrollLoadEnabled) {
//            // 设置Footer
//            if (null == mLoadMoreFooterLayout) {
//                mLoadMoreFooterLayout = new RotateFooterLoadingLayout(getContext());
//            }
//            if (null == mLoadMoreFooterLayout.getParent()) {
//                mListView.addFooterView(mLoadMoreFooterLayout, null, false);
//            }
//            mLoadMoreFooterLayout.show(true);
//        } else {
//            if (null != mLoadMoreFooterLayout) {
//                mLoadMoreFooterLayout.show(false);
//            }
//        }
    }
    
    @Override
    public LoadingLayout getFooterLoadingLayout() {
        if (isScrollLoadEnabled()) {
            return mLoadMoreFooterLayout;
        }
        return super.getFooterLoadingLayout();
    }

//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if (isScrollLoadEnabled() && hasMoreData()) {
//            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
//                    || scrollState == OnScrollListener.SCROLL_STATE_FLING) {
//                if (isReadyForPullUp()) {
//                    startLoading();
//                }
//            }
//        }
//
//        if (null != mScrollListener) {
//            mScrollListener.onScrollStateChanged(view, scrollState);
//        }
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        if (null != mScrollListener) {
//            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//        }
//    }
    
    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }
    
    /**
     * 表示是否还有更多数据
     * 
     * @return true表示还有更多数据
     */
    private boolean hasMoreData() {
        if ((null != mLoadMoreFooterLayout) && (mLoadMoreFooterLayout.getState() == ILoadingLayout.State.NO_MORE_DATA)) {
            return false;
        }
        return true;
    }
    
    /**
     * 判断第一个child是否完全显示出来
     * 
     * @return true完全显示出来，否则false
     */
    private boolean isFirstItemVisible() {
//        final Adapter adapter = mListView.getAdapter();
//
//        if (null == adapter || adapter.isEmpty()) {
//            return true;
//        }
//        int mostTop = (mListView.getChildCount() > 0) ? mListView.getChildAt(0).getTop() : 0;
//        System.out.println("isFirstItemVisible ....mostTop:"+mostTop);
//        if (mostTop >= 0) {
//            return true;
//        }
//
//        return false;

        return false;
    }

    /**
     * 判断最后一个child是否完全显示出来
     * 
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
//        final Adapter adapter = mListView.getAdapter();
//
//        if (null == adapter || adapter.isEmpty()) {
//            return true;
//        }
//        final int lastItemPosition = adapter.getCount() - 1;
//        final int lastVisiblePosition = mListView.getLastVisiblePosition();
//
//        /**
//         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
//         * internally uses a FooterView which messes the positions up. For me we'll just subtract
//         * one to account for it and rely on the inner condition which checks getBottom().
//         */
//        if (lastVisiblePosition >= lastItemPosition - 1) {
//            final int childIndex = lastVisiblePosition - mListView.getFirstVisiblePosition();
//            final int childCount = mListView.getChildCount();
//            final int index = Math.min(childIndex, childCount - 1);
//            final View lastVisibleChild = mListView.getChildAt(index);
//            if (lastVisibleChild != null) {
//                return lastVisibleChild.getBottom() <= mListView.getBottom();
//            }
//        }
//        return false;

        return false;
    }
    
    public RecyclerView getmListView() {
 		return mListView;
 	}

 	public void setmListView(RecyclerView mListView) {
 		this.mListView = mListView;
 	}
}
