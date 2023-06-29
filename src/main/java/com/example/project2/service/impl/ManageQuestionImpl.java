package com.example.project2.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.project2.PO.QuestionMessage;
import com.example.project2.mapper.QuestionMapper;
import com.example.project2.service.ManageQuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Slf4j
public class ManageQuestionImpl implements ManageQuestionService {

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

}
