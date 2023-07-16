package com.example.project2.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.project2.service.AsyncServer;
import com.example.project2.service.ManageQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/async")
@CrossOrigin
/**
 *  @CrossOrigin注解中的maxAge表示跨域请求的预检请求（OPTIONS请求）的缓存时间，单位为秒。当浏览器发送跨域请求时，会先发送一个预检请求（OPTIONS请求）来检查服务器是否允许跨域访问。maxAge参数用于设置预检请求的缓存时间，即在指定的时间内，浏览器不会再发送预检请求，而是直接发送正式的跨域请求。这样可以减少预检请求的发送次数，提高请求的效率。在示例中，maxAge = 3600表示预检请求的缓存时间为3600秒（1小时）。
 */
public class AsyncController {


    @Autowired
    private AsyncServer asyncServer;

    @Autowired
    private ManageQuestionService manageQuestionService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject asynctest() throws Exception {
        JSONObject jsonObject = new JSONObject();
        long startTime = System.currentTimeMillis();
        int counter = 100;
        for (int i = 0; i < counter; i++) {
            asyncServer.asyncTest(i);
        }
        long endTime = System.currentTimeMillis();
        jsonObject.put("msg", "成功");
        jsonObject.put("spendtime", endTime - startTime);
        return jsonObject;
    }


    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject asynctest1() throws Exception {
        JSONObject jsonObject1 = manageQuestionService.asyncQueryQuestionMessages();
        return jsonObject1;
    }


}
