package com.qmtv.crashintercept.resolve_crash;

import android.content.Intent;
import android.widget.Toast;

import com.qmtv.crashintercept.Main2Activity;
import com.qmtv.crashintercept.Main3Activity;
import com.qmtv.crashintercept.Main4Activity;
import com.qmtv.lib_crash_intercept.ActivityBind;
import com.qmtv.lib_crash_intercept.base.IResoleCrash;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * description：处理拦截到的错误
 *
 * @author 王旭
 * create date：2018/9/14
 */
public class ResolveCrash implements IResoleCrash {
    @Override
    public void resolveCrash(ActivityBind errorActivityBind, Thread thread, Throwable throwable) {
        Toast.makeText(errorActivityBind.activity.getApplicationContext(), "Shit happens!",
                Toast.LENGTH_SHORT).show();

        // 这种处理适用于启动页、引导页此等过渡页,跳转后此页必须退出
        if (errorActivityBind.activity instanceof Main2Activity) {
            errorActivityBind.activity.startActivity(new Intent(errorActivityBind.activity,
                    Main3Activity.class));
            errorActivityBind.activity.finish();
        }

        if (errorActivityBind.activity instanceof Main3Activity) {
            // 模拟黑白名单，白名单内的异常破坏力小可以不做处理，黑名单内的异常破坏力大必须做降级处理
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            throwable.printStackTrace(writer);
            StringBuffer buffer = stringWriter.getBuffer();
            if (buffer.toString().contains("(Main3Activity.java:17)")) {
                Toast.makeText(errorActivityBind.activity.getApplicationContext(),
                        "Xu~~~ nothing happens!",
                        Toast.LENGTH_SHORT).show();
            } else if (buffer.toString().contains("(Main3Activity.java:24)")) {
                errorActivityBind.activity.startActivity(new Intent(errorActivityBind.activity,
                        Main4Activity.class));
                errorActivityBind.activity.finish();
            }
        }
    }
}
