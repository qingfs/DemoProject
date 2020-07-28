package com.example.data;

public class MessageFuture {

    private volatile boolean success = false;
    private Response response;
    private final Object object = new Object();

    public Response getMessage(){
        synchronized (object){
            while (!success){
                try {
                    // 等待结果，有结果时调用notify
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    public void setMessage(Response response){
        synchronized (object){
            this.response = response;
            this.success = true;
            object.notify();
        }
    }
}