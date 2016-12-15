package strollmuseum.iot.zhjy.com.pullrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh.PullToRefreshBase;
import strollmuseum.iot.zhjy.com.pullrefresh.pullRrefresh.PullToRefreshListView;
import strollmuseum.iot.zhjy.com.pullrefresh.utils.UiUtils;

/**
 * 进行展示刷新界面的
 */
public class MainActivity extends AppCompatActivity {

    PullToRefreshListView pull_refresh_view;
    LinearLayout ll;
    List<String> nameList=new ArrayList<String>();
    UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //进行刷新listView
        ll=(LinearLayout)findViewById(R.id.ll);
        pull_refresh_view=new PullToRefreshListView(this);
        pull_refresh_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1500));
        ll.addView(pull_refresh_view);
//        pull_refresh_view=(PullToRefreshListView)findViewById(R.id.pull_refresh_view);
        //进行初始化数据
        initData();
        pull_refresh_view.setHasMoreData(true);
        pull_refresh_view.getmListView().setAdapter(userAdapter);
        pull_refresh_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                UiUtils.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nameList.clear();
                        initData();
                        userAdapter.notifyDataSetChanged();
                        pull_refresh_view.onPullDownRefreshComplete();
                    }
                },1500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
                userAdapter.notifyDataSetChanged();
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
    }
