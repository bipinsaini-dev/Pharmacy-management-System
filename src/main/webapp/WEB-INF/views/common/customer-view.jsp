<c:set var="pageTitle" value="Customer Details" />
<c:set var="activeNav" value="customers" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3">
  <div class="card-header d-flex justify-content-between"><span><c:out value="${customer.customerName}" /></span><a class="btn btn-sm btn-light" href="${ctx}/${base}/customers">Back</a></div>
  <div class="card-body">
    <dl class="row detail-list mb-0">
      <dt class="col-sm-3">Phone</dt><dd class="col-sm-9"><c:out value="${customer.phone}" default="-" /></dd>
      <dt class="col-sm-3">Email</dt><dd class="col-sm-9"><c:out value="${customer.email}" default="-" /></dd>
      <dt class="col-sm-3">Address</dt><dd class="col-sm-9"><c:out value="${customer.address}" default="-" /></dd>
      <dt class="col-sm-3">Customer since</dt><dd class="col-sm-9"><fmt:formatDate value="${customer.createdAt}" pattern="dd MMM yyyy" /></dd>
    </dl>
  </div>
</div>
<div class="card">
  <div class="card-header">Purchase history (${fn:length(bills)} bill(s))</div>
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead><tr><th>Invoice</th><th>Date</th><th>Pharmacist</th><th>Payment</th><th class="text-end">Total</th><th></th></tr></thead>
      <tbody>
        <c:forEach var="s" items="${bills}">
          <tr>
            <td><c:out value="${s.invoiceNumber}" /></td>
            <td><fmt:formatDate value="${s.saleDate}" pattern="dd MMM yyyy, hh:mm a" /></td>
            <td><c:out value="${s.pharmacistName}" /></td>
            <td><c:out value="${s.paymentMethod}" /></td>
            <td class="text-end">&#8377;<fmt:formatNumber value="${s.grandTotal}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td class="text-end">
              <c:if test="${base eq 'admin' or s.userId == sessionScope.user.id}">
                <a class="btn btn-sm btn-outline-primary" href="${ctx}/${base}/sales?action=invoice&id=${s.id}">Invoice</a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty bills}"><tr><td colspan="6" class="text-center text-muted py-4">No purchases yet.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
