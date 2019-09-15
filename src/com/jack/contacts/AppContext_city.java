package com.jack.contacts;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.View;

import com.dandelionlvfengli.BaseActivity;
import com.dandelionlvfengli.extensions.ViewExtensions;
import com.dandelionlvfengli.model.IView;
import com.jack.chengshidingwei.ChengshidingweiTM;

public class AppContext_city {

	public static Application application;

	/**
	 * 城市定位
	 */
	public static ChengshidingweiTM chengshidingweiTM = new ChengshidingweiTM();

	private static ArrayList<ViewContext> viewModelStack = new ArrayList<ViewContext>();

	private static Activity currentActivity;

	public static BaseActivity getContext() {
		return (BaseActivity) currentActivity;
	}

	public static Activity getCurrentActivity() {
		return currentActivity;
	}

	public static ViewContext getViewContext() {
		return viewModelStack.size() == 0 ? null : viewModelStack
				.get(viewModelStack.size() - 1);
	}

	public static void push(ViewContext viewContext) {

		viewModelStack.get(viewModelStack.size() - 1).onCover();

		viewModelStack.add(viewContext);
		Intent intent = new Intent(getContext(), viewContext.getActivityClass());
		getContext().startActivity(intent);
	}

	public static void enter(Activity activity) {
		currentActivity = activity;
	}

	public static void enter(BaseActivity activity, boolean isCreate) {

		currentActivity = activity;

		if (isCreate) {

			ViewContext viewContext = viewModelStack
					.get(viewModelStack.size() - 1);

			IView view = (IView) ViewExtensions.createView(
					viewContext.getViewClass(), activity);
			activity.setContentView((View) view);
			view.bind(viewContext.getViewModel());

			viewContext.onPush();
			// viewContext.updateImpersonatedViewModel();
		}
	}

	public static void settleFirstActivity(BaseActivity activity,
			ViewContext viewContext) {
		viewModelStack.add(viewContext);
		enter(activity, true);
	}

	public static void pop() {
		currentActivity.finish();
	}

	public static void settlePop() {
		viewModelStack.remove(viewModelStack.size() - 1);
		if (viewModelStack.size() > 0) {
			ViewContext viewContext = viewModelStack
					.get(viewModelStack.size() - 1);
			viewContext.onReveal();
			// viewContext.updateImpersonatedViewModel();
		}
	}
}
