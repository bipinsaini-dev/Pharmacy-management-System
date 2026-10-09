package com.pharmacare.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class InvoiceUtil {
    private InvoiceUtil() { }

    /**
     * Next invoice number in the form INV-yyyyMMdd-0001. Must be called inside the billing
     * transaction; the last invoice row of the day is locked so concurrent bills do not clash.
     */
    public static String nextInvoiceNumber(Connection conn) throws SQLException {
        String prefix = "INV-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        String sql = "SELECT invoice_number FROM sales WHERE invoice_number LIKE ? ORDER BY invoice_number DESC LIMIT 1 FOR UPDATE";
        int next = 1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String last = rs.getString(1);
                    next = Integer.parseInt(last.substring(prefix.length())) + 1;
                }
            }
        }
        return prefix + String.format("%04d", next);
    }
}
