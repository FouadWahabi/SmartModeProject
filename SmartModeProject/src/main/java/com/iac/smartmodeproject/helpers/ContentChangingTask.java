package com.iac.smartmodeproject.helpers;

import android.content.Loader;
import android.os.AsyncTask;

public abstract class ContentChangingTask extends AsyncTask<Object, Void, Void> {
	private Loader<?> loader = null;

	public ContentChangingTask(Loader<?> loader) {
		this.loader = loader;
	}

	@Override
	protected void onPostExecute(Void param) {
		loader.onContentChanged();
	}
}
