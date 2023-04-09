package cn.edu.sustech.cs209.chatting.common;

public enum ResponseStatus {
    /**
     * 请求处理成功
     */
    OK,
    /**
     * 服务器内部出错
     */
    SERVER_ERROR,
    /**
     * 请求的资源未找到
     */
    NOT_FOUND,
    /**
     * 错误的请求对象
     */
    BAD_REQUEST
}
