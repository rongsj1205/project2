package com.example.project2.mapper;


import com.example.project2.PO.QuestionMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper {
    int insertQuestionMessage(QuestionMessage questionMessage);
    List<QuestionMessage> queryQuestionMessage();
    List<QuestionMessage> queryQuestionMessageByType(String queryQuestionType);
    Integer queryQuestionCounts();
    List<QuestionMessage> queryQuestionMessageById(@Param("fromId") int fromId, @Param("toId") int toId);


}
