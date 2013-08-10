/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.my.run;

import com.my.json.JSONArray;
import com.my.json.JSONException;
import com.my.json.JSONObject;
import com.my.search.SearchNine;
import com.my.util.UrlUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Item;
import com.taobao.api.domain.TaobaokeItemDetail;
import com.taobao.api.request.ItemGetRequest;
import com.taobao.api.request.TaobaokeItemsDetailGetRequest;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.TaobaokeItemsDetailGetResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author dell
 */
public class RunNine {

    private ApplicationContext context;
    private TaobaoClient client;
    private TaobaoClient keClient;

    public RunNine() {
        context = new FileSystemXmlApplicationContext("/src/main/resources/applicationContext.xml");
        client = (DefaultTaobaoClient) context.getBean("taoBaoClient");
        keClient = (DefaultTaobaoClient) context.getBean("taoBaoKeClient");
    }

    private List<String> getNineItemsUrl() {
        List<String> itemsList = SearchNine.searchNineItems();
        Collections.shuffle(itemsList);
        return itemsList;
    }

    private String replaceContent(String content) {
        return content.replaceAll("'", "  ");
    }

    private long genDate(String date) throws ParseException {
        date = date.replaceAll("\\.", "-");
        long dhs = Math.round(Math.random() * 3600 * 8 + 8 * 3600);
        long returnTime = (new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()) / 1000L + dhs;
        return returnTime;
    }

    private List<String> genSql(int itemPk) {
        String dataList = "";
        List<String> sqlList = new ArrayList<>();

        List<String> itemsList = getNineItemsUrl();
        for (String url : itemsList) {
            try {
                System.out.println("parse url-----:" + url);
                Map<String, String> para = UrlUtil.parseUrl(url);
                if (para != null) {
                    ItemGetRequest req = new ItemGetRequest();
                    req.setFields("num_iid,title,detail_url,price,pic_url");
                    req.setNumIid(Long.valueOf(para.get("id")));
                    ItemGetResponse igr = client.execute(req);
                    Item item = igr.getItem();


                    TaobaokeItemsDetailGetRequest  tkReq = new TaobaokeItemsDetailGetRequest ();
                    String nunIids = String.valueOf(item.getNumIid());
                    tkReq.setFields("num_iid,detail_url,click_url");
                    tkReq.setPid(32709857L);
                    tkReq.setNumIids(nunIids);
                    TaobaokeItemsDetailGetResponse  response = keClient.execute(tkReq);
                    System.out.println(response.getBody());
                    TaobaokeItemDetail tbid = response.getTaobaokeItemDetails().get(0);   
                    String itemTgUrl = tbid.getClickUrl();
                    System.out.println("############" + itemTgUrl);
                    String sql = "insert into pp_items (cid,level,item_key,title,img,simg,bimg,price,url,info,sid,hits,dlikes,likes,haves,"
                            + "comments,comments_last,is_index,status,add_time,last_time,uid,seo_title,seo_keys,sort_order,seo_desc,"
                            + "remark,remark_status) values ";
                    sql = sql + "("
                            + para.get("cidurl") + ","
                            + "3" + ","
                            + "'taobao_" + item.getNumIid() + "',"
                            + "'" + item.getTitle() + "',"
                            + "'" + item.getPicUrl() + "_210x1000.jpg" + "',"
                            + "'" + item.getPicUrl() + "_64x64.jpg" + "',"
                            + "'" + item.getPicUrl() + "',"
                            + 9.9 + ","
                            + "'" + item.getDetailUrl() + "',"
                            + "'" + " " + "',"
                            + "1,"
                            + "0,"
                            + "0,"
                            + String.valueOf(item.getVolume() == null ? Math.round(Math.random() * 500 + 2000) : item.getVolume()) + ","
                            + String.valueOf(item.getVolume() == null ? Math.round(Math.random() * 500 + 2000) : item.getVolume()) + ","
                            + "0,"
                            + "' ',"
                            + "0,"
                            + "1,"
                            + (long) (System.currentTimeMillis() / 1000) + ","
                            + (long) (System.currentTimeMillis() / 1000) + ","
                            + "0,"
                            + "'" + item.getTitle() + "',"
                            + "'" + item.getTitle() + "',"
                            + "0,"
                            + "'',"
                            + "'',"
                            + "1);";
                    sqlList.add(sql);

                    String detailUrl = item.getDetailUrl();
                    Document doc = Jsoup.connect(detailUrl).get();
                    if (detailUrl.indexOf("detail.tmall.com") != -1) {
                        itemPk++;
                        continue;
                    } else {
                        String dataListApi = doc.select("#reviews").attr("data-listApi");
                        if (dataListApi != null && !"".equals(dataListApi)) {
                            System.out.println("********** get data-listApi");
                            String dataUrl = dataListApi + "&currentPageNum=1&rateType=&orderType=sort_weight&showContent=1&attribute=&callback=jsonp_reviews_list";
                            dataList = Jsoup.connect(dataUrl).get().text();
                            dataList = dataList.substring(19, dataList.length() - 1);
                            JSONObject jo = new JSONObject(dataList);
                            if (!jo.isNull("comments")) {
                                JSONArray comments = jo.getJSONArray("comments");
                                if (comments != null) {
                                    String commentsql = "insert into pp_user_comments(uid,pid,info,type,orig,status,add_time) values (#sql);";
                                    for (int i = comments.length() - 1; i >= 0; i--) {
                                        JSONObject commentObject = comments.getJSONObject(i);
                                        String content = commentObject.get("content").toString();
                                        String date = commentObject.get("date").toString();
                                        content = replaceContent(content);
                                        long commentDate = genDate(date);
                                        String realSql = "0," + itemPk + ",'" + content + "','item','index',1," + commentDate;
                                        String insertSql = commentsql.replace("#sql", realSql);
                                        sqlList.add(insertSql);
                                    }
                                }
                            }
                            itemPk++;
                        } else {
                            itemPk++;
                        }
                    }
                }
            } catch (ApiException | JSONException | IOException | ParseException ex) {
                if (ex instanceof ApiException) {
                    System.out.println("apiException:" + ex);
                } else {
                    System.out.println(ex);
                    itemPk++;
                }
                continue;
            }
        }
        return sqlList;
    }

    public void genSqlToFile() {
        int itemPk = 1;
        List<String> sqlList = genSql(itemPk);
        try {
            try (FileOutputStream fos = new FileOutputStream(new File("d:\\items.sql")); OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                for (String itemsql : sqlList) {
                    osw.write(itemsql + "\r\n");
                }
                osw.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        // ProxyUtil.setProxy();
        new RunNine().genSqlToFile();
    }
}
