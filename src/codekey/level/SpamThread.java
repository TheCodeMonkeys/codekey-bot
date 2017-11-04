package codekey.level;

import codekey.main.Listener;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/23/17.
 */
public class SpamThread extends Thread {

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                Thread.sleep(300000);//5 mins
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            Listener.lastMessage.clear();

        }

    }
}
