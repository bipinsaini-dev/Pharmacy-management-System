<c:set var="pageTitle" value="Pharmacist Dashboard" />
<c:set var="activeNav" value="dashboard" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="card quick-start mb-3">
  <div class="card-body d-flex flex-wrap align-items-center gap-3">
    <div>
      <h2 class="h5 mb-1">Welcome, <c:out value="${sessionScope.user.fullName}" />!</h2>
      <div class="text-muted">Start a new bill or look up a medicine.</div>
    </div>
    <div class="ms-auto d-flex gap-2">
      <a class="btn btn-primary" href="${ctx}/pharmacist/billing"><i class="bi bi-receipt me-1"></i>New bill</a>
      <a class="btn btn-outline-primary" href="${ctx}/pharmacist/medicines"><i class="bi bi-search me-1"></i>Search medicines</a>
    </div>
  </div>
</div>

<div class="row g-3 mb-3">
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/pharmacist/sales">
    <div class="stat-icon bg-green"><i class="bi bi-currency-rupee"></i></div>
    <div><div class="stat-label">My Sales Today</div><div class="stat-value">&#8377;<fmt:formatNumber value="${stats.todaySales}" minFractionDigits="2" maxFractionDigits="2" /></div>
    <small class="text-muted">${stats.todayBills} bill(s)</small></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/pharmacist/sales">
    <div class="stat-icon bg-blue"><i class="bi bi-receipt-cutoff"></i></div>
    <div><div class="stat-label">My Total Sales</div><div class="stat-value">&#8377;<fmt:formatNumber value="${stats.totalSales}" minFractionDigits="2" maxFractionDigits="2" /></div>
    <small class="text-muted">${stats.totalBills} bill(s)</small></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/pharmacist/medicines">
    <div class="stat-icon bg-teal"><i class="bi bi-capsule"></i></div>
    <div><div class="stat-label">Medicines in Catalogue</div><div class="stat-value">${stats.totalMedicines}</div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/pharmacist/customers">
    <div class="stat-icon bg-violet"><i class="bi bi-people"></i></div>
    <div><div class="stat-label">Customers</div><div class="stat-value">${stats.totalCustomers}</div></div></a></div>
  <div class="col-4"><a class="stat-card" href="${ctx}/pharmacist/medicines?status=LOW">
    <div class="stat-icon bg-amber"><i class="bi bi-exclamation-triangle"></i></div>
    <div><div class="stat-label">Low Stock</div><div class="stat-value">${stats.lowStock}</div></div></a></div>
  <div class="col-4"><a class="stat-card" href="${ctx}/pharmacist/medicines?status=EXPIRED">
    <div class="stat-icon bg-red"><i class="bi bi-calendar-x"></i></div>
    <div><div class="stat-label">Expired</div><div class="stat-value">${stats.expired}</div></div></a></div>
  <div class="col-4"><a class="stat-card" href="${ctx}/pharmacist/medicines?status=NEAR">
    <div class="stat-icon bg-orange"><i class="bi bi-hourglass-split"></i></div>
    <div><div class="stat-label">Near Expiry</div><div class="stat-value">${stats.nearExpiry}</div></div></a></div>
</div>

<div class="card">
  <div class="card-header d-flex justify-content-between align-items-center">
    <span>My recent bills</span><a class="small" href="${ctx}/pharmacist/sales">View all</a>
  </div>
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead><tr><th>Invoice</th><th>Customer</th><th>Date</th><th class="text-end">Total</th><th>Payment</th><th></th></tr></thead>
      <tbody>
        <c:forEach var="s" items="${stats.recentBills}">
          <tr>
            <td><c:out value="${s.invoiceNumber}" /></td>
            <td><c:out value="${s.customerName}" /></td>
            <td><fmt:formatDate value="${s.saleDate}" pattern="dd MMM yyyy, hh:mm a" /></td>
            <td class="text-end">&#8377;<fmt:formatNumber value="${s.grandTotal}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td><span class="badge text-bg-light border"><c:out value="${s.paymentMethod}" /></span></td>
            <td class="text-end"><a class="btn btn-sm btn-outline-primary" href="${ctx}/pharmacist/sales?action=invoice&id=${s.id}">Invoice</a></td>
          </tr>
        </c:forEach>
        <c:if test="${empty stats.recentBills}"><tr><td colspan="6" class="text-center text-muted py-4">No bills yet - create your first bill.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
