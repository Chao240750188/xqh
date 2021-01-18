package com.essence.handler;

/**
 * @ClassName Msg
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/11/28 17:32
 * @Version 1.0
 **/
public enum Msg {

    /**
     * success
     */
    Success(200, "success"),
    /**
     * error
     */
    Error(500, "System internal error"),
    NotAcceptable(406, "Not Acceptable"),
    /**
     * database name not match
     */
    DBNameNotMatch(600, "database name not match"),
    /**
     * no data match
     */
    NoDataMatch(601, "no data match"),
    /**
     * sql error
     */
    SqlError(602, "sql error"),
    /**
     * insert fail
     */
    InsertFail(610, "insert fail"),
    /**
     * insert fail
     */
    DuplicateKeyError(611, "This record already exists."),
    /**
     * update fail
     */
    UpdateFail(620, "update fail"),
    /**
     * query fail
     */
    QueryFail(630, "query fail"),
    /**
     * delete fail
     */
    DeleteFail(640, "delete fail"),
    /**
     * [server] runtime exception
     */
    RuntimeException(700, "[server] runtime exception"),
    /**
     * [server] null pointer exception
     */
    NullPointerException(701, "[server] null pointer exception"),
    /**
     * [server] class cast exception
     */
    ClassCastException(702, "[server] class cast exception"),
    /**
     * [server] io exception
     */
    IoException(703, "[server] io exception"),
    /**
     * [server] no such method exception
     */
    NoSuchMethodException(704, "[server] no such method exception"),
    /**
     * [server] index out of bounds exception
     */
    IndexOutOfBoundsException(705, "[server] index out of bounds exception"),
    /**
     * [server] request not readable
     */
    RequestNotReadable(706, "[server] request not readable"),
    MethodNotAllowed(404, "[server] request not exists"),
    /**
     * parameter error
     */
    ParameterError(800, "parameter error");

    private int code;
    private String tip;

    public int getCode() {
        return code;
    }

    public String getTip() {
        return tip;
    }

    Msg(int code, String tip) {
        this.code = code;
        this.tip = tip;
    }
}
