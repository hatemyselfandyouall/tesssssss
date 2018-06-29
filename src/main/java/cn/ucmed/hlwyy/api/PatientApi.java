package cn.ucmed.hlwyy.api;

import cn.ucmed.hlwyy.common.ApiResponse;
import cn.ucmed.hlwyy.exception.ServiceException;
import cn.ucmed.hlwyy.model.patient.HisDoctor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 公共接口-就诊人接口
 *
 * @author HJH
 * @since 2017-12-12
 **/
@Api(tags = "公共接口-就诊人接口")
@RestController
@RequestMapping("/util/patient")
public class PatientApi {

    private static final Logger LOG = LoggerFactory.getLogger(PatientApi.class);


    @RequestMapping(value = "/jidaPayHis", produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> jidaPayHis(
            @ApiParam(value = "reservationPrice") @RequestParam(value = "reservationPrice", required = false) String reservationPrice,
            @ApiParam(value = "reservationPatientId") @RequestParam(value = "reservationPatientId", required = false) String reservationPatientId,
            @ApiParam(value = "reservationPatientIdCard") @RequestParam(value = "reservationPatientIdCard", required = false) String reservationPatientIdCard,
            @ApiParam(value = "reservationPatientPhone") @RequestParam(value = "reservationPatientPhone", required = false) String reservationPatientPhone,
            @ApiParam(value = "reservationPatientName") @RequestParam(value = "reservationPatientName", required = false) String reservationPatientName,
            @ApiParam(value = "reservationCreateTime") @RequestParam(value = "reservationCreateTime", required = false) String reservationCreateTime,
            @ApiParam(value = "reservationDoctorName") @RequestParam(value = "reservationDoctorName", required = false) String reservationDoctorName,
            @ApiParam(value = "reservationDepartmentName") @RequestParam(value = "reservationDepartmentName", required = false) String reservationDepartmentName,
            @ApiParam(value = "recordHisPatientId") @RequestParam(value = "recordHisPatientId", required = false) String recordHisPatientId,
            @ApiParam(value = "departmentKeshiId") @RequestParam(value = "departmentKeshiId", required = false) String departmentKeshiId,
            @ApiParam(value = "recordHisCardNo") @RequestParam(value = "recordHisCardNo", required = false) String recordHisCardNo,
            @ApiParam(value = "R") @RequestParam(value = "R", required = false) String R,
            @ApiParam(value = "I") @RequestParam(value = "I", required = false) String I,
            @ApiParam(value = "record_id") @RequestParam(value = "record_id", required = false) String record_id,
            @ApiParam(value = "biz_type") @RequestParam(value = "biz_type", required = false) String biz_type,
            @ApiParam(value = "fee") @RequestParam(value = "fee", required = false) String fee,
            @ApiParam(value = "pay_time") @RequestParam(value = "pay_time", required = false) String pay_time,
            @ApiParam(value = "pay_type") @RequestParam(value = "pay_type", required = false) String pay_type,
            @ApiParam(value = "plat_type") @RequestParam(value = "plat_type", required = false) String plat_type,
            @ApiParam(value = "trade_status") @RequestParam(value = "trade_status", required = false) String trade_status,
            @ApiParam(value = "pay_order_no") @RequestParam(value = "pay_order_no", required = false) String pay_order_no,
            @ApiParam(value = "out_trade_no") @RequestParam(value = "out_trade_no", required = false) String out_trade_no,
            @ApiParam(value = "update_code") @RequestParam(value = "update_code", required = false) String update_code,
            @ApiParam(value = "buyer_third_party_id") @RequestParam(value = "buyer_third_party_id", required = false) String buyer_third_party_id,
            @ApiParam(value = "third_party_no") @RequestParam(value = "third_party_no", required = false)String third_party_no) {
        try {
            LOG.info("调用回调方法");
            JSONObject res=new JSONObject();
            res.put("R",R);
            res.put("I",I);
            res.put("record_id",record_id);
            res.put("biz_type",biz_type);
            res.put("fee",fee);
            res.put("pay_time",pay_time);
            res.put("pay_type",pay_type);
            res.put("plat_type",plat_type);
            res.put("trade_status",trade_status);
            res.put("pay_order_no",pay_order_no);
            res.put("out_trade_no",out_trade_no);
            res.put("update_code",update_code);
            res.put("buyer_third_party_id",buyer_third_party_id);
            res.put("third_party_no",third_party_no);
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            String orderNo=tuisongjiaofei(res, reservationPrice, reservationPatientId, reservationPatientIdCard, reservationPatientPhone, reservationPatientName, reservationCreateTime, cl);
            LOG.info("调用回调方法tuisongjiaofei成功，返回"+orderNo);
            String TranNo=null;
            if(orderNo!=null){
                TranNo=zhifuqueren(orderNo, res, reservationPrice, cl);
            }
            //调用院方his接口
            JSONObject his1Data=this.his1(res, reservationDoctorName, reservationDepartmentName, recordHisPatientId, reservationCreateTime, departmentKeshiId,cl);
            this.his2(TranNo, his1Data, res,  recordHisCardNo, recordHisPatientId, reservationPrice,cl);
            return  ApiResponse.responseSuccess(his1Data);
        } catch (Exception e) {
            LOG.error("支付宝wap回调错误", e);
            return  ApiResponse.responseException();
        }
    }

    /**
     * 卓健微信接口-推送缴费订单
     * @param zhuojianpay
     * @return
     */
    private String tuisongjiaofei(JSONObject zhuojianpay,String reservationPrice,String reservationPatientId,String reservationPatientIdCard,String reservationPatientPhone,String reservationPatientName,String reservationCreateTime,ClassLoader classLoader){
        try {
            //1.封装参数
            JSONObject params=new JSONObject();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String MerBillNo=zhuojianpay.optString("out_trade_no");
            String MerNo="E000009901";
            SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
            String testTime=sdf2.format(sdf1.parse(reservationCreateTime));
            String OrderDate=testTime;
            String OrderAmount=reservationPrice;
            String OrderSource="07";
            String OrderSourceCode="04";
            String key="DE4YN0GR0ST4NR6TDWYWAHHJOTBMRFWA";
            String SignData=md5((MerBillNo+"|"+MerNo+"|"+OrderDate+"|"+OrderAmount+"|"+OrderSource+"|"+OrderSourceCode+"|"+key).toUpperCase());
            params.put("MerBillNo", MerBillNo);
            params.put("MerNo", MerNo);
            params.put("OrderDate", OrderDate);
            params.put("OrderAmount", OrderAmount);
            params.put("OrderSource", OrderSource);
            params.put("OrderSourceCode", OrderSourceCode);
            params.put("ProductName", "图文咨询");
            params.put("CreateBy", "Zhuojian");
            params.put("OrderType", "02");
            //就诊卡可能为空
            params.put("SourceId", StringUtils.isEmpty(reservationPatientId)?reservationPatientIdCard:reservationPatientId);
            params.put("Phone", reservationPatientPhone);
            params.put("PatientName", reservationPatientName);
            params.put("SignType", "01");
            params.put("SignData", SignData);
            XMLSerializer xmlSerializer=new XMLSerializer();
            xmlSerializer.setRootName("data");
            xmlSerializer.setTypeHintsEnabled(false);
            String inputXml=xmlSerializer.write(params);
            LOG.info("ZHUOJIAN1-in:" + inputXml);
//            if(true){
//                return UUID.randomUUID().toString();
//            }
            //2.调用接口
            String wsdl="http://222.168.29.22:8000/MessageApi.asmx?wsdl";
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient(wsdl);
            Object[] results;
            Thread.currentThread().setContextClassLoader(classLoader);
            results = client.invoke("MuPayInterface",new Object[] {"F00010202",inputXml,"JSON"});
            //3.返回数据
            String result = results == null || results.length == 0 ? null : results[0].toString();
            LOG.info("ZHUOJIAN1-out:"+result);
            JSONObject resultJ=JSONObject.fromObject(result).getJSONObject("data");
            if("0".equals(resultJ.getString("RspCode"))){
                return resultJ.getString("OrderNo");
            }else{
                LOG.error("图文咨询订单支付回调后调用【推送缴费订单】失败，reservation表id："+"原因："+resultJ.getString("RspMsg"));
                return null;
            }
        } catch (Exception e) {
            LOG.error("图文咨询订单支付回调后调用【推送缴费订单】失败，reservation表id：",e);
            return null;
        }
    }

    private static String md5(String password) {
        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 卓健微信接口-支付确认
     * @param orderNo
     * @param zhuojianpay
     * @param reservationPrice
     * @return
     */
    private String zhifuqueren(String orderNo,JSONObject zhuojianpay,String reservationPrice,ClassLoader classLoader){
        try {
            //1.封装参数
            JSONObject params=new JSONObject();
            String MerBillNo=zhuojianpay.optString("out_trade_no");
            String ChannelCode="WEIXIN_WAP";
            String ChannelTransNo=zhuojianpay.optString("out_trade_no");
            String PayAmount=reservationPrice;
            String key="DE4YN0GR0ST4NR6TDWYWAHHJOTBMRFWA";
            String SignData=md5((MerBillNo+"|"+orderNo+"|"+ChannelCode+"|"+ChannelTransNo+"|"+PayAmount+"|"+key).toUpperCase());
            params.put("MerBillNo", MerBillNo);
            params.put("ChannelCode", ChannelCode);
            params.put("ChannelTransNo", ChannelTransNo);
            params.put("OrderNo", orderNo);
            params.put("PayType", "11");
            params.put("OperUser", "Zhuojian");
            params.put("PayAppCode", "07");
            params.put("PaySourceCode", "04");
            params.put("BankPayInfo", "04");
            params.put("PayAmount", PayAmount);
            params.put("SignType", "01");
            params.put("TermNo", "0");//必填
            params.put("SignData", SignData);
            JSONObject outside=new JSONObject();
            outside.put("row", params);
            XMLSerializer xmlSerializer=new XMLSerializer();
            xmlSerializer.setTypeHintsEnabled(false);
            xmlSerializer.setRootName("data");
            String inputXml=xmlSerializer.write(outside);
            LOG.info("ZHUOJIAN2-in:"+inputXml);
//            if(true){
//                return null;
//            }
            //2.调用接口
            String wsdl="http://222.168.29.22:8000/MessageApi.asmx?wsdl";
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient(wsdl);
            Object[] results;
            Thread.currentThread().setContextClassLoader(classLoader);
            results = client.invoke("MuPayInterface",new Object[] {"F000109",inputXml,"JSON"});
            //3.返回数据
            String result = results == null || results.length == 0 ? null : results[0].toString();
            LOG.info("ZHUOJIAN2-out:"+result);
            JSONObject resultJ=JSONObject.fromObject(result).getJSONObject("data");
            if("0".equals(resultJ.getString("RspCode"))){
                return resultJ.getString("TranNo");
            }else{
                LOG.error("图文咨询订单支付回调后调用【支付确认接口】失败"+"原因："+resultJ.getString("RspMsg"));
                return null;
            }
        } catch (Exception e) {
            LOG.error("图文咨询订单支付回调后调用【支付确认接口】失败",e);
            return null;
        }
    }
    /**
     * his咨询服务收费信息录入
     * @param zhuojianpay

     * @return
     */
    private   JSONObject his1(JSONObject zhuojianpay,String reservationDoctorName,String reservationDepartmentName,String recordHisPatientId,String reservationCreateTime,String departmentKeshiId,ClassLoader classLoader){
        try {
            //查找保存就诊卡时，从his返回的数据
            HisDoctor hisDoctor= null;
            //1.封装参数
            JSONObject params=new JSONObject();
            params.put("TranCode", "41001");
            params.put("TranFlowNo", zhuojianpay.optString("trade_no"));
            if(!StringUtils.isEmpty(recordHisPatientId)){
                params.put("BingrenID", recordHisPatientId);
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("Kaidanrq", reservationCreateTime);
            params.put("Caozuoren", "MUZHUOJIAN");
            params.put("UserID", "MUZHUOJIAN");
            if(!StringUtils.isEmpty(departmentKeshiId)&&hisDoctor!=null){
                params.put("Kaidanks", departmentKeshiId);
                params.put("Kaidanys", hisDoctor.getId());
                params.put("Zhixingks", departmentKeshiId);
            }else {
                params.put("Kaidanks","01");
                params.put("Kaidanys", "DBA");
                params.put("Zhixingks", "28");
            }
            params.put("Shoufeixm", "4092");
            params.put("DeviceID", "MUZHUOJIAN");
            XMLSerializer xmlSerializer=new XMLSerializer();
            xmlSerializer.setTypeHintsEnabled(false);
            xmlSerializer.setRootName("Request");
            String inputXml=xmlSerializer.write(params);
            LOG.info("his2-in:"+inputXml);
            //2.调用接口
            String wsdl="https://222.168.29.22:8082/Service1.asmx?wsdl";
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient(wsdl);
            Object[] results;
            Thread.currentThread().setContextClassLoader(classLoader);

            results = client.invoke("Getyinyi_sp",new Object[] {inputXml});
            //3.返回接口数据
            String result = results == null || results.length == 0 ? null : results[0].toString();
            result=xmlSerializer.read(result).toString();
            LOG.info("his2-out:"+result);
            JSONObject resultJ=JSONObject.fromObject(result);
            if("0".equals(resultJ.getString("ResultCode"))){
                if(resultJ!=null){
                    resultJ.put("patientId", recordHisPatientId);
                }
                return resultJ;
            }else{
                LOG.error("保存就诊人调用his【虚拟办卡（电子就诊卡）】失败，reservation表id："+"原因："+resultJ.getString("ErrorMsg"));
                return null;
            }
        } catch (Exception e) {
            LOG.error("保存就诊人调用his【虚拟办卡（电子就诊卡）】失败，reservation表id：", e);
            return null;
        }
    }

    /**
     * his缴费接口（11015）
     * @param his1Data
     * @param zhuojianpay
     */
    private  void his2(String orderNo,JSONObject his1Data, JSONObject zhuojianpay,String recordHisCardNo,String recordHisPatientId,String reservationPrice,ClassLoader classLoader) {
        try {
            //1.封装参数
            JSONObject params = new JSONObject();
            params.put("TranCode", "11015");
            if (zhuojianpay.has("record_id")) {
                String temp = zhuojianpay.optString("record_id");
                temp = temp.substring(0, temp.length() / 2);
                params.put("TranFlowNo", temp);
            }
            params.put("CardTypeCode", 0);
            if (!StringUtils.isEmpty(recordHisCardNo)) {
                params.put("CardNo", recordHisCardNo);
            }
            if (!StringUtils.isEmpty(recordHisPatientId)) {
                params.put("PatientID", recordHisPatientId);
            }
            if (his1Data != null) {
                if (his1Data.containsKey("Jiuzhenid")) {
                    params.put("AdmRowId", his1Data.getString("Jiuzhenid"));
                }
                if (his1Data.containsKey("Yijiid")) {
                    params.put("ChuFangId", his1Data.getString("Yijiid"));
                }
            }
            params.put("Zhifufs", "3");
            params.put("acctno", "E000009901");
            params.put("sourceID", "4");
            params.put("refNum", orderNo);
            params.put("Amount", reservationPrice);
            params.put("UserId", "MUZHUOJIAN");
            params.put("DeviceID", "MUZHUOJIAN");
            params.put("InsurePayStr", "");
            params.put("BankCardNo", "");
            params.put("SystemReferNo", "");
            params.put("TradeTime", "");
            params.put("PosRequestFlowNo", "");
            params.put("PosID", "");
            params.put("PassWord", "");
            params.put("YBJIUZHENID", "");
            XMLSerializer xmlSerializer = new XMLSerializer();
            xmlSerializer.setTypeHintsEnabled(false);
            xmlSerializer.setRootName("Request");
            String inputXml = xmlSerializer.write(params);
            LOG.info("his3-in:" + inputXml);
            //2.调用接口
            String wsdl = "https://222.168.29.22:8082/Service1.asmx?wsdl";
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient(wsdl);
            Object[] results;
            Thread.currentThread().setContextClassLoader(classLoader);
            results = client.invoke("Getyinyi_sp", new Object[]{inputXml});
            //3.返回接口数据
            String result = results == null || results.length == 0 ? null : results[0].toString();
            result = xmlSerializer.read(result).toString();
            LOG.info("his3-out:" + result);
            JSONObject resultJ = JSONObject.fromObject(result);
            if ("0".equals(resultJ.getString("ResultCode"))) {

            } else {
                LOG.error("保存就诊人调用his【虚拟办卡（电子就诊卡）】失败，reservation表id：" + "原因：" + resultJ.getString("ErrorMsg"));
            }
        } catch (Exception e) {
            LOG.error("保存就诊人调用his【虚拟办卡（电子就诊卡）】失败，reservation表id：", e);
        }
    }


    @ApiOperation(value = "获取就诊人详情", notes = "获取就诊人详情")
    @RequestMapping(value = "/jidaHis1", produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> jidaHis1(
            @ApiParam(value = "patientName", required = true) @RequestParam(value = "patientName") String patientName,
            @ApiParam(value = "patientSex", required = true) @RequestParam(value = "patientSex")String patientSex,
            @ApiParam(value = "patientIdCard", required = true) @RequestParam(value = "patientIdCard")String patientIdCard,
            @ApiParam(value = "patientPhone", required = true) @RequestParam(value = "patientPhone")String patientPhone,
            @ApiParam(value = "patientId", required = true) @RequestParam(value = "patientId")String patientId) {
        JSONObject result=null;
        try {
             result=his1(patientName,patientSex,patientIdCard,patientPhone,patientId);
            return ApiResponse.responseSuccess(result);
        } catch (ServiceException se) {
            return ApiResponse.responseError(se.getMessage()+result);
        } catch (Exception e) {
            LOG.error("公共接口-就诊人接口-获取就诊人详情-错误", e);
            return ApiResponse.responseException();
        }
    }

    @ApiOperation(value = "获取就诊人详情", notes = "获取就诊人详情")
    @RequestMapping(value = "/test", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> test(
            ) {
        try {
            return ApiResponse.responseSuccess("111");
        } catch (ServiceException se) {
            return ApiResponse.responseError(se.getMessage()+"222");
        } catch (Exception e) {
            LOG.error("公共接口-就诊人接口-获取就诊人详情-错误", e);
            return ApiResponse.responseException();
        }
    }

	//his虚拟办卡（电子就诊卡）（31003）
    private JSONObject his1(String patientName,String patientSex,String patientIdCard,String patientPhone,String patientId){
    	try {
    		//1.封装参数
            JSONObject params=new JSONObject();
            params.put("TranCode", "31003");
            params.put("CardTypeCode", "0");
            params.put("PatientName", patientName);
            params.put("Sex", StringUtils.isEmpty(patientSex)||"1".equals(patientSex)?"男":"女");
            String idCard=patientIdCard;
            if(idCard.length()==18){
            	String tempbirth=idCard.substring(6, 14);
            	params.put("Birthday", tempbirth.substring(0,4)+"-"+tempbirth.substring(4,6)+"-"+tempbirth.substring(6,8));
            	params.put("IDCardNo", idCard);
            }
            params.put("Tel",patientPhone);
            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("ActDate", sdf2.format(new Date()));
            params.put("UserID", "MUZHUOJIAN");
            params.put("CompanyType", "SST");
            params.put("ZhenliaoType", "1");
            params.put("DeviceID", "QYW");
            XMLSerializer xmlSerializer=new XMLSerializer();
	    	xmlSerializer.setTypeHintsEnabled(false);
	    	xmlSerializer.setRootName("Request");
	    	String inputXml=xmlSerializer.write(params);
	    	LOG.info("his1-in:" + inputXml);
    		//2.调用接口
	    	String wsdl="https://222.168.29.22:8082/Service1.asmx?wsdl";
	    	JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
	    	Client client = clientFactory.createClient(wsdl);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            long timeout = 10 * 60 * 1000;//
            policy.setConnectionTimeout(timeout);
            policy.setReceiveTimeout(timeout);
            conduit.setClient(policy);
	    	Object[] results;
			results = client.invoke("Getyinyi_sp",new Object[] {inputXml});
			//3.保存数据到patient_his_data
			String result = results == null || results.length == 0 ? null : results[0].toString();
			result=xmlSerializer.read(result).toString();
			LOG.info("his1-out:"+result);
			JSONObject resultJ=JSONObject.fromObject(result);
            return resultJ;
		} catch (Exception e) {
			LOG.error("保存就诊人调用his【虚拟办卡（电子就诊卡）】失败，patient表id："+patientId,e);
            return null;
		}
    }
    public static void main(String[] args) {
        try {
			JSONObject params=new JSONObject();
			//his1
//			params.put("TranCode", "31003");
//			params.put("CardTypeCode", "0");
//			params.put("PatientName", "董乐和");
//			params.put("Sex", "男");
//			params.put("Birthday","1993-07-01");
//			params.put("Tel", "13333333333");
//			params.put("ActDate", "2018-06-04 14:36:00");
//			params.put("UserID", "Zhuojian");
//			params.put("CompanyType", "SST");
//			params.put("ZhenliaoType", "1");
//			params.put("Amt", "0");
//			params.put("IDCardNo", "542322199307016078");
//			params.put("DeviceID", "QYW");
			//his2
			params.put("TranCode", "41001");
            params.put("BingrenID", "116799769");
            params.put("Kaidanrq", "2018-05-20 19:15:00");
            params.put("Caozuoren", "DBA");
            params.put("UserID", "DBA");
        	params.put("Kaidanks", "01");
        	params.put("Kaidanys", "DBA");
        	params.put("Shoufeixm", "4092");
        	params.put("DeviceID", "MUs01");
            params.put("Zhixingks", "28");
            //his3
//			params.put("TranCode", "11015");
//			params.put("TranFlowNo", "0");
//			params.put("ChuFangId", "('0')");
//            params.put("CardTypeCode", "0");
//            params.put("CardNo", "800001251");
//            params.put("PatientID", "114144319");
//            params.put("AdmRowId", "0");
//            params.put("Zhifufs", "3");
//            params.put("acctno", "E000009901");
//            params.put("sourceID", "4");
//            params.put("refNum", "0");
//            params.put("Amount", "0");
//            params.put("UserId", "Zhuojian");
//            params.put("DeviceID", "QYW");
			XMLSerializer xmlSerializer=new XMLSerializer();
			xmlSerializer.setTypeHintsEnabled(false);
			xmlSerializer.setRootName("Request");
			String inputXml=xmlSerializer.write(params);
//            inputXml="<Request>\n" +
//                    "<TranCode>11015</TranCode>\n" +
//                    "<CardTypeCode>0</CardTypeCode>\n" +
//                    "<CardNo>18406407</CardNo>\n" +
//                    "<PassWord></PassWord>\n" +
//                    "<PatientID>151727374</PatientID>\n" +
//                    "<AdmRowId>113555218</AdmRowId><ChuFangId>('17060753','137656604','137656605','137656606','137633095')</ChuFangId>\n" +
//                    "<BankPayResponse>\n" +
//                    "<BankCardNo>987654321</BankCardNo>\n" +
//                    "<Amt>142.25</Amt>\n" +
//                    "<SystemReferNo>654231</SystemReferNo>\n" +
//                    "<TradeTime>20171101</TradeTime>\n" +
//                    "<PosRequestFlowNo>6541235</PosRequestFlowNo>\n" +
//                    "<PosID>1123456</PosID>\n" +
//                    "</BankPayResponse>\n" +
//                    "<InsurePayStr></InsurePayStr>\n" +
//                    "<ZHIFUFS></ZHIFUFS>\n" +
//                    "<acctno>654321</acctno>\n" +
//                    "<sourceID>987654</sourceID>\n" +
//                    "<refNum>123456</refNum>\n" +
//                    "<Amount>142.25</Amount>\n" +
//                    "<UserId>DBA</UserId>\n" +
//                    "<DeviceID>DBA</DeviceID>\n" +
//                    "</Request>";
			System.out.println(inputXml);
			//2.调用接口

	    	String wsdl="https://222.168.29.22:8082/Service1.asmx?wsdl";
	    	JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
	    	Client client = clientFactory.createClient(wsdl);
	    	Object[] results;
			results = client.invoke("Getyinyi_sp",new Object[] {inputXml});
			//3.保存数据到patient_his_data
			String result = results == null || results.length == 0 ? null : results[0].toString();
			System.out.println(result);
			result=xmlSerializer.read(result).toString();
			JSONObject resultJ=JSONObject.fromObject(result);
			if(resultJ!=null&&"0".equals(resultJ.getString("ResultCode"))){
				System.out.println(resultJ.toString());
				//return resultJ.getString("PatientID");
			}else{
				//return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
