package com.isoft.framework.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBTools {
    
    public static void close(Connection c, Statement s, ResultSet r) {
        try {
            if (r != null) r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (s != null) s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (c != null && !c.isClosed()) {
                if (c.isReadOnly()) {
                    c.setReadOnly(false);
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //
            // This block is required since the isReadOnly and setReadOnly methods
            // can throw exceptions
            //
            try {
                c.close();
            } catch (Exception ex) {
                // Here we don't log anything since a lot of time this method is
                // called twince with the same connection and in this case (that is
                // not an error case) the exception will be logged. We don't want
                // these stacktrace.
            }
        }
    } 
}