package com.jack.contacts;

import com.dandelionlvfengli.HardwareKeyAction;
import com.dandelionlvfengli.HardwareKeyActionType;

public abstract class ViewContext {

	public abstract Class<?> getActivityClass();

	public abstract Class<?> getViewModelClass();

	public abstract Class<?> getViewClass();

	public abstract String getTitle();

	public void setTitle(String title) {
		AppContext_city.getContext().setTitle(
				title == null ? getTitle() : title);
	}

	protected HardwareKeyAction getBackKeyAction() {
		return new HardwareKeyAction(HardwareKeyActionType.Proceed);
	}

	public void onPush() {
	}

	public void onPop() {
	}

	public void onCover() {
	}

	public void onReveal() {
	}

	private Object viewModel;

	public Object getViewModel() {

		if (viewModel == null) {
			try {
				viewModel = getViewModelClass().getConstructor(getClass())
						.newInstance(new Object[] { this });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return viewModel;
	}

	public Object getImpersonatedViewModel() {
		Object vm = getViewModel();
		return vm instanceof IDelegateResponsibility ? ((IDelegateResponsibility) vm)
				.getImpersonatedViewModel() : vm;
	}

	// public void updateImpersonatedViewModel() {
	//
	// Object vm = getImpersonatedViewModel();
	//
	// if (vm instanceof GlobalDownloadListener) {
	// FileTransferScheduler
	// .setDownloadListener((GlobalDownloadListener) vm);
	// }
	//
	// if (vm instanceof GlobalUploadListener) {
	// FileTransferScheduler.setUploadListener((GlobalUploadListener) vm);
	// }
	// }
}
