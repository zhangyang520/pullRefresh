package strollmuseum.iot.zhjy.com.pullrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh.PullToRefreshBase;
import strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh.PullToRefreshListView;
import strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh.PullToRefreshRecyclerView;
import strollmuseum.iot.zhjy.com.pullrefresh.utils.UiUtils;

/**
 * 进行展示刷新界面的
 */
public class MainActivity extends AppCompatActivity {

//    PullToRefreshListView pull_refresh_view;
    PullToRefreshRecyclerView pull_refresh_view;
    UserAdapter userAdapter;
    ArrayList<String> nameList=new ArrayList<String>();
    RecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //进行刷新listView
        LinearLayout ll=(LinearLayout)findViewById(R.id.ll);
//        pull_refresh_view=new PullToRefreshListView(this,false);
//        pull_refresh_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1500));
//        ll.addView(pull_refresh_view);
////        pull_refresh_view=(PullToRefreshListView)findViewById(R.id.pull_refresh_view);
//        //进行初始化数据
//        initData();
//        pull_refresh_view.setHasMoreData(true);
//        pull_refresh_view.getmListView().setAdapter(userAdapter);
//        pull_refresh_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                UiUtils.getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        nameList.clear();
//                        initData();
//                        userAdapter.notifyDataSetChanged();
//                        pull_refresh_view.onPullDownRefreshComplete();
//                    }
//                },1500);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                initData();
//                userAdapter.notifyDataSetChanged();
//                pull_refresh_view.onPullUpRefreshComplete();
//            }
//        });

        //第二种的RecyclerView
        pull_refresh_view=new PullToRefreshRecyclerView(this,false);
        pull_refresh_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1500));
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pull_refresh_view.getmListView().setLayoutManager(linearLayoutManager);
        ll.addView(pull_refresh_view);
        //进行初始化数据
        initRecyclerAdapterData();
        pull_refresh_view.setHasMoreData(true);
        pull_refresh_view.getmListView().setAdapter(recyclerViewAdapter);
        pull_refresh_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                UiUtils.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nameList.clear();
                        initRecyclerAdapterData();
                        recyclerViewAdapter.notifyDataSetChanged();
                        pull_refresh_view.onPullDownRefreshComplete();
                    }
                },1500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                initRecyclerAdapterData();
                recyclerViewAdapter.notifyDataSetChanged();
                pull_refresh_view.onPullUpRefreshComplete();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for(int i=0;i<20;++i){
            nameList.add("zhangyang"+"..."+i);
        }
        if(userAdapter==null){
            userAdapter=new UserAdapter();
        }
    }

    /**
     * 进行初始化数据
     */
    private void initRecyclerAdapterData() {
        for(int i=0;i<20;++i){
            nameList.add("zhangyang"+"..."+i);
        }
        if(recyclerViewAdapter==null){
            recyclerViewAdapter=new RecyclerViewAdapter();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    class UserAdapter<String> extends BaseAdapter{
        ViewHolder viewHolder;
            @Override
            public int getCount() {
                return nameList.size();
            }

            @Override
            public Object getItem(int position) {
                return nameList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null){
                    viewHolder=new ViewHolder();
                    View view=View.inflate(MainActivity.this,R.layout.item_user,null);
                    viewHolder.textView=(TextView)view.findViewById(R.id.tv_user);
                    view.setTag(viewHolder);
                    convertView=view;
                }else{
                    viewHolder=(ViewHolder)convertView.getTag();
                }
                viewHolder.textView.setText(nameList.get(position));
                return convertView;
            }
        }

        class ViewHolder{
            TextView textView;
        }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=View.inflate(MainActivity.this,R.layout.item_user,null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            holder.textView.setText(nameList.get(position));
            System.out.println("RecyclerViewAdapter onBindViewHolder position:" + position);
                     holder.ll_top1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                         @Override
                         public void onGlobalLayout() {
                             holder.ll_top1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                             System.out.println("holder.textView ..measureWidth:" + holder.textView.getMeasuredWidth()+"..ll_top1 width:"+holder.ll_top1.getMeasuredWidth());
                         }
                     });

        }

        @Override
        public int getItemCount() {
            return nameList.size();
        }
     }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout ll_top1;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.tv_user);
            ll_top1=(LinearLayout)itemView.findViewById(R.id.ll_top1);
        }
    }
    }
