<c:set var="pageTitle" value="${base eq 'admin' ? 'Medicines' : 'Inventory'}" />
<c:set var="activeNav" value="medicines" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<c:set var="isAdminView" value="${base eq 'admin'}" />

<div class="card mb-3">
  <div class="card-body">
    <form method="get" action="${ctx}/${base}/medicines" class="row g-2 align-items-end">
      <div class="col-12 col-md-4">
        <label class="form-label small mb-1">Search</label>
        <input type="search" class="form-control" name="q" value="<c:out value='${q}'/>" placeholder="Name, generic name or batch">
      </div>
      <div class="col-6 col-md-2">
        <label class="form-label small mb-1">Category</label>
        <select class="form-select" name="category">
          <option value="0">All</option>
          <c:forEach var="c" items="${categories}"><option value="${c.id}" ${category == c.id ? 'selected' : ''}><c:out value="${c.categoryName}" /></option></c:forEach>
        </select>
      </div>
      <div class="col-6 col-md-2">
        <label class="form-label small mb-1">Supplier</label>
        <select class="form-select" name="supplier">
          <option value="0">All</option>
          <c:forEach var="s" items="${suppliers}"><option value="${s.id}" ${supplier == s.id ? 'selected' : ''}><c:out value="${s.supplierName}" /></option></c:forEach>
        </select>
      </div>
      <div class="col-6 col-md-2">
        <label class="form-label small mb-1">Stock status</label>
        <select class="form-select" name="status">
          <option value="">All</option>
          <option value="AVAILABLE" ${status eq 'AVAILABLE' ? 'selected' : ''}>Available</option>
          <option value="LOW" ${status eq 'LOW' ? 'selected' : ''}>Low stock</option>
          <option value="OUT" ${status eq 'OUT' ? 'selected' : ''}>Out of stock</option>
          <option value="EXPIRED" ${status eq 'EXPIRED' ? 'selected' : ''}>Expired</option>
          <option value="NEAR" ${status eq 'NEAR' ? 'selected' : ''}>Near expiry (90 days)</option>
        </select>
      </div>
      <div class="col-6 col-md-2">
        <label class="form-label small mb-1">Sort by</label>
        <div class="input-group">
          <select class="form-select" name="sort">
            <option value="id" ${sort eq 'id' ? 'selected' : ''}>ID</option>
            <option value="name" ${sort eq 'name' ? 'selected' : ''}>Name</option>
            <option value="category" ${sort eq 'category' ? 'selected' : ''}>Category</option>
            <option value="expiry" ${sort eq 'expiry' ? 'selected' : ''}>Expiry</option>
            <option value="price" ${sort eq 'price' ? 'selected' : ''}>Price</option>
            <option value="stock" ${sort eq 'stock' ? 'selected' : ''}>Stock</option>
          </select>
          <select class="form-select" name="dir" style="max-width:5.5rem">
            <option value="asc" ${dir ne 'desc' ? 'selected' : ''}>Asc</option>
            <option value="desc" ${dir eq 'desc' ? 'selected' : ''}>Desc</option>
          </select>
        </div>
      </div>
      <div class="col-12 d-flex gap-2">
        <button class="btn btn-primary" type="submit"><i class="bi bi-funnel me-1"></i>Apply</button>
        <a class="btn btn-light" href="${ctx}/${base}/medicines">Reset</a>
        <c:if test="${isAdminView}">
          <a class="btn btn-success ms-auto" href="${ctx}/admin/medicines?action=add"><i class="bi bi-plus-lg me-1"></i>Add medicine</a>
        </c:if>
        <c:if test="${not isAdminView}">
          <a class="btn btn-success ms-auto" href="${ctx}/pharmacist/medicines?action=add"><i class="bi bi-plus-lg me-1"></i>Add medicine</a>
        </c:if>
      </div>
    </form>
  </div>
</div>

<div class="card">
  <div class="card-header d-flex justify-content-between">
    <span>${result.total} medicine(s) found</span>
    <span class="text-muted small">Page ${result.page} of ${result.pages}</span>
  </div>
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead>
        <tr>
          <th>ID</th><th>Medicine</th><th>Generic name</th><th>Category</th><th>Supplier</th><th>Batch</th>
          <th>Expiry</th><th class="text-end">Price</th><th class="text-end">Stock</th><th>Status</th><th class="text-end">Actions</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="m" items="${result.items}">
          <tr>
            <td>${m.id}</td>
            <td class="fw-semibold"><c:out value="${m.medicineName}" /></td>
            <td><c:out value="${m.genericName}" /></td>
            <td><c:out value="${m.categoryName}" /></td>
            <td><c:out value="${m.supplierName}" /></td>
            <td><c:out value="${m.batchNumber}" /></td>
            <td class="${m.expired ? 'text-danger fw-semibold' : (m.nearExpiry ? 'text-warning-emphasis fw-semibold' : '')}">
              <fmt:formatDate value="${m.expiryDate}" pattern="dd MMM yyyy" />
            </td>
            <td class="text-end">&#8377;<fmt:formatNumber value="${m.sellingPrice}" minFractionDigits="2" maxFractionDigits="2" /></td>
            <td class="text-end">${m.stockQuantity}</td>
            <td><span class="badge ${m.badgeClass}"><c:out value="${m.stockStatus}" /></span>
                <c:if test="${m.nearExpiry}"><span class="badge text-bg-warning">NEAR EXPIRY</span></c:if></td>
            <td class="text-end text-nowrap">
              <a class="btn btn-sm btn-outline-secondary" title="Details" href="${ctx}/${base}/medicines?action=view&id=${m.id}"><i class="bi bi-eye"></i></a>
              <c:if test="${isAdminView}">
                <a class="btn btn-sm btn-outline-primary" title="Edit" href="${ctx}/admin/medicines?action=edit&id=${m.id}"><i class="bi bi-pencil"></i></a>
                <button type="button" class="btn btn-sm btn-outline-danger" title="Delete" data-delete-url="${ctx}/admin/medicines"
                        data-delete-id="${m.id}" data-delete-name="<c:out value='${m.medicineName}'/>"><i class="bi bi-trash"></i></button>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty result.items}"><tr><td colspan="11" class="text-center text-muted py-4">No medicines match your filters.</td></tr></c:if>
      </tbody>
    </table>
  </div>
  <c:if test="${result.pages > 1}">
    <div class="card-footer">
      <nav aria-label="Medicine pages">
        <ul class="pagination pagination-sm mb-0 justify-content-center flex-wrap">
          <li class="page-item ${result.page <= 1 ? 'disabled' : ''}">
            <a class="page-link" href="${ctx}/${base}/medicines?${qs}&page=${result.page - 1}">Previous</a></li>
          <c:forEach begin="1" end="${result.pages}" var="p">
            <li class="page-item ${p == result.page ? 'active' : ''}">
              <a class="page-link" href="${ctx}/${base}/medicines?${qs}&page=${p}">${p}</a></li>
          </c:forEach>
          <li class="page-item ${result.page >= result.pages ? 'disabled' : ''}">
            <a class="page-link" href="${ctx}/${base}/medicines?${qs}&page=${result.page + 1}">Next</a></li>
        </ul>
      </nav>
    </div>
  </c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
