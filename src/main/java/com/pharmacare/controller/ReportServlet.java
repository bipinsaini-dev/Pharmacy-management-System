package com.pharmacare.controller;

import com.pharmacare.service.ReportService;
import com.pharmacare.service.ReportService.ReportTable;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/** Admin reports with date filtering, print (browser) and CSV export. */
@WebServlet("/admin/reports")
public class ReportServlet extends BaseServlet {
    private final ReportService service = new ReportService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String type = param(req, "type");
        if (!ReportService.TYPES.contains(type)) type = "daily";

        Date from = ValidationUtil.toDate(req.getParameter("from"));
        Date to = ValidationUtil.toDate(req.getParameter("to"));
        if (from == null) from = Date.valueOf(service.defaultFrom(type));
        if (to == null) to = Date.valueOf(LocalDate.now());
        if (from.after(to)) {
            Date tmp = from;
            from = to;
            to = tmp;
        }

        ReportTable table = service.build(type, from, to);
        if ("csv".equals(param(req, "export"))) {
            writeCsv(resp, type, table);
            return;
        }
        req.setAttribute("type", type);
        req.setAttribute("from", from.toString());
        req.setAttribute("to", to.toString());
        req.setAttribute("report", table);
        render(req, resp, "admin/reports");
    }

    private void writeCsv(HttpServletResponse resp, String type, ReportTable table) throws Exception {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"report-" + type + "-" + LocalDate.now() + ".csv\"");
        PrintWriter w = resp.getWriter();
        w.write('\uFEFF');                                   // BOM so Excel reads UTF-8 correctly
        w.write(csvLine(table.getHeaders()));
        for (List<String> row : table.getRows()) w.write(csvLine(row));
        w.flush();
    }

    private String csvLine(List<String> cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(csvCell(cells.get(i)));
        }
        return sb.append("\r\n").toString();
    }

    private String csvCell(String v) {
        String s = v == null ? "" : v;
        // neutralise spreadsheet formula injection
        if (!s.isEmpty() && "=+@".indexOf(s.charAt(0)) >= 0) s = "'" + s;
        if (s.startsWith("-") && ValidationUtil.toDecimal(s) == null) s = "'" + s;
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
