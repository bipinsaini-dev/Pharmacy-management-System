<c:set var="pageTitle" value="Reports" />
<c:set var="activeNav" value="reports" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3 no-print"><div class="card-body">
  <form method="get" action="${ctx}/admin/reports" class="row g-2 align-items-end">
    <div class="col-12 col-md-4">
      <label class="form-label small mb-1">Report</label>
      <select class="form-select" name="type">
        <option value="daily" ${type eq 'daily' ? 'selected' : ''}>Daily sales</option>
        <option value="monthly" ${type eq 'monthly' ? 'selected' : ''}>Monthly sales</option>
        <option value="yearly" ${type eq 'yearly' ? 'selected' : ''}>Yearly sales</option>
        <option value="top" ${type eq 'top' ? 'selected' : ''}>Top selling medicines</option>
        <option value="low" ${type eq 'low' ? 'selected' : ''}>Low stock</option>
        <option value="expired" ${type eq 'expired' ? 'selected' : ''}>Expired medicines</option>
        <option value="near" ${type eq 'near' ? 'selected' : ''}>Near expiry medicines</option>
        <option value="purchase" ${type eq 'purchase' ? 'selected' : ''}>Purchase report</option>
      </select>
    </div>
    <div class="col-6 col-md-2"><label class="form-label small mb-1">From</label><input type="date" class="form-control" name="from" value="${from}"></div>
    <div class="col-6 col-md-2"><label class="form-label small mb-1">To</label><input type="date" class="form-control" name="to" value="${to}"></div>
    <div class="col-12 col-md-4 d-flex flex-wrap gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-bar-chart me-1"></i>Generate</button>
      <a class="btn btn-outline-success" href="${ctx}/admin/reports?type=${type}&from=${from}&to=${to}&export=csv"><i class="bi bi-filetype-csv me-1"></i>CSV</a>
      <button class="btn btn-outline-secondary" type="button" onclick="window.print()"><i class="bi bi-printer me-1"></i>Print</button>
    </div>
    <div class="col-12 form-text">The date range applies to sales, top-selling and purchase reports. Stock and expiry reports show the current position.</div>
  </form>
</div></div>

<div class="card print-area">
  <div class="card-header d-flex justify-content-between flex-wrap">
    <span><c:out value="${report.title}" /></span>
    <span class="text-muted small">Generated <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd MMM yyyy, hh:mm a" /></span>
  </div>
  <div class="table-responsive">
    <table class="table table-striped table-hover align-middle mb-0">
      <thead><tr><c:forEach var="h" items="${report.headers}"><th><c:out value="${h}" /></th></c:forEach></tr></thead>
      <tbody>
        <c:forEach var="row" items="${report.rows}">
          <tr><c:forEach var="cell" items="${row}"><td><c:out value="${cell}" /></td></c:forEach></tr>
        </c:forEach>
        <c:if test="${empty report.rows}"><tr><td colspan="${fn:length(report.headers)}" class="text-center text-muted py-4">No data for the selected report.</td></tr></c:if>
      </tbody>
    </table>
  </div>
  <c:if test="${not empty report.summary}"><div class="card-footer fw-semibold"><c:out value="${report.summary}" /></div></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
