package com.box.common.core.browser.agent;

import java.io.Closeable;


class CloseUtils {


    public static void closeIO(Closeable closeable){
        try {

            if(closeable!=null)
                closeable.close();
        }catch (Exception e){

            e.printStackTrace();
        }

    }
}
