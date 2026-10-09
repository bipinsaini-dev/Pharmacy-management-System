<c:set var="pageTitle" value="${base eq 'admin' ? 'Sales' : 'My Bills'}" />
<c:set var="activeNav" value="sales" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3"><div class="card-body">
  <form method="get" action="${ctx}/${base}/sales" class="row g-2 align-items-end">
    <div class="col-12 col-md-4"><label class="form-label small mb-1">Search</label>
      <input type="search" class="form-control" name="q" value="<c:out value='${q}'/>" placeholder="Invoice, customer, pharmacist, payment"></div>
    <div class="col-6 col-md-2"><label class="form-label small mb-1">From</label><input type="date" class="form-control" name="from" value="<c:out value='${from}'/>"></div>
    <div class="col-6 col-md-2"><label class="form-label small mb-1">To</label><input type="date" class="form-control" name="to" value="<c:out value='${to}'/>"></div>
    <div class="col-12 col-md-4 d-flex gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-funnel me-1"></i>Filter</button>
      <a class="btn btn-light" href="${ctx}/${base}/sales">Reset</a>
    </div>
  </form>
</div></div>

<div class="card">
  <div class="card-header d-flex justify-content-between"><span>${fn:length(sales)} invoice(s)</span>
    <span>Total: <strong>&#8377;<fmt:formatNumber value="${listTotal}" minFractionDigits="2" maxFractionDigits="2" /></strong></span></div>
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead><tr><th>Invoice</th><th>Customer</th><th>Pharmacist</th><th>Date</th>
        <th class="text-end">Subtotal</th><th class="text-end">Discount</th><th class="text-end">GST</th><th class="text-end">Grand total</th><th>Payment</th><th class="text-end">Actions</th></tr></thead>
      <tbody>
        <c:forEach var="s" items="${sales}">
          <tr>
            <td class="fw-semibold"><c:out value="${s.invoiceNumber}" /></td>
            <td><c:out value="${s.customerName}" /></td>
            <td><c:out value="${s.pharmacistName}" /></td>
            <td><fmt:formatDate value="${s.saleDate}" pattern="dd MMM yyyy, hh:mm a" /></td>
            <td class="text-end"><fmt:formatNumber value="${s.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td class="text-end"><fmt:formatNumber value="${s.discount}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td class="text-end"><fmt:formatNumber value="${s.gst}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td class="text-end fw-semibold"><fmt:formatNumber value="${s.grandTotal}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td><span class="badge text-bg-light border"><c:out value="${s.paymentMethod}" /></span></td>
            <td class="text-end text-nowrap">
              <a class="btn btn-sm btn-outline-primary" title="View invoice" href="${ctx}/${base}/sales?action=invoice&id=${s.id}"><i class="bi bi-eye"></i></a>
              <a class="btn btn-sm btn-outline-secondary" title="Print" href="${ctx}/${base}/sales?action=invoice&id=${s.id}&print=1"><i class="bi bi-printer"></i></a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty sales}"><tr><td colspan="10" class="text-center text-muted py-4">No invoices found.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
