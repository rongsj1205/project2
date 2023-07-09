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
import org.springframework.util.CollectionUtils;


import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

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
     * 基于多线程高效查询所有数据信息，会出现数据丢失的高效查询方法
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

    /**
     *不会出现数据丢失的高效查询方法
     * @param pageSize
     * @return
     */
    public List<QuestionMessage> getQuestionMessages(int pageSize) {
        List<QuestionMessage> resultList = new ArrayList<>();

        try {
            //pageSize每个sql查询的数量
            Integer sumQuestions = questionMapper.queryQuestionCounts();  //查询问题总数
            int pageCount = (sumQuestions + pageSize - 1) / pageSize;
            long startTime = System.currentTimeMillis(); // 开始时间

            /**
             * BlockingQueue 是 Java 并发编程中的一个接口，位于 java.util.concurrent 包中。它的作用是提供一种线程安全的队列数据结构，用于在生产者和消费者之间进行数据交换。
             * BlockingQueue 的主要作用如下：
             * 数据交换：BlockingQueue 提供了一个可靠的、线程安全的机制来传递数据，允许生产者将数据放入队列，同时消费者可以从队列中取出数据进行处理。这样可以实现多线程间的数据交换和协作。
             * 线程同步：BlockingQueue 内部实现了同步机制，包括互斥锁、条件等待和通知机制。它确保了在有需要时，生产者和消费者可以正确地进行阻塞、等待和唤醒操作，以实现线程之间的同步和协作。
             * 容量限制：BlockingQueue 通常具有固定的容量，可以控制队列的长度，防止队列无限增长。当队列已满时，生产者将被阻塞，直到队列中有空间可用。当队列为空时，消费者将被阻塞，直到队列中有数据可用。
             * 延迟和超时：BlockingQueue 中的一些方法提供了延迟和超时特性。例如，可以使用 offer(element, timeout, unit) 方法去尝试在指定的时间内将元素放入队列，如果超时仍无法放入，则返回 false。
             * 通过以上特性，BlockingQueue 可以在并发环境中以一种可靠的、线程安全的方式进行数据交换和线程同步。它在各种多线程场景中非常有用，例如线程池的任务队列、生产者消费者模式等。常见的实现类包括 ArrayBlockingQueue、LinkedBlockingQueue、PriorityBlockingQueue 等
             */
            BlockingQueue<Future<List<QuestionMessage>>> queue = new LinkedBlockingQueue();

            for (int i = 0; i < pageCount; i++) {
                /**
                 * 对于 Thread.sleep(0) 这句代码的理解，需要从两个方面进行解释。
                 * 延迟执行： 当线程执行到 Thread.sleep(0) 时，它实际上就是请求让出当前线程的时间片给其他具有相同优先级的线程执行。这种情况下，当前线程会进行短暂的休眠，然后立即重新调度。对于类似于此代码的情况，通常是为了让其他具有相同或更高优先级的线程有机会执行。
                 * 请注意，这不同于传入一个正整数参数的 Thread.sleep() 调用，后者会使线程休眠指定的毫秒数。使用 Thread.sleep(0) 实际上是对线程调度器进行了一种暗示，告诉它可以选择立即重新调度当前线程或者继续执行其他线程。
                 * 线程切换： 当一个线程执行 Thread.sleep(0) 时，它会触发线程的上下文切换。线程的上下文切换是指操作系统在多线程环境下，将当前线程的执行上下文（例如寄存器状态和执行位置）保存起来，并切换到另一个线程执行。然后，在适当的时机，操作系统会恢复之前保存的线程的执行上下文，使其继续执行。
                 * 这种线程切换的作用是为了实现操作系统的多线程调度，以便公平地分配处理器资源给各个线程，并避免某个线程长时间独占处理器。
                 * 总的来说，Thread.sleep(0) 是对线程调度器的一种提示，帮助线程让出时间片给其他线程执行，并触发线程的上下文切换，以实现多线程的公平调度。
                 */
                Thread.sleep(0);

                /**
                 * Future 类是 Java 并发编程中的一个接口，它的作用是表示一个可能还没有完成的异步任务的结果。
                 * Future 类的主要作用如下：
                 * 提供异步计算的结果：Future 对象可以用于获取异步任务的计算结果。当提交一个任务给线程池或其他执行器时，可以通过 submit() 或 execute() 方法返回一个 Future 对象，这个对象可以用于后续获取执行结果。通过 Future 对象的 get() 方法可以获得任务的计算结果，如果任务还未完成，get() 方法会阻塞当前线程，直到任务执行完成并返回结果。
                 * 控制任务的执行：Future 对象提供了一些方法来控制任务的执行，例如 cancel() 方法可以用于取消任务的执行，isDone() 方法可以判断任务是否已经完成，isCancelled() 方法可以判断任务是否已经被取消。这些方法可以对任务的执行进行更精细的操作和监控。
                 * 处理任务的异常：Future 对象还提供了一些方法来处理任务执行过程中的异常。通过 get() 方法获取任务执行结果时，如果任务发生异常，将会抛出相应的异常。可以通过 isCompletedExceptionally() 方法来判断任务是否以异常的方式执行完成，并通过 get() 方法的重载版本来获取异常信息。
                 * 通过使用 Future 类，可以更好地管理和监控异步任务的执行结果，使得在并发编程中可以更方便地处理异步操作的返回值和异常情况。
                 */
                Future<List<QuestionMessage>> future;
                int fromIndex = i * pageSize + 1;
                int toIndex = (i + 1) * pageSize < sumQuestions ? (i + 1) * pageSize : sumQuestions;
                future = asyncServer.getAttendCountSubList(fromIndex, toIndex);
                queue.add(future);
            }

            int queueSize = queue.size();
            log.debug("queue size:" + queueSize);
            for (int i = 0; i < queueSize; i++) {
                List<QuestionMessage> subAttendList = queue.take().get();
                if (!CollectionUtils.isEmpty(subAttendList)) {
                    resultList.addAll(subAttendList);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;

    }


}
