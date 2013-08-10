/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.my.search;

import com.my.util.SearchUrl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dell
 */
public class SearchNine {

    public static Map<String, List<String>> cidToKeyMap;

    static {
        if (cidToKeyMap == null) {

            cidToKeyMap = new HashMap<>();
            List<String> searchsx = new ArrayList<>();
            searchsx.add("项链9.9");
            searchsx.add("手链9.9");
            searchsx.add("发饰9.9");
            searchsx.add("耳饰9.9");
            searchsx.add("手镯9.9");

          
            List<String> searchjj = new ArrayList<>();
            searchjj.add("住宅家具9.9");
            searchjj.add("装饰摆件9.9");
            searchjj.add("墙贴9.9");
            searchjj.add("睡衣9.9");
            searchjj.add("家居服9.9");
            searchjj.add("收纳整理防尘9.9");
            searchjj.add("清洁洗衣卫浴9.9");
            searchjj.add("家居拖鞋9.9");
            searchjj.add("凉拖9.9");
            searchjj.add("床品套件9.9");
            searchjj.add("居家日用9.9");
            searchjj.add("创意礼品9.9");

           
            List<String> searchmy = new ArrayList<>();
            searchmy.add("饼干9.9");
            searchmy.add("糕点9.9");
            searchmy.add("膨化小食9.9");
            searchmy.add("肉干9.9");
            searchmy.add("肉脯9.9");
            searchmy.add("豆干9.9");
            searchmy.add("肉类9.9");
            searchmy.add("蜜饯9.9");
            searchmy.add("枣类9.9");
            searchmy.add("梅9.9");
            searchmy.add("果脯9.9");
            searchmy.add("山核桃9.9");
            searchmy.add("坚果9.9");
            searchmy.add("炒货9.9");
            
            /**
            List<String> searchxb = new ArrayList<>();
            searchxb.add("单鞋9.9");
            searchxb.add("凉鞋9.9");
            searchxb.add("帆布鞋9.9");
            searchxb.add("松糕鞋9.9");
            searchxb.add("坡跟单鞋9.9");
            searchxb.add("鱼嘴鞋9.9");
            searchxb.add("尖头鞋9.9");
            searchxb.add("娃娃鞋9.9");
            searchxb.add("日常休闲鞋9.9");
            searchxb.add("凉拖9.9");
            searchxb.add("休闲皮鞋9.9");
            searchxb.add("运动休闲鞋9.9");
            searchxb.add("商务休闲鞋9.9");
            searchxb.add("懒人鞋9.9");


            List<String> searchfx = new ArrayList<>();
            searchfx.add("连衣裙9.9");
            searchfx.add("T恤9.9");
            searchfx.add("蕾丝衫9.9");
            searchfx.add("雪纺衫9.9");
            searchfx.add("大码女装9.9");
            searchfx.add("中老年服装9.9");
            searchfx.add("衬衫9.9");
            searchfx.add("休闲裤9.9");
            searchfx.add("半身裙9.9");
            searchfx.add("短袖T恤9.9");
            searchfx.add("长袖T恤9.9");
            searchfx.add("衬衫9.9");
            searchfx.add("牛仔长裤9.9");
            searchfx.add("T恤(9.9");
            searchfx.add("夹克9.9");
            searchfx.add("工装制服9.9");
            **/

            cidToKeyMap.put("412", searchsx);
            cidToKeyMap.put("414", searchjj);
            cidToKeyMap.put("415", searchmy);
            //cidToKeyMap.put("425", searchxb);
            //cidToKeyMap.put("426", searchfx);

        }
    }

    public static List<String> searchNineItems() {
        String searchUrl = SearchUrl.NINE_URL;
        List<String> urls = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            for (Map.Entry<String, List<String>> m : cidToKeyMap.entrySet()) {
                String cid = m.getKey();
                List<String> searchContext = m.getValue();
                for (String sc : searchContext) {
                    try {
                        int spage = (i - 1) * 40;
                        String realUrl = searchUrl.replace("query", sc).replace("spage", String.valueOf(spage));
                        Document doc = Jsoup.connect(realUrl).get();
                        Elements elements = doc.select("ul li.list-item");
                        for (Element e : elements) {
                            String money = e.select("ul li em").html().trim();
                            if (money.indexOf("9.") != -1) {
                                String[] array = money.split("\\.");
                                if (array[0].length() == 1) {
                                    String itemUrl = e.select("div a").attr("href") + "&cidurl=" + cid;
                                    urls.add(itemUrl);
                                    System.out.println("add item url------" + itemUrl);
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SearchNine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return urls;
    }
}
