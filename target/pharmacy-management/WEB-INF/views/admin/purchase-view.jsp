<c:set var="pageTitle" value="Purchase #${purchase.id}" />
<c:set var="activeNav" value="purchases" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card">
  <div class="card-header d-flex justify-content-between"><span>Purchase #${purchase.id}</span><a class="btn btn-sm btn-light" href="${ctx}/admin/purchases">Back</a></div>
  <div class="card-body">
    <dl class="row detail-list">
      <dt class="col-sm-3">Supplier</dt><dd class="col-sm-9"><c:out value="${purchase.supplierName}" /></dd>
      <dt class="col-sm-3">Date</dt><dd class="col-sm-9"><fmt:formatDate value="${purchase.purchaseDate}" pattern="dd MMM yyyy" /></dd>
      <dt class="col-sm-3">Recorded by</dt><dd class="col-sm-9"><c:out value="${purchase.userName}" /></dd>
    </dl>
    <div class="table-responsive">
      <table class="table table-bordered align-middle mb-0">
        <thead class="table-light"><tr><th>Medicine</th><th class="text-end">Quantity</th><th class="text-end">Price</th><th class="text-end">Total</th></tr></thead>
        <tbody>
          <c:forEach var="i" items="${purchase.items}">
            <tr><td><c:out value="${i.medicineName}" /></td><td class="text-end">${i.quantity}</td>
              <td class="text-end"><fmt:formatNumber value="${i.purchasePrice}" minFractionDigits="2" maxFractionDigits="2" /></td>
              <td class="text-end"><fmt:formatNumber value="${i.total}" minFractionDigits="2" maxFractionDigits="2" /></td></tr>
          </c:forEach>
        </tbody>
        <tfoot><tr class="fw-bold"><td colspan="3" class="text-end">Total</td><td class="text-end">&#8377;<fmt:formatNumber value="${purchase.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></td></tr></tfoot>
      </table>
    </div>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
