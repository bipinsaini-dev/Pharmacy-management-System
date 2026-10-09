<c:set var="pageTitle" value="Categories" />
<c:set var="activeNav" value="categories" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="d-flex justify-content-end mb-3">
  <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#categoryModal" data-modal-title="Add category"><i class="bi bi-plus-lg me-1"></i>Add category</button>
</div>
<div class="card"><div class="table-responsive">
  <table class="table table-hover align-middle mb-0">
    <thead><tr><th>ID</th><th>Category</th><th>Description</th><th class="text-end">Medicines</th><th class="text-end">Actions</th></tr></thead>
    <tbody>
      <c:forEach var="c" items="${categories}">
        <tr>
          <td>${c.id}</td>
          <td class="fw-semibold"><c:out value="${c.categoryName}" /></td>
          <td><c:out value="${c.description}" default="-" /></td>
          <td class="text-end"><span class="badge text-bg-light border">${c.medicineCount}</span></td>
          <td class="text-end text-nowrap">
            <button type="button" class="btn btn-sm btn-outline-primary" title="Edit" data-bs-toggle="modal" data-bs-target="#categoryModal"
                    data-modal-title="Edit category" data-id="${c.id}" data-category-name="<c:out value='${c.categoryName}'/>"
                    data-description="<c:out value='${c.description}'/>"><i class="bi bi-pencil"></i></button>
            <button type="button" class="btn btn-sm btn-outline-danger" title="Delete" data-delete-url="${ctx}/admin/categories"
                    data-delete-id="${c.id}" data-delete-name="<c:out value='${c.categoryName}'/>"><i class="bi bi-trash"></i></button>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty categories}"><tr><td colspan="5" class="text-center text-muted py-4">No categories yet.</td></tr></c:if>
    </tbody>
  </table>
</div></div>

<div class="modal fade crud-modal" id="categoryModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content needs-validation" method="post" action="${ctx}/admin/categories" novalidate>
      <input type="hidden" name="action" value="save">
      <input type="hidden" name="id" value="0">
      <div class="modal-header"><h5 class="modal-title">Category</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div>
      <div class="modal-body">
        <div class="mb-3"><label class="form-label">Category name *</label><input class="form-control" name="categoryName" required maxlength="80"><div class="invalid-feedback">Category name is required.</div></div>
        <div class="mb-0"><label class="form-label">Description</label><textarea class="form-control" name="description" rows="2" maxlength="255"></textarea></div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn btn-primary">Save</button></div>
    </form>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
