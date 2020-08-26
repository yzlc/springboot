package xyz.yzlc.common.model.http;

/**
 * 返回数据
 */
public class Response<T> {
    private int code = HttpStatusEnum.OK.getCode();
    private String msg = HttpStatusEnum.OK.getMsg();
    private T data;

    /**
     * 200
     *
     * @return 200
     */
    public static Response ok() {
        return new Response();
    }

    /**
     * 200
     *
     * @param o 数据
     * @return 200
     */
    public static Response ok(Object o) {
        Response<Object> response = new Response<>();
        response.data = o;
        return response;
    }

    /**
     * 400
     *
     * @param msg 错误信息
     * @return 400
     */
    public static Response badRequest(String msg) {
        Response response = new Response();
        response.code = HttpStatusEnum.BAD_REQUEST.getCode();
        response.msg = msg;
        return response;
    }

    /**
     * 500
     *
     * @return 500
     */
    public static Response internalServerError() {
        Response response = new Response();
        response.code = HttpStatusEnum.INTERNAL_SERVER_ERROR.getCode();
        response.msg = HttpStatusEnum.INTERNAL_SERVER_ERROR.getMsg();
        return response;
    }

    public static void main(String[] args) {
        System.out.println(ok());
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}