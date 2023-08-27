package com.company.gdx.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.company.gdx.SpaceWarGame;
import com.company.gdx.client.dto.InputStateImpl;
import com.company.gdx.client.ws.EventListenerCallback;
import com.company.gdx.client.ws.WebSocket;
import com.google.gwt.user.client.Timer;


public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                //return new GwtApplicationConfiguration(480, 320);
        }
        private native WebSocket getWebSocket(String url)
                /*-{
                        return new WebSocket(url);
                }-*/
        ;

        private native void log(Object obj)
                /*-{
                        console.log(obj);
                }-*/
        ;

        private native String toJson(Object obj)
                /*-{
                        return JSON.stringify(obj);
                }-*/
        ;
        @Override
        public ApplicationListener createApplicationListener () {

                SpaceWarGame spaceWarGame = new SpaceWarGame(new InputStateImpl());
                MessageProcessor messageProcessor = new MessageProcessor(spaceWarGame);

                WebSocket client = getWebSocket("ws://localhost:8888/ws");

                spaceWarGame.setMessageSender(message -> {
                        client.send(toJson(message));
                });

                Timer timer = new Timer() {
                        @Override
                        public void run() {
                                spaceWarGame.handleTimer();
                        }
                };

                EventListenerCallback callback = event -> {
                        messageProcessor.processEvent(event);
                        log(event.getData());
                };

                client.addEventListener("open", event -> {
                        timer.scheduleRepeating(1000 / 60);
                        messageProcessor.processEvent(event);
                });
                client.addEventListener("close", callback);
                client.addEventListener("error", callback);
                client.addEventListener("message", callback);

                return spaceWarGame;
        }

}