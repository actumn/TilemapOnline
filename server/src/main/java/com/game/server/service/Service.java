package com.game.server.service;

import org.json.simple.JSONObject;

/**
 * Created by Lee on 2016-06-24.
 */
public interface Service {
    void start() throws Exception;
    void addPacket(JSONObject packet);

    void sendPacket(Service service, JSONObject object);
    JSONObject pollPacket();
}
