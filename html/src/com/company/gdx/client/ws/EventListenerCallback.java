package com.company.gdx.client.ws;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface EventListenerCallback {
    void callEvent(WsEvent event);
}
