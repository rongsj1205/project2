package com.example.project2.PO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionMessage {
    private int id;
    private String questionName;
    private String questionType;
    private String questionAnswer;
    private String questionLink;

}
