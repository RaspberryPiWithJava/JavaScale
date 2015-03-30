package com.nighthacking.recipe;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public interface Display {

    void say(String message);

    void say(String message, Object... args);

    void countdown(int seconds) throws InterruptedException;
}
