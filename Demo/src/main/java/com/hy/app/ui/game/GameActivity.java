package com.hy.app.ui.game;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.hy.app.R;
import com.hy.app.adapter.GameAdapter;
import com.hy.app.bean.GameInfo;
import com.hy.app.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeYan on 2016/5/13.
 */
public class GameActivity extends BaseActivity {
    private GridView gdvStart, gdvEnd;
    private TextView txtSteps;
    private List<GameInfo> startDatas, endDatas;
    private GameAdapter startAdapter;
    private int location, steps;

    @Override
    public int getLayoutId() {
        return R.layout.act_game;
    }

    @Override
    public void initView() {
        gdvStart = findViewById(R.id.game_gdvStart);
        gdvEnd = findViewById(R.id.game_gdvEnd);
        txtSteps = findViewById(R.id.game_txtSteps);
        setOnClickListener(R.id.game_btnUp, null);
        setOnClickListener(R.id.game_btnDown, null);
        setOnClickListener(R.id.game_btnLeft, null);
        setOnClickListener(R.id.game_btnRight, null);
    }

    @Override
    public void initData() {
        initHeaderBack(R.string.app_name, 0);
        setTitle("游戏测试");
        int yellow = getResources().getColor(R.color.yellow);
        int red = getResources().getColor(R.color.red);
        int blue = getResources().getColor(R.color.blue);
        startDatas = new ArrayList<>();
        startDatas.add(new GameInfo(yellow, 1));
        startDatas.add(new GameInfo(red, 2));
        startDatas.add(new GameInfo(blue, 3));
        startDatas.add(new GameInfo(blue, 4));
        startDatas.add(new GameInfo(red, 5));
        startDatas.add(new GameInfo(red, 6));
        startDatas.add(new GameInfo(blue, 7));
        startDatas.add(new GameInfo(blue, 8));
        startDatas.add(new GameInfo(red, 9));
        startDatas.add(new GameInfo(red, 10));
        startDatas.add(new GameInfo(blue, 11));
        startDatas.add(new GameInfo(blue, 12));
        startDatas.add(new GameInfo(red, 13));
        startDatas.add(new GameInfo(red, 14));
        startDatas.add(new GameInfo(blue, 15));
        startDatas.add(new GameInfo(blue, 16));
        endDatas = new ArrayList<>();
        endDatas.add(new GameInfo(yellow, 1));
        endDatas.add(new GameInfo(blue, 2));
        endDatas.add(new GameInfo(red, 3));
        endDatas.add(new GameInfo(blue, 4));
        endDatas.add(new GameInfo(blue, 5));
        endDatas.add(new GameInfo(red, 6));
        endDatas.add(new GameInfo(blue, 7));
        endDatas.add(new GameInfo(red, 8));
        endDatas.add(new GameInfo(red, 9));
        endDatas.add(new GameInfo(blue, 10));
        endDatas.add(new GameInfo(red, 11));
        endDatas.add(new GameInfo(blue, 12));
        endDatas.add(new GameInfo(blue, 13));
        endDatas.add(new GameInfo(red, 14));
        endDatas.add(new GameInfo(blue, 15));
        endDatas.add(new GameInfo(red, 16));
        location = 0;
        steps = 0;
        //gdvEnd.setAdapter(new GameAdapter(getCurContext(), endDatas));
        updateUI();
    }


    private void requestData() {}


    private void updateUI() {
        if (startAdapter == null) {
            startAdapter = new GameAdapter(getCurContext(), startDatas);
            //gdvStart.setAdapter(startAdapter);
        } else
            startAdapter.refresh(startDatas);
    }

    @Override
    public void onViewClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.game_btnUp:
                if (location > 3) {
                    change(location, location - 4);
                }
                break;
            case R.id.game_btnDown:
                if (location < 12) {
                    change(location, location + 4);
                }
                break;
            case R.id.game_btnLeft:
                if ((location + 1) % 4 != 1) {
                    change(location, location - 1);
                }
                break;
            case R.id.game_btnRight:
                if ((location + 1) % 4 != 0) {
                    change(location, location + 1);
                }
                break;
        }
    }

    private void change(int from, int to) {
        location = to;
        int yellow = startDatas.get(from).getColor();
        startDatas.get(from).setColor(startDatas.get(to).getColor());
        startDatas.get(to).setColor(yellow);
        steps++;
        txtSteps.setText(steps + "步");
        updateUI();
    }
}
