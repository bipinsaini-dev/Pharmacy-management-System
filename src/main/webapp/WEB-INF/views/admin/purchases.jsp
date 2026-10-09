<c:set var="pageTitle" value="Purchases" />
<c:set var="activeNav" value="purchases" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3"><div class="card-body">
  <form method="get" action="${ctx}/admin/purchases" class="row g-2 align-items-end">
    <div class="col-6 col-md-3"><label class="form-label small mb-1">From</label><input type="date" class="form-control" name="from" value="<c:out value='${from}'/>"></div>
    <div class="col-6 col-md-3"><label class="form-label small mb-1">To</label><input type="date" class="form-control" name="to" value="<c:out value='${to}'/>"></div>
    <div class="col-12 col-md-6 d-flex gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-funnel me-1"></i>Filter</button>
      <a class="btn btn-light" href="${ctx}/admin/purchases">Reset</a>
      <a class="btn btn-success ms-auto" href="${ctx}/admin/purchases?action=new"><i class="bi bi-plus-lg me-1"></i>New purchase</a>
    </div>
  </form>
</div></div>
<div class="card"><div class="table-responsive">
  <table class="table table-hover align-middle mb-0">
    <thead><tr><th>ID</th><th>Date</th><th>Supplier</th><th>Recorded by</th><th class="text-end">Total</th><th class="text-end">Actions</th></tr></thead>
    <tbody>
      <c:forEach var="p" items="${purchases}">
        <tr>
          <td>${p.id}</td>
          <td><fmt:formatDate value="${p.purchaseDate}" pattern="dd MMM yyyy" /></td>
          <td><c:out value="${p.supplierName}" /></td>
          <td><c:out value="${p.userName}" /></td>
          <td class="text-end">&#8377;<fmt:formatNumber value="${p.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></td>
          <td class="text-end"><a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/purchases?action=view&id=${p.id}"><i class="bi bi-eye"></i></a></td>
        </tr>
      </c:forEach>
      <c:if test="${empty purchases}"><tr><td colspan="6" class="text-center text-muted py-4">No purchase records found.</td></tr></c:if>
    </tbody>
  </table>
</div></div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
