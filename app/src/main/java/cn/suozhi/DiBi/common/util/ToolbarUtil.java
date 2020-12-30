package cn.suozhi.DiBi.common.util;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.suozhi.DiBi.R;

/**
 * ToolBar工具类
 */
public class ToolbarUtil {

    /**
     * 初始化ToolBar
     */
    public static void initToolbar(Toolbar toolbar, String title, View.OnClickListener clickListener) {
        ImageView tbBack;
        TextView tbTitle;
        switch (toolbar.getId()) {
            case R.id.toolbar_text:
                tbBack = toolbar.findViewById(R.id.toolbar_back);
                tbTitle = toolbar.findViewById(R.id.toolbar_title);
                break;
//            case R.id.toolbar_icon:
            default:
                tbBack = toolbar.findViewById(R.id.toolbar_centerBack);
                tbTitle = toolbar.findViewById(R.id.toolbar_centerTitle);
                break;
            /*default:
                tbBack = toolbar.findViewById(R.id.toolbar_transBack);
                tbTitle = toolbar.findViewById(R.id.toolbar_transTitle);
                break;*/
        }
        tbBack.setOnClickListener(clickListener);
        tbTitle.setText(title);
    }


    /**
     * 初始化ToolBar
     */
    public static void initToolbar(Toolbar toolbar, String title, View.OnClickListener clickListener, @DrawableRes int drawbleId) {
        ImageView tbBack;
        ImageView ivRight;
        TextView tbTitle;
        switch (toolbar.getId()) {
            case R.id.toolbar_centerTitle:
                tbBack = toolbar.findViewById(R.id.toolbar_centerBack);
                tbTitle = toolbar.findViewById(R.id.toolbar_centerTitle);
                ivRight = toolbar.findViewById(R.id.toolbar_right);
                break;
//            case R.id.toolbar_icon:
            default:
                tbBack = toolbar.findViewById(R.id.toolbar_centerBack);
                tbTitle = toolbar.findViewById(R.id.toolbar_centerTitle);
                ivRight = toolbar.findViewById(R.id.toolbar_right);
                break;
        }
        tbBack.setOnClickListener(clickListener);
        tbTitle.setText(title);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(drawbleId);
    }
}
