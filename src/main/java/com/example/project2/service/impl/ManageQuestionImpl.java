package com.example.project2.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.project2.PO.QuestionMessage;
import com.example.project2.mapper.QuestionMapper;
import com.example.project2.service.AsyncServer;
import com.example.project2.service.ManageQuestionService;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class ManageQuestionImpl implements ManageQuestionService {

    @Autowired
    private AsyncServer asyncServer;
    @Autowired
    private QuestionMapper questionMapper;

    private static final QuestionMessage questionMessage = new QuestionMessage();

    @Override
    public boolean insertQuestionMessage(JSONObject jsonObject) {
        String questionName = jsonObject.getString("questionName");
        boolean nullOrEmpty = checkStringNull(questionName);
        if (nullOrEmpty) {
            log.info("输入问题为空，请重新输入");
            return false;
        }
        boolean checkflag = checkRepeatQuestion(questionName);
        if (checkflag) {
            log.info("该问题已存在");
            return false;
        }
        String questionType = jsonObject.getString("questionType");
        String questionAnswer = jsonObject.getString("questionAnswer");
        String questionLink = jsonObject.getString("questionLink");
        questionMessage.setQuestionName(questionName);
        questionMessage.setQuestionType(questionType);
        questionMessage.setQuestionAnswer(questionAnswer);
        questionMessage.setQuestionLink(questionLink);

        int insertResult = questionMapper.insertQuestionMessage(questionMessage);
        Integer insertResult2 = insertResult;
        Integer compare = 1;
        if (insertResult2.compareTo(compare) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<QuestionMessage> queryQuestionMessage() {
        List<QuestionMessage> questionMessages = questionMapper.queryQuestionMessage();
        return questionMessages;
    }

    @Override
    public List<QuestionMessage> queryQuestionMessageByType(String queryQuestionType) {
        List<QuestionMessage> questionMessages = questionMapper.queryQuestionMessageByType(queryQuestionType);
        return questionMessages;
    }


    private boolean checkRepeatQuestion(String questionName) {
        //PageHelper.startPage(1, 5);
        List<QuestionMessage> questionMessages = queryQuestionMessage();
        // PageInfo<QuestionMessage> pageInfo = new PageInfo<>(questionMessages);
        //log.info("问题数据：" + pageInfo);
        for (QuestionMessage question : questionMessages) {
            if (question.getQuestionName().equals(questionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkStringNull(String name) {
        if (null == name) {
            log.info("空指针异常!");
            return true;
        } else if (name.isBlank()) {
            log.info("字符串为空或包含空格！");
            return true;
        } else if (name.isEmpty()) {
            log.info("字符串为空！");
            return true;
        }
        return false;
    }

    /**
     * 基于多线程高效查询所有数据信息
     *
     * @return
     */
    @Override
    public JSONObject asyncQueryQuestionMessages() {
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();

        Integer sumQuestions = questionMapper.queryQuestionCounts();  //查询问题总数
        int pageSize = BigDecimal.valueOf(10).intValue();  //每个sql查询的数量
        int pageNum = (sumQuestions + pageSize - 1) / pageSize;

        long startTime = System.currentTimeMillis(); // 开始时间

        CountDownLatch countDownLatch = new CountDownLatch(pageNum);
        for (int i = 0; i < pageNum; i++) {
            int fromIndex = i * pageSize + 1;
            int toIndex = (i + 1) * pageSize < sumQuestions ? (i + 1) * pageSize : sumQuestions;
            asyncServer.asyncQueryAllQuestionMessage(fromIndex, toIndex, countDownLatch);
        }
        try {
            countDownLatch.await();
            long endTime = System.currentTimeMillis(); //结束时间
            log.info("一共耗时: " + (endTime - startTime) + " ms");
            if (null != AsyncServer.allList) {
                for (QuestionMessage quest : AsyncServer.allList) {
                    JSONObject resultQuestion = new JSONObject();
                    String questionName = quest.getQuestionName();
                    String questionAnswer = quest.getQuestionAnswer();
                    String questionType = quest.getQuestionType();
                    int id = quest.getId();
                    if (StringUtils.isNullOrEmpty(questionAnswer)) {
                        questionAnswer = "该问题没有答案";
                    }
                    resultQuestion.put("resultQuestionName", questionName);
                    resultQuestion.put("resultQuestionAnswer", questionAnswer);
                    resultQuestion.put("questionType", questionType);
                    resultQuestion.put("id", id);
                    resultArray.add(resultQuestion);
                }
                AsyncServer.allList.clear();
            }

        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }

        result.put("resultArrary", sortJSONArray(resultArray, "id"));
        return result;
    }

    /**
     * 创建自定义比较器,对 List<Object> 进行排序
     *
     * @param list
     * @param id
     * @return
     */
    public List<QuestionMessage> sortList(List<QuestionMessage> list, String id) {
        // 创建自定义的比较器
        Comparator<QuestionMessage> comparator = new Comparator<QuestionMessage>() {
            @Override
            public int compare(QuestionMessage o1, QuestionMessage o2) {
                return String.valueOf(o1.getId()).compareTo(String.valueOf(o2.getId()));
            }

        };
        // 对 List 进行排序
        Collections.sort(list, comparator);
        return list;

    }

    /**
     * @param jsonArray
     * @param key
     * @return
     * @throws JSONException
     */
    public static JSONArray sortJSONArray(JSONArray jsonArray, String key) throws JSONException {
        List<JSONObject> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getJSONObject(i));
        }

        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject obj1, JSONObject obj2) {
                try {
                    Object value1 = obj1.get(key);
                    Object value2 = obj2.get(key);
                    return ((Comparable) value1).compareTo(value2);
                } catch (JSONException e) {
                    // 处理异常
                    e.printStackTrace();
                }
                return 0;
            }
        });

        JSONArray sortedArray = new JSONArray();
        for (JSONObject jsonObject : list) {
            sortedArray.add(jsonObject);
        }

        return sortedArray;
    }

}
