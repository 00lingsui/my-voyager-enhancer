package com.voyager.enhancer;

import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    private static final String TAG = "VoyagerEnhancer";

    // 目标域名白名单
    private static final String[] TARGET_DOMAINS = {
            "chatgpt.com",
            "gemini.google.com",
            "claude.ai",
            "chat.deepseek.com"
    };

    private static String cachedScript = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 全局 Hook WebViewClient 的 onPageFinished 方法
        XposedHelpers.findAndHookMethod(
                WebViewClient.class,
                "onPageFinished",
                WebView.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        WebView webView = (WebView) param.args[0];
                        String url = (String) param.args[1];

                        if (url == null || webView == null) return;

                        if (isTargetAiSite(url)) {
                            XposedBridge.log(TAG + " => 命中 AI 站点: " + url + "，执行前端增强脚本注入");
                            injectScript(webView);
                        }
                    }
                }
        );
    }

    private boolean isTargetAiSite(String url) {
        for (String domain : TARGET_DOMAINS) {
            if (url.contains(domain)) return true;
        }
        return false;
    }

    private void injectScript(WebView webView) {
        webView.post(() -> {
            try {
                // 开启基础权限
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);

                // 读取脚本
                String script = getScriptContent(webView.getContext());
                if (script != null && !script.isEmpty()) {
                    webView.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            XposedBridge.log(TAG + " => 脚本注入反馈: " + value);
                        }
                    });
                }
            } catch (Throwable t) {
                XposedBridge.log(TAG + " => 注入异常: " + t.getMessage());
            }
        });
    }

    private synchronized String getScriptContent(Context context) {
        if (cachedScript != null) return cachedScript;
        try {
            // 从模块自身 assets 读取
            Context modContext = context.createPackageContext("com.voyager.enhancer", Context.CONTEXT_IGNORE_SECURITY);
            InputStream is = modContext.getAssets().open("voyager_enhancer.js");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            cachedScript = sb.toString();
            return cachedScript;
        } catch (Exception e) {
            XposedBridge.log(TAG + " => 读取本地 assets 失败，使用内嵌 fallback 注入: " + e.getMessage());
            return getFallbackScript();
        }
    }

    private String getFallbackScript() {
        return "(function(){\n" +
               "  if(window.__V_LOADED__) return; window.__V_LOADED__ = true;\n" +
               "  var d = document.createElement('div');\n" +
               "  d.id = 'voyager-fab-btn';\n" +
               "  d.innerText = '📁';\n" +
               "  d.style.cssText = 'position:fixed;left:0;top:40%;z-index:999999;background:#10a37f;color:#fff;padding:8px 10px;border-radius:0 12px 12px 0;font-size:16px;cursor:pointer;';\n" +
               "  d.onclick = function() { alert('Voyager 移动端增强已就绪！可进入设置添加文件夹分类与常用 Prompt。'); };\n" +
               "  document.body.appendChild(d);\n" +
               "})();";
    }
}
