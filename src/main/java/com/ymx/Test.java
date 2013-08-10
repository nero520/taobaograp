package com.ymx;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author cz
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
           try {
			  Document doc =  Jsoup.connect("http://www.amazon.cn/gp/product/B00CHW97OO/ref=s9_hps_gw_g107_ir05/476-2269575-0439862?pf_rd_m=A1AJ19PSB66TGU&pf_rd_s=center-2&pf_rd_r=15PM2QPGSQKZ38A6W0MJ&pf_rd_t=101&pf_rd_p=67181672&pf_rd_i=899254051").get();
		      String tt = doc.select(".priceLarge").val();
		      System.out.println("xxxxxxxxxxxxxxxxxxxxx" + tt);
           } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
