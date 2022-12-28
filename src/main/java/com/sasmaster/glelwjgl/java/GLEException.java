package com.sasmaster.glelwjgl.java;


public class GLEException extends RuntimeException
{
    private static String VERSION;
    
    public GLEException() {
    }
    
    public GLEException(String message) {
        super(message);
    }
    
    static {
        VERSION = new String("$Revision: 1.1 $");
    }
}
