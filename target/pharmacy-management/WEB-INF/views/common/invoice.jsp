<c:set var="pageTitle" value="Invoice ${sale.invoiceNumber}" />
<c:set var="activeNav" value="sales" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="d-flex gap-2 mb-3 no-print">
  <button class="btn btn-primary" type="button" onclick="window.print()"><i class="bi bi-printer me-1"></i>Print invoice</button>
  <a class="btn btn-outline-secondary" href="${ctx}/${base}/billing"><i class="bi bi-receipt me-1"></i>New bill</a>
  <a class="btn btn-light" href="${ctx}/${base}/sales">Back to ${base eq 'admin' ? 'sales' : 'my bills'}</a>
</div>

<div class="invoice print-area">
  <div class="d-flex justify-content-between flex-wrap gap-3 border-bottom pb-3 mb-3">
    <div>
      <div class="invoice-brand"><i class="bi bi-capsule-pill"></i> PharmaCare</div>
      <div>12 MG Road, Camp, Pune, Maharashtra 411001</div>
      <div>Phone: +91 20 1234 5678</div>
      <div>GSTIN: 27XXXXX0000X1Z5 <small class="text-muted">(placeholder)</small></div>
    </div>
    <div class="text-sm-end">
      <h2 class="h4 mb-1">TAX INVOICE</h2>
      <div><strong>Invoice No:</strong> <c:out value="${sale.invoiceNumber}" /></div>
      <div><strong>Date:</strong> <fmt:formatDate value="${sale.saleDate}" pattern="dd MMM yyyy, hh:mm a" /></div>
    </div>
  </div>

  <div class="row mb-3">
    <div class="col-sm-6">
      <div class="text-muted small text-uppercase">Billed to</div>
      <div class="fw-semibold"><c:out value="${sale.customerName}" /></div>
      <c:if test="${not empty sale.customerPhone}"><div>Phone: <c:out value="${sale.customerPhone}" /></div></c:if>
      <c:if test="${not empty sale.customerAddress}"><div><c:out value="${sale.customerAddress}" /></div></c:if>
    </div>
    <div class="col-sm-6 text-sm-end">
      <div class="text-muted small text-uppercase">Served by</div>
      <div class="fw-semibold"><c:out value="${sale.pharmacistName}" /></div>
      <div>Payment: <strong><c:out value="${sale.paymentMethod}" /></strong></div>
    </div>
  </div>

  <table class="table table-bordered align-middle">
    <thead class="table-light"><tr><th>#</th><th>Medicine</th><th>Batch</th><th class="text-end">Qty</th><th class="text-end">Price</th><th class="text-end">Total</th></tr></thead>
    <tbody>
      <c:forEach var="i" items="${sale.items}" varStatus="st">
        <tr>
          <td>${st.count}</td>
          <td><c:out value="${i.medicineName}" /></td>
          <td><c:out value="${i.batchNumber}" /></td>
          <td class="text-end">${i.quantity}</td>
          <td class="text-end"><fmt:formatNumber value="${i.price}" minFractionDigits="2" maxFractionDigits="2" /></td>
          <td class="text-end"><fmt:formatNumber value="${i.total}" minFractionDigits="2" maxFractionDigits="2" /></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <div class="row justify-content-end">
    <div class="col-sm-6 col-lg-5">
      <table class="table table-sm mb-0">
        <tr><td>Subtotal</td><td class="text-end">&#8377;<fmt:formatNumber value="${sale.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></td></tr>
        <tr><td>Discount</td><td class="text-end">- &#8377;<fmt:formatNumber value="${sale.discount}" minFractionDigits="2" maxFractionDigits="2" /></td></tr>
        <tr><td>GST</td><td class="text-end">&#8377;<fmt:formatNumber value="${sale.gst}" minFractionDigits="2" maxFractionDigits="2" /></td></tr>
        <tr class="fw-bold fs-5"><td>Grand total</td><td class="text-end">&#8377;<fmt:formatNumber value="${sale.grandTotal}" minFractionDigits="2" maxFractionDigits="2" /></td></tr>
      </table>
    </div>
  </div>
  <p class="text-center text-muted small mt-4 mb-0">Thank you for choosing PharmaCare. Get well soon! &middot; Medicines once sold will not be taken back.</p>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
