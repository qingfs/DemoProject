package com.example.client;

import com.example.annotation.RpcClient;

@RpcClient
public interface SendMessage {
    public String sendName(String name);
}
