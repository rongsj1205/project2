package com.example.project2.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.project2.PO.QuestionMessage;
import com.example.project2.service.ManageQuestionService;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Autowired
    private ManageQuestionService manageQuestion;

    @RequestMapping("/record")
    @ResponseBody
    @CrossOrigin
    public JSONObject record(@RequestBody JSONObject jsonObject) {
        boolean resultFlag = manageQuestion.insertQuestionMessage(jsonObject);
        if (resultFlag) {
            jsonObject.put("resultMessage", "答案提交成功");
            return jsonObject;
        }
        jsonObject.put("resultMessage", "答案提交失败");
        return jsonObject;
    }

    @RequestMapping("/query")
    @ResponseBody
    @CrossOrigin
    public JSONObject query(@RequestBody JSONObject jsonObject) {
        String inputQuestion = jsonObject.getString("inputQuestion");
        String inputAnswerType = jsonObject.getString("inputAnswerType");
        jsonObject.clear();

        jsonObject.put("resultType", "1");
        JSONArray resultArray = new JSONArray();
        List<QuestionMessage> questionMessages = null;
        if (!StringUtils.isNullOrEmpty(inputAnswerType)) {
            questionMessages = manageQuestion.queryQuestionMessageByType(inputAnswerType);
            for (QuestionMessage quest : questionMessages) {
                JSONObject resultQuestion = new JSONObject();
                String questionName = quest.getQuestionName();
                String questionAnswer = quest.getQuestionAnswer();
                String questionType = quest.getQuestionType();
                if (StringUtils.isNullOrEmpty(questionAnswer)) {
                    questionAnswer = "该问题没有答案";
                }
                resultQuestion.put("resultQuestionName", questionName);
                resultQuestion.put("resultQuestionAnswer", questionAnswer);
                resultQuestion.put("questionType", questionType);
                resultArray.add(resultQuestion);

            }
        } else {
            questionMessages = manageQuestion.queryQuestionMessage();
            if (questionMessages.size() == 0) {
                jsonObject.put("resultType", "0");
                jsonObject.put("resultMessage", "查询结果为空");
                return jsonObject;
            }
            for (QuestionMessage quest : questionMessages) {
                JSONObject resultQuestion = new JSONObject();
                String questionName = quest.getQuestionName();
                String questionAnswer = quest.getQuestionAnswer();
                String questionType = quest.getQuestionType();
                if (StringUtils.isNullOrEmpty(inputQuestion)) {
                    if (StringUtils.isNullOrEmpty(questionAnswer)) {
                        questionAnswer = "该问题没有答案";
                    }
                    resultQuestion.put("resultQuestionName", questionName);
                    resultQuestion.put("resultQuestionAnswer", questionAnswer);
                    resultQuestion.put("questionType", questionType);
                    resultArray.add(resultQuestion);
                } else if (questionName.contains(inputQuestion)) {
                    resultQuestion.put("resultQuestionName", questionName);
                    resultQuestion.put("resultQuestionAnswer", questionAnswer);
                    resultQuestion.put("questionType", questionType);
                    resultArray.add(resultQuestion);
                    break;
                }
            }
        }
        if (resultArray.size() == 0) {
            jsonObject.put("resultType", "0");
            jsonObject.put("resultMessage", "查询结果为空");
        } else {
            Set<String> set = new HashSet<>();
            for (int i = 0; i < resultArray.size(); i++) {
                String questionType = resultArray.getJSONObject(i).getString("questionType");
                set.add(questionType);
            }
            List questionTypes = new ArrayList();
            for (String element : set) {
                questionTypes.add(element);
            }
            jsonObject.put("questionTypes", questionTypes);
        }

        jsonObject.put("resultQuestions", resultArray);
        return jsonObject;
    }
}
