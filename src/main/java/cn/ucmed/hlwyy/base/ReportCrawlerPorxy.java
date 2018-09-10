package cn.ucmed.hlwyy.base;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YS-GZD-1495 on 2018/7/5.
 */
public abstract class ReportCrawlerPorxy  {


    private final static Logger logger = Logger.getLogger(ReportCrawlerPorxy.class);




    public static void main(String[] args) {
        String wsdlUrl = "http://test.ltc.ucmed.cn/test/open.htm";
        JSONObject obj = new JSONObject();
        obj.put("a", "1");
        obj.put("b", "2");
        String msg = obj.toString();
        String xmls = null;
        HttpPost post = new HttpPost(wsdlUrl);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpEntity entity = new StringEntity(msg, "UTF-8");
            post.setHeader("Content-Type", "text/xml; charset=utf-8");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            post.setEntity(new UrlEncodedFormEntity(nvps));
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
        } catch (UnsupportedEncodingException e) {
            logger.debug(e);

        } catch (ParseException e) {
            logger.debug(e);

        } catch (IOException e) {
            logger.debug(e);

        }
    }



    /**
     * HTTP请求
     *
     * @param msg
     * @param wsdlUrl
     * @param action
     * @return
     */
    public String invoker(String msg, String wsdlUrl, String action) {
        String xmls = null;
        HttpPost post = new HttpPost(wsdlUrl);

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            logger.info(wsdlUrl + " : input"+ msg);
            HttpEntity entity = new StringEntity(msg, "UTF-8");
            post.setHeader("Content-Type", "text/xml; charset=utf-8");
            if (StringUtils.isNotBlank(action)) {
                post.setHeader("SOAPAction", action);
            }
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            xmls = EntityUtils.toString(response.getEntity()).toString();
            logger.info(wsdlUrl + " : output"+ msg);
        } catch (UnsupportedEncodingException e) {
            logger.debug(e);
        } catch (ParseException e) {
            logger.debug(e);

        } catch (IOException e) {
            logger.debug(e);

        }
        return xmls;
    }

//    /**
//     * wcf的we
//     *
//     * @return
//     */
//    public String wcfinvoker(String wsdlUrl, String tempUrl, String action, Map<String, String> param) {
//
//        Service service = new Service();
//        Object retVal2 = null;
//        try {
//            Call call2 = (Call) service.createCall();
//            call2.setTargetEndpointAddress(wsdlUrl);
//            call2.setUseSOAPAction(true);
////			             call2.setOperationName(new QName("http://tempuri.org/", "GetResultReport"));//设置函数名
//            call2.setOperationName(new QName("http://tempuri.org/", action));//设置函数名
////			             call2.setSOAPActionURI("http://tempuri.org/IWReportResult/GetResultReport");//设置URI
//            call2.setSOAPActionURI(tempUrl);//设置URI
//            List<String> values = new ArrayList<>();
//            for (Map.Entry<String, String> entry : param.entrySet()) {
//                values.add(entry.getValue());
//                call2.addParameter(new QName("http://tempuri.org/", entry.getKey()), XMLType.XSD_STRING, ParameterMode.IN);  // 这里设置对应参数名称
//            }
//            call2.setReturnClass(String.class);
//            retVal2 = call2.invoke(values.toArray());  //调用并带上参数数据
//        } catch (Exception e) {
//            logger.debug(e);
//
//        }
//        if (retVal2 == null) {
//            return "";
//        }
//        return retVal2.toString();
//    }
}
