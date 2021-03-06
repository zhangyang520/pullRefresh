package strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import strollmuseum.iot.zhjy.com.pullrefresh.utils.DateUtil;
import strollmuseum.iot.zhjy.com.pullrefresh.utils.UiUtils;

/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 * 
 * @author Li Hong
 * @since 2013-7-29
 * @param <T>
 */
public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T> {
    /**
     * 定义了下拉刷新和上拉加载更多的接口。
     * 
     * @author Li Hong
     * @since 2013-7-29
     */
    public interface OnRefreshListener<V extends View> {
     
        /**
         * 下拉松手后会被调用
         * 
         * @param refreshView 刷新的View
         */
        void onPullDownToRefresh(final PullToRefreshBase<V> refreshView);
        
        /**
         * 加载更多时会被调用或上拉时调用
         * 
         * @param refreshView 刷新的View
         */
        void onPullUpToRefresh(final PullToRefreshBase<V> refreshView);
    }
    
    /**回滚的时间*/
    private static final int SCROLL_DURATION = 150;
    /**阻尼系数*/
    private static final float OFFSET_RADIO = 2.5f;
    /**上一次移动的点 */
    private float mLastMotionY = -1;
    /**下拉刷新和加载更多的监听器 */
    private OnRefreshListener<T> mRefreshListener;
    /**下拉刷新的布局 */
    private LoadingLayout mHeaderLayout;
    /**上拉加载更多的布局*/
    private LoadingLayout mFooterLayout;
    /**HeaderView的高度*/
    private int mHeaderHeight;
    /**FooterView的高度*/
    private int mFooterHeight;
    /**下拉刷新是否可用*/
    private boolean mPullRefreshEnabled = true;
    /**上拉加载是否可用*/
    private boolean mPullLoadEnabled = true;
    /**判断滑动到底部加载是否可用*/
    private boolean mScrollLoadEnabled = false;
    /**是否截断touch事件*/
    private boolean mInterceptEventEnable = true;
    /**表示是否消费了touch事件，如果是，则不调用父类的onTouchEvent方法*/
    private boolean mIsHandledTouchEvent = false;
    /**移动点的保护范围值*/
    private int mTouchSlop;
    /**下拉的状态*/
    private ILoadingLayout.State mPullDownState = ILoadingLayout.State.NONE;
    /**上拉的状态*/
    private ILoadingLayout.State mPullUpState = ILoadingLayout.State.NONE;
    /**可以下拉刷新的View*/
    T mRefreshableView;
    /**平滑滚动的Runnable*/
    private SmoothScrollRunnable mSmoothScrollRunnable;
    /**可刷新View的包装布局*/
    private FrameLayout mRefreshableViewWrapper;
    
    private String pullUpWhenPullDownError="下拉刷新中,不能加载更多";
    private String pullDownWhenPullUpError="上拉加载中,不能下拉刷新";
    private String refreshTitle;

    boolean headFootOpposite;
    /**
     * 构造方法
     * @param context context
     */
    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造方法
     * @param context context
     * @param attrs attrs
     */
    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshBase(Context context,boolean headFootOpposite) {
        super(context);
        this.headFootOpposite=headFootOpposite;
        init(context, null);
    }
    /**
     * 构造方法
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        if(!headFootOpposite){
            mHeaderLayout = createHeaderLoadingLayout(context, attrs);
            mFooterLayout = createFooterLoadingLayout(context, attrs);
        }else{
            System.out.println("mFooterLayout 111111");
            mFooterLayout = createHeaderLoadingLayout(context, attrs);
            System.out.println("mFooterLayout 222222");
//            Toast.makeText(context,"设置了",Toast.LENGTH_SHORT).show();
            mFooterLayout.setPullToRefreshString("上拉可以刷新");
            mFooterLayout.setReleaseToRefreshing("松开可以刷新");
            mFooterLayout.setRefreshing("上拉刷新,正在加载中");

            mHeaderLayout = createFooterLoadingLayout(context, attrs);
            mHeaderLayout.setPullToRefreshString("下拉可以加载更多");
            mHeaderLayout.setReleaseToRefreshing("松开可以加载更多");
            mHeaderLayout.setRefreshing("下拉加载更多中...");
        }
        mRefreshableView = createRefreshableView(context, attrs);
        
        if (null == mRefreshableView) {
            throw new NullPointerException("Refreshable view can not be null.");
        }
        
        addRefreshableView(context, mRefreshableView);
        addHeaderAndFooter(context);

        // 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshLoadingViewsSize();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }
    
    /**
     * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
     */
    private void refreshLoadingViewsSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        int headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getContentSize() : 0;
        int footerHeight = (null != mFooterLayout) ? mFooterLayout.getContentSize() : 0;
        
        if (headerHeight < 0) {
            headerHeight = 0;
        }
        if (footerHeight < 0) {
            footerHeight = 0;
        }
        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;

        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = -headerHeight;
        int pBottom = -footerHeight;
        
        //进行缩减显示的范围
        setPadding(pLeft, pTop, pRight, pBottom);
    }
    
    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // We need to update the header/footer when our size changes
        refreshLoadingViewsSize();
        
        // 设置刷新View的大小
        refreshRefreshableViewSize(w, h);
        
        /**
         * As we're currently in a Layout Pass, we need to schedule another one
         * to layout any changes we've made here
         */
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }
    
    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.VERTICAL != orientation) {
            throw new IllegalArgumentException("This class only supports VERTICAL orientation.");
        }
        
        // Only support vertical orientation
        super.setOrientation(orientation);
    }
    
    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        System.out.println("onInterceptTouchEvent...");
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }
        
        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            System.out.println("onInterceptTouchEvent... !isPullLoadEnabled ||| !isPullRefreshEnabled");
            return false;
        }
        
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            return false;
        }
        
        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }
        
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionY = event.getY();
            mIsHandledTouchEvent = false;
            break;
            
        case MotionEvent.ACTION_MOVE:
            final float deltaY = event.getY() - mLastMotionY;
            final float absDiff = Math.abs(deltaY);
            // 这里有三个条件：
            // 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
            // 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
            // 3，isPullLoading()，理由与第2条相同
            if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading())  {
                mLastMotionY = event.getY();
                // 第一个显示出来，Header已经显示或拉下
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    // 1，Math.abs(getScrollY()) > 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
                    // 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
                    // 依然能向上滑动，直到HeaderView完全不可见
                    // 2，deltaY > 0.5f：表示下拉的值大于0.5f
                    mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY > 0.5f);
                    // 如果截断事件，我们则仍然把这个事件交给刷新View去处理，典型的情况是让ListView/GridView将按下
                    // Child的Selector隐藏
                    if (mIsHandledTouchEvent) {
                        mRefreshableView.onTouchEvent(event);
                    }
                    System.out.println("onInterceptTouchEvent... ACTION_MOVE PullRefresh : mIsHandledTouchEvent...."+mIsHandledTouchEvent);
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    // 原理如上
                    mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY < -0.5f);
                    System.out.println("onInterceptTouchEvent... ACTION_MOVE PullLoad : mIsHandledTouchEvent...."+mIsHandledTouchEvent);
                }
            }
            break; 
            
        default:
            break;
        }
        System.out.println("onInterceptTouchEvent... mIsHandledTouchEvent:"+mIsHandledTouchEvent);
        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionY = ev.getY();
            mIsHandledTouchEvent = false;
            break;
            
        case MotionEvent.ACTION_MOVE:
            System.out.println("ACTION_MOVE...");
            final float deltaY = ev.getY() - mLastMotionY;
            mLastMotionY = ev.getY();
            if (isPullRefreshEnabled() && isReadyForPullDown()) {
                System.out.println("ACTION_MOVE...isReadyForPullDown ");
                pullHeaderLayout(deltaY / OFFSET_RADIO);
                handled = true;
            } else if (isPullLoadEnabled() && isReadyForPullUp()) {//是否处于上拉加载更多的UI状态
                System.out.println("ACTION_MOVE...isReadyForPullUp ");
                //将dy传递到"footerView"业务处理中...
                pullFooterLayout(deltaY / OFFSET_RADIO);
                handled = true;
            } else {
                System.out.println("ACTION_MOVE...else ");
                mIsHandledTouchEvent = false;
            }
            break;
            
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (mIsHandledTouchEvent) {
                mIsHandledTouchEvent = false;
                // 当第一个显示出来时
                if (isReadyForPullDown()) {
                    // 调用刷新
                    if (mPullRefreshEnabled && (mPullDownState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                        if(mPullUpState== ILoadingLayout.State.REFRESHING){
                        	Toast.makeText(UiUtils.getContext(),refreshTitle+pullDownWhenPullUpError,Toast.LENGTH_SHORT).show();
                        }else{
                        	startRefreshing();
                            handled = true;
                        }
                    }
                    //重置headView位置:因为位置的变化只是涉及到本身位置
                    resetHeaderLayout();
                } else if (isReadyForPullUp()) {
                    // 处理上拉状态为:释放刷新时候的业务逻辑
                    if (isPullLoadEnabled() && (mPullUpState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                        if(mPullDownState== ILoadingLayout.State.REFRESHING){
                            //如果下拉刷新的状态为正在刷新.....
                        	Toast.makeText(UiUtils.getContext(),refreshTitle+pullUpWhenPullDownError,Toast.LENGTH_SHORT).show();
                        }else{
                            //进行上拉刷新的ui处理
                        	startLoading();
                            handled = true;
                        }
                    }
                    //进行重置footerLayout的位置
                    resetFooterLayout();
                }
            }
            break;

        default:
            break;
        }
        
        return handled;
    }
    
    @Override
    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        mPullRefreshEnabled = pullRefreshEnabled;
    }
    
    @Override
    public void setPullLoadEnabled(boolean pullLoadEnabled) {
        mPullLoadEnabled = pullLoadEnabled;
    }
    
    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        mScrollLoadEnabled = scrollLoadEnabled;
    }
    
    @Override
    public boolean isPullRefreshEnabled() {
        return mPullRefreshEnabled && (null != mHeaderLayout);
    }
    
    @Override
    public boolean isPullLoadEnabled() {
        return mPullLoadEnabled && (null != mFooterLayout);
    }
  
    @Override
    public boolean isScrollLoadEnabled() {
        return mScrollLoadEnabled;
    }
    
    @Override
    public void setOnRefreshListener(OnRefreshListener<T> refreshListener) {
        mRefreshListener = refreshListener;
    }
    
    @Override
    public void onPullDownRefreshComplete() {
        //只有正在刷新中
        if (isPullRefreshing()) {
            //重置状态
            mPullDownState = ILoadingLayout.State.RESET;
            //暂无用处
            onStateChanged(ILoadingLayout.State.RESET, true);
            
            // 回滚动有一个时间，我们在回滚完成后再设置状态为normal
            // 在将LoadingLayout的状态设置为normal之前，我们应该禁止
            // 截断Touch事件，因为设里有一个post状态，如果有post的Runnable
            // 未被执行时，用户再一次发起下拉刷新，如果正在刷新时，这个Runnable
            // 再次被执行到，那么就会把正在刷新的状态改为正常状态，这就不符合期望
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    //暂无用处
                    setInterceptTouchEventEnabled(true);
                    mHeaderLayout.setState(ILoadingLayout.State.RESET);
                    //设置最后一次的时间
                    mHeaderLayout.setLastUpdatedLabel(DateUtil.dateFormat2(Calendar.getInstance().getTime()));
                }
            }, getSmoothScrollDuration());

            //重置headView:主要讲headView进行消失
            resetHeaderLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    /**
     * 加载更多完成的处理
     */
    @Override
    public void onPullUpRefreshComplete() {
        //是否处于正在加载中...
        if (isPullLoading()) {
            mPullUpState = ILoadingLayout.State.RESET;
            onStateChanged(ILoadingLayout.State.RESET, false);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mFooterLayout.setState(ILoadingLayout.State.RESET);
                    //设置最后一次的时间
                    mFooterLayout.setLastUpdatedLabel(DateUtil.dateFormat2(Calendar.getInstance().getTime()));
                }
            }, getSmoothScrollDuration());
            //重置底部布局业务
            resetFooterLayout();
            setInterceptTouchEventEnabled(false);
        }
    }
    
    @Override
    public T getRefreshableView() {
        return mRefreshableView;
    }
    
    @Override
    public LoadingLayout getHeaderLoadingLayout() {
        return mHeaderLayout;
    }
    
    @Override
    public LoadingLayout getFooterLoadingLayout() {
        return mFooterLayout;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label){
        if (null != mHeaderLayout) {
            mHeaderLayout.setLastUpdatedLabel(label);
        }
        
        if (null != mFooterLayout) {
            mFooterLayout.setLastUpdatedLabel(label);
        }
    }
    
    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     * 
     * @param smoothScroll 表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
     * @param delayMillis 延迟时间
     */
    public void doPullRefreshing(final boolean smoothScroll, final long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int newScrollValue = -mHeaderHeight;
                int duration = smoothScroll ? SCROLL_DURATION : 0;
                
                startRefreshing();
                smoothScrollTo(newScrollValue, duration, 0);
            }
        }, delayMillis);
    }
    
    /**
     * 创建可以刷新的View
     * 
     * @param context context
     * @param attrs 属性
     * @return View
     */
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);
    
    /**
     * 判断刷新的View是否滑动到顶部
     * 
     * @return true表示已经滑动到顶部，否则false
     */
    protected abstract boolean isReadyForPullDown();
    
    /**
     * 判断刷新的View是否滑动到底
     * 
     * @return true表示已经滑动到底部，否则false
     */
    protected abstract boolean isReadyForPullUp();
    
    /**
     * 创建Header的布局
     * 
     * @param context context
     * @param attrs 属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new HeaderLoadingLayout(context);
    }
    
    /**
     * 创建Footer的布局
     * 
     * @param context context
     * @param attrs 属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createFooterLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateFooterLoadingLayout(context);
    }
    
    /**
     * 得到平滑滚动的时间，派生类可以重写这个方法来控件滚动时间
     * 
     * @return 返回值时间为毫秒
     */
    protected long getSmoothScrollDuration() {
        return SCROLL_DURATION;
    }
    
    /**
     * 计算刷新View的大小
     * 
     * @param width 当前容器的宽度
     * @param height 当前容器的宽度
     */
    protected void refreshRefreshableViewSize(int width, int height) {
        if (null != mRefreshableViewWrapper) {
            LayoutParams lp = (LayoutParams) mRefreshableViewWrapper.getLayoutParams();
            if (lp.height != height) {
                lp.height = height;
                mRefreshableViewWrapper.requestLayout();
            }
        }
    }
    
    /**
     * 将刷新View添加到当前容器中
     * @param context context
     * @param refreshableView 可以刷新的View
     */
    protected void addRefreshableView(Context context, T refreshableView) {
        int width  = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        
        // 创建一个包装容器
        mRefreshableViewWrapper = new FrameLayout(context);
        mRefreshableViewWrapper.addView(refreshableView, width, height);

        // 这里把Refresh view的高度设置为一个很小的值，它的高度最终会在onSizeChanged()方法中设置为MATCH_PARENT
        // 这样做的原因是，如果此是它的height是MATCH_PARENT，那么footer得到的高度就是0，所以，我们先设置高度很小
        // 我们就可以得到header和footer的正常高度，当onSizeChanged后，Refresh view的高度又会变为正常。
        height = 10;
        addView(mRefreshableViewWrapper, new LayoutParams(width, height));
    }
    
    /**
     * 添加Header和Footer
     * 
     * @param context context
     */
    protected void addHeaderAndFooter(Context context) {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        
        final LoadingLayout headerLayout = mHeaderLayout;
        final LoadingLayout footerLayout = mFooterLayout;
        
        if (null != headerLayout) {
            if (this == headerLayout.getParent()) {
                removeView(headerLayout);
            }
            addView(headerLayout, 0, params);
        }
        
        if (null != footerLayout) {
            if (this == footerLayout.getParent()) {
                removeView(footerLayout);
            }
            addView(footerLayout, -1, params);
        }
    }
    
    /**
     * 拉动Header Layout时调用
     * @param delta 移动的距离
     */
    protected void pullHeaderLayout(float delta) {
        // 向上滑动，并且当前scrollY为0时，不滑动
        int oldScrollY = getScrollYValue();
        System.out.println("pullHeaderLayout .....oldScrollY:"+oldScrollY+//
                                    "....delta:"+delta+"....oldScrollY - delta:"+(oldScrollY - delta));
        /*
         * 关键一点是:向上的滑动方向检测到，headView部分一定要滑动到顶部:
         * 通过整体方面的scrollBy
         */
        if (delta < 0 && (oldScrollY - delta) >= 0) {
            System.out.println("pullHeaderLayout setScrollTo (0,0)");
//            setScrollTo(0, 0);
            smoothScrollTo(0);
            return;
        }

        // 向下滑动布局
        setScrollBy(0, -(int)delta);

        //随着竖直方向滑动的距离,进行变化arrow的角度
        if (null != mHeaderLayout && 0 != mHeaderHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mHeaderHeight;
            //进行变化arrow的角度
            mHeaderLayout.onPull(scale);
        }

        /**
         *   前提是:不能正在刷新
         *  (1)根据竖直方向上滑动的scrollY值和headHeight进行比对，
         *     scrollY>=headHeight:relase_to_refresh
         *     scrollY<headHeight: pull_to_refresh
         *
         */
        int scrollY = Math.abs(getScrollYValue());
        if (isPullRefreshEnabled() && !isPullRefreshing()) {
            if (scrollY > mHeaderHeight) {
                mPullDownState = ILoadingLayout.State.RELEASE_TO_REFRESH;
            } else {
                mPullDownState = ILoadingLayout.State.PULL_TO_REFRESH;
            }
            /*
             * 进行设置当前下拉的状态：
             *    因为headViewLayout中的UI操作
             *    需要用到该状态值！
             */
            mHeaderLayout.setState(mPullDownState);
            //暂时没有用到
            onStateChanged(mPullDownState, true);
        }
    }

    /**
     * 在onTouchEvent中的ACTION_MOVE:检测到isReadyPullUp状态
     *
     * @param delta 移动的距离
     */
    protected void pullFooterLayout(float delta){
        int oldScrollY = getScrollYValue();
        //如果监测到向下滑动的迹象:那么footerLayout进行滑动到底层
        if (delta > 0 && (oldScrollY - delta) <= 0) {
            System.out.println("pullFooterLayout setScrollTo (0,0)");
//            setScrollTo(0, 0);
            smoothScrollTo(0);
            return;
        }
        
        setScrollBy(0, -(int)delta);
        
        if (null != mFooterLayout && 0 != mFooterHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mFooterHeight;
            mFooterLayout.onPull(scale);
        }
        
        int scrollY = Math.abs(getScrollYValue());
        if (isPullLoadEnabled() && !isPullLoading()) {
            if (scrollY > mFooterHeight) {
                mPullUpState = ILoadingLayout.State.RELEASE_TO_REFRESH;
                System.out.println("pullFooterLayout RELEASE_TO_REFRESH....");
            } else {
                mPullUpState = ILoadingLayout.State.PULL_TO_REFRESH;
                System.out.println("pullFooterLayout PULL_TO_REFRESH....");
            }

            System.out.println("pullFooterLayout setState....");
            mFooterLayout.setState(mPullUpState);

            onStateChanged(mPullUpState, false);
        }
    }

    /**
     * 进行重置header的位置:
     *       正在刷新 && abs(滑动的距离)<=mHeaderHeight-——>:恢复到原位
     *       正在刷新 && abs(滑动的距离)>mHeaderHeight ----->:滑动到位置:-mHeaderHeight
     *       其他情况为:滑动到0
     */
    protected void resetHeaderLayout() {
        final int scrollY = Math.abs(getScrollYValue());
        final boolean refreshing = isPullRefreshing();
        
        if (refreshing && scrollY <= mHeaderHeight) {
            //正在刷新 && abs(滑动的距离)<=mHeaderHeight
            smoothScrollTo(0);
            return;
        }
        
        if (refreshing) {
            //正在刷新 && abs(滑动的距离)>mHeaderHeight
            smoothScrollTo(-mHeaderHeight);
        } else {
            //其他情况为:滑动到0
            smoothScrollTo(0);
        }
    }
    
    /**
     * 重置footer
     *     正在加载更多&&scrollY < mFooterHeight---->将scrollY值滑动到0
     *     正在加载更多&& scrollY > mFooterHeight---->将scrollY值滑动到mFooterHeight
     *     其他情况:将scrollY值滑动到0
     */
    protected void resetFooterLayout() {
        int scrollY = Math.abs(getScrollYValue());
        boolean isPullLoading = isPullLoading();

        //正在加载更多&&scrollY < mFooterHeight
        if (isPullLoading && scrollY <= mFooterHeight) {
            //将scrollY值滑动到0
            smoothScrollTo(0);
            return;
        }

        //正在加载更多&& scrollY > mFooterHeight
        if (isPullLoading) {
            //将scrollY值滑动到mFooterHeight
            smoothScrollTo(mFooterHeight);
        } else {
            //其他情况:将scrollY值滑动到0
            smoothScrollTo(0);
        }
    }
    
    /**
     * 判断是否正在下拉刷新
     * 
     * @return true正在刷新，否则false
     */
    protected boolean isPullRefreshing() {
        return (mPullDownState == ILoadingLayout.State.REFRESHING);
    }
    
    /**
     * 是否正的上拉加载更多
     * 
     * @return true正在加载更多，否则false
     */
    protected boolean isPullLoading() {
        return (mPullUpState == ILoadingLayout.State.REFRESHING);
    }
    
    /**
     * 开始刷新，当下拉松开后被调用
     */
    protected void startRefreshing() {
        // 如果正在刷新,ui不需要进行变化
        if (isPullRefreshing()) {
            return;
        }
        //变动状态值
        mPullDownState = ILoadingLayout.State.REFRESHING;
        //暂时无用
        onStateChanged(ILoadingLayout.State.REFRESHING, true);

        //设置状态为“正在刷新....”
        if (null != mHeaderLayout) {
            mHeaderLayout.setState(ILoadingLayout.State.REFRESHING);
        }
        
        if (null != mRefreshListener) {
            // 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行刷新回调
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullDownToRefresh(PullToRefreshBase.this);
                }
            }, getSmoothScrollDuration()); 
        }
    }

    /**
     * 开始加载更多，上拉松开后调用
     */
    protected void startLoading() {
        // 如果正在加载
        if (isPullLoading()) {
            return;
        }
        
        mPullUpState = ILoadingLayout.State.REFRESHING;
        //暂时无用
        onStateChanged(ILoadingLayout.State.REFRESHING, false);
        
        if (null != mFooterLayout) {
            mFooterLayout.setState(ILoadingLayout.State.REFRESHING);
        }
        
        if (null != mRefreshListener) {
            //因为滚动回原始位置的时间是200，我们需要等回滚完后才执行加载回调
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullUpToRefresh(PullToRefreshBase.this);
                }
            }, getSmoothScrollDuration()); 
        }
    }
    
    /**
     * 当状态发生变化时调用
     * 
     * @param state 状态
     * @param isPullDown 是否向下
     */
    protected void onStateChanged(ILoadingLayout.State state, boolean isPullDown) {
        
    }
    
    /**
     * 设置滚动位置
     * 
     * @param x 滚动到的x位置
     * @param y 滚动到的y位置
     */
    private void setScrollTo(int x, int y) {
        scrollTo(x, y);
    }
    
    /**
     * 设置滚动的偏移
     * 
     * @param x 滚动x位置
     * @param y 滚动y位置
     */
    private void setScrollBy(int x, int y) {
        scrollBy(x, y);
    }
    
    /**
     * 得到当前Y的滚动值
     * 
     * @return 滚动值
     */
    private int getScrollYValue() {
        return getScrollY();
    }
    
    /**
     * 平滑滚动
     * 
     * @param newScrollValue 滚动的值
     */
    private void smoothScrollTo(int newScrollValue) {
        smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0);
    }
    
    /**
     * 平滑滚动
     *     根据当前的scrollY值进行滑动到目标值!
     * @param newScrollValue 滚动的值
     * @param duration 滚动时候
     * @param delayMillis 延迟时间，0代表不延迟
     */
    private void smoothScrollTo(int newScrollValue, long duration, long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }
        
        int oldScrollValue = this.getScrollYValue();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);
        }
        
        if (post) {
            if (delayMillis > 0) {
                postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                post(mSmoothScrollRunnable);
            }
        }
    }
    
    /**
     * 设置是否截断touch事件
     * 
     * @param enabled true截断，false不截断
     */
    private void setInterceptTouchEventEnabled(boolean enabled) {
        mInterceptEventEnable = enabled;
    }
    
    /**
     * 标志是否截断touch事件
     * 
     * @return true截断，false不截断
     */
    private boolean isInterceptTouchEventEnabled() {
        return mInterceptEventEnable;
    }
    
    /**
     * 实现了平滑滚动的Runnable
     * 
     * @author Li Hong
     * @since 2013-8-22
     */
    final class SmoothScrollRunnable implements Runnable {
        /**动画效果*/
        private final Interpolator mInterpolator;
        /**结束Y*/
        private final int mScrollToY;
        /**开始Y*/
        private final int mScrollFromY;
        /**滑动时间*/
        private final long mDuration;
        /**是否继续运行*/
        private boolean mContinueRunning = true;
        /**开始时刻*/
        private long mStartTime = -1;
        /**当前Y*/
        private int mCurrentY = -1;

        /**
         * 构造方法
         * 
         * @param fromY 开始Y
         * @param toY 结束Y
         * @param duration 动画时间
         */
        public SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * If the duration is 0, we scroll the view to target y directly.
             */
            if (mDuration <= 0) {
                setScrollTo(0, mScrollToY);
                return;
            }
            
            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {
                
                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                final long oneSecond = 1000;    // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;
                
                setScrollTo(0, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullToRefreshBase.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }
    
    /**
     * 当状态设置为{@link State#RESET}时调用
     */
    protected void onReset() {
        
    }

	public String getRefreshTitle() {
		return refreshTitle;
	}

	public void setRefreshTitle(String refreshTitle) {
		this.refreshTitle = refreshTitle;
	}
    
    
}
