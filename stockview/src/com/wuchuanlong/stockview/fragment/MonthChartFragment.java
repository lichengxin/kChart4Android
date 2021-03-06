package com.wuchuanlong.stockview.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.stockview.R;
import com.wedroid.framework.fragment.WeDroidFragment;
import com.wuchuanlong.stockview.BigStockChartActivity;
import com.wuchuanlong.stockview.chart.ChartTouchEvent;
import com.wuchuanlong.stockview.chart.CircleLoadingView;
import com.wuchuanlong.stockview.chart.SingleStockInfo;
import com.wuchuanlong.stockview.chart.StockBusiness;
import com.wuchuanlong.stockview.chart.StockCache;
import com.wuchuanlong.stockview.chart.StockView;
import com.wuchuanlong.stockview.chart.TimeKChartView;
import com.wuchuanlong.stockview.chart.TouchCallBack;
import com.wuchuanlong.stockview.chart.Type;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class MonthChartFragment extends WeDroidFragment {
	StockView kChartView;
	CircleLoadingView circleLoadingView;
	private List<SingleStockInfo> dataList;
	private int oriSize = 100;

	@Override
	public void onResume() {
		super.onResume();
		List<SingleStockInfo> infos = StockCache.get(StockCache.MONTH_CHAR_DATE, List.class);
		if (infos != null && !infos.isEmpty()) {
			dataList = infos;
			updateStockView(dataList);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		StockCache.put(StockCache.MONTH_CHAR_DATE, dataList);
	}

	@Override
	protected View initContentView(LayoutInflater inflater) {
		return View.inflate(mContext, R.layout.chart_stock_k_chart, null);
	}

	@Override
	protected void initViewById(View view) {
		kChartView = (StockView) $(R.id.stock_view);
		circleLoadingView = (CircleLoadingView) $(R.id.loading_view);
	}

	@Override
	protected void initListener() {
		kChartView.setTouchCallback(new TouchCallBack() {
			@Override
			public void updateViewInTouch(SingleStockInfo info) {
				Activity activity = getActivity();
				if (activity != null && activity instanceof ChartTouchEvent) {
					ChartTouchEvent event = (ChartTouchEvent) activity;
					event.updateRelativeView(info, Type.MONTH);
				}
			}

			@Override
			public void ifParentIterceptorEvent(boolean interceptor) {
				Activity activity = getActivity();
				if (activity != null && activity instanceof ChartTouchEvent) {
					ChartTouchEvent event = (ChartTouchEvent) activity;
					event.ifParentIterceptorEvent(interceptor);
				}
			}

			@Override
			public void enterBigView() {
				Activity activity = getActivity();
				if (activity != null && activity instanceof ChartTouchEvent) {
					ChartTouchEvent event = (ChartTouchEvent) activity;
					event.clickedTwo();
				}
			}
		});
	}

	// private boolean requesting = false;

	@Override
	protected void initData(Bundle savedInstanceState) {
		String code = StockCache.get(StockCache.CODE, String.class);
		String market = StockCache.get(StockCache.MARKET, String.class);
		if (kChartView != null) {
			// if (!requesting && kChartView!=null) {
			// requesting = true;
			circleLoadingView.showLoading();
			kChartView.setVisibility(View.GONE);
			Map<String, String> map = new HashMap<String, String>();
			map.put("stock_code", code);
			map.put("market", market);
			map.put("type", Type.MONTH.getValue());
			map.put("count", oriSize + "");
			new StockBusiness(StockBusiness.MONTH_CHART, this, map).execute();
		}
	}

	public void requestSuccess(Object result, int requestToken) {
		circleLoadingView.hiden();
		kChartView.setVisibility(View.VISIBLE);
		if (requestToken == StockBusiness.MONTH_CHART) {
			dataList = (List<SingleStockInfo>) result;
			updateStockView(dataList);
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			if (dataList == null || dataList.isEmpty()) {
				initData(null);
			}
		}
	}

	@Override
	public void requestFail(Object errorMessage, int requestToken) {
		// requesting = false;
	}

	public void updateStockView(List<SingleStockInfo> list) {
		kChartView.setStockList(list);
		kChartView.setOriSize(oriSize);
		kChartView.invalidate();
	}
}
