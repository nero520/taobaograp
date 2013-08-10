/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.my.test;

import com.my.json.JSONArray;
import com.my.json.JSONException;
import com.my.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author cz
 */
public class Test {

    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("http://item.taobao.com/item.htm?id=15514550427").get();
            String dataListApi = doc.select("#reviews").attr("data-listApi");
            String dataUrl = dataListApi + "&currentPageNum=1&rateType=&orderType=sort_weight&showContent=1&attribute=&callback=jsonp_reviews_list";
            String dataList = Jsoup.connect(dataUrl).get().text();
            dataList = dataList.substring(19, dataList.length() - 1);
            JSONObject jo = new JSONObject(dataList);
            if (!jo.isNull("comments")) {
                JSONArray comments = jo.getJSONArray("comments");
                String sql = "insert into pp_user_comments(uid,pid,info,type,orig,status,add_time) values (#sql);";
                for (int i = 0; i < comments.length(); i++) {
                    String content = comments.getJSONObject(i).get("content").toString();
                    String realSql = "0,13577,'" + content + "','item','index',1," + (long) (System.currentTimeMillis() / 1000);
                    String insertSql = sql.replace("#sql", realSql);
                    System.out.println(insertSql);
                }
            }
        } catch (JSONException | IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
