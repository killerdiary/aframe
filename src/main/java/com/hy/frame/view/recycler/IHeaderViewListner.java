package com.hy.frame.view.recycler;

import android.view.View;

@Deprecated
public interface IHeaderViewListner {
    void bindHearderData(HeaderHolder holder, int position);

    HeaderHolder createHeaderView(View v);
}