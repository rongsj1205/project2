package com.example.project2.service;

import com.alibaba.fastjson.JSONObject;
import com.example.project2.PO.QuestionMessage;

import java.util.List;

public interface ManageQuestionService {
    boolean insertQuestionMessage(JSONObject jsonObject);
    List<QuestionMessage> queryQuestionMessage();
    List<QuestionMessage> queryQuestionMessageByType(String queryQuestionType);
    JSONObject asyncQueryQuestionMessages();
}
