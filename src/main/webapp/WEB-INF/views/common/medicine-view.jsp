<c:set var="pageTitle" value="Medicine Details" />
<c:set var="activeNav" value="medicines" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card">
  <div class="card-header d-flex flex-wrap gap-2 justify-content-between align-items-center">
    <span><c:out value="${medicine.medicineName}" /> <span class="badge ${medicine.badgeClass} ms-2"><c:out value="${medicine.stockStatus}" /></span></span>
    <div class="d-flex gap-2">
      <c:if test="${base eq 'admin'}"><a class="btn btn-sm btn-primary" href="${ctx}/admin/medicines?action=edit&id=${medicine.id}"><i class="bi bi-pencil me-1"></i>Edit</a></c:if>
      <a class="btn btn-sm btn-light" href="${ctx}/${base}/medicines">Back to list</a>
    </div>
  </div>
  <div class="card-body">
    <dl class="row detail-list mb-0">
      <dt class="col-sm-3">Generic name</dt><dd class="col-sm-9"><c:out value="${medicine.genericName}" default="-" /></dd>
      <dt class="col-sm-3">Category</dt><dd class="col-sm-9"><c:out value="${medicine.categoryName}" /></dd>
      <dt class="col-sm-3">Supplier</dt><dd class="col-sm-9"><c:out value="${medicine.supplierName}" /></dd>
      <dt class="col-sm-3">Batch number</dt><dd class="col-sm-9"><c:out value="${medicine.batchNumber}" /></dd>
      <dt class="col-sm-3">Manufactured</dt><dd class="col-sm-9"><fmt:formatDate value="${medicine.manufactureDate}" pattern="dd MMM yyyy" /></dd>
      <dt class="col-sm-3">Expiry date</dt><dd class="col-sm-9"><fmt:formatDate value="${medicine.expiryDate}" pattern="dd MMM yyyy" />
        <c:if test="${medicine.expired}"><span class="badge bg-dark ms-2">EXPIRED</span></c:if>
        <c:if test="${medicine.nearExpiry}"><span class="badge text-bg-warning ms-2">NEAR EXPIRY</span></c:if></dd>
      <c:if test="${base eq 'admin'}">
        <dt class="col-sm-3">Purchase price</dt><dd class="col-sm-9">&#8377;<fmt:formatNumber value="${medicine.purchasePrice}" minFractionDigits="2" maxFractionDigits="2" /></dd>
      </c:if>
      <dt class="col-sm-3">Selling price</dt><dd class="col-sm-9">&#8377;<fmt:formatNumber value="${medicine.sellingPrice}" minFractionDigits="2" maxFractionDigits="2" /></dd>
      <dt class="col-sm-3">Stock quantity</dt><dd class="col-sm-9">${medicine.stockQuantity} <span class="text-muted">(reorder level ${medicine.reorderLevel})</span></dd>
      <dt class="col-sm-3">Description</dt><dd class="col-sm-9"><c:out value="${medicine.description}" default="-" /></dd>
    </dl>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
