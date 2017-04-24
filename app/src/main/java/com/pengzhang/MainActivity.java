package com.pengzhang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pengzhang.adapter.MyAdapter;
import com.pengzhang.view.DividerItemDecoration;
import com.pengzhang.view.HorizontalPageLayoutManager;
import com.pengzhang.view.PagingItemDecoration;
import com.pengzhang.helper.PagingScrollHelper;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private RecyclerView recyclerView;
    private Button btnScrollBy;
    private Button btnScrollTo;
    private Button btnPageTo;
    private Button btnPageAdd;
    private Button btnPageStart;
    private EditText etPageNum;
    private PagingScrollHelper scrollHelper = new PagingScrollHelper();
    private RadioGroup radioGroup;
    private RecyclerView.ItemDecoration lastItemDecoration = null;
    private HorizontalPageLayoutManager horizontalPageLayoutManager = null;
    private LinearLayoutManager hLinearLayoutManager = null;
    private LinearLayoutManager vLinearLayoutManager = null;
    private DividerItemDecoration hDividerItemDecoration = null;
    private DividerItemDecoration vDividerItemDecoration = null;
    private PagingItemDecoration pagingItemDecoration = null;
    private ValueAnimator mAnimator = null;
    private float lastFraction;
    private boolean isPageModel = false;
    private int scrollBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        init();
        initListener();
        switchLayout(R.id.rb_horizontal_page);

    }

    private void findView() {
        radioGroup = (RadioGroup) findViewById(R.id.rg_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        etPageNum = (EditText) findViewById(R.id.et_page_num);
        btnPageStart = (Button) findViewById(R.id.btn_page_start);
        btnScrollBy = (Button) findViewById(R.id.btn_scroll_by);
        btnScrollTo = (Button) findViewById(R.id.btn_scroll_to);
        btnPageTo = (Button) findViewById(R.id.btn_page_to);
        btnPageAdd = (Button) findViewById(R.id.btn_page_add);
    }

    private void init() {
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);

        hLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);

        vDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        vLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        horizontalPageLayoutManager = new HorizontalPageLayoutManager(3, 4);
        pagingItemDecoration = new PagingItemDecoration(this, horizontalPageLayoutManager);

    }

    private void initListener() {
        btnPageStart.setOnClickListener(this);
        btnPageTo.setOnClickListener(this);
        btnPageAdd.setOnClickListener(this);
        btnScrollBy.setOnClickListener(this);
        btnScrollTo.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switchLayout(checkedId);
            }
        });
    }

    private void switchLayout(int checkedId) {
        RecyclerView.LayoutManager layoutManager = null;
        RecyclerView.ItemDecoration itemDecoration = null;
        switch (checkedId) {
            case R.id.rb_horizontal_page:
                layoutManager = horizontalPageLayoutManager;
                itemDecoration = pagingItemDecoration;
                break;
            case R.id.rb_vertical_page:
                layoutManager = vLinearLayoutManager;
                itemDecoration = vDividerItemDecoration;
                break;
            case R.id.rb_vertical_page2:
                layoutManager = hLinearLayoutManager;
                itemDecoration = hDividerItemDecoration;
                break;
        }
        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.removeItemDecoration(lastItemDecoration);
            recyclerView.addItemDecoration(itemDecoration);
            lastItemDecoration = itemDecoration;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_page_start:
                scrollHelper.setUpRecycleView(recyclerView);
                isPageModel = true;
                break;
            case R.id.btn_page_to:
                setPageTo();
                break;
            case R.id.btn_page_add :
                pageAdd();
                break;
            case R.id.btn_scroll_by :
                scrollBy();
                break;
            case R.id.btn_scroll_to :
                scrollTo();
                break;
            default:
                break;
        }
    }

    private void setPageTo() {
        if (!isPageModel) return;
        String num = TextUtils.isEmpty(etPageNum.getText().toString().trim()) ? "0" : etPageNum.getText().toString().trim();
        scrollHelper.setPageNum(Integer.valueOf(num));
    }

    private void pageAdd() {
        if(!isPageModel)return;
        String num= TextUtils.isEmpty(etPageNum.getText().toString().trim()) ? "0" : etPageNum.getText().toString().trim();
        int index=Integer.valueOf(num);
        scrollHelper.setIndexPage(index);
    }

    private void scrollTo() {
        if(!isPageModel)return;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                String num= TextUtils.isEmpty(etPageNum.getText().toString().trim()) ? "0" : etPageNum.getText().toString().trim();
                vLinearLayoutManager.scrollToPositionWithOffset(0, -Integer.valueOf(num));

            }
        });
    }

    private void scrollBy() {
        if(!isPageModel)return;
        String num= TextUtils.isEmpty(etPageNum.getText().toString().trim()) ? "0" : etPageNum.getText().toString().trim();
        scrollBy=Integer.valueOf(num);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mAnimator == null) {
                    mAnimator = ValueAnimator.ofInt(0, 1);
                    mAnimator.setDuration(300);
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            //获取动画完成比例值
                            float fraction = mAnimator.getAnimatedFraction();
                            int scroll = (int) (scrollBy * (fraction - lastFraction));
                            //根据比例值对目标view进行滑动
                            recyclerView.scrollBy(0,scroll);
                            lastFraction=fraction;
                        }
                    });

                    mAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(scrollHelper!=null){
                                scrollHelper.updateLayoutManger();
                            }
                            lastFraction=0;
                        }
                    });
                } else {
                    mAnimator.cancel();
                    mAnimator.setIntValues(0, 1);
                }
                mAnimator.start();
            }
        });
    }
}
