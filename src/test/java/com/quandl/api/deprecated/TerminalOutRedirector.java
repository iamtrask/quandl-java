package com.quandl.api.deprecated;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.google.common.base.Throwables;

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
    public void close() {
        RuntimeException raise = null;
        
        try {
            System.setOut(origOut);
        } catch (RuntimeException e) {
            raise = e;
        }
        try {
            System.setErr(origErr);
        } catch (RuntimeException e) {
            if(raise != null) {
                e.addSuppressed(raise);
            }
            raise = e;
        }
        
        try {
            out.close();
            err.close();
        } catch (IOException | RuntimeException e) {
            if(raise != null) {
                e.addSuppressed(raise);
            }
            Throwables.propagate(e);
        }
        
        if(raise != null) {
            throw raise;
        }
    }
}
