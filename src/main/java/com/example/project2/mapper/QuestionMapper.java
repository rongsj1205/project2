package com.example.project2.mapper;


import com.example.project2.PO.QuestionMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {
    int insertQuestionMessage(QuestionMessage questionMessage);

    List<QuestionMessage> queryQuestionMessage();
    List<QuestionMessage> queryQuestionType(String queryQuestionType);


}
