package com.quandl.api.deprecated;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Class which enables temporary redirection of System.out and System.err
 * to readable streams via an AutoCloseable.  For use testing code that has
 * to write to stdout/stderr.  Better would be for such code to allow
 * the caller to suppress such output, but this class can be used
 * when that's not possible.
 */
public class TerminalOutRedirector implements AutoCloseable {
    private final PrintStream origOut;
    private final PrintStream origErr;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    
    public TerminalOutRedirector() {
        origOut = System.out;
        origErr = System.err;
        
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }
    
    public String getCapturedOut() {
        return out.toString();
    }

    public String getCapturedErr() {
        return err.toString();
    }

    @Override
    public void close() throws Exception {
        Exception raise = null;
        
        try {
            System.setOut(origOut);
        } catch (Exception e) {
            raise = e;
        }
        try {
            System.setErr(origErr);
        } catch (Exception e) {
            if(raise != null) {
                e.addSuppressed(raise);
            }
            raise = e;
        }
        
        try {
            out.close();
            err.close();
        } catch (Exception e) {
            if(raise != null) {
                e.addSuppressed(raise);
            }
            raise = e;
        }
        
        if(raise != null) {
            throw raise;
        }
    }
}
