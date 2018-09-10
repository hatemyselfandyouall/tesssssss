package cn.ucmed.Util;

import cn.ucmed.hlwyy.common.constant.DataBaseConstants;
import cn.ucmed.hlwyy.common.util.HttpClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * Created by YS-GZD-1495 on 2018/7/11.
 */
public class RemoteUtil {

    private final static Logger LOG = Logger.getLogger(RemoteUtil.class);

    /**
     * 获取医生详情
     * @Description
     * @return
     */
    public static String getDoctorDetail(String userId) {
        JSONObject res = null;
        JSONObject parmas = new JSONObject();
        parmas.put("userIds", userId);
        parmas.put("pagesize", DataBaseConstants.PAGE_SIZE_ONE);
        parmas.put("pageno", DataBaseConstants.PAGE_NO_ONE);
        parmas.put("getDetail", "1");
        JSONObject heard = new JSONObject();
        heard.put("appid", "twzxhtaf17d488af0464d60393c7dce7b255");
        String workId="null";
        try {
            LOG.info("[开始调用远程组获取医生详情接口1]-[" + userId + "]");
            res = HttpClient.doGetWithMap("http://userplat.zwjk.com" + "/api/DjDoctor/GetUserDoctorList?" + cancatArgs(parmas), heard);
            LOG.info("[开始调用远程组获取医生详情接口1]-[" + res + "]");
            if (null != res.optJSONObject("ret_data")) {
                JSONArray list=res.optJSONObject("ret_data").optJSONArray("list");
                for (int i=0;i<list.size();i++){
                    JSONObject detail=list.optJSONObject(i);
                    if (detail!=null&&!StringUtils.isEmpty(detail.optString("WorkId"))){
                        return detail.optString("WorkId");
                    }
                    JSONArray doctorArray=detail.optJSONArray("DocList");
                    for (int j=0;j<doctorArray.size();j++) {
                        JSONObject doctorDetail=doctorArray.optJSONObject(j);
                        if (doctorDetail!=null&&!StringUtils.isEmpty(doctorDetail.optString("WorkId"))) {
                            return doctorDetail.optString("WorkId");
                        }
                    }
                }
//               return res.optJSONObject("ret_data");
            }else {
                return "null";
            }
        } catch(Exception e) {
            LOG.error("[获取医生详情失败]",e);
        }
        return workId;
    }

//    /**
//     * 获取医生详情
//     * @Description
//     * @return
//     */
//    public static JSONObject getDoctorDetailByDoctorId(String doctorId) {
//        JSONObject res = null;
//        JSONObject parmas = new JSONObject();
//        parmas.put("doctorId", doctorId);
//        JSONObject heard = new JSONObject();
//        heard.put("appid", "twzxhtaf17d488af0464d60393c7dce7b255");
//        try {
//            LOG.info("[开始调用远程组获取医生详情接口2]-[" + doctorId + "]");
//            res = HttpClient.doGetWithMap("http://userplat.zwjk.com"  + "/api/DjDoctor/GetDoctorDetail?" + cancatArgs(parmas), heard);
//            if (null != res.optJSONObject("ret_data")) {
//                res=res.optJSONObject("ret_data");
//            }
//            LOG.info("[开始调用远程组获取医生详情接口2]-[" + res + "]");
//        } catch(Exception e) {
//            LOG.error("[获取医生详情失败]",e);
//        }
//        return res;
//    }

    /**
     * 拼接请求参数
     */
    private static String cancatArgs(JSONObject args) {
        StringBuilder builder = new StringBuilder();
        @SuppressWarnings("unchecked")
        Set<String> keys = args.keySet();
        for (String key : keys) {
            builder.append(key).append("=");
            builder.append(args.get(key));
            builder.append("&");
        }
        if(builder.toString().endsWith("&")) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(getDoctorDetail("1418041921114011376"));
    }

    /**
     * 生成签名的时间戳
     *
     * @return
     */
    public static String getTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
