package cn.suozhi.DiBi.common.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 键盘监听
 */
public class KeyboardChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private View mContentView;
    private int mOriginHeight;
    private int mPreHeight;
    private int limit = 10;
    private KeyBoardListener mKeyBoardListener;

    public interface KeyBoardListener {

        void onKeyboardChange(boolean isShow, int keyboardHeight);
    }

    public KeyboardChangeListener setKeyBoardListener(KeyBoardListener keyBoardListener) {
        this.mKeyBoardListener = keyBoardListener;
        return this;
    }

    public KeyboardChangeListener setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public KeyboardChangeListener(Activity context) {
        if (context == null) {
            return;
        }
        mContentView = findContentView(context);
        if (mContentView != null) {
            addContentTreeObserver();
        }
    }

    private View findContentView(Activity context) {
        return context.findViewById(android.R.id.content);
    }

    private void addContentTreeObserver() {
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        int currHeight = mContentView.getHeight();
        if (currHeight == 0) {
            return;
        }
        boolean hasChange = false;
        if (mPreHeight == 0) {
            mPreHeight = currHeight;
            mOriginHeight = currHeight;
        } else {
            if (mPreHeight != currHeight) {
                hasChange = true;
                mPreHeight = currHeight;
            } else {
                hasChange = false;
            }
        }
        if (hasChange) {
            boolean isShow;
            int keyboardHeight = 0;
            if (mOriginHeight == currHeight) {
                //hidden
                isShow = false;
            } else {
                //show
                keyboardHeight = mOriginHeight - currHeight;
                isShow = keyboardHeight >= limit;
            }

            if (mKeyBoardListener != null) {
                mKeyBoardListener.onKeyboardChange(isShow, keyboardHeight);
            }
        }
    }

    public void destroy() {
        if (mContentView != null) {
            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }
}
