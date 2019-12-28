package com.example.schedulemanagement.db;

import android.util.Log;

import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/24
 *     desc   : 任务的增删改查
 * </pre>
 */

public class TaskDao extends BaseDao {
    private static final String TAG = "TaskDao";

    //根据sql语句查询task表
    private List<Task> search(String sql, Object... params) {
        List<Task> res = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("taskId"));
                task.setcId(rs.getInt("cId"));
                task.setpId(rs.getInt("pId"));
                task.setTimeId(rs.getInt("timeId"));
                task.setStartTime(rs.getString("startTime"));
                task.setUserId(rs.getInt("userId"));
                task.setTitle(rs.getString("title"));
                task.setContent(rs.getString("content"));
                task.setDate(rs.getString("date"));
                task.setPriority(rs.getInt("priority"));
                task.setState(rs.getBoolean("state"));
                res.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return res;
    }

    //查询已经完成的任务表
    private List<Task> findDoneTasks(String sql, Object... params) {
        return search(sql, params);
    }

    //查询未完成的任务表
    private List<Task> findUnDoneTasks(String sql, Object... params) {
        return search(sql, params);
    }

    //查询全部任务
    public Event queryAllTasks() {
        //已完成按日期，开始时间倒序排序
        String queryDoneSql = "select * from [task_3117004905_袁健策] where [state] = 1 and [userId]=? order by date desc,startTime desc";
        //未完成按日期，开始时间正序排序
        String queryUndoneSql = "select * from [task_3117004905_袁健策] where [state] = 0 and [userId]=? order by date,startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql, User.getInstance().getUserId()));
        event.setUndone(findUnDoneTasks(queryUndoneSql, User.getInstance().getUserId()));
        return event;
    }


    //根据日期查找任务
    public Event queryTaskByDate(String date) {
        //已完成按开始时间倒序排序
        String queryDoneSql = "select * from [task_3117004905_袁健策] where [state] = 1 and [date] =? and [userId]=? order by startTime desc";
        //未完成按开始时间正序排序
        String queryUndoneSql = "select * from [task_3117004905_袁健策] where [state] = 0 and [date]=? and [userId]=? order by startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql, date, User.getInstance().getUserId()));
        event.setUndone(findUnDoneTasks(queryUndoneSql, date, User.getInstance().getUserId()));
        return event;
    }

    //根据任务号查找任务
    public Task queryTaskById(int taskId) {
        String sql = "select * from [task_3117004905_袁健策] where [taskId]=?";
        if (search(sql, taskId).size() == 0) return null;
        return search(sql, taskId).get(0);
    }

    //根据分类号查找任务
    public Event queryTaskByCategoryId(int cId) {
        //已完成按开始时间倒序排序
        String queryDoneSql = "select * from [task_3117004905_袁健策] where [state] = 1 and [cId] =? and [userId]=? order by date desc,startTime desc";
        //未完成按开始时间正序排序
        String queryUndoneSql = "select * from [task_3117004905_袁健策] where [state] = 0 and [cId]=? and [userId]=? order by date,startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql, cId, User.getInstance().getUserId()));
        event.setUndone(findUnDoneTasks(queryUndoneSql, cId, User.getInstance().getUserId()));
        return event;
    }

    //根据标签号查找任务
    public Event queryTaskByTagId(int tagId) {
        //已完成按开始时间倒序排序
        String queryDoneSql =
                " select * " +
                        " from task_3117004905_袁健策" +
                        " where [state] = 1 and [taskId] in " +
                        " (select [taskId] from " +
                        " task_select_tag_3117004905_袁健策 " +
                        " where  [tagId]=? and [userId]=?) " +
                        " order by date desc,startTime desc ";
        //未完成按开始时间正序排序,子查询
        String queryUndoneSql =
                " select * " +
                        " from task_3117004905_袁健策" +
                        " where [state] = 0 and [taskId] in " +
                        " (select [taskId] from " +
                        " task_select_tag_3117004905_袁健策 " +
                        " where  [tagId]=? and [userId]=?) " +
                        " order by date,startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql, tagId, User.getInstance().getUserId()));
        event.setUndone(findUnDoneTasks(queryUndoneSql, tagId, User.getInstance().getUserId()));
        return event;
    }


    //添加
    public void insert(Task task, List<Integer> tagIds) {
        String sql = "insert into task_3117004905_袁健策(userId,cId,title,content,date,startTime,priority,state) values(?,?,?,?,?,?,?,?)";
        //插入任务记录并返回任务号
        int taskId = executeUpdateNeedReturnPK(sql, new Object[]{
                        User.getInstance().getUserId(),
                        task.getcId(),
                        task.getTitle(),
                        task.getContent(),
                        task.getDate(),
                        task.getStartTime(),
                        task.getPriority(),
                        task.isState(),
                }
        );
        //插入到任务-标签表
        insertTaskSTag(taskId, tagIds);
    }

    //删除
    public int delete(int taskId) {
        String sql = "delete from [task_3117004905_袁健策] where [taskId] = ?";
        return executeUpdate(sql, new Object[]{taskId});
    }

    //删除任务-标签表的数据
    public void deleteTaskSTag(int taskId, List<Integer> tagIds) {
        for (int tagId : tagIds) {
            String sql = "delete from [task_select_tag_3117004905_袁健策] where [taskId] = ? and [tagId] =?";
            executeUpdate(sql, new Object[]{taskId, tagId});
        }
    }

    //添加任务-标签表的数据
    private void insertTaskSTag(int taskId, List<Integer> tagIds) {
        for (int tagId : tagIds) {
            String tstSql = "insert into task_select_tag_3117004905_袁健策(taskId,tagId,userId) values(?,?,?)";
            executeUpdate(tstSql, new Object[]{taskId, tagId, User.getInstance().getUserId()});
        }
    }

    //更新task表
    public int update(Task task) {
        String sql = "update task_3117004905_袁健策 set [cId]=?,[title]=?,[content]=?,[date]=?,[startTime]=?,[priority]=?,[state]=? where [taskId] = ?";
        return executeUpdate(sql, new Object[]{
                        task.getcId(),
                        task.getTitle(),
                        task.getContent(),
                        task.getDate(),
                        task.getStartTime(),
                        task.getPriority(),
                        task.isState(),
                        task.getTaskId(),
                }
        );
    }

    //详细页的修改
    public void updateTaskAndTag(Task task, List<Integer> oldTagIds, List<Integer> newTagIds) {
        //1.更新task表
        update(task);
        //2.更新task_select_task表
        //首先删除旧标签的数据
        if (oldTagIds != null) deleteTaskSTag(task.getTaskId(), oldTagIds);
        //添加新标签的数据
        if (newTagIds !=null&&newTagIds.size() != 0) insertTaskSTag(task.getTaskId(), newTagIds);
    }

}
