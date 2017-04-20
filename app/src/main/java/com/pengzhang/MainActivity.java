package com.pengzhang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainActivity extends AppCompatActivity implements PagingScrollHelper.onPageChangeListener {
    private RecyclerView recyclerView;
    private TextView tvPage;
    private Button btnTurnPage;
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
        tvPage = (TextView) findViewById(R.id.tv_page);
        btnTurnPage = (Button) findViewById(R.id.btn_turn_page);
        etPageNum = (EditText) findViewById(R.id.et_page_num);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    private void init() {
        MyAdapter  myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);

        hLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);

        vDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        vLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        horizontalPageLayoutManager = new HorizontalPageLayoutManager(3, 4);
        pagingItemDecoration = new PagingItemDecoration(this, horizontalPageLayoutManager);

    }

    private void initListener() {
        scrollHelper.setOnPageChangeListener(this);
        tvPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollHelper.setUpRecycleView(recyclerView);
                scrollHelper.updateLayoutManger();
            }
        });
        btnTurnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollHelper.setPageNum(Integer.valueOf(etPageNum.getText().toString().trim()));
            }
        });
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
    public void onPageChange(int index) {

    }
}
