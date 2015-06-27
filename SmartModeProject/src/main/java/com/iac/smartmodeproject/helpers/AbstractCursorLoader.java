package com.iac.smartmodeproject.helpers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

abstract public class AbstractCursorLoader extends AsyncTaskLoader<Cursor> {
  abstract protected Cursor buildCursor();
  Cursor lastCursor=null;

  public AbstractCursorLoader(Context context) {
    super(context);
  }
  
  /** 
   * Runs on a worker thread, loading in our data. Delegates
   * the real work to concrete subclass' buildCursor() method. 
   */
  @Override
  public Cursor loadInBackground() {
    Cursor cursor = buildCursor();

    if (cursor!=null) {
      // Ensure the cursor window is filled
      cursor.getCount();
    }
    
    return(cursor);
  }

  /**
   * Runs on the UI thread, routing the results from the
   * background thread to whatever is using the Cursor
   * (e.g., a CursorAdapter).
   */
  @Override
  public void deliverResult(Cursor cursor) {
    if (isReset()) {
      // An async query came in while the loader is stopped
      if (cursor!=null) {
        cursor.close();
      }
      
      return;
    }
    
    Cursor oldCursor=lastCursor;
    lastCursor=cursor;

    if (isStarted()) {
      super.deliverResult(cursor);
    }

    if (oldCursor!=null && oldCursor!=cursor && !oldCursor.isClosed()) {
      oldCursor.close();
    }
  }

  /**
   * Starts an asynchronous load of the list data.
   * When the result is ready the callbacks will be called
   * on the UI thread. If a previous load has been completed
   * and is still valid the result may be passed to the
   * callbacks immediately.
   * 
   * Must be called from the UI thread.
   */
  @Override
  protected void onStartLoading() {
    if (lastCursor!=null) {
      deliverResult(lastCursor);
    }
    
    if (takeContentChanged() || lastCursor==null) {
      forceLoad();
    }
  }

  /**
   * Must be called from the UI thread, triggered by a
   * call to stopLoading().
   */
  @Override
  protected void onStopLoading() {
    // Attempt to cancel the current load task if possible.
    cancelLoad();
  }

  /**
   * Must be called from the UI thread, triggered by a
   * call to cancel(). Here, we make sure our Cursor
   * is closed, if it still exists and is not already closed.
   */
  @Override
  public void onCanceled(Cursor cursor) {
    if (cursor!=null && !cursor.isClosed()) {
      cursor.close();
    }
  }

  /**
   * Must be called from the UI thread, triggered by a
   * call to reset(). Here, we make sure our Cursor
   * is closed, if it still exists and is not already closed.
   */
  @Override
  protected void onReset() {
    super.onReset();

    // Ensure the loader is stopped
    onStopLoading();

    if (lastCursor!=null && !lastCursor.isClosed()) {
      lastCursor.close();
    }
    
    lastCursor=null;
  }
}